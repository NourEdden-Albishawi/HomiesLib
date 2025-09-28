package lib.homies.framework.spigot.utils;

import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.profile.PlayerProfile;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lib.homies.framework.utils.HomiesItemStack;
import lib.homies.framework.utils.PlayerHeadUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Spigot-specific implementation of the {@link PlayerHeadUtils} interface.
 * This class provides utility methods to generate {@link HomiesItemStack}s that represent player heads
 * using Bukkit's {@link Material#PLAYER_HEAD} and {@link PlayerProfile} functionality.
 */
public class SpigotPlayerHeadUtils implements PlayerHeadUtils {

    /**
     * Creates a player head {@link HomiesItemStack} using a base64 encoded texture value.
     * This method leverages Bukkit's {@link PlayerProfile} to set the texture.
     * @param textureValue The base64 encoded texture string.
     * @return A {@link SpigotItemStack} representing the player head.
     */
    @Override
    public HomiesItemStack createPlayerHeadByTexture(String textureValue) {
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), null); // UUID is dummy, name is null
        // Corrected API usage: add property to the profile's properties collection
        ((com.destroystokyo.paper.profile.PlayerProfile) profile).getProperties().add(new ProfileProperty("textures", textureValue));

        return new SpigotItemBuilder(Material.PLAYER_HEAD)
                .setPlayerProfile(new SpigotPlayerProfile(profile))
                .build();
    }

    /**
     * Creates a player head {@link HomiesItemStack} for a given player UUID.
     * This method fetches the player's texture from the Mojang session server.
     * @param playerUUID The {@link UUID} of the player.
     * @return A {@link SpigotItemStack} representing the player head.
     * @throws IllegalArgumentException if the texture value cannot be fetched.
     */
    @Override
    public HomiesItemStack createPlayerHead(UUID playerUUID) {
        String textureValue = this.getTextureValue(playerUUID);
        if (textureValue == null) {
            throw new IllegalArgumentException("Could not fetch texture value for UUID: " + playerUUID);
        }
        return createPlayerHeadByTexture(textureValue);
    }

    /**
     * Creates a player head {@link HomiesItemStack} for a given player name.
     * This method resolves the player name to a UUID and then fetches the texture.
     * @param playerName The name of the player.
     * @return A {@link SpigotItemStack} representing the player head.
     * @throws IllegalArgumentException if the player is not found or texture cannot be fetched.
     */
    @Override
    public HomiesItemStack createPlayerHead(String playerName) {
        // Bukkit.getOfflinePlayer(playerName) can return a player even if they don't exist
        // but their UUID is known. For texture fetching, a valid UUID is needed.
        UUID playerUUID = Bukkit.getOfflinePlayer(playerName).getUniqueId();
        if (playerUUID == null) {
            throw new IllegalArgumentException("Player not found: " + playerName);
        }
        return createPlayerHead(playerUUID);
    }

    /**
     * Retrieves the base64 encoded texture value for a given player UUID from the Mojang session server.
     * @param playerUUID The {@link UUID} of the player.
     * @return The base64 encoded texture string, or {@code null} if not found or an error occurs.
     */
    @Override
    public String getTextureValue(UUID playerUUID) {
        try {
            String uuidString = playerUUID.toString().replace("-", "");
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuidString);

            InputStreamReader reader = new InputStreamReader(url.openStream());

            JsonObject profile = JsonParser.parseReader(reader).getAsJsonObject();

            // Assuming the first property is always the textures property
            JsonObject properties = profile.getAsJsonArray("properties").get(0).getAsJsonObject();

            return properties.get("value").getAsString();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to fetch texture for UUID " + playerUUID, e);
            return null;
        }
    }
}
