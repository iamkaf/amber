//? if <1.19 {
/*package com.iamkaf.amber.platform;

import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.event.v1.events.common.client.ClientCommandEvents;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;

/^*
 * Runs {@link ClientCommandEvents} commands before Forge 1.19.
 *
 * <p>Forge 1.17.1 and 1.18 have no client command API. Forge 1.18.1 and 1.18.2 have one, but they
 * merge server commands into the same tree, so a client command that shares a path with a server
 * command, such as {@code /amber doctor}, is sent to the server instead. Forge fires
 * {@link ClientChatEvent} for typed chat and for clicked chat commands before either happens, so
 * this dispatcher runs the client commands first. Commands it does not know go on unchanged, as
 * they do with Forge's own client commands.</p>
 ^/
final class ForgeChatClientCommands {
    private static CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();

    private ForgeChatClientCommands() {
    }

    static void register() {
        MinecraftForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggedInEvent event) -> collect());
        MinecraftForge.EVENT_BUS.addListener(ForgeChatClientCommands::onChat);
    }

    /^* Collects commands on every login, as Forge does from 1.18.1. ^/
    private static void collect() {
        CommandDispatcher<CommandSourceStack> commands = new CommandDispatcher<>();
        ClientCommandEvents.EVENT.invoker().register(commands,
                ForgeAmberEventHandlers.EventHandlerCommon.legacyBuiltinRegistryAccess());
        dispatcher = commands;
    }

    private static void onChat(ClientChatEvent event) {
        String message = event.getMessage();
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !message.startsWith("/")) return;

        StringReader reader = new StringReader(message);
        reader.skip();
        CommandSourceStack source = new Source(player);
        try {
            dispatcher.execute(reader, source);
        } catch (CommandSyntaxException exception) {
            if (exception.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand()
                    || exception.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument()) {
                return;
            }
            source.sendFailure(syntaxError(exception));
        } catch (CommandRuntimeException exception) {
            source.sendFailure(exception.getComponent());
        } catch (RuntimeException exception) {
            Constants.LOG.error("Error executing client command \"{}\"", message, exception);
            String detail = exception.getMessage() == null ? exception.getClass().getName() : exception.getMessage();
            source.sendFailure(new TranslatableComponent("command.failed").withStyle(style ->
                    style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(detail)))));
        }
        event.setCanceled(true);
        // A cancelled message skips chat history, so keep typed commands recallable as Forge 1.18.1 does.
        if (typed(event.getOriginalMessage())) {
            Minecraft.getInstance().gui.getChat().addRecentChat(message);
        }
    }

    private static Component syntaxError(CommandSyntaxException exception) {
        Component message = ComponentUtils.fromMessage(exception.getRawMessage());
        String context = exception.getContext();
        return context == null ? message
                : new TranslatableComponent("command.context.parse_error", message, exception.getCursor(), context);
    }

    /^* Chat clicks send the clicked command while the chat box still holds whatever the player typed. ^/
    private static boolean typed(String message) {
        return Minecraft.getInstance().screen instanceof ChatScreen chat && chat.children().stream()
                .anyMatch(child -> child instanceof EditBox input && input.getValue().trim().equals(message));
    }

    private static int permissionLevel(LocalPlayer player) {
        int level = 4;
        while (level > 0 && !player.hasPermissions(level)) level--;
        return level;
    }

    /^* A client-side source, like Forge's later ClientCommandSourceStack, with no server or server level. ^/
    private static final class Source extends CommandSourceStack {
        private final LocalPlayer player;

        Source(LocalPlayer player) {
            super(player, player.position(), player.getRotationVector(), null, permissionLevel(player),
                    player.getName().getString(), player.getDisplayName(), null, player);
            this.player = player;
        }

        @Override
        public void sendSuccess(Component message, boolean broadcastToAdmins) {
            player.sendMessage(message, Util.NIL_UUID);
        }
    }
}
*///?}
