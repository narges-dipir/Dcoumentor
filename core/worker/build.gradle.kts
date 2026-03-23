plugins {
    id("challenge.android.library")
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "app.narges.documentor.core.worker"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:dispatcher"))
    implementation(project(":core:model"))
    implementation(project(":data:articles"))
    implementation(project(":domain:articles"))

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.startup.runtime)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    testImplementation(libs.junit)
}
