pluginManagement {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven("https://jitpack.io")
        gradlePluginPortal()

    }
    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories {
            google()
            jcenter()
            mavenCentral()
            maven("https://jitpack.io")
        }
    }

    rootProject.name = "Grocessary Managment App"
    include(":app")
}
 