package lib.homies.framework.menu;

import lib.homies.framework.HomiesLib;
import lib.homies.framework.player.HomiesPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * An abstract base class for implementing paginated menus.
 * This class handles the core logic of managing pages, displaying items,
 * and providing navigation buttons (next/previous page).
 * Subclasses must define the specific items to paginate, the layout of pagination slots,
 * and the appearance of navigation buttons.
 */
public abstract class AbstractPaginatedHomiesMenu extends AbstractHomiesMenu {

    protected List<HomiesMenuItem> paginatedContentItems = new ArrayList<>();
    protected int currentPage = 0; // 0-indexed
    protected boolean isLoading = false;

    /**
     * Defines the specific inventory slots where the paginated items will be placed.
     * @return An array of integer slots (0-based indices).
     */
    protected abstract int[] getPaginationSlots();

    /**
     * Provides the {@link HomiesMenuItem} for navigating to the next page.
     * This button should typically trigger {@link #nextPage()} and then re-open the menu.
     * @param player The {@link HomiesPlayer} viewing the menu.
     * @return The {@link HomiesMenuItem} for the next page button.
     */
    protected abstract HomiesMenuItem getNextPageButton(HomiesPlayer player);

    /**
     * Provides the {@link HomiesMenuItem} for navigating to the previous page.
     * This button should typically trigger {@link #previousPage()} and then re-open the menu.
     * @param player The {@link HomiesPlayer} viewing the menu.
     * @return The {@link HomiesMenuItem} for the previous page button.
     */
    protected abstract HomiesMenuItem getPreviousPageButton(HomiesPlayer player);

    /**
     * Provides a filler item for empty pagination slots on the current page.
     * @return The {@link HomiesMenuItem} to use as a filler, or {@code null} if no filler is desired.
     */
    protected HomiesMenuItem getFillerItem() {
        return null; // Default: no filler item
    }

    /**
     * Provides a loading item to display in all pagination slots while content is being fetched asynchronously.
     * @return The {@link HomiesMenuItem} to use as a loading indicator.
     */
    protected abstract HomiesMenuItem getLoadingItem();

    /**
     * Sets the list of all items that will be paginated in this menu.
     * This method should be called after the items have been loaded.
     * @param items The {@link List} of all {@link HomiesMenuItem}s to be displayed across pages.
     */
    public void setPaginatedContentItems(List<HomiesMenuItem> items) {
        this.paginatedContentItems = items;
        this.isLoading = false;
    }

    /**
     * Initiates asynchronous loading of paginated content items.
     * While loading, the menu will display the {@link #getLoadingItem() loading item}.
     * Once the {@code itemLoader} completes, the menu will be refreshed for the player.
     *
     * @param player The {@link HomiesPlayer} for whom the menu is being loaded.
     * @param itemLoader A {@link Consumer} that, when executed, will provide the complete list of {@link HomiesMenuItem}s.
     *                   The consumer should call {@link #setPaginatedContentItems(List)} with the loaded items.
     */
    public void loadPaginatedContentItemsAsync(HomiesPlayer player, Consumer<List<HomiesMenuItem>> itemLoader) {
        this.isLoading = true;
        HomiesLib.getSchedulerService().runAsync(() -> {
            itemLoader.accept(new ArrayList<>()); // Pass an empty list initially, actual items will be set by the consumer
            HomiesLib.getSchedulerService().runLater(() -> {
                // Re-open the menu on the main thread to refresh content with loaded items
                HomiesLib.getMenuManager().openMenu(player, this);
            }, 1L); // Small delay to ensure refresh
        });
    }

    /**
     * Calculates the maximum number of pages required for the current items.
     * @return The total number of pages (1-indexed).
     */
    protected int getMaxPage() {
        int itemsPerPage = getPaginationSlots().length;
        if (itemsPerPage == 0) return 1;
        return (int) Math.ceil((double) paginatedContentItems.size() / itemsPerPage);
    }

    /**
     * Moves the menu to the next page if available.
     */
    public void nextPage() {
        if (currentPage < getMaxPage() - 1) {
            currentPage++;
        }
    }

    /**
     * Moves the menu to the previous page if available.
     */
    public void previousPage() {
        if (currentPage > 0) {
            currentPage--;
        }
    }

    /**
     * Resets the current page to the first page (page 0).
     */
    public void resetPage() {
        this.currentPage = 0;
    }

    /**
     * Generates the map of items for the current page, including navigation buttons and filler items.
     * @param player The {@link HomiesPlayer} viewing the menu.
     * @return A map of slot to {@link HomiesMenuItem} for the current page.
     */
    @Override
    public Map<Integer, HomiesMenuItem> getItems(HomiesPlayer player) {
        Map<Integer, HomiesMenuItem> currentItems = new HashMap<>();

        if (isLoading) {
            // Fill all pagination slots with the loading item
            HomiesMenuItem loadingItem = getLoadingItem();
            if (loadingItem != null) {
                for (int slot : getPaginationSlots()) {
                    currentItems.put(slot, loadingItem);
                }
            }
            return currentItems; // Don't add navigation buttons or actual content while loading
        }

        // Add navigation buttons
        if (currentPage > 0) {
            currentItems.put(getPreviousPageButtonSlot(), getPreviousPageButton(player));
        }
        if (currentPage < getMaxPage() - 1) {
            currentItems.put(getNextPageButtonSlot(), getNextPageButton(player));
        }

        // Add paginated content items
        int itemsPerPage = getPaginationSlots().length;
        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, paginatedContentItems.size());

        List<HomiesMenuItem> itemsForPage = paginatedContentItems.subList(startIndex, endIndex);

        int slotIndex = 0;
        for (int itemSlot : getPaginationSlots()) {
            if (slotIndex < itemsForPage.size()) {
                currentItems.put(itemSlot, itemsForPage.get(slotIndex));
            } else {
                // Fill empty slots if a filler item is provided
                HomiesMenuItem filler = getFillerItem();
                if (filler != null) {
                    currentItems.put(itemSlot, filler);
                }
            }
            slotIndex++;
        }

        // Subclasses can add more fixed items or override this method for custom layouts
        return currentItems;
    }

    /**
     * Subclasses must define the inventory slot for the next page button.
     * @return The integer slot for the next page button.
     */
    protected abstract int getNextPageButtonSlot();

    /**
     * Subclasses must define the inventory slot for the previous page button.
     * @return The integer slot for the previous page button.
     */
    protected abstract int getPreviousPageButtonSlot();
}
