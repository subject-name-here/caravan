plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.jetbrainsKotlinSerialization) apply false
    alias(libs.plugins.firebase) apply false
    alias(libs.plugins.firebaseCrashlytics) apply false
}

tasks.register("prepareKotlinIdeaImport")