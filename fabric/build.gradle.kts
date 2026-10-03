import org.gradle.language.jvm.tasks.ProcessResources

plugins {
    id("com.iamkaf.multiloader.fabric")
}

val minecraftVersion = project.name
val isModernLine = !minecraftVersion.startsWith("1.")

fun minecraftVersionAtLeast(targetVersion: String): Boolean {
    if (isModernLine) {
        return true
    }
    val currentParts = minecraftVersion.split(".").map { it.toInt() }
    val targetParts = targetVersion.split(".").map { it.toInt() }
    for (i in 0 until maxOf(currentParts.size, targetParts.size)) {
        val currentPart = currentParts.getOrElse(i) { 0 }
        val targetPart = targetParts.getOrElse(i) { 0 }
        if (currentPart != targetPart) {
            return currentPart > targetPart
        }
    }
    return true
}

configurations.configureEach {
    withDependencies {
        filter { dependency ->
            dependency.group == "com.terraformersmc" && dependency.name == "modmenu"
        }.toList().forEach { dependency ->
            remove(dependency)
            add(project.dependencies.create("maven.modrinth:modmenu:${dependency.version}"))
        }
    }
}

dependencies {
    compileOnly("org.slf4j:slf4j-api:1.7.36")
}

tasks.withType<ProcessResources>().configureEach {
    inputs.property("amberFabricMixinFilter", minecraftVersion)
    filesMatching("amber.fabric.mixins.json") {
        if (!minecraftVersionAtLeast("1.20.5")) {
            filter { line: String -> if (line.contains("\"BuiltInRegistriesMixin\",")) "" else line }
            filter { line: String -> if (line.contains("\"ItemAccessor\",")) "" else line }
        }
    }

    if (!minecraftVersionAtLeast("1.20")) {
        inputs.property("amberAccessWidener", "none")
        filesMatching("fabric.mod.json") {
            filter { line: String -> if (line.contains("\"accessWidener\": \"amber.accesswidener\",")) "" else line }
        }
    }
    if (minecraftVersion == "1.17" || minecraftVersion == "1.17.1" || minecraftVersion == "1.18" || minecraftVersion == "1.18.1") {
        inputs.property("amberFabricApiModuleDepends", "fabric")
        filesMatching("fabric.mod.json") {
            filter { line: String -> line.replace("\"fabric-api\": \"*\",", "\"fabric\": \"*\",") }
        }
    }
    if (minecraftVersion == "1.19" || minecraftVersion == "1.19.1") {
        val fabricApiModuleDepends = """"fabric-api-base": "*",
        "fabric-command-api-v2": "*",
        "fabric-entity-events-v1": "*",
        "fabric-events-interaction-v0": "*",
        "fabric-item-groups-v0": "*",
        "fabric-lifecycle-events-v1": "*",
        "fabric-loot-api-v2": "*",
        "fabric-networking-api-v1": "*",
        "fabric-rendering-v1": "*","""

        inputs.property("amberFabricApiModuleDepends", fabricApiModuleDepends)
        filesMatching("fabric.mod.json") {
            filter { line: String -> line.replace("\"fabric-api\": \"*\",", fabricApiModuleDepends) }
        }
    }
}
