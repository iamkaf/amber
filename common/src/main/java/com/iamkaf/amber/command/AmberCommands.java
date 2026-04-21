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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
//? if >1.15.2 {
import net.minecraft.network.chat.MutableComponent;
//?}
//? if <=1.18.2 {
import net.minecraft.network.chat.TextComponent;
//?}

public class AmberCommands {

    private static final LiteralArgumentBuilder<CommandSourceStack> DOCTOR_COMMAND =
            Commands.literal("doctor").executes(commandContext -> {
                ModInfo modInfo = Platform.getModInfo(Constants.MOD_ID);

                // wat??
                if (modInfo == null) {
                    //? if <=1.18.2 {
                    commandContext.getSource().sendFailure(new TextComponent("Mod info not found!"));
                    //?} else {
                    commandContext.getSource().sendFailure(Component.literal("Mod info not found!"));
                    //?}
                    return Command.SINGLE_SUCCESS;
                }

                //? if <=1.15.2 {
                Component message = new TextComponent(modInfo.name() + " Doctor\n")
                //?} else if <=1.18.2 {
                MutableComponent message = new TextComponent(modInfo.name() + " Doctor\n")
                //?} else {
                MutableComponent message = Component.literal(modInfo.name() + " Doctor\n")
                //?}
                        .append(" - Version: " + modInfo.version() + "\n")
                        .append(" - Platform: " + Platform.getPlatformName() + "\n")
                        // FIXME: Kaf PLEASE find a way to get this number automatically omg PLEASE
                        .append(" - Minecraft: 26.1.2" + "\n")
                        .append(" - Networking: " + (AmberNetworking.isInitialized() ? "Initialized" :
                                "Not " + "Initialized") + "\n")
                        .append("Mixins: \n");
                for (String mixin : AmberMod.AMBER_MIXINS) {
                    //? if <=1.18.2 {
                    message.append(new TextComponent(mixin + "\n")
                    //?} else {
                    message.append(Component.literal(mixin + "\n")
                    //?}
                            .withStyle(ChatFormatting.GOLD));
                }
                message.append("\n").append("Amber Mods: \n");
                for (AmberModInfo amberMod : AmberMod.AMBER_MODS) {
                    //? if <=1.18.2 {
                    message.append(new TextComponent(String.format(
                    //?} else {
                    message.append(Component.literal(String.format(
                    //?}
                                    "%s - %s\n",
                                    amberMod.name(),
                                    amberMod.version()
                            ))
                            .withStyle(ChatFormatting.AQUA));
                }
                //? if <=1.19.4 {
                commandContext.getSource().sendSuccess(message, true);
                //?} else {
                commandContext.getSource().sendSuccess(() -> message, true);
                //?}
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
