package lib.homies.framework.utils;

import lib.homies.framework.inventory.HomiesInventory;

import java.util.List;

/**
 * A platform-agnostic interface for utility methods related to inventories.
 * This service provides common operations for manipulating {@link HomiesInventory} instances.
 */
public interface InventoryUtils {
    /**
     * Fills an entire {@link HomiesInventory} with a specific {@link HomiesItemStack}.
     * @param inventory The {@link HomiesInventory} to fill.
     * @param item The {@link HomiesItemStack} to use as filler.
     */
    void fill(HomiesInventory inventory, HomiesItemStack item);

    /**
     * Fills the border slots of a {@link HomiesInventory} with a specific {@link HomiesItemStack}.
     * Assumes a standard 9-slot row inventory layout.
     * @param inventory The {@link HomiesInventory} to fill the borders of.
     * @param item The {@link HomiesItemStack} to use as filler for the borders.
     */
    void fillBorders(HomiesInventory inventory, HomiesItemStack item);

    /**
     * Paginates a list of {@link HomiesItemStack}s, returning a sublist for a specific page.
     * This method is platform-agnostic and only handles list slicing.
     * @param items The full list of {@link HomiesItemStack}s to paginate.
     * @param page The 1-indexed page number to retrieve.
     * @param perPage The maximum number of items per page.
     * @return A {@link List} of {@link HomiesItemStack}s for the specified page.
     */
    List<HomiesItemStack> paginate(List<HomiesItemStack> items, int page, int perPage);
}
