plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.services)
    id("kotlin-kapt")
}

android {
    namespace = "com.princelaghari.ailatestfinder"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.princelaghari.ailatestfinder"
        minSdk = 24
        targetSdk = 34
        versionCode = 3
        versionName = "1.0.2"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("premium") {
            // Safe automated keystore lookup.
            // On GitHub Actions we generate "temp-keystore.jks" in the root directory.
            val keystoreFile = rootProject.file("temp-keystore.jks")
            if (keystoreFile.exists()) {
                storeFile = keystoreFile
                storePassword = "password123"
                keyAlias = "premiumalias"
                keyPassword = "password123"
            }
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            // If the premium keystore is found, use it to sign debug builds to bypass Play Protect
            val keystoreFile = rootProject.file("temp-keystore.jks")
            if (keystoreFile.exists()) {
                signingConfig = signingConfigs.getByName("premium")
            }
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            val keystoreFile = rootProject.file("temp-keystore.jks")
            if (keystoreFile.exists()) {
                signingConfig = signingConfigs.getByName("premium")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Chrome Custom Tabs
    implementation(libs.androidx.browser)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Firebase (BOM allows omitting versions)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.common.ktx)

    // AdMob Ads
    implementation(libs.play.services.ads)

    // Image Loader
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
