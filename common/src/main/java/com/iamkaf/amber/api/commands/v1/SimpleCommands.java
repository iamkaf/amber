package com.iamkaf.amber.api.commands.v1;

import com.iamkaf.amber.api.platform.v1.ModInfo;
import com.iamkaf.amber.api.platform.v1.Platform;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
//? if <=1.18.2 {
import net.minecraft.network.chat.TextComponent;
//?}

public class SimpleCommands {
    public static LiteralArgumentBuilder<CommandSourceStack> createBaseCommand(String modId) {
        return Commands.literal(modId).executes(commandContext -> {
            ModInfo modInfo = Platform.getModInfo(modId);

            // wat??
            if (modInfo == null) {
                //? if <=1.18.2 {
                commandContext.getSource().sendFailure(new TextComponent("Mod info not found!"));
                //?} else {
                commandContext.getSource().sendFailure(Component.literal("Mod info not found!"));
                //?}
                return Command.SINGLE_SUCCESS;
            }

            //? if <=1.18.2 {
            commandContext.getSource()
                    .sendSuccess(
                            new TextComponent(modInfo.name()).append(" - Version: " + modInfo.version()),
                            false
                    );
            //?} else if <=1.19.4 {
            commandContext.getSource()
                    .sendSuccess(
                            Component.literal(modInfo.name()).append(" - Version: " + modInfo.version()),
                            false
                    );
            //?} else {
            commandContext.getSource()
                    .sendSuccess(
                            () -> Component.literal(modInfo.name()).append(" - Version: " + modInfo.version()),
                            false
                    );
            //?}
            return Command.SINGLE_SUCCESS;
        });
    }
}
