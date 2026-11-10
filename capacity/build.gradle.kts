plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "org.bob1.capacity"
    compileSdk = 36
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin { jvmToolchain(17) }
}

dependencies {
    // Everything native comes from Kindling :android (also brings firebase-messaging as `api`).
    api(libs.kindling.android)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment.ktx)   // PermissionHelper.registerLauncher needs a FragmentActivity
    implementation(libs.kotlinx.coroutines.android)
}
