pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.buildFileName = "root.gradle.kts"

include("main")
include("intellij-plugin")
include("swing-app")
