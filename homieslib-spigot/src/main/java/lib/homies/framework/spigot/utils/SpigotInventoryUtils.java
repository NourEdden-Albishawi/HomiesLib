package lib.homies.framework.spigot.utils;

import lib.homies.framework.inventory.HomiesInventory;
import lib.homies.framework.spigot.inventory.SpigotInventory;
import lib.homies.framework.utils.HomiesItemStack;
import lib.homies.framework.utils.InventoryUtils;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Spigot-specific implementation of the {@link InventoryUtils} interface.
 * This class provides utility methods for manipulating Bukkit {@link Inventory} instances.
 */
public class SpigotInventoryUtils implements InventoryUtils {
    /**
     * Fills an entire {@link HomiesInventory} with a specific {@link HomiesItemStack}.
     * The inventory and item must be Spigot-specific implementations.
     * @param inventory The {@link HomiesInventory} to fill. Must be a {@link SpigotInventory}.
     * @param item The {@link HomiesItemStack} to use as filler. Must be a {@link SpigotItemStack}.
     */
    @Override
    public void fill(HomiesInventory inventory, HomiesItemStack item) {
        if (inventory instanceof SpigotInventory && item instanceof SpigotItemStack) {
            Inventory bukkitInv = ((SpigotInventory) inventory).getInventory();
            ItemStack bukkitItem = ((SpigotItemStack) item).getItemStack();
            for (int i = 0; i < bukkitInv.getSize(); i++) {
                bukkitInv.setItem(i, bukkitItem);
            }
        }
    }

    /**
     * Fills the border slots of a {@link HomiesInventory} with a specific {@link HomiesItemStack}.
     * Assumes a standard 9-slot row inventory layout.
     * The inventory and item must be Spigot-specific implementations.
     * @param inventory The {@link HomiesInventory} to fill the borders of. Must be a {@link SpigotInventory}.
     * @param item The {@link HomiesItemStack} to use as filler for the borders. Must be a {@link SpigotItemStack}.
     */
    @Override
    public void fillBorders(HomiesInventory inventory, HomiesItemStack item) {
        if (inventory instanceof SpigotInventory && item instanceof SpigotItemStack) {
            Inventory bukkitInv = ((SpigotInventory) inventory).getInventory();
            ItemStack bukkitItem = ((SpigotItemStack) item).getItemStack();
            int size = bukkitInv.getSize();
            int rowLength = 9;

            for (int i = 0; i < size; i++) {
                if (i < rowLength || i >= size - rowLength || i % rowLength == 0 || i % rowLength == rowLength - 1) {
                    bukkitInv.setItem(i, bukkitItem);
                }
            }
        }
    }

    /**
     * Paginates a list of {@link HomiesItemStack}s, returning a sublist for a specific page.
     * This method is platform-agnostic and only handles list slicing.
     * @param items The full list of {@link HomiesItemStack}s to paginate.
     * @param page The 1-indexed page number to retrieve.
     * @param perPage The maximum number of items per page.
     * @return A {@link List} of {@link HomiesItemStack}s for the specified page.
     */
    @Override
    public List<HomiesItemStack> paginate(List<HomiesItemStack> items, int page, int perPage) {
        int fromIndex = Math.max(0, (page - 1) * perPage);
        int toIndex = Math.min(items.size(), fromIndex + perPage);
        return items.subList(fromIndex, toIndex);
    }
}
