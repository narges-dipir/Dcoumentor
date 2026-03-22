plugins {
    id("challenge.android.library")
    id("challenge.android.compose")
}

android {
    namespace = "app.narges.documentor.core.navigation"
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.androidx.navigation3.runtime)
}
