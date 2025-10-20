// Este archivo define las dependencias y configuración de tu módulo de la app.
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.app.xanostoregym"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.app.xanostoregym"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    // Habilitamos View Binding para acceder a las vistas XML de forma más segura y fácil
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Retrofit para llamadas a la API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Convertidor Gson para transformar JSON a objetos Kotlin
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // Interceptor para ver los logs de las llamadas a la API (muy útil para depurar)
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // Glide para cargar imágenes desde una URL de forma eficiente
    implementation("com.github.bumptech.glide:glide:4.15.1")
}