package lib.homies.framework.spigot.inventory;

import lib.homies.framework.inventory.HomiesInventory;
import lib.homies.framework.spigot.utils.SpigotItemStack;
import lib.homies.framework.utils.HomiesItemStack;
import org.bukkit.inventory.Inventory;

/**
 * Spigot-specific implementation of the {@link HomiesInventory} interface.
 * This class wraps a Bukkit {@link Inventory} object, providing a platform-specific
 * way to interact with in-game inventories.
 */
public class SpigotInventory implements HomiesInventory {
    private final Inventory inventory;

    /**
     * Constructs a new SpigotInventory wrapping a given Bukkit Inventory.
     * @param inventory The Bukkit {@link Inventory} to wrap.
     */
    public SpigotInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Gets the underlying Bukkit {@link Inventory} object.
     * @return The wrapped Bukkit Inventory.
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Gets the total size (number of slots) of this inventory.
     * @return The size of the inventory.
     */
    @Override
    public int getSize() {
        return inventory.getSize();
    }

    /**
     * Sets an item in a specific slot of the inventory.
     * The provided {@link HomiesItemStack} must be a {@link SpigotItemStack} to be set in the Bukkit Inventory.
     * @param index The slot index (0-based).
     * @param item The {@link HomiesItemStack} to set.
     */
    @Override
    public void setItem(int index, HomiesItemStack item) {
        if (item instanceof SpigotItemStack) {
            inventory.setItem(index, ((SpigotItemStack) item).getItemStack());
        }
    }
}
