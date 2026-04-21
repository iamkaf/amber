package com.iamkaf.amber.api.registry.v1;

import com.iamkaf.amber.Constants;
import com.iamkaf.amber.api.platform.v1.Platform;
import com.iamkaf.amber.platform.Services;
import com.iamkaf.amber.util.Env;
import net.minecraft.client.KeyMapping;

import java.util.ArrayList;

/**
 * A utility class for managing keybinds in the Amber API.
 * This class allows for the registration of keybinds.
 * It is designed to be used in a client environment only.
 *
 * @since 6.0.5
 */
public class KeybindHelper {
    private static final ArrayList<KeyMapping> KEYBINDINGS = new ArrayList<>();
    public static boolean forgeEventAlreadyFired = false;

    /**
     * Registers a keybind.
     *
     * @param keybind the keybind to register
     */
    public static KeyMapping register(KeyMapping keybind) {
        if (!Platform.getEnvironment().equals(Env.CLIENT)) {
            return null;
        }
        if (forgeEventAlreadyFired) {
            Constants.LOG.error(
                    "Attempted to register a keybind after the Forge event has already fired. This is not allowed.");
            Constants.LOG.error("Please ensure that all keybinds are registered before the Forge event is fired.");
            Constants.LOG.error("{}", keybind.getName());
        }

        KEYBINDINGS.add(keybind);

        if (Platform.getPlatformName().equals("Fabric")) {
            Services.KEYBIND_REGISTER.register(keybind);
        }

        return keybind;
    }

    /**
     * Gets the list of registered keybinds.
     *
     * @return the list of keybinds
     */
    public static ArrayList<KeyMapping> getKeybindings() {
        return KEYBINDINGS;
    }
}
