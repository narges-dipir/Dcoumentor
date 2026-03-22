plugins {
    id("challenge.android.library")
}

android {
    namespace = "app.narges.documentor.core.dispatcher"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.hilt.android)
    implementation(libs.javax.inject)
    compileOnly(libs.hilt.android.testing)

    testImplementation(libs.hilt.android.testing)
}
