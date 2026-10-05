package draylar.goml.compat.webmap.player;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import draylar.goml.GetOffMyLawn;
import draylar.goml.api.HeadTextureProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a player record with their name and head icon for web map display.
 * 
 * <p>If the player or block cannot be resolved, placeholders are used.
 * 
 * @see PlayerHeadIcon
 */
public class PlayerRecord {
    private static final String PROFILE_LOOKUP_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private static final String PLAYER_HEAD_URL = "https://mc-heads.net/avatar/%s/16";
    private static final Integer REQUEST_TIMEOUT = 3000;

    private UUID uuid = null;
    private PlayerHeadIcon playerIcon = null;
    private String name = null;
    private MinecraftServer server = null;

    /**
     * Explicitly defines name and icon, cannot be updated after construction.
     * 
     * @param playerIcon the player head icon
     * @param playerName the player name, or null for placeholder
     */
    public PlayerRecord(PlayerHeadIcon playerIcon, @Nullable String playerName){
        this.name = (playerName != null) ? playerName : this.name;
        this.playerIcon = playerIcon;
    }
    /**
     * Gets player name and icon from server player registry.
     * 
     * @param uuid the player's UUID
     * @param server the Minecraft server instance
     * @param name the fallback name if lookup fails, or null
     */
    public PlayerRecord(UUID uuid, MinecraftServer server, @Nullable String name) {
        this.server = server;
        this.uuid = uuid;
        this.playerIcon = PlayerHeadIcon.fromUrl(PLAYER_HEAD_URL.formatted(uuid.toString().replace("-", "")));
        this.resolvePlayer();

        // If name is still null after player resolution, assume it failed and use defaults
        if (this.name == null) {
            this.name = name;
        }

        // The Mojang profile may be missing the "textures" property (offline mode,
        // rate limiting, cached responses without skins, ...) while the name was
        // resolved successfully. Never leave the icon null — fall back to the
        // built-in default icon so web map rendering cannot NPE.
        if (this.playerIcon == null) {
            this.playerIcon = new PlayerHeadIcon(null);
        }

        if (this.name == null) {
            this.name = Component.translatable("text.goml.webmap.label.unknown", "Unknown?").getString();
        }
    }
    /**
     * Gets an icon from the block's legacy head texture and sets display name.
     * 
     * @param headBlock the textured claim block
     * @param displayName the display name, or null for placeholder
     */
    public PlayerRecord(HeadTextureProvider headBlock, @Nullable String displayName) {
        final String DEFAULT_BLOCK_ICON = "iVBORw0KGgoAAAANSUhEUgAAAAgAAAAICAIAAABLbSncAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAX0lEQVQImWWMyRGAIBAEG4qvq8EQhEEYhpmYAkHwMARykSMBHyBUYb9mu2ZWXSfHzoTzqOdmWxdsHjpITEUDzQYhSD9NUz/MiP1bEEDPzW9t/qqinSemElPBZmyu2XlexGAiqcN7MbsAAAAASUVORK5CYII=";
        this.name = (displayName != null) ? displayName : this.name;
        this.playerIcon = new PlayerHeadIcon(
                headBlock == null
                        ? DEFAULT_BLOCK_ICON
                        : getHeadImage(headBlock.getHeadTexture()).orElse(DEFAULT_BLOCK_ICON)
        );
    }

    /**
     * @return the name of the player. Only updated by calling {@link #refreshPlayer(MinecraftServer)}.
     */
    public String getName() {
        return this.name;
    }
    /**
     * @return {@link PlayerHeadIcon} containing the player's head. Only updated when calling {@link #resolvePlayer()}.
     */
    public PlayerHeadIcon getHeadIcon() {
        return this.playerIcon;
    }
    
    /**
     * Resolves the player's name and icon using Minecraft API. Has no effect if not constructed with player UUID.
     */
    public void resolvePlayer() {
        if(this.uuid != null) {
            mainTry:
            try {
                // Check cache first
                String json = PlayerRecordCache.getProfile(this.uuid);

                // On cache miss
                if (json == null) {
                    json = openConnection(URI.create(PROFILE_LOOKUP_URL + this.uuid));
                    PlayerRecordCache.putProfile(this.uuid, json);
                }
                
                Gson gson = new Gson();
                Map<String, Object> profile = gson.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());

                if (profile == null) {
                    break mainTry;
                }

                // Player avatars are loaded by the map browser from mc-heads.net.
                // Keep this server-side request focused on resolving the display name.
                this.name = (String) profile.get("name");
            } catch (Exception exception) {
                GetOffMyLawn.LOGGER.warn("Unable to get data for player with UUID {}: ", this.uuid, exception);
            }

            // If name is still null, attempt to retrieve it from UserCache
            server.services().nameToIdCache().get(uuid).ifPresent(
                profile -> this.name = profile.name()
            );
        }
    }

	private static Optional<String> getHeadImage(@Nullable String skinData) {
		if (skinData == null || skinData.isBlank()) {
			return Optional.empty();
		}

		String textureUrl = PlayerHeadRenderer.getUrlFromSkinValue(skinData);
		if (textureUrl == null || textureUrl.isBlank()) {
			return Optional.empty();
		}

		return PlayerHeadRenderer.headImageFromSkinUrl(textureUrl);
	}

    private static String openConnection(URI resourceUri) throws IOException {
        URLConnection connection = resourceUri.toURL().openConnection();
        connection.setReadTimeout(REQUEST_TIMEOUT);
        connection.setConnectTimeout(REQUEST_TIMEOUT);

        try (InputStream stream = connection.getInputStream()) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } finally {
            if (connection instanceof java.net.HttpURLConnection httpConn) {
                httpConn.disconnect();
            }
        }
    }
}
