pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Documentor"
include(":app")
include(":benchmark")
include(":core:common")
include(":core:dispatcher")
include(":core:model")
include(":core:navigation")
include(":core:network")
include(":core:ui")
include(":data:countries")
include(":domain:countries")
include(":feature:countries")
include(":feature:countrydetails")
