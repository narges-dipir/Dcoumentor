plugins {
    id("challenge.android.application")
    id("challenge.android.compose")
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "app.narges.documentor"

    defaultConfig {
        applicationId = "app.narges.documentor"
    }
}

dependencies {
    implementation(project(":core:navigation"))
    implementation(project(":core:dispatcher"))
    implementation(project(":core:worker"))
    implementation(project(":core:ui"))
    implementation(project(":data:articles"))
    implementation(project(":domain:articles"))
    implementation(project(":feature:articlelist"))
    implementation(project(":feature:articlecreate"))
    implementation(project(":feature:articledetails"))

    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.coil.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
