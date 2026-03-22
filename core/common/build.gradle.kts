plugins {
    id("challenge.android.library")
}

android {
    namespace = "app.narges.documentor.core.common"
}

dependencies {
    implementation(project(":core:model"))
    implementation(libs.retrofit.core)
    implementation(libs.kotlinx.coroutines.core)
}
