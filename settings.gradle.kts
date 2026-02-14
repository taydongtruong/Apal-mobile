pluginManagement {
    repositories {
        google() // Đơn giản hóa để Gradle tự tìm mọi thứ từ Google
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

rootProject.name = "Apal"
include(":app")