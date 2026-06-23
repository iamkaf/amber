pluginManagement {
    repositories {
        mavenLocal()
        maven("https://maven.kaf.sh") { name = "Kaf Maven" }
        maven("https://maven.kikugie.dev/snapshots") {
            name = "KikuGie Snapshots"
            content {
                includeGroupByRegex("dev\\.kikugie(\\..*)?")
            }
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("com.iamkaf.multiloader.settings") version providers.gradleProperty("project.plugins").get()
}
