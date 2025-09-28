package lib.homies.framework.utils;

import java.util.UUID;

/**
 * A platform-agnostic interface for utility methods related to creating player heads.
 * This service provides methods to generate {@link HomiesItemStack}s that represent player heads
 * based on UUID, player name, or texture values.
 */
public interface PlayerHeadUtils {
    /**
     * Creates a player head {@link HomiesItemStack} using a base64 encoded texture value.
     * @param textureValue The base64 encoded texture string.
     * @return A {@link HomiesItemStack} representing the player head.
     */
    HomiesItemStack createPlayerHeadByTexture(String textureValue);

    /**
     * Creates a player head {@link HomiesItemStack} for a given player UUID.
     * This method typically fetches the player's texture from a Mojang-like service.
     * @param playerUUID The {@link UUID} of the player.
     * @return A {@link HomiesItemStack} representing the player head.
     */
    HomiesItemStack createPlayerHead(UUID playerUUID);

    /**
     * Creates a player head {@link HomiesItemStack} for a given player name.
     * This method typically resolves the player name to a UUID and then fetches the texture.
     * @param playerName The name of the player.
     * @return A {@link HomiesItemStack} representing the player head.
     */
    HomiesItemStack createPlayerHead(String playerName);

    /**
     * Retrieves the base64 encoded texture value for a given player UUID.
     * This method typically queries a Mojang-like session server.
     * @param playerUUID The {@link UUID} of the player.
     * @return The base64 encoded texture string, or {@code null} if not found or an error occurs.
     */
    String getTextureValue(UUID playerUUID);
}
