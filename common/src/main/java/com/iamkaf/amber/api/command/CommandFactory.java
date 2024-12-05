package com.iamkaf.amber.api.command;

import com.iamkaf.amber.api.util.LiteralSetHolder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.platform.Platform;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public abstract class CommandFactory {
    public static CommandParent create(String modId, String modName, String commandName) {
        return new CommandParent(modId, modName, commandName, new LiteralSetHolder<>());
    }

    public static class CommandParent {
        private final String modId;
        private final String modName;
        private final String mainCommand;
        private final LiteralSetHolder<SubCommand> subCommands;

        public CommandParent(String modId, String modName, String commandName,
                LiteralSetHolder<SubCommand> subCommandHolder) {
            this.modId = modId;
            this.modName = modName;
            mainCommand = commandName;
            subCommands = subCommandHolder;
        }

        public CommandParent addSubcommand(SubCommand command) {
            subCommands.add(command);
            return this;
        }

        public CommandParent register() {
            CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) -> {
                LiteralArgumentBuilder<CommandSourceStack> main = Commands.literal(mainCommand);
                // show commands list
                dispatcher.register(main.executes(this::displayInfo));
                // register subcommands
                subCommands.get().forEach(cmd -> dispatcher.register(cmd.register(main)));
            });
            return this;
        }

        private int displayInfo(CommandContext<CommandSourceStack> context) {
            var version = Platform.getMod(modId).getVersion();
            var amberVersion = Platform.getMod("amber").getVersion();
            context.getSource()
                    .sendSuccess(() -> Component.literal(String.format("--- %s v%s ---", modName, version)),
                            false
                    );

            subCommands.getSet()
                    .forEach(cmd -> context.getSource()
                            .sendSuccess(() -> Component.literal(String.format("/%s %s - %s",
                                    mainCommand,
                                    cmd.getName(),
                                    cmd.getDescription()
                            )), false));
            context.getSource()
                    .sendSuccess(() -> Component.literal(String.format("- Amber v%s", amberVersion))
                            .withStyle(ChatFormatting.GRAY), false);
            return 1;
        }
    }
}