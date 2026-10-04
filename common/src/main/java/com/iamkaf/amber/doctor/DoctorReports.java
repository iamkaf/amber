package com.iamkaf.amber.doctor;

import com.iamkaf.amber.AmberMod;
import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.core.v2.AmberModInfo;
import com.iamkaf.amber.api.doctor.v1.*;
import com.iamkaf.amber.api.platform.v1.Platform;
import com.iamkaf.amber.networking.v1.AmberNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public final class DoctorReports {
    private DoctorReports() {}

    public static AmberModInfo amberInfo() {
        var info = Platform.getModInfo(Constants.MOD_ID);
        return new AmberModInfo(Constants.MOD_ID, info.name(), info.version());
    }

    public static void amber(DoctorSection section) {
        section.information("platform", DoctorText.translatable("amber.doctor.platform"),
                DoctorText.literal(Platform.getPlatformName()));
        //? if >=1.21.6
        String version = SharedConstants.getCurrentVersion().name();
        //? if <1.21.6
        /*String version = SharedConstants.getCurrentVersion().getName();*/
        section.information("minecraft", DoctorText.literal("Minecraft"), DoctorText.literal(version));
        boolean initialized = AmberNetworking.isInitialized();
        section.check("networking", DoctorText.translatable("amber.doctor.networking"),
                initialized ? DoctorStatus.OK : DoctorStatus.ERROR,
                DoctorText.translatable(initialized ? "amber.doctor.initialized" : "amber.doctor.not_initialized"));
        // The full mixin list wraps across several chat lines, so it stays behind a hover.
        section.information("mixins", DoctorText.translatable("amber.doctor.mixins"),
                DoctorText.literal(String.valueOf(AmberMod.AMBER_MIXINS.size())).withStyle(style -> style
                        .withUnderlined(true)
                        .withHoverEvent(DoctorText.showText(DoctorText.literal(String.join("\n", AmberMod.AMBER_MIXINS))))));
        String mods = AmberMod.AMBER_MODS.stream().sorted(java.util.Comparator.comparing(AmberModInfo::id))
                .map(mod -> mod.name() + " " + mod.version()).collect(java.util.stream.Collectors.joining(", "));
        section.information("mods", DoctorText.translatable("amber.doctor.mods"), DoctorText.literal(mods));
    }

    public static void render(List<Doctor.Report> reports, Consumer<Component> output) {
        output.accept(DoctorText.translatable("amber.doctor.title").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        for (Doctor.Report report : reports) {
            output.accept(DoctorText.literal(report.mod().name() + " " + report.mod().version() + " ")
                    .withStyle(ChatFormatting.BOLD)
                    .append(status(report.status()).copy().withStyle(style -> style.withBold(false))));
            for (DoctorSection.Entry entry : report.entries()) {
                var line = DoctorText.literal("  ").append(entry.label().copy().withStyle(ChatFormatting.GRAY))
                        .append(DoctorText.literal(": ").withStyle(ChatFormatting.GRAY));
                entry.status().ifPresent(value -> line.append(status(value)).append(" "));
                line.append(entry.explanation());
                output.accept(line);
                entry.remedy().ifPresent(remedy -> output.accept(DoctorText.literal("    ").append(remedy)
                        .withStyle(ChatFormatting.ITALIC)));
            }
        }
    }

    private static Component status(DoctorStatus status) {
        ChatFormatting color = switch (status) {
            case OK -> ChatFormatting.GREEN;
            case UNKNOWN -> ChatFormatting.GRAY;
            case WARNING -> ChatFormatting.YELLOW;
            case ERROR -> ChatFormatting.RED;
        };
        return DoctorText.translatable("amber.doctor.status." + status.name().toLowerCase(Locale.ROOT)).withStyle(color);
    }
}
