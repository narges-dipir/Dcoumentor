plugins {
    id("challenge.android.application")
    id("challenge.android.compose")
}

android {
    namespace = "app.narges.documentor"

    defaultConfig {
        applicationId = "app.narges.documentor"
    }
}

dependencies {
    implementation(project(":feature:articles"))
    implementation(project(":feature:articledetails"))

    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.coil.compose)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
