# XanoStoreGym - Aplicación Android Nativa con Kotlin y Xano

**XanoStoreGym** es una aplicación Android nativa, desarrollada íntegramente en Kotlin, que consume una API RESTful construida en la plataforma no-code **Xano**. El proyecto está diseñado con un enfoque didáctico avanzado, abarcando flujos reales de un e-commerce con gestión de roles.

La aplicación implementa un flujo completo incluyendo:
* **Roles de Usuario:** Navegación y permisos diferenciados para Administradores y Clientes.
* **Seguridad:** Login con validación de bloqueo de cuenta y redirección por rol.
* **Catálogo Avanzado:** Detalle de producto con **carrusel de imágenes** (`ViewPager2`) y selector de cantidad basado en stock real.
* **Gestión Admin:** Panel para bloquear/desbloquear usuarios (Switch) y crear productos con **subida de múltiples imágenes**.
* **Carrito Interactivo:** Edición de cantidades en tiempo real dentro del carrito.

---
### 🎯 Objetivos de Aprendizaje
Este proyecto sirve como una guía práctica para entender los siguientes conceptos:
* **Gestión de Roles y Seguridad:** Cómo manejar lógica de negocio para separar flujos de Admin y Cliente, y restringir el acceso a usuarios bloqueados.
* **Componentes UI Avanzados:** Uso de `ViewPager2` y `TabLayout` para galerías de imágenes, y selectores personalizados (`Drawables`) para una UI moderna.
* **Integración de APIs Compleja:** Consumo de endpoints REST (`Retrofit`) manejando listas, objetos anidados, subida de archivos `Multipart` y métodos `PATCH` para actualizaciones parciales.
* **Lógica de Estado:** Manejo de sesión con `SharedPreferences` extendido (Token, Rol, Carrito local) y validaciones de stock en tiempo real.
* **View Binding y RecyclerView:** Uso de adaptadores personalizados con *callbacks* (listeners) para manejar eventos en listas (clicks, switches, botones +/-).

---
### 📂 Estructura del Proyecto
El proyecto está organizado siguiendo una arquitectura por capas, separando ahora la lógica de Admin y Cliente.
```
XanoStoreGym/ ├─ app/ ├── src/main/java/com/app/xanostoregym/ │ ├── api/ # Lógica de red y sesión │ │ ├── ApiClient.kt # Configuración de Retrofit │ │ ├── ApiService.kt # Endpoints (Auth, Products, Users, Orders) │ │ └── SessionManager.kt # Gestión de Token, Rol, Carrito y Logout │ ├── model/ # Modelos de Datos │ │ ├── LoginResponse.kt # Incluye User con Role y Blocked status │ │ ├── Product.kt # Producto con lista de ImageResource │ │ └── [Otros modelos: Order, CartItem, ImageResource...] │ └── ui/ # Interfaz de Usuario │ ├── adapter/ │ │ ├── CartAdapter.kt # Adaptador de carrito con control de cantidad (+/-) │ │ ├── ImagePagerAdapter.kt # Adaptador para el carrusel de imágenes │ │ ├── ProductAdapter.kt # Catálogo general │ │ └── UserAdapter.kt # Lista de usuarios con Switch de bloqueo │ ├── AdminActivity.kt # Contenedor principal para Administradores │ ├── ClientActivity.kt # Contenedor principal para Clientes │ ├── LoginActivity.kt # Login inteligente con redirección por rol │ ├── AddProductFragment.kt # Creación de productos con subida múltiple │ ├── AdminUsersFragment.kt # Gestión de usuarios (Bloquear/Desbloquear) │ ├── CartFragment.kt # Carrito de compras editable │ ├── ProductDetailFragment.kt # Detalle con Carrusel y Stock │ ├── ProductsFragment.kt # Catálogo (Vista Cliente) │ └── ProfileFragment.kt # Perfil de usuario ├── src/main/res/ │ ├── drawable/ # Recursos gráficos personalizados │ │ ├── bg_border_rounded.xml # Bordes para botones de cantidad │ │ └── tab_indicator_selector.xml # Indicadores del carrusel │ ├── layout/ # Diseños XML │ │ ├── activity_admin.xml │ │ ├── activity_login.xml │ │ ├── fragment_product_detail.xml # Incluye ViewPager2 │ │ ├── item_cart.xml # Fila de carrito con botones de edición │ │ ├── item_image_carousel.xml # Ítem para el slider de fotos │ │ ├── item_user.xml # Fila de usuario con Switch │ │ └── [Otros layouts...] │ └── menu/ # Menús de navegación (Admin vs Cliente) └── build.gradle.kts # Configuración del módulo
```

---
### 🔧 Configuración de Android y Librerías
* **namespace:** `com.app.xanostoregym`
* **compileSdk:** 34
* **buildFeatures:** `viewBinding = true`
* **Librerías Clave:**
  * `androidx.viewpager2:viewpager2`: Para el carrusel de imágenes.
  * `com.google.android.material:material`: Para `TabLayout`, botones y estilos.
  * `com.squareup.retrofit2`: Comunicación con API Xano.
  * `com.github.bumptech.glide`: Carga y cacheo de imágenes.
  * `org.jetbrains.kotlinx:kotlinx-coroutines`: Manejo asíncrono.

---
### 🌐 Endpoints de la API Utilizados
La API en Xano debe estar configurada para exponer los campos `role` y `blocked`.
* **Auth:**
  * `POST /auth/login`: Login (retorna token).
  * `GET /auth/me`: Perfil (retorna rol y estado de bloqueo).
* **Productos:**
  * `GET /product`: Listar productos.
  * `GET /product/{id}`: Detalle único.
  * `POST /product`: Crear producto (Admin).
  * `POST /upload/image`: Subir imagen (Multipart).
  * `PATCH /product/{id}`: Asignar array de imágenes al producto.
* **Usuarios (Admin):**
  * `GET /user`: Listar todos los usuarios.
  * `PATCH /user/{id}`: Editar usuario (usado para el Switch `blocked`).
* **Pedidos:**
  * `POST /order`: Crear una orden de compra.

---
### 📘 Detalle de Clases y Flujos de Trabajo Actualizados

#### `api/SessionManager.kt`
Ahora gestiona no solo el token, sino también el **Rol del usuario** (`saveUserRole`) para decidir la navegación futura y actualiza la cantidad de ítems en el carrito sin duplicarlos.

#### `ui/LoginActivity.kt`
Implementa la lógica de seguridad:
1. Autentica credenciales.
2. Consulta `/auth/me` para verificar si `user.blocked == true`.
3. Si está bloqueado, impide el acceso. Si no, redirige a `AdminActivity` o `ClientActivity` según el rol.

#### `ui/ProductDetailFragment.kt`
* **Carrusel:** Usa `ViewPager2` con `ImagePagerAdapter` y `TabLayoutMediator` para mostrar múltiples fotos con indicadores circulares.
* **Stock:** Controla que el usuario no pueda seleccionar más cantidad de la disponible (`maxStock`) usando botones locales antes de añadir al carrito.

#### `ui/AddProductFragment.kt` (Admin)
Utiliza `ActivityResultContracts.GetMultipleContents()` para permitir seleccionar varias fotos de la galería a la vez. Luego, realiza un ciclo de subida al servidor y finalmente asocia todas las imágenes al producto creado.

#### `ui/AdminUsersFragment.kt` (Admin)
Muestra la lista de usuarios usando `UserAdapter`. Este adaptador incluye un **Switch** que, al ser activado/desactivado, llama inmediatamente a la API (`PATCH /user/{id}`) para bloquear o desbloquear el acceso de ese usuario en tiempo real.

#### `ui/CartFragment.kt`
Permite editar la cantidad de productos (`+` / `-`) directamente en la lista. Valida contra el stock máximo del producto y recalcula el total a pagar dinámicamente.

---
### ⚙️ Ejecución del Proyecto
1. Clona el repositorio.
2. Abre en Android Studio y sincroniza Gradle.
3. Asegúrate de que la API de Xano tenga los campos `role` y `blocked` visibles en los endpoints.
4. Ejecuta la app.

> **Credenciales de Prueba (Roles):**
>
> **👮 Admin:**
> * Email: `admin@gym.com`
> * Pass: `a12345678`
> * *Acceso: Panel de gestión, crear productos, bloquear usuarios.*
>
> **👤 Cliente:**
> * Email: `cliente@gym.com`
> * Pass: `a12345678`
> * *Acceso: Catálogo, comprar, carrito, perfil.*

---
