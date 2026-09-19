//? if >=1.21.11 {
package com.iamkaf.amber.api.doctor.v1;

import com.iamkaf.amber.api.core.v2.AmberModInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Server-side diagnostic contributors, registered once during initialization. */
public final class Doctor {
    private static final DoctorRegistry<ServerContext> SERVER = new DoctorRegistry<>();

    private Doctor() {
    }

    public static void registerServer(AmberModInfo mod, DoctorContributor<ServerContext> contributor) {
        SERVER.register(mod, contributor);
    }

    /** Invoke on the server thread. Results are sorted by mod ID. */
    public static List<Report> inspectServer(ServerContext context) {
        if (!context.server().isSameThread()) {
            throw new IllegalStateException("Server Doctor checks must run on the server thread");
        }
        return SERVER.inspect(context);
    }

    public record ServerContext(MinecraftServer server, Optional<ServerPlayer> player) {
        public ServerContext {
            Objects.requireNonNull(server);
            Objects.requireNonNull(player);
        }
    }

    public record Report(AmberModInfo mod, DoctorStatus status, List<DoctorSection.Entry> entries) {
        public Report {
            entries = List.copyOf(entries);
        }
    }
}
//?}
