package app.narges.documentor.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val libs = libsCatalog()
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

        pluginManager.withPlugin("com.android.application") {
            extensions.configure<ApplicationExtension> {
                buildFeatures {
                    compose = true
                }
            }
        }

        pluginManager.withPlugin("com.android.library") {
            extensions.configure<LibraryExtension> {
                buildFeatures {
                    compose = true
                }
            }
        }

        dependencies.add("implementation", dependencies.platform(libs.findLibrary("androidx-compose-bom").get()))
        dependencies.add("implementation", libs.findLibrary("androidx-compose-ui").get())
        dependencies.add("implementation", libs.findLibrary("androidx-compose-ui-tooling-preview").get())
        dependencies.add("implementation", libs.findLibrary("androidx-compose-material3").get())
        dependencies.add("debugImplementation", libs.findLibrary("androidx-compose-ui-tooling").get())

        Unit
    }
}
