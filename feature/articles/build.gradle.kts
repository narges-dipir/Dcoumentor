plugins {
    id("challenge.android.library")
    id("challenge.android.compose")
}

android {
    namespace = "app.narges.documentor.feature.articles"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:navigation"))
    implementation(project(":core:ui"))
    implementation(project(":domain:articles"))
}
