plugins {
    id("challenge.android.library")
}

android {
    namespace = "app.narges.documentor.core.network"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
}
