package com.iamkaf.amber.api.commands.v1;

import com.iamkaf.amber.api.platform.v1.ModInfo;
import com.iamkaf.amber.api.platform.v1.Platform;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
//? if >=1.19
import net.minecraft.network.chat.MutableComponent;
//? if <1.19
/*import net.minecraft.network.chat.TextComponent;*/

public class SimpleCommands {
    public static LiteralArgumentBuilder<CommandSourceStack> createBaseCommand(String modId) {
        return LiteralArgumentBuilder.<CommandSourceStack>literal(modId).executes(commandContext -> {
            ModInfo modInfo = Platform.getModInfo(modId);

            // wat??
            if (modInfo == null) {
                commandContext.getSource().sendFailure(literal("Mod info not found!"));
                return Command.SINGLE_SUCCESS;
            }

            var message = literal(modInfo.name());
            message.append(" - Version: " + modInfo.version());
            //? if >=1.20
            commandContext.getSource().sendSuccess(() -> message, false);
            //? if <1.20
            /*commandContext.getSource().sendSuccess(message, false);*/
            return Command.SINGLE_SUCCESS;
        });
    }

    //? if >=1.19
    private static MutableComponent literal(String value) {
    //? if <1.19
    /*private static TextComponent literal(String value) {*/
        //? if >=1.19
        return Component.literal(value);
        //? if <1.19
        /*return new TextComponent(value);*/
    }
}
