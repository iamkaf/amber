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
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
//? if <1.19
/*import net.minecraft.network.chat.TextComponent;*/

public class AmberCommands {

    private static final LiteralArgumentBuilder<CommandSourceStack> DOCTOR_COMMAND =
            Commands.literal("doctor").executes(commandContext -> {
                ModInfo modInfo = Platform.getModInfo(Constants.MOD_ID);

                // wat??
                if (modInfo == null) {
                    commandContext.getSource().sendFailure(literal("Mod info not found!"));
                    return Command.SINGLE_SUCCESS;
                }

                //? if >=1.20
                commandContext.getSource().sendSuccess(() -> doctorMessage(modInfo), true);
                //? if <1.20
                /*commandContext.getSource().sendSuccess(doctorMessage(modInfo), true);*/
                return Command.SINGLE_SUCCESS;
            });

    private static MutableComponent doctorMessage(ModInfo modInfo) {
        MutableComponent message = literal(modInfo.name() + " Doctor\n")
                .append(" - Version: " + modInfo.version() + "\n")
                .append(" - Platform: " + Platform.getPlatformName() + "\n")
                //? if >=1.21.6
                .append(" - Minecraft: " + SharedConstants.getCurrentVersion().name() + "\n")
                //? if <1.21.6
                /*.append(" - Minecraft: " + SharedConstants.getCurrentVersion().getName() + "\n")*/
                .append(" - Networking: " + (AmberNetworking.isInitialized() ? "Initialized" :
                        "Not " + "Initialized") + "\n")
                .append("Mixins: \n");
        for (String mixin : AmberMod.AMBER_MIXINS) {
            message.append(literal(mixin + "\n")
                    .withStyle(style -> style.withColor(ChatFormatting.GOLD)));
        }
        message.append("\n").append("Amber Mods: \n");
        for (AmberModInfo amberMod : AmberMod.AMBER_MODS) {
            message.append(literal(String.format(
                            "%s - %s\n",
                            amberMod.name(),
                            amberMod.version()
                    ))
                    .withStyle(style -> style.withColor(ChatFormatting.AQUA)));
        }
        return message;
    }

    private static MutableComponent literal(String value) {
        //? if >=1.19
        return Component.literal(value);
        //? if <1.19
        /*return new TextComponent(value);*/
    }

    public static void initialize() {
        Constants.LOG.info("Registering Amber commands for {}", Services.PLATFORM.getPlatformName());
        CommandEvents.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(SimpleCommands.createBaseCommand(Constants.MOD_ID)
                    .then(DOCTOR_COMMAND));
        });
    }
}
