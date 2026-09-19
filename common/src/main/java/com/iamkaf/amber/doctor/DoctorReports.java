//? if >=1.21.11 {
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
        section.information("mixins", DoctorText.translatable("amber.doctor.mixins"),
                DoctorText.literal(String.join(", ", AmberMod.AMBER_MIXINS)));
        var mods = DoctorText.literal("");
        AmberMod.AMBER_MODS.stream().sorted(java.util.Comparator.comparing(AmberModInfo::id))
                .forEach(mod -> mods.append(mod.name() + " " + mod.version() + "\n"));
        section.information("mods", DoctorText.translatable("amber.doctor.mods"), mods);
    }

    public static void render(List<Doctor.Report> reports, Consumer<Component> output) {
        output.accept(DoctorText.translatable("amber.doctor.title"));
        for (Doctor.Report report : reports) {
            output.accept(DoctorText.literal(report.mod().name() + " " + report.mod().version() + " ")
                    .append(status(report.status())));
            for (DoctorSection.Entry entry : report.entries()) {
                var line = DoctorText.literal("  ").append(entry.label()).append(": ");
                entry.status().ifPresent(value -> line.append(status(value)).append(" "));
                line.append(entry.explanation());
                output.accept(line);
                entry.remedy().ifPresent(remedy -> output.accept(DoctorText.literal("    ").append(remedy)));
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
//?}
