plugins {
    id("challenge.android.library")
}

android {
    namespace = "app.narges.documentor.benchmark"
}

dependencies {
    implementation(project(":app"))
}
