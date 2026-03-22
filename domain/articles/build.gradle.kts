plugins {
    id("challenge.android.library")
}

android {
    namespace = "app.narges.documentor.domain.articles"
}

dependencies {
    api(project(":core:model"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
    testImplementation(libs.kotlinx.coroutines.test)
}
