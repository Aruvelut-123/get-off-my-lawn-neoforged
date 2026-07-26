package eu.pb4.placeholders.impl;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * NeoForge implementation of Placeholder API's loader bridge.
 *
 * <p>The upstream class only exposes these two constants, but obtains them
 * through Fabric Loader. Keeping the same binary API lets the rest of the
 * embedded Placeholder API stay loader-neutral.</p>
 */
public final class LoaderUtil {
    public static final boolean IS_DEV = !FMLEnvironment.isProduction();
    public static final boolean IS_CLIENT = FMLEnvironment.getDist() == Dist.CLIENT;

    private LoaderUtil() {
    }
}
