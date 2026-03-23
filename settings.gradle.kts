pluginManagement {
    includeBuild("build-logic")
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
include(":core:worker")
include(":data:articles")
include(":domain:articles")
include(":feature:articlelist")
include(":feature:articlecreate")
include(":feature:articledetails")
