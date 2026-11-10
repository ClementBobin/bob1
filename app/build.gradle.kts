import java.util.Properties
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

// Push is optional while developing: without google-services.json the app builds and runs, it just has no FCM token.
if (file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
    apply(plugin = "com.google.firebase.crashlytics")
    apply(plugin = "com.google.firebase.firebase-perf")
}

val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) load(f.inputStream())
}

// Accept version injection from CI via -PversionName=x.y.z -PversionCode=N
// Falls back to local defaults when building outside CI.
val ciVersionName: String = findProperty("versionName") as String? ?: "dev"
val ciVersionCode: Int    = (findProperty("versionCode") as String?)?.toInt() ?: 1

// ./gradlew assembleDebug -Pbob1.apiUrl=http://192.168.1.20:5000/api/v1/   (must end with a slash)
val apiUrl = providers.gradleProperty("bob1.apiUrl").orElse("http://10.0.2.2:5000/api/v1/")

android {
    // Must match the Kotlin package (com.mirage.bob1): it decides where BuildConfig and R are generated
    // and what ".MainActivity" / ".Bob1Application" resolve to in the manifest.
    // Independent from applicationId, which stays com.mirage.bob1 (Play Store package).
    namespace = "com.mirage.bob1"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.mirage.bob1"
        minSdk = 26
        targetSdk = 37
        versionCode = ciVersionCode
        versionName = ciVersionName
    }

    buildTypes {
        debug {
            buildConfigField("String", "API_BASE_URL", "\"${apiUrl.get()}\"")
            manifestPlaceholders["usesCleartext"] = "true"   // dev API runs on http://
        }
        release {
            isMinifyEnabled = false
            buildConfigField("String", "API_BASE_URL", "\"${providers.gradleProperty("bob1.apiUrl").orElse("https://oak.tail025bf6.ts.net/api/v1/").get()}\"")
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
        isCoreLibraryDesugaringEnabled = true
    }

    kotlin { jvmToolchain(17) }

    val keystorePath = (project.findProperty("KEYSTORE_PATH") as? String)?.takeIf { it.isNotBlank() }
    val keystorePassword = project.findProperty("KEYSTORE_PASSWORD") as? String
    val keyAlias = project.findProperty("KEY_ALIAS") as? String
    val keyPassword = project.findProperty("KEY_PASSWORD") as? String


    signingConfigs {
        if (keystorePath != null && keystorePassword != null && keyAlias != null && keyPassword != null) {
            create("release") {
                storeFile = file(keystorePath)
                storePassword = keystorePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            }
        }
    }
    buildTypes {
        release {
            val releaseConfig = signingConfigs.findByName("release")
            signingConfig = releaseConfig ?: signingConfigs.getByName("debug")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
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
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)

    coreLibraryDesugaring(libs.desugar.jdk.libs)
}