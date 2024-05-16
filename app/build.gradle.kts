plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.jetbrainsKotlinSerialization)
    alias(libs.plugins.firebase)
    alias(libs.plugins.firebaseCrashlytics)
}

android {
    namespace = "com.unicorns.invisible.caravan"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.unicorns.invisible.caravan"
        minSdk = 23
        targetSdk = 34
        versionCode = 9
        versionName = "0.99.9"

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
        kotlinCompilerExtensionVersion = "1.5.10"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    val composeBom = platform("androidx.compose:compose-bom:2024.02.02")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.activity)

    implementation(libs.coil)

    implementation(libs.serialization)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crahslytics)
    implementation(libs.firebase.analytics)

    implementation(libs.cronet)

}
