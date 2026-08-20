import java.util.Properties
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) load(f.inputStream())
}

// Accept version injection from CI via -PversionName=x.y.z -PversionCode=N
// Falls back to local defaults when building outside CI.
val ciVersionName: String = findProperty("versionName") as String? ?: "1.0.0"
val ciVersionCode: Int    = (findProperty("versionCode") as String?)?.toInt() ?: 1

android {
    namespace = "com.bob1.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.bob1.app"
        minSdk = 29
        targetSdk = 36
        versionCode = ciVersionCode
        versionName = ciVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "boolean",
            "MOCK_API",
            localProps.getProperty("MOCK_API", "false")
        )
        buildConfigField(
            "String",
            "BASE_URL",
            "\"${localProps.getProperty("BASE_URL", "https://bob-1-api-git-dev-clementbobins-projects.vercel.app")}\""
        )
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "..."
    }

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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.mock)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.kindling.core)
    implementation(libs.kindling.compose)
    implementation(libs.kindling.utils)
    implementation(libs.kindling.android)

    implementation(libs.coil.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    coreLibraryDesugaring(libs.android.desugar.jdk.libs)
}
