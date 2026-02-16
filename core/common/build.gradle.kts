plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines.core)
            implementation(libs.datetime)
            implementation(libs.koin.core)
        }
        androidMain.dependencies {
            implementation(libs.coroutines.android)
            implementation(libs.koin.android)
            implementation(libs.androidx.biometric)
            implementation(libs.androidx.appcompat)
        }
    }
}

android {
    namespace = "com.merchpulse.core.common"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
