package lib.homies.framework.spigot.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lib.homies.framework.utils.HomiesItemStack;
import lib.homies.framework.utils.PlayerHeadUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Level;

public class SpigotPlayerHeadUtils implements PlayerHeadUtils {

    @Override
    public HomiesItemStack createPlayerHeadByTexture(String textureValue) {
        // This method is difficult to implement reliably across all versions without the modern PlayerProfile API.
        // For now, we will return a default Steve head as a fallback.
        return new SpigotItemStack(new ItemStack(Material.PLAYER_HEAD));
    }

    @Override
    public HomiesItemStack createPlayerHead(UUID playerUUID) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(player);
            head.setItemMeta(meta);
        }
        return new SpigotItemStack(head);
    }

    @Override
    public HomiesItemStack createPlayerHead(String playerName) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        return createPlayerHead(player.getUniqueId());
    }

    @Override
    public String getTextureValue(UUID playerUUID) {
        try {
            String uuidString = playerUUID.toString().replace("-", "");
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuidString);

            InputStreamReader reader = new InputStreamReader(url.openStream());
            JsonElement jsonElement = JsonParser.parseReader(reader);

            // Gracefully handle empty responses from Mojang server
            if (jsonElement == null || jsonElement.isJsonNull()) {
                Bukkit.getLogger().log(Level.WARNING, "Failed to fetch texture for UUID " + playerUUID + ": Mojang API returned an empty response (player may not have a skin).");
                return null;
            }

            JsonObject profile = jsonElement.getAsJsonObject();
            if (!profile.has("properties")) {
                Bukkit.getLogger().log(Level.WARNING, "Failed to fetch texture for UUID " + playerUUID + ": Profile JSON does not contain a 'properties' array.");
                return null;
            }

            JsonObject properties = profile.getAsJsonArray("properties").get(0).getAsJsonObject();
            return properties.get("value").getAsString();

        } catch (Exception e) {
            // Don't print a full stack trace for common issues like 404s or timeouts.
            Bukkit.getLogger().log(Level.WARNING, "Could not fetch texture for UUID " + playerUUID + ": " + e.getMessage());
            return null;
        }
    }
}
