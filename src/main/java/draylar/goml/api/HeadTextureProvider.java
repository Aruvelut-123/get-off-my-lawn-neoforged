package draylar.goml.api;

/**
 * Loader-neutral access to the legacy player-head texture used by web-map
 * integrations. Native NeoForge clients render the regular block model.
 */
public interface HeadTextureProvider {
    String getHeadTexture();
}
