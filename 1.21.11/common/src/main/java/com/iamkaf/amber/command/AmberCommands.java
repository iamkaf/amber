package com.iamkaf.amber.command;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.commands.v1.SimpleCommands;
import com.iamkaf.amber.api.core.v2.AmberModInfo;
import com.iamkaf.amber.api.event.v1.events.common.CommandEvents;
import com.iamkaf.amber.api.platform.v1.ModInfo;
import com.iamkaf.amber.api.platform.v1.Platform;
import com.iamkaf.amber.networking.v1.AmberNetworking;
import com.iamkaf.amber.platform.Services;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class AmberCommands {

    private static final LiteralArgumentBuilder<CommandSourceStack> DOCTOR_COMMAND =
            Commands.literal("doctor").executes(commandContext -> {
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
                                    // FIXME: Kaf PLEASE find a way to get this number automatically omg PLEASE
                                    .append(" - Minecraft: 1.21.11" + "\n")
                                    .append(" - Networking: " + (AmberNetworking.isInitialized() ? "Initialized" :
                                            "Not " + "Initialized") + "\n")
                                    .append("Mixins: \n");
                            for (String mixin : AmberMod.AMBER_MIXINS) {
                                message.append(Component.literal(mixin + "\n")
                                        .withStyle(style -> style.withColor(0xFFAA00)));
                            }
                            message.append("\n").append("Amber Mods: \n");
                            for (AmberModInfo amberMod : AmberMod.AMBER_MODS) {
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
            });

    public static void initialize() {
        CommandEvents.EVENT.register((dispatcher, registryAccess, environment) -> {
            Constants.LOG.info("Registering Amber commands for {}", Services.PLATFORM.getPlatformName());
            dispatcher.register(SimpleCommands.createBaseCommand(Constants.MOD_ID)
                    .then(DOCTOR_COMMAND));
        });
    }
}
