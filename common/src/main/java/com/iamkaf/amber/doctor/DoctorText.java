//? if >=1.21.11 {
package com.iamkaf.amber.doctor;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/** Text construction across supported component APIs. */
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
}
//?}
