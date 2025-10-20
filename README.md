# XanoStoreGym - Aplicación Android Nativa con Kotlin y Xano

**XanoStoreGym** es una aplicación Android nativa, desarrollada íntegramente en Kotlin, que consume una API RESTful construida en la plataforma no-code **Xano**. El proyecto está diseñado con un enfoque didáctico, con el código minuciosamente comentado para que un estudiante pueda comprender la arquitectura, flujos de trabajo, librerías y buenas prácticas fundamentales en el desarrollo Android moderno.

La aplicación implementa un flujo completo de e-commerce, incluyendo:
* Autenticación de usuario (login y obtención de perfil).
* Listado de productos con búsqueda en tiempo real y vista de detalle.
* Creación de productos con subida de imágenes (multipart/form-data).
* Gestión de sesión y perfil de usuario.

---
### 🎯 Objetivos de Aprendizaje
Este proyecto sirve como una guía práctica para entender los siguientes conceptos:
* **Integración de APIs:** Cómo consumir una API REST utilizando `Retrofit`, `OkHttp` y `Gson` en conjunto con Coroutines de Kotlin para manejar la asincronía.
* **Interfaz de Usuario con XML:** Construcción de interfaces de usuario robustas y modernas utilizando layouts XML, `RecyclerView` para listas eficientes y `Fragments` para una navegación modular.
* **View Binding:** Acceder a los componentes de la UI de forma segura y sin `findViewById`.
* **Gestión de Sesión:** Manejar el estado de la sesión de un usuario (guardar y recuperar un token) utilizando `SharedPreferences`.
* **Manejo de Archivos:** Implementar la selección de imágenes desde la galería del dispositivo y su subida a un servidor como `multipart/form-data`.
* **Carga de Imágenes Remotas:** Utilizar la librería `Glide` para descargar, cachear y mostrar imágenes desde una URL de manera eficiente.
* **Estructura de Proyecto:** Organizar el código en una arquitectura limpia por capas (UI, API, Model).

---
### 📂 Estructura del Proyecto
El proyecto está organizado siguiendo una arquitectura por capas para separar responsabilidades.

XanoStoreGym/
├─ app/
├── src/main/java/com/app/xanostoregym/
│ ├── api/ # Lógica de conexión a la red y gestión de sesión
│ │ ├── ApiClient.kt # Objeto Singleton que configura Retrofit
│ │ ├── ApiService.kt # Interfaz que define todos los endpoints de la API
│ │ └── SessionManager.kt # Clase para guardar y leer el token con SharedPreferences
│ ├── model/ # Data Classes que representan los datos de la API
│ │ ├── LoginResponse.kt # Modelo para la respuesta del login
│ │ └── Product.kt # Modelo para un producto y su imagen
│ └── ui/ # Capa de interfaz de usuario (Activities y Fragments)
│ ├── adapter/
│ │ └── ProductAdapter.kt # Adaptador para el RecyclerView de productos
│ ├── AddProductFragment.kt # Lógica del formulario para crear productos
│ ├── LoginActivity.kt # Lógica de la pantalla de inicio de sesión
│ ├── MainActivity.kt # Contenedor principal con la BottomNavigationView
│ ├── ProductDetailFragment.kt # Lógica para la vista de detalle de un producto
│ ├── ProductsFragment.kt # Lógica para mostrar la lista de productos y la búsqueda
│ └── ProfileFragment.kt # Lógica para mostrar perfil y cerrar sesión
├── src/main/res/
│ ├── layout/ # Archivos XML que definen la interfaz gráfica
│ │ ├── activity_login.xml
│ │ ├── activity_main.xml
│ │ ├── fragment_add_product.xml
│ │ ├── fragment_product_detail.xml
│ │ ├── fragment_products.xml
│ │ ├── fragment_profile.xml
│ │ └── item_product.xml
│ └── menu/
│ └── bottom_nav_menu.xml # Define los ítems de la barra de navegación inferior
└── build.gradle.kts # Configuración del módulo y dependencias

---
### 🔧 Configuración de Android y Librerías
* **namespace:** `com.app.xanostoregym`
* **compileSdk:** 34, **minSdk:** 24, **targetSdk:** 34
* **compileOptions:** `JavaVersion.VERSION_1_8`
* **buildFeatures:** `viewBinding = true`
* **Librerías Principales:**
    * **AndroidX:** `core-ktx`, `appcompat`, `recyclerview`, `constraintlayout`
    * **Material:** `material` (para componentes de UI modernos)
    * **Networking:** `retrofit`, `converter-gson`, `okhttp-logging-interceptor`
    * **Corrutinas:** Integradas en Kotlin y AndroidX.
    * **Imágenes:** `glide`

---
### 🌐 Endpoints de la API Utilizados
Todas las llamadas se dirigen a la URL base de Xano configurada en `ApiClient.kt`.
* **`POST /auth/login`**: Autentica a un usuario y devuelve un `authToken`.
* **`GET /auth/me`**: Devuelve los datos del usuario autenticado, usando el `authToken`.
* **`GET /product`**: Obtiene la lista completa de productos.
* **`GET /product/{id}`**: Obtiene los detalles de un único producto por su ID.
* **`POST /product`**: Crea un nuevo producto (sin imagen). Requiere token.
* **`POST /upload/image`**: Sube un archivo de imagen. Requiere token.
* **`PATCH /product/{id}`**: Actualiza un producto existente (usado para adjuntar la imagen). Requiere token.

---
### 📘 Detalle de Clases y Flujos de Trabajo

#### `api/`
* **`ApiClient.kt`**: Objeto singleton que configura Retrofit con la URL base, el convertidor Gson y un interceptor de logs para depuración. Provee una única instancia `ApiClient.instance` para toda la app.
* **`ApiService.kt`**: Interfaz donde se definen, con anotaciones de Retrofit, todas las funciones que se corresponden con los endpoints de la API (ej. `@GET("product")`).
* **`SessionManager.kt`**: Clase simple que abstrae el uso de `SharedPreferences`. Permite guardar, leer y borrar el `authToken` de forma sencilla y centralizada.

#### `model/`
* **`LoginResponse.kt`**: Define la estructura de la respuesta JSON del login, conteniendo el `authToken` y un objeto `User`.
* **`Product.kt`**: Define la estructura de un producto, incluyendo un objeto anidado `ImageResource` para la imagen.

#### `ui/`
* **`LoginActivity.kt`**: Primera pantalla que ve el usuario. Si ya existe un token en `SessionManager`, navega directamente a `MainActivity`. De lo contrario, maneja el formulario de login, llama a la API y, si tiene éxito, guarda el token y navega a `MainActivity`.
* **`MainActivity.kt`**: Actividad principal que aloja la `BottomNavigationView` y un `FrameLayout`. Se encarga de intercambiar los `Fragments` (Productos, Añadir, Perfil) según la selección del usuario en el menú.
* **`ProductsFragment.kt`**: Carga la lista de productos desde la API usando una corrutina. Configura el `RecyclerView` con un `GridLayoutManager` para mostrar los productos en una cuadrícula. Implementa la lógica de la `SearchView` para filtrar la lista.
* **`ProductDetailFragment.kt`**: Recibe un `productId` a través de sus argumentos. Llama al endpoint `GET /product/{id}` para obtener los detalles y actualiza la UI con la información recibida.
* **`AddProductFragment.kt`**: Muestra un formulario para crear un producto. Utiliza un `ActivityResultLauncher` para abrir la galería y seleccionar una imagen. Implementa el flujo completo en tres pasos: 1) Crear producto, 2) Subir imagen, 3) Adjuntar imagen.
* **`ProfileFragment.kt`**: Llama al endpoint `GET /auth/me` enviando el token guardado para obtener y mostrar los datos del usuario. También contiene el botón de "Cerrar Sesión", que borra el token de `SessionManager` y regresa a `LoginActivity`.

#### `ui/adapter/`
* **`ProductAdapter.kt`**: Conecta la lista de productos con el `RecyclerView`. En su `ViewHolder`, utiliza `Glide` para cargar la imagen del producto desde la URL. Implementa la interfaz `Filterable` para permitir la búsqueda y una `OnItemClickListener` personalizada para manejar los clics en cada producto.

---
### ⚙️ Ejecución del Proyecto
1. Clona el repositorio.
2. Abre el proyecto en Android Studio.
3. Espera a que Gradle sincronice las dependencias.
4. Conecta un dispositivo o inicia un emulador.
5. Haz clic en el botón "Run 'app'" (▶️) para compilar e instalar la aplicación.

> **Credenciales de Prueba:**
> * **Email:** `adminB@gmail.com`
> * **Password:** `abcd12345`

---
### 💡 Sugerencia Pedagógica
Para los estudiantes, se recomienda seguir el flujo lógico de la aplicación:
1. Analizar `LoginActivity.kt`: Entender cómo se hace la llamada a la API y se guarda el token.
2. Estudiar `MainActivity.kt`: Comprender cómo funciona la navegación con `BottomNavigationView` y `Fragments`.
3. Explorar `ProductsFragment.kt` y `ProductAdapter.kt`: Ver cómo se consume una lista de datos y se muestra en un `RecyclerView`.
4. Revisar `AddProductFragment.kt`: Analizar el flujo más complejo de creación de datos y subida de archivos.
5. Finalmente, estudiar la capa `api` para entender cómo se configuró Retrofit.





XanoStoreKotlin/
├─ app/
│  ├─ src/main/java/com/miapp/xanostorekotlin
│  │  ├─ api/                      # Configuración y servicios HTTP
│  │  │  ├─ ApiConfig.kt          # Lee base URLs desde BuildConfig
│  │  │  ├─ RetrofitClient.kt     # Fábricas de Retrofit/OkHttp
│  │  │  ├─ AuthInterceptor.kt    # Inserta Authorization: Bearer <token>
│  │  │  ├─ TokenManager.kt       # Persistencia de token/usuario
│  │  │  ├─ AuthService.kt        # POST auth/login, GET auth/me
│  │  │  ├─ ProductService.kt     # GET/POST product
│  │  │  └─ UploadService.kt      # POST upload (multipart)
│  │  ├─ model/                   # Data classes (requests/responses/entidades)
│  │  │  ├─ User.kt
│  │  │  ├─ AuthResponse.kt
│  │  │  ├─ LoginRequest.kt
│  │  │  ├─ Product.kt
│  │  │  ├─ ProductImage.kt
│  │  │  ├─ CreateProductRequest.kt
│  │  │  └─ CreateProductResponse.kt
│  │  ├─ ui/
│  │  │  ├─ MainActivity.kt       # Login
│  │  │  ├─ HomeActivity.kt       # BottomNav: Perfil/Productos/Agregar
│  │  │  ├─ ProductDetailActivity.kt # Detalle de producto con carrusel
│  │  │  ├─ fragments/
│  │  │  │  ├─ ProductsFragment.kt # Lista + búsqueda
│  │  │  │  ├─ AddProductFragment.kt # Form + upload imágenes
│  │  │  │  └─ ProfileFragment.kt  # Perfil y logout
│  │  │  └─ adapter/
│  │  │     ├─ ProductAdapter.kt   # RecyclerView de productos
│  │  │     └─ ImagePreviewAdapter.kt # Previsualización selección de imágenes
│  ├─ src/main/res
│  │  ├─ layout/
│  │  │  ├─ activity_main.xml
│  │  │  ├─ activity_home.xml
│  │  │  ├─ activity_product_detail.xml
│  │  │  ├─ fragment_products.xml
│  │  │  ├─ fragment_add_product.xml
│  │  │  ├─ fragment_profile.xml
│  │  │  ├─ item_product.xml
│  │  │  ├─ item_image_preview.xml
│  │  │  └─ item_image_slider.xml
│  │  ├─ menu/bottom_nav_menu.xml  # Menú de navegación inferior
│  │  ├─ values/strings.xml, colors.xml, themes.xml
│  │  └─ values-night/themes.xml
│  ├─ build.gradle.kts             # Configuración del módulo app
│  └─ proguard-rules.pro
├─ docs/ENDPOINTS.md               # Endpoints y ejemplos cURL
├─ gradle/libs.versions.toml       # Catálogo de versiones y librerías
├─ settings.gradle.kts             # Módulos incluidos
└─ build.gradle.kts                # Plugins a nivel raíz