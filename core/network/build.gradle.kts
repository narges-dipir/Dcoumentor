plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "app.narges.documentor.core.network"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)
}
