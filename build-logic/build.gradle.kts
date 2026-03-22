plugins {
    `kotlin-dsl`
}

group = "app.narges.documentor.buildlogic"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

gradlePlugin {
    plugins {
        register("androidApplicationConvention") {
            id = "challenge.android.application"
            implementationClass = "app.narges.documentor.buildlogic.AndroidApplicationConventionPlugin"
        }
        register("androidLibraryConvention") {
            id = "challenge.android.library"
            implementationClass = "app.narges.documentor.buildlogic.AndroidLibraryConventionPlugin"
        }
        register("androidComposeConvention") {
            id = "challenge.android.compose"
            implementationClass = "app.narges.documentor.buildlogic.AndroidComposeConventionPlugin"
        }
        register("jvmLibraryConvention") {
            id = "challenge.jvm.library"
            implementationClass = "app.narges.documentor.buildlogic.JvmLibraryConventionPlugin"
        }
    }
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
}
