package lib.homies.framework.menu;

import lib.homies.framework.player.HomiesPlayer;

/**
 * A platform-agnostic interface for managing menus.
 * This service allows framework developers to open, close, and register custom menus.
 */
public interface MenuManager {
    /**
     * Opens a specific menu for a given player.
     * @param player The {@link HomiesPlayer} for whom to open the menu.
     * @param menu The {@link HomiesMenu} instance to open.
     */
    void openMenu(HomiesPlayer player, HomiesMenu menu);

    /**
     * Closes the currently open menu for a given player.
     * If the player does not have a menu open, this method does nothing.
     * @param player The {@link HomiesPlayer} for whom to close the menu.
     */
    void closeMenu(HomiesPlayer player);

    /**
     * Registers a menu with a unique ID, allowing it to be retrieved later.
     * This is useful for menus that are accessed by ID (e.g., via commands).
     * @param id A unique string identifier for the menu.
     * @param menu The {@link HomiesMenu} instance to register.
     */
    void registerMenu(String id, HomiesMenu menu);

    /**
     * Retrieves a registered menu by its unique ID.
     * @param id The unique string identifier of the menu.
     * @return The {@link HomiesMenu} instance if found, otherwise {@code null}.
     */
    HomiesMenu getMenu(String id);
}
