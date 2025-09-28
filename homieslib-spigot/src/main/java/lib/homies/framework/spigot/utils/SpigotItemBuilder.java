package lib.homies.framework.spigot.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import lib.homies.framework.utils.HomiesItemStack;
import lib.homies.framework.utils.HomiesPlayerProfile;
import lib.homies.framework.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

/**
 * Spigot-specific implementation of the {@link ItemBuilder} interface.
 * This class uses Bukkit's {@link ItemStack} and {@link ItemMeta} to construct items
 * with various properties, adhering to the platform-agnostic API.
 */
public class SpigotItemBuilder implements ItemBuilder {
    private final ItemStack item;
    private final ItemMeta meta;

    /**
     * Constructs a new SpigotItemBuilder with a specified material.
     * @param material The Bukkit {@link Material} to start building the item from.
     */
    public SpigotItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    /**
     * Sets the amount of the item stack.
     * @param amount The number of items in the stack.
     * @return The ItemBuilder instance for chaining.
     */
    @Override
    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    /**
     * Sets the display name of the item.
     * Color codes are translated using {@link ChatColor#translateAlternateColorCodes(char, String)}.
     * @param displayName The display name of the item.
     * @return The ItemBuilder instance for chaining.
     */
    @Override
    public ItemBuilder name(String displayName) {
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        return this;
    }

    /**
     * Sets the lore (description lines) of the item.
     * Color codes are translated for each line.
     * @param lines An array of strings representing the lore lines.
     * @return The ItemBuilder instance for chaining.
     */
    @Override
    public ItemBuilder lore(String... lines) {
        meta.setLore(Arrays.stream(lines)
                .map(l -> ChatColor.translateAlternateColorCodes('&', l))
                .toList());
        return this;
    }

    /**
     * Adds an enchantment to the item.
     * @param enchantment The name of the enchantment (e.g., "SHARPNESS").
     * @param level The level of the enchantment.
     * @return The ItemBuilder instance for chaining.
     */
    @Override
    public ItemBuilder enchant(String enchantment, int level) {
        Enchantment bukkitEnchantment = Enchantment.getByName(enchantment);
        if (bukkitEnchantment != null) {
            meta.addEnchant(bukkitEnchantment, level, true);
        }
        return this;
    }

    /**
     * Adds item flags to hide certain attributes (e.g., enchantments, attributes).
     * @param flags An array of strings representing the item flags (e.g., "HIDE_ENCHANTS").
     * @return The ItemBuilder instance for chaining.
     */
    @Override
    public ItemBuilder flag(String... flags) {
        for (String flag : flags) {
            try {
                meta.addItemFlags(ItemFlag.valueOf(flag.toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Log warning for invalid flag
            }
        }
        return this;
    }

    /**
     * Sets the player profile for skull items.
     * This method expects a {@link SpigotPlayerProfile} to unwrap the underlying Paper {@link PlayerProfile}.
     * @param profile The {@link HomiesPlayerProfile} to set for the skull.
     * @return The ItemBuilder instance for chaining.
     */
    @Override
    public ItemBuilder setPlayerProfile(HomiesPlayerProfile profile) {
        if (meta instanceof SkullMeta && profile instanceof SpigotPlayerProfile) {
            ((SkullMeta) meta).setPlayerProfile((PlayerProfile) ((SpigotPlayerProfile) profile).getPlayerProfile());
        }
        return this;
    }

    /**
     * Builds and returns the constructed {@link HomiesItemStack} as a {@link SpigotItemStack}.
     * @return The final {@link SpigotItemStack}.
     */
    @Override
    public HomiesItemStack build() {
        item.setItemMeta(meta);
        return new SpigotItemStack(item);
    }
}
