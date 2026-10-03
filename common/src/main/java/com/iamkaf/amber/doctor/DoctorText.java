package com.iamkaf.amber.doctor;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import java.net.URI;
import java.nio.file.Path;

/** Text, click, and hover construction across supported component APIs. */
public final class DoctorText {
    private DoctorText() {
    }

    public static MutableComponent literal(String text) {
        //? if >=1.19
        return Component.literal(text);
        //? if <1.19
        /*return new net.minecraft.network.chat.TextComponent(text);*/
    }

    public static MutableComponent translatable(String key, Object... arguments) {
        //? if >=1.19
        return Component.translatable(key, arguments);
        //? if <1.19
        /*return new net.minecraft.network.chat.TranslatableComponent(key, arguments);*/
    }

    //? if >=1.21.5 {
    public static ClickEvent openFile(Path path) {
        return new ClickEvent.OpenFile(path);
    }

    public static ClickEvent openUrl(URI uri) {
        return new ClickEvent.OpenUrl(uri);
    }

    public static ClickEvent runCommand(String command) {
        return new ClickEvent.RunCommand(command);
    }

    public static HoverEvent showText(Component text) {
        return new HoverEvent.ShowText(text);
    }
    //?} else {
    /*public static ClickEvent openFile(Path path) {
        // Same value as the 1.21.5 OpenFile(Path) record constructor.
        return new ClickEvent(ClickEvent.Action.OPEN_FILE, path.toFile().toString());
    }

    public static ClickEvent openUrl(URI uri) {
        return new ClickEvent(ClickEvent.Action.OPEN_URL, uri.toString());
    }

    public static ClickEvent runCommand(String command) {
        return new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
    }

    public static HoverEvent showText(Component text) {
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, text);
    }
    *///?}
}
