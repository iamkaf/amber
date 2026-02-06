package com.iamkaf.amber.platform;
import com.iamkaf.amber.platform.services.IKeybindRegister;

import com.iamkaf.amber.Constants;
import com.iamkaf.amber.platform.services.IAmberEventSetup;
import com.iamkaf.amber.platform.services.INetworkingService;
import com.iamkaf.amber.platform.services.IPlatformHelper;
import com.iamkaf.amber.platform.services.IRegistrarManager;

import java.util.ServiceLoader;

/**
 * Internal service loader registry for Amber's platform-specific implementations.
 * 
 * <p><strong>⚠️ INTERNAL API - DO NOT USE</strong></p>
 * 
 * <p>This class is for <strong>internal Amber usage only</strong> and should not be used by mod developers.
 * The APIs in this class are implementation details that may change without notice between versions.</p>
 * 
 * <p><strong>For mod developers:</strong> Use the public APIs in {@code com.iamkaf.amber.api.*} packages instead.
 * These provide stable, documented interfaces that abstract away platform differences.</p>
 * 
 * <p>Service loaders are a built-in Java feature that allow us to locate implementations of an interface 
 * that vary from one environment to another. In the context of MultiLoader we use this feature to access 
 * a mock API in the common code that is swapped out for the platform specific implementation at runtime.</p>
 * 
 * This class uses Java's ServiceLoader mechanism to dynamically load platform-specific
 *           implementations at runtime. Each platform (Fabric, Forge, NeoForge) provides its own 
 *           implementations via META-INF/services files.
 */
public class Services {

    /**
     * Platform helper service for internal use.
     * <p><strong>⚠️ INTERNAL API:</strong> Use {@link com.iamkaf.amber.api.platform.v1.Platform} instead.</p>
     */
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    /**
     * Event setup service for internal use.
     * <p><strong>⚠️ INTERNAL API:</strong> Use event classes in {@code com.iamkaf.amber.api.event.v1.*} instead.</p>
     */
    public static final IAmberEventSetup AMBER_EVENT_SETUP = load(IAmberEventSetup.class);

    /**
     * Registrar manager service for internal use.
     * <p><strong>⚠️ INTERNAL API:</strong> Use public registration APIs instead.</p>
     */
    public static final IRegistrarManager REGISTRAR_MANAGER = load(IRegistrarManager.class);

    /**
     * Networking service for internal use.
     * <p><strong>⚠️ INTERNAL API:</strong> Use public networking APIs instead.</p>
     */
    public static final INetworkingService NETWORKING = load(INetworkingService.class);

    /**
     * Loads a platform-specific service implementation using Java's ServiceLoader.
     * 
     * <p><strong>⚠️ INTERNAL API:</strong> This method is for internal Amber usage only.</p>
     * 
     * <p>Service implementations must be defined in META-INF/services files with the fully qualified 
     * class name of the service interface. Inside the file, write the fully qualified class name of 
     * the implementation to load for the platform.</p>
     * 
     * @param <T> the service interface type
     * @param clazz the service interface class
     * @return the loaded service implementation
     * @throws NullPointerException if no service implementation is found
     */
    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
    
    /**
     * Keybind registration service for internal use.
     * <p><strong>⚠️ INTERNAL API:</strong> Use public keybind APIs instead.</p>
     */
    public static final IKeybindRegister KEYBIND_REGISTER = load(IKeybindRegister.class);
}