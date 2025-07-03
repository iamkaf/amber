package com.iamkaf.amber;

import com.iamkaf.amber.api.commands.v1.SimpleCommands;
import com.iamkaf.amber.api.common.client.CommonClientUtils;
import com.iamkaf.amber.api.core.v2.AmberModInfo;
import com.iamkaf.amber.api.event.v1.events.common.CommandEvents;
import com.iamkaf.amber.api.event.v1.events.common.client.HudEvents;
import com.iamkaf.amber.api.platform.v1.ModInfo;
import com.iamkaf.amber.api.platform.v1.Platform;
import com.iamkaf.amber.api.registry.v1.DeferredRegister;
import com.iamkaf.amber.api.registry.v1.RegistrySupplier;
import com.iamkaf.amber.platform.Services;
import com.iamkaf.amber.util.Env;
import com.iamkaf.amber.util.EnvExecutor;
import com.mojang.brigadier.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Common entry point for the Amber mod.
 * Replace the contents with your own implementation.
 */
public class AmberMod {
    public static final ArrayList<AmberModInfo> AMBER_MODS = new ArrayList<>();
    public static final ArrayList<String> AMBER_MIXINS = new ArrayList<>();


    public static DeferredRegister<Item> ITEMS = DeferredRegister.create(Constants.MOD_ID, Registries.ITEM);
    public static RegistrySupplier<Item> TEST_ITEM =
            ITEMS.register("test_item", (key) -> new Item(new Item.Properties().setId(key)));

    /**
     * Called during mod initialization for all loaders.
     */
    public static void init() {
        Constants.LOG.info("Initializing Everlasting Amber Dreams on {}...", Services.PLATFORM.getPlatformName());

        Services.AMBER_EVENT_SETUP.registerCommon();
        EnvExecutor.runInEnv(Env.CLIENT, () -> Services.AMBER_EVENT_SETUP::registerClient);
        EnvExecutor.runInEnv(Env.SERVER, () -> Services.AMBER_EVENT_SETUP::registerServer);

        ITEMS.register();

        // TODO: Move this somewhere neat
        CommandEvents.EVENT.register((dispatcher, registryAccess, environment) -> {
            Constants.LOG.info("Registering Amber commands for {}", Services.PLATFORM.getPlatformName());
            dispatcher.register(SimpleCommands.createBaseCommand(Constants.MOD_ID)
                    .then(Commands.literal("doctor").executes(commandContext -> {
                        ModInfo modInfo = Platform.getModInfo(Constants.MOD_ID);

                        // wat??
                        if (modInfo == null) {
                            commandContext.getSource().sendFailure(Component.literal("Mod info not found!"));
                            return Command.SINGLE_SUCCESS;
                        }

                        commandContext.getSource().sendSuccess(
                                () -> {
                                    MutableComponent message = Component.literal(modInfo.name() + " Doctor\n")
                                            .append(" - Version: " + modInfo.version() + "\n")
                                            .append(" - Platform: " + Platform.getPlatformName() + "\n")
                                            .append(" - Minecraft: 1.21.6" + "\n\n")
                                            .append("Mixins: \n");
                                    for (String mixin : AMBER_MIXINS) {
                                        message.append(Component.literal(mixin + "\n")
                                                .withStyle(style -> style.withColor(0xFFAA00)));
                                    }
                                    message.append("\n").append("Amber Mods: \n");
                                    for (AmberModInfo amberMod : AMBER_MODS) {
                                        message.append(Component.literal(String.format(
                                                        "%s - %s\n",
                                                        amberMod.name(),
                                                        amberMod.version()
                                                ))
                                                .withStyle(style -> style.withColor(0x00AAFF)));
                                    }
                                    return message;
                                }, true
                        );
                        return Command.SINGLE_SUCCESS;
                    })));
        });

        HudEvents.RENDER_HUD.register((guiGraphics, tickCounter) -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.options.hideGui) {
                return;
            }

            var writer = new CommonClientUtils.TextWriter(guiGraphics, mc.font);
            writer.writeLine(Component.literal(String.format(
                    "Amber v%s",
                    Objects.requireNonNull(Platform.getModInfo(Constants.MOD_ID)).version()
            )));
            writer.writeLine(Component.literal("Everlasting Amber Dreams"));
        });
    }

    /**
     * Gets the event bus for a specific Amber mod on Forge and NeoForge.
     *
     * @param modId the ID of the mod to get the event bus for
     * @return the event bus for the mod, or null if not found or on Fabric.
     */
    public static @Nullable Object getEventBus(String modId) {
        if (Platform.getPlatformName().equals("Fabric")) {
            return null;
        }

        return AMBER_MODS.stream()
                .filter(mod -> mod.id().equals(modId))
                .findFirst()
                .map(AmberModInfo::eventBus)
                .orElse(null);
    }
}
