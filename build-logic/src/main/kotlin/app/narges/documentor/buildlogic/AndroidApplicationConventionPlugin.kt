package app.narges.documentor.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val libs = libsCatalog()
        pluginManager.apply("com.android.application")

        extensions.configure<ApplicationExtension> {
            compileSdk = 36

            defaultConfig {
                minSdk = 24
                targetSdk = 36
                versionCode = 1
                versionName = "1.0"
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }

            buildTypes {
                release {
                    isMinifyEnabled = false
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro",
                    )
                }
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }

            packaging {
                resources {
                    excludes += "/META-INF/{AL2.0,LGPL2.1}"
                }
            }
        }

        dependencies.add("implementation", libs.findLibrary("androidx-core-ktx").get())
        dependencies.add("implementation", libs.findLibrary("androidx-lifecycle-runtime-ktx").get())
        dependencies.add("implementation", libs.findLibrary("androidx-activity-compose").get())
        dependencies.add("testImplementation", libs.findLibrary("junit").get())
        dependencies.add("androidTestImplementation", libs.findLibrary("androidx-junit").get())
        dependencies.add("androidTestImplementation", libs.findLibrary("androidx-espresso-core").get())

        Unit
    }
}
