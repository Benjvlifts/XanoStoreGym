package com.app.xanostoregym.ui

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.app.xanostoregym.R
import com.app.xanostoregym.api.ApiClient
import com.app.xanostoregym.api.SessionManager
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class AddProductFragment : Fragment() {
    // CAMBIO 1: Ahora guardamos una LISTA de Uris, no una sola
    private var selectedUris: List<Uri> = emptyList()

    private lateinit var ivProductImage: ImageView
    private lateinit var tvImageCount: TextView // Nuevo TextView para mostrar cuántas hay
    private lateinit var progressBar: ProgressBar
    private lateinit var sessionManager: SessionManager

    // CAMBIO 2: Usamos GetMultipleContents en lugar de GetContent
    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            selectedUris = uris
            // Mostramos la primera imagen como previsualización
            ivProductImage.setImageURI(uris.first())
            // Actualizamos el texto informativo (puedes agregar este TextView al XML o usar un Toast)
            Toast.makeText(context, "Se seleccionaron ${uris.size} imágenes", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)
        sessionManager = SessionManager(requireContext())

        ivProductImage = view.findViewById(R.id.ivProductImage)
        progressBar = view.findViewById(R.id.progressBar)

        val btnSelectImage = view.findViewById<Button>(R.id.btnSelectImage)
        val btnCreateProduct = view.findViewById<Button>(R.id.btnCreateProduct)

        // CAMBIO 3: El launcher ahora espera un tipo de input genérico "image/*"
        btnSelectImage.setOnClickListener { selectImageLauncher.launch("image/*") }
        btnCreateProduct.setOnClickListener { createProductFlow(view) }

        return view
    }

    private fun createProductFlow(view: View) {
        val token = sessionManager.fetchAuthToken()
        if (token == null) {
            Toast.makeText(context, "Sesión inválida", Toast.LENGTH_SHORT).show()
            return
        }
        val bearerToken = "Bearer $token"

        val name = view.findViewById<EditText>(R.id.etProductName).text.toString()
        val desc = view.findViewById<EditText>(R.id.etProductDescription).text.toString()
        val price = view.findViewById<EditText>(R.id.etProductPrice).text.toString()
        val stock = view.findViewById<EditText>(R.id.etProductStock).text.toString()
        val brand = view.findViewById<EditText>(R.id.etProductBrand).text.toString()
        val category = view.findViewById<EditText>(R.id.etProductCategory).text.toString()

        if (name.isEmpty() || price.isEmpty()) {
            Toast.makeText(context, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1. Crear el producto base
                val jsonProd = JSONObject().apply {
                    put("name", name); put("description", desc); put("price", price.toDouble())
                    put("stock", stock.toIntOrNull() ?: 0); put("brand", brand); put("category", category)
                }
                val bodyProd = jsonProd.toString().toRequestBody("application/json".toMediaTypeOrNull())
                val resCreate = ApiClient.instance.createProduct(bearerToken, bodyProd)

                if (resCreate.isSuccessful && resCreate.body() != null) {
                    val productId = resCreate.body()!!.id

                    // CAMBIO 4: Lógica de subida múltiple
                    if (selectedUris.isNotEmpty()) {
                        val imageArray = JSONArray() // Aquí acumularemos las respuestas de Xano

                        // Recorremos cada imagen seleccionada
                        for (uri in selectedUris) {
                            try {
                                // A) Preparamos el archivo
                                val part = uriToMultipartBodyPart(uri)
                                // B) Subimos la imagen individualmente
                                val resUpload = ApiClient.instance.uploadImage(bearerToken, part)

                                if (resUpload.isSuccessful && resUpload.body() != null) {
                                    // C) Obtenemos el objeto de respuesta de Xano
                                    val imageObj = resUpload.body()!!.first()
                                    // D) Lo convertimos a JSON y lo guardamos en nuestro array acumulador
                                    val imageJsonStr = Gson().toJson(imageObj)
                                    imageArray.put(JSONObject(imageJsonStr))
                                }
                            } catch (e: Exception) {
                                Log.e("UploadError", "Error subiendo una de las imágenes", e)
                            }
                        }

                        // CAMBIO 5: Adjuntar TODAS las imágenes juntas al producto
                        if (imageArray.length() > 0) {
                            val attachJson = JSONObject().apply { put("image", imageArray) }
                            val attachBody = attachJson.toString().toRequestBody("application/json".toMediaTypeOrNull())
                            ApiClient.instance.attachImageToProduct(bearerToken, productId, attachBody)
                        }
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Producto Creado con ${selectedUris.size} fotos", Toast.LENGTH_LONG).show()
                        clearForm(view)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) { progressBar.visibility = View.GONE }
            }
        }
    }

    private fun clearForm(view: View) {
        view.findViewById<EditText>(R.id.etProductName).text.clear()
        view.findViewById<EditText>(R.id.etProductDescription).text.clear()
        view.findViewById<EditText>(R.id.etProductPrice).text.clear()
        view.findViewById<EditText>(R.id.etProductStock).text.clear()
        view.findViewById<EditText>(R.id.etProductBrand).text.clear()
        view.findViewById<EditText>(R.id.etProductCategory).text.clear()
        ivProductImage.setImageResource(android.R.drawable.ic_menu_camera)
        selectedUris = emptyList() // Limpiamos la lista
    }

    private fun uriToMultipartBodyPart(uri: Uri): MultipartBody.Part {
        val fileDir = requireContext().applicationContext.filesDir
        // Usamos un nombre único temporal para cada archivo para evitar sobreescritura en el ciclo
        val file = File(fileDir, "temp_img_${System.currentTimeMillis()}.jpg")
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)

        var fileName = "image.jpg"
        requireContext().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (idx != -1) fileName = cursor.getString(idx)
            }
        }
        val requestFile = file.asRequestBody(requireContext().contentResolver.getType(uri)?.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("content", fileName, requestFile)
    }
}