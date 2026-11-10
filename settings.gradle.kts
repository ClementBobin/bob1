pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") // Kindling fallback
    }
}

rootProject.name = "Bob1-mobile"

include(":app")
include(":room")      // external module: Room database (entities, DAOs)
include(":capacity")  // external module: native capabilities (session, push, network, haptics...) on top of Kindling :android
