package dev.al3mid3x.lib.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

public class PlayerHeadUtils {

    private final ItemFactory itemFactory;

    public PlayerHeadUtils(ItemFactory itemFactory) {
        this.itemFactory = itemFactory;
    }

    public PlayerHeadUtils() {
        this(new ItemFactory(Material.PLAYER_HEAD));
    }

    public ItemStack createPlayerHeadByTexture(String textureValue) {
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), null);
        profile.setProperty(new ProfileProperty("textures", textureValue));

        return itemFactory
                .setPlayerProfile(profile)
                .complete();
    }


    public ItemStack createPlayerHead(UUID playerUUID) {
        String textureValue = this.getTextureValue(playerUUID);
        if (textureValue == null) {
            throw new IllegalArgumentException("Could not fetch texture value for UUID: " + playerUUID);
        }
        return createPlayerHead(textureValue);
    }

    public ItemStack createPlayerHead(String playerName) {
        UUID playerUUID = Bukkit.getPlayerUniqueId(playerName);
        if (playerUUID == null) {
            throw new IllegalArgumentException("Player not found: " + playerName);
        }
        return createPlayerHead(playerUUID);
    }

    public String getTextureValue(UUID playerUUID) {
        try {
            String uuidString = playerUUID.toString().replace("-", "");
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuidString);

            InputStreamReader reader = new InputStreamReader(url.openStream());

            JsonObject profile = JsonParser.parseReader(reader).getAsJsonObject();

            JsonObject properties = profile.getAsJsonArray("properties").get(0).getAsJsonObject();

            String textureBase64 = properties.get("value").getAsString();

            return textureBase64;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
}