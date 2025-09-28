package lib.homies.framework.inventory;

import lib.homies.framework.utils.HomiesItemStack;

/**
 * A platform-agnostic representation of an inventory.
 * This interface defines basic operations for interacting with an inventory,
 * abstracting away platform-specific inventory implementations.
 */
public interface HomiesInventory {
    /**
     * Gets the total size (number of slots) of this inventory.
     * @return The size of the inventory.
     */
    int getSize();

    /**
     * Sets an item in a specific slot of the inventory.
     * @param index The slot index (0-based).
     * @param item The {@link HomiesItemStack} to set.
     */
    void setItem(int index, HomiesItemStack item);
}
