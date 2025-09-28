package lib.homies.framework.spigot.utils;

import lib.homies.framework.utils.ItemBuilder;
import lib.homies.framework.utils.ItemBuilderFactory;
import org.bukkit.Material;

/**
 * Spigot-specific implementation of the {@link ItemBuilderFactory} interface.
 * This factory creates {@link SpigotItemBuilder} instances, converting a material name string
 * into a Bukkit {@link Material}.
 */
public class SpigotItemBuilderFactory implements ItemBuilderFactory {
    /**
     * Creates a new {@link SpigotItemBuilder} instance for a given material name.
     * @param material The name of the material (e.g., "STONE", "DIAMOND_SWORD").
     *                 This string is converted to a Bukkit {@link Material}.
     * @return A new {@link SpigotItemBuilder} instance.
     * @throws IllegalArgumentException if the provided material name does not correspond to a valid Bukkit Material.
     */
    @Override
    public ItemBuilder create(String material) {
        return new SpigotItemBuilder(Material.valueOf(material.toUpperCase()));
    }
}
