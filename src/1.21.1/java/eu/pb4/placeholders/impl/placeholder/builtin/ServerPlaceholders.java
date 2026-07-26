package eu.pb4.placeholders.impl.placeholder.builtin;

/**
 * NeoForge bridge for Placeholder API's optional built-in server placeholders.
 *
 * <p>GOML embeds Placeholder API for its text parser and its own {@code goml:*}
 * placeholders. The upstream built-ins inspect Fabric Loader metadata, which is
 * not available in a native NeoForge runtime, so they are intentionally not
 * registered here.</p>
 */
public final class ServerPlaceholders {
    private ServerPlaceholders() {
    }

    public static void register() {
    }
}
