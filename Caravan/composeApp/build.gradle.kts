import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.jetbrainsKotlinSerialization)
    alias(libs.plugins.firebase)
    alias(libs.plugins.firebaseCrashlytics)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    jvm("desktop")
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "composeApp"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }
    
    sourceSets {
        val desktopMain by getting
        
        androidMain.dependencies {
            implementation(libs.androidx.appcompat)

            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.material)
            implementation(libs.compose.material3)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            val composeBom = project.dependencies.platform("androidx.compose:compose-bom:2025.03.00")
            implementation(composeBom)

            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.crahslytics)
            implementation(libs.firebase.analytics)

            implementation(libs.gp)
            implementation(libs.ac2)
            implementation(libs.ac1)
            implementation(libs.ac3)

            implementation(libs.coil)
            implementation(libs.coil.svg)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)

            implementation(libs.serialization)
            implementation(libs.datetime)

            implementation(kotlin("script-runtime"))

            implementation(libs.resaca)
            implementation(libs.ktor1)
            implementation(libs.ktor2)

            implementation(libs.fastscroller.core)
            // Optional: Provides scroll bar Material Design 2 theme by defaultMaterialScrollbarStyle
            implementation(libs.fastscroller.material)
            // Optional: Provides scroll bar Material Design 3 theme by defaultMaterialScrollbarStyle
            implementation(libs.fastscroller.material3)
            // Optional: Provides scroll bar indicator shape
            implementation(libs.fastscroller.indicator)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "caravan.composeapp.generated.resources"
    generateResClass = auto
}

android {
    namespace = "com.unicorns.invisible.caravan"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.unicorns.invisible.caravan"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 112
        versionName = "3.0.A"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.ui.graphics.android)
    implementation(libs.androidx.ui.geometry.android)
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "com.unicorns.invisible.caravan.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.unicorns.invisible.caravan"
            packageVersion = "1.0.0"
        }
    }
}
