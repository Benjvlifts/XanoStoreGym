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

¡Por supuesto! Entendido perfectamente. Quieres que tome el texto que me proporcionaste y lo convierta a la sintaxis correcta de Markdown para que se vea profesional en GitHub.

Aquí tienes el texto formateado. Simplemente copia todo el contenido del siguiente bloque y pégalo en tu archivo README.md. Cuando lo subas a GitHub, se verá exactamente como esperas.

Markdown

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

XanoStoreGym/ ├─ app/ │ ├─ src/main/java/com/app/xanostoregym/ │ │ ├─ api/ # Lógica de conexión a la red y gestión de sesión │ │ │ ├─ ApiClient.kt # Objeto Singleton que configura Retrofit │ │ │ ├─ ApiService.kt # Interfaz que define todos los endpoints de la API │ │ │ └─ SessionManager.kt # Clase para guardar y leer el token con SharedPreferences │ │ ├─ model/ # Data Classes que representan los datos de la API │ │ │ ├─ LoginResponse.kt # Modelo para la respuesta del login │ │ │ └─ Product.kt # Modelo para un producto y su imagen │ │ └─ ui/ # Capa de interfaz de usuario (Activities y Fragments) │ │ ├─ adapter/ │ │ │ └─ ProductAdapter.kt # Adaptador para el RecyclerView de productos │ │ ├─ AddProductFragment.kt # Lógica del formulario para crear productos │ │ ├─ LoginActivity.kt # Lógica de la pantalla de inicio de sesión │ │ ├─ MainActivity.kt # Contenedor principal con la BottomNavigationView │ │ ├─ ProductDetailFragment.kt # Lógica para la vista de detalle de un producto │ │ ├─ ProductsFragment.kt # Lógica para mostrar la lista de productos y la búsqueda │ │ └─ ProfileFragment.kt # Lógica para mostrar perfil y cerrar sesión │ ├─ src/main/res/ │ │ ├─ layout/ # Archivos XML que definen la interfaz gráfica │ │ │ ├─ activity_login.xml │ │ │ ├─ activity_main.xml │ │ │ ├─ fragment_add_product.xml │ │ │ ├─ fragment_product_detail.xml │ │ │ ├─ fragment_products.xml │ │ │ ├─ fragment_profile.xml │ │ │ └─ item_product.xml │ │ └─ menu/ │ │ └─ bottom_nav_menu.xml # Define los ítems de la barra de navegación inferior │ └─ build.gradle.kts # Configuración del módulo y dependencias └─ AndroidManifest.xml # Define los componentes y permisos de la app

> **Nota:** La aplicación se conecta a un backend de Xano preconfigurado. Las credenciales de prueba para el login son:
> * **Email:** `adminB@gmail.com`
> * **Password:** `abcd12345`