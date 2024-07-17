plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.jetbrainsKotlinSerialization)
    alias(libs.plugins.firebase)
    alias(libs.plugins.firebaseCrashlytics)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.unicorns.invisible.caravan"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.unicorns.invisible.caravan"
        minSdk = 23
        targetSdk = 34
        versionCode = 47
        versionName = "1.6.C"

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

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.work)
    implementation(libs.androidx.futures)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    val composeBom = platform("androidx.compose:compose-bom:2024.05.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.activity)
    implementation(libs.compose.foundation)

    implementation(libs.coil)
    implementation(libs.coil.svg)

    implementation(libs.serialization)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crahslytics)
    implementation(libs.firebase.analytics)

    implementation(libs.cronet)
    implementation(kotlin("script-runtime"))

    implementation(libs.gp)
    implementation(libs.ac2)
    implementation(libs.ac1)
    implementation(libs.ac3)

    implementation(libs.compose.runtime)
    implementation(libs.resaca)
}
