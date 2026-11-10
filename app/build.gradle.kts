plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

// Push is optional while developing: without google-services.json the app builds and runs, it just has no FCM token.
if (file("google-services.json").exists()) apply(plugin = "com.google.gms.google-services")

// ./gradlew assembleDebug -Pbob1.apiUrl=http://192.168.1.20:5000/api/v1/   (must end with a slash)
val apiUrl = providers.gradleProperty("bob1.apiUrl").orElse("http://10.0.2.2:5000/api/v1/")

android {
    namespace = "org.bob1.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "org.bob1.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        debug {
            buildConfigField("String", "API_BASE_URL", "\"${apiUrl.get()}\"")
            manifestPlaceholders["usesCleartext"] = "true"   // dev API runs on http://
        }
        release {
            isMinifyEnabled = false
            buildConfigField("String", "API_BASE_URL", "\"${providers.gradleProperty("bob1.apiUrl").orElse("https://api.bob1.example/api/v1/").get()}\"")
            manifestPlaceholders["usesCleartext"] = "false"
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin { jvmToolchain(17) }
}

dependencies {
    implementation(project(":room"))
    implementation(project(":capacity"))

    // Kindling: ui (core), compose (KScreen / KViewModel / KDestination), android (via :capacity)
    implementation(libs.kindling.core)
    implementation(libs.kindling.utils)
    implementation(libs.kindling.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
}
