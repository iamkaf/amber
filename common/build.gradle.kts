import org.gradle.api.tasks.Sync
import net.neoforged.moddevgradle.dsl.NeoForgeExtension

plugins {
    id("com.iamkaf.multiloader.common")
}

val minecraftVersion = project.name

if (minecraftVersion == "26.3") {
    extensions.configure<NeoForgeExtension> {
        accessTransformers.from(rootProject.file("versions/$minecraftVersion/common/src/main/resources/META-INF/accesstransformer.cfg"))
    }
}

tasks.named<Sync>("stageMergedJavaSources").configure {
    if (minecraftVersion == "1.14.4" || minecraftVersion == "1.15" || minecraftVersion.startsWith("1.15.")) {
        from(rootProject.file("common/src/legacy-pre116/java"))
    }
}

dependencies {
    compileOnly("org.apache.logging.log4j:log4j-api:2.17.1")
    compileOnly("org.slf4j:slf4j-api:1.7.36")
}
