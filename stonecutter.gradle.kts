plugins {
    id("dev.kikugie.stonecutter")
    id("com.iamkaf.multiloader.root")
    id("fabric-loom") version "1.15.5" apply false
    id("net.neoforged.moddev") version "2.0.141" apply false
}

stonecutter active "26.1.2"

stonecutter parameters {
    replacements.string(eval(current.version, ">=1.21.11")) {
        replace("ResourceLocation", "Identifier")
    }
}
