//? if >=1.21.11 {
package com.iamkaf.amber.api.doctor.v1;

import com.iamkaf.amber.api.core.v2.AmberModInfo;
import net.minecraft.client.Minecraft;

import java.util.List;
import java.util.Objects;

/** Client-only entry point. Register from client initialization, never common setup. */
public final class ClientDoctor {
    private static final DoctorRegistry<Context> CLIENT = new DoctorRegistry<>();

    private ClientDoctor() {
    }

    public static void register(AmberModInfo mod, DoctorContributor<Context> contributor) {
        CLIENT.register(mod, contributor);
    }

    public static List<Doctor.Report> inspect(Context context) {
        if (!context.client().isSameThread()) {
            throw new IllegalStateException("Client Doctor checks must run on the client thread");
        }
        return CLIENT.inspect(context);
    }

    public record Context(Minecraft client) {
        public Context {
            Objects.requireNonNull(client);
        }
    }
}
//?}
