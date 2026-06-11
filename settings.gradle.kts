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
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.kikugie.stonecutter") version "0.9.2"
    id("com.iamkaf.multiloader.settings") version providers.gradleProperty("project.plugins").get()
}
