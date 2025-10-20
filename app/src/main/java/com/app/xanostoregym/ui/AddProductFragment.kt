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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class AddProductFragment : Fragment() {
    private var selectedImageUri: Uri? = null
    private lateinit var ivProductImage: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var sessionManager: SessionManager

    // Lanzador para el selector de imágenes de la galería
    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            ivProductImage.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)
        sessionManager = SessionManager(requireContext())
        ivProductImage = view.findViewById(R.id.ivProductImage)
        val btnSelectImage = view.findViewById<Button>(R.id.btnSelectImage)
        val btnCreateProduct = view.findViewById<Button>(R.id.btnCreateProduct)
        progressBar = view.findViewById(R.id.progressBar)

        btnSelectImage.setOnClickListener { selectImageLauncher.launch("image/*") }
        btnCreateProduct.setOnClickListener { createProductFlow(view) }

        return view
    }

    private fun createProductFlow(view: View) {
        val token = sessionManager.fetchAuthToken()
        if (token == null) {
            Toast.makeText(context, "Error de autenticación. Vuelve a iniciar sesión.", Toast.LENGTH_LONG).show()
            return
        }
        val bearerToken = "Bearer $token"

        val name = view.findViewById<EditText>(R.id.etProductName).text.toString()
        val description = view.findViewById<EditText>(R.id.etProductDescription).text.toString()
        val price = view.findViewById<EditText>(R.id.etProductPrice).text.toString()
        val stock = view.findViewById<EditText>(R.id.etProductStock).text.toString()
        val brand = view.findViewById<EditText>(R.id.etProductBrand).text.toString()
        val category = view.findViewById<EditText>(R.id.etProductCategory).text.toString()

        if (name.isEmpty() || price.isEmpty() || stock.isEmpty()) {
            Toast.makeText(context, "Nombre, Precio y Stock son requeridos", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // PASO 1: Crear el producto (sin imagen)
                val productJson = JSONObject().apply {
                    put("name", name); put("description", description); put("price", price.toDouble())
                    put("stock", stock.toInt()); put("brand", brand); put("category", category)
                }
                val productRequestBody = productJson.toString().toRequestBody("application/json".toMediaTypeOrNull())
                val createResponse = ApiClient.instance.createProduct(bearerToken, productRequestBody)

                if (!createResponse.isSuccessful) throw Exception("Error al crear el producto: ${createResponse.errorBody()?.string()}")
                val createdProduct = createResponse.body()!!

                // Si no se seleccionó imagen, terminamos el flujo aquí.
                if (selectedImageUri == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Producto creado sin imagen", Toast.LENGTH_LONG).show()
                        clearForm(view)
                    }
                    return@launch
                }

                // PASO 2: Subir la imagen
                val imagePart = uriToMultipartBodyPart(selectedImageUri!!)
                val uploadResponse = ApiClient.instance.uploadImage(bearerToken, imagePart)

                if (!uploadResponse.isSuccessful) throw Exception("Error al subir la imagen: ${uploadResponse.errorBody()?.string()}")
                val imagePaths = uploadResponse.body() ?: throw Exception("La API no devolvió el path de la imagen")

                // PASO 3: Adjuntar la imagen al producto
                val attachJson = JSONObject().apply { put("image", imagePaths.firstOrNull()) } // Xano espera el path, no un array aquí.
                val attachRequestBody = attachJson.toString().toRequestBody("application/json".toMediaTypeOrNull())
                val attachResponse = ApiClient.instance.attachImageToProduct(bearerToken, createdProduct.id, attachRequestBody)

                withContext(Dispatchers.Main) {
                    if (attachResponse.isSuccessful) {
                        Toast.makeText(context, "¡Producto creado y con imagen!", Toast.LENGTH_LONG).show()
                        clearForm(view)
                    } else {
                        Toast.makeText(context, "Producto creado, pero falló al adjuntar imagen", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("AddProductFragment", "Error en flujo de creación", e)
                    Toast.makeText(context, "Error en el proceso: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                withContext(Dispatchers.Main) { progressBar.visibility = View.GONE }
            }
        }
    }

    // Función para limpiar el formulario después de crear un producto
    private fun clearForm(view: View) {
        view.findViewById<EditText>(R.id.etProductName).text.clear()
        view.findViewById<EditText>(R.id.etProductDescription).text.clear()
        view.findViewById<EditText>(R.id.etProductPrice).text.clear()
        view.findViewById<EditText>(R.id.etProductStock).text.clear()
        view.findViewById<EditText>(R.id.etProductBrand).text.clear()
        view.findViewById<EditText>(R.id.etProductCategory).text.clear()
        ivProductImage.setImageResource(android.R.drawable.ic_menu_camera)
        selectedImageUri = null
    }

    // Función auxiliar para convertir un URI a un formato que Retrofit pueda enviar
    private fun uriToMultipartBodyPart(uri: Uri): MultipartBody.Part {
        val fileDir = requireContext().applicationContext.filesDir
        val file = File(fileDir, "temp_image_file")
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)

        var fileName = "image.jpg"
        requireContext().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                fileName = cursor.getString(nameIndex)
            }
        }
        val requestFile = file.asRequestBody(requireContext().contentResolver.getType(uri)?.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("content", fileName, requestFile)
    }
}