import org.gradle.api.tasks.Sync
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
            add(project.dependencies.create("maven.modrinth:mOgUt4GM:${dependency.version}"))
        }
    }
}

tasks.named<Sync>("stageMergedJavaSources").configure {
    if (minecraftVersion == "1.14.4" || minecraftVersion == "1.15" || minecraftVersion.startsWith("1.15.")) {
        from(rootProject.file("common/src/legacy-pre116/java"))
    }
}

if (minecraftVersion == "1.16" || minecraftVersion == "1.16.1") {
    configurations.configureEach {
        exclude(group = "maven.modrinth", module = "mOgUt4GM")
        resolutionStrategy.force("net.fabricmc.fabric-api:fabric-resource-loader-v0:0.2.5+059ea8667c")
    }
}

dependencies {
    compileOnly("org.slf4j:slf4j-api:1.7.36")
    if (minecraftVersion == "1.16" || minecraftVersion == "1.16.1") {
        "modImplementation"("net.fabricmc.fabric-api:fabric-api-base:0.2.0+ab87788d3a")
        "modImplementation"("net.fabricmc.fabric-api:fabric-command-api-v1:1.0.9+6a2618f53a")
        "modImplementation"("net.fabricmc.fabric-api:fabric-events-interaction-v0:0.4.1+6a2618f53a")
        "modImplementation"("net.fabricmc.fabric-api:fabric-key-binding-api-v1:1.0.1+730711c63a")
        "modImplementation"("net.fabricmc.fabric-api:fabric-lifecycle-events-v1:1.2.0+ffb68a873a")
        "modImplementation"("net.fabricmc.fabric-api:fabric-loot-tables-v1:1.0.1+6a2618f53a")
        "modImplementation"("net.fabricmc.fabric-api:fabric-networking-api-v1:1.0.0+4358fbc63a")
        "modImplementation"("net.fabricmc.fabric-api:fabric-rendering-v1:1.5.0+c26373133a")
        "modImplementation"("net.fabricmc.fabric-api:fabric-item-groups-v0:0.2.0+438f963602")
        "modImplementation"("net.fabricmc.fabric-api:fabric-resource-loader-v0:0.2.5+059ea8667c")
    }
    if (minecraftVersion == "1.14.4" || minecraftVersion == "1.15" || minecraftVersion.startsWith("1.15.") || minecraftVersion.startsWith("1.16")) {
        "modLocalRuntime"("org.slf4j:slf4j-api:1.7.36")
        "modLocalRuntime"("org.slf4j:slf4j-simple:1.7.36")
    }
}

tasks.withType<ProcessResources>().configureEach {
    inputs.property("amberLegacyFabricMixinFilter", minecraftVersion)
    inputs.property("amberLegacyFabricMixinFilterRules", "respawn-pre116-v1")
    filesMatching("amber.fabric.mixins.json") {
        if (minecraftVersion == "1.14.4" || minecraftVersion == "1.15" || minecraftVersion == "1.15.1" || minecraftVersion == "1.15.2") {
            filter { line: String -> if (line.contains("\"AnimalMixin\",")) "" else line }
        } else {
            filter { line: String -> if (line.contains("\"BreedGoalMixin\",")) "" else line }
        }
        if (minecraftVersionAtLeast("1.16.2")) {
            filter { line: String -> if (line.contains("\"PlayerListRespawnMixin\",")) "" else line }
        }
        if (!minecraftVersionAtLeast("1.20.5")) {
            filter { line: String -> if (line.contains("\"BuiltInRegistriesMixin\",")) "" else line }
            filter { line: String -> if (line.contains("\"ItemAccessor\",")) "" else line }
        }
    }

    if (minecraftVersion == "1.16" || minecraftVersion == "1.16.1") {
        val fabricApiModuleDepends = """"fabric-api-base": "*",
        "fabric-command-api-v1": "*",
        "fabric-events-interaction-v0": "*",
        "fabric-key-binding-api-v1": "*",
        "fabric-item-groups-v0": "*",
        "fabric-lifecycle-events-v1": "*",
        "fabric-loot-tables-v1": "*",
        "fabric-networking-api-v1": "*",
        "fabric-resource-loader-v0": "*",
        "fabric-rendering-v1": "*","""

        inputs.property("amberFabricApiModuleDepends", fabricApiModuleDepends)
        filesMatching("fabric.mod.json") {
            filter { line: String -> line.replace("\"fabric-api\": \"*\",", fabricApiModuleDepends) }
        }
    }
    if (!minecraftVersionAtLeast("1.20")) {
        inputs.property("amberAccessWidener", "none")
        filesMatching("fabric.mod.json") {
            filter { line: String -> if (line.contains("\"accessWidener\": \"amber.accesswidener\",")) "" else line }
        }
    }
    if (minecraftVersion == "1.14.4" || minecraftVersion == "1.15" || minecraftVersion == "1.15.1" || minecraftVersion == "1.15.2" || minecraftVersion == "1.16.2" || minecraftVersion == "1.16.3" || minecraftVersion == "1.16.4" || minecraftVersion == "1.16.5" || minecraftVersion == "1.17" || minecraftVersion == "1.17.1" || minecraftVersion == "1.18" || minecraftVersion == "1.18.1") {
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
