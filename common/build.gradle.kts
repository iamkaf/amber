import net.neoforged.moddevgradle.dsl.NeoForgeExtension

plugins {
    id("com.iamkaf.multiloader.common")
}

val minecraftVersion = project.name

// Graph-only builds skip ModDevGradle, so the extension exists only when the plugin is applied.
if (minecraftVersion == "26.3") {
    pluginManager.withPlugin("net.neoforged.moddev") {
        extensions.configure<NeoForgeExtension> {
            accessTransformers.from(rootProject.file("versions/$minecraftVersion/common/src/main/resources/META-INF/accesstransformer.cfg"))
        }
    }
}

dependencies {
    compileOnly("org.slf4j:slf4j-api:1.7.36")
}
