plugins {
    id("challenge.android.library")
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "app.narges.documentor.core.dispatcher"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.hilt.android)
    implementation(libs.javax.inject)
    ksp(libs.hilt.compiler)
    compileOnly(libs.hilt.android.testing)

    testImplementation(libs.hilt.android.testing)
}
