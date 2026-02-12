pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MerchPulse"

include(":app-android")
include(":shared")
include(":core:common")
include(":core:designsystem")
include(":core:database-android")
include(":feature:auth")
include(":feature:home")
include(":feature:products")
include(":feature:stock")
include(":feature:employees")
include(":feature:punching")