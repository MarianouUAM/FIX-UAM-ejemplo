plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    // 1. Agregamos el plugin de Google Services para Firebase
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.fixuamrepopoo"

    compileSdk = 36 // Ajustado a la versión estable actual

    defaultConfig {
        applicationId = "com.example.fixuamrepopoo"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Opciones de compilación necesarias para Compose
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Videos de ayuda en el login
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-ui:1.3.1")

    // --- NUEVO: LIBRERÍAS DE FIREBASE ---
    // Importamos el BOM (Bill of Materials) para que gestione las versiones solo
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    // Agregamos Firestore (La base de datos en tiempo real)
    implementation("com.google.firebase:firebase-firestore")

    // (Opcional) Dejo Room por si querés guardar cosas locales más adelante,
    // pero si querés purgarlo, podés borrar estas tres líneas.
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation("com.google.code.gson:gson:2.10.1")
}