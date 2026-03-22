plugins {
    id("challenge.android.library")
    alias(libs.plugins.ksp)
}

android {
    namespace = "app.narges.documentor.data.articles"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:dispatcher"))
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":domain:articles"))
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.kotlinx.coroutines.core)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.room.testing)
}
