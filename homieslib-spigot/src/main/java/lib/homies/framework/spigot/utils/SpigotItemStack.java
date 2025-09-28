package lib.homies.framework.spigot.utils;

import lib.homies.framework.utils.HomiesItemStack;
import org.bukkit.inventory.ItemStack;

/**
 * Spigot-specific implementation of the {@link HomiesItemStack} interface.
 * This class wraps a Bukkit {@link ItemStack} object, providing a platform-specific
 * way to interact with in-game items while adhering to the platform-agnostic API.
 */
public class SpigotItemStack implements HomiesItemStack {
    private final ItemStack itemStack;

    /**
     * Constructs a new SpigotItemStack wrapping a given Bukkit ItemStack.
     * @param itemStack The Bukkit {@link ItemStack} to wrap.
     */
    public SpigotItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    /**
     * Gets the underlying Bukkit {@link ItemStack} object.
     * This method allows direct access to Bukkit's item functionality when needed
     * within the Spigot implementation module.
     * @return The wrapped Bukkit ItemStack.
     */
    public ItemStack getItemStack() {
        return itemStack;
    }
}
