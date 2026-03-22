plugins {
    id("challenge.android.library")
    id("challenge.android.compose")
}

android {
    namespace = "app.narges.documentor.core.ui"
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.coil.compose)
}
