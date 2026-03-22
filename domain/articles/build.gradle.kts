plugins {
    id("challenge.android.library")
}

android {
    namespace = "app.narges.documentor.domain.articles"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(libs.kotlinx.coroutines.core)
}
