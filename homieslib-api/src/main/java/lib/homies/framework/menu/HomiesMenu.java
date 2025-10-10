package lib.homies.framework.menu;

import lib.homies.framework.player.HomiesPlayer;

import java.util.Map;

/**
 * A platform-agnostic interface representing a menu (e.g., an inventory GUI).
 * Implementations define the menu's structure, title, size, and interaction logic.
 */
public interface HomiesMenu {
    /**
     * Gets the title of the menu, which may be dynamic based on the player.
     * @param player The {@link HomiesPlayer} viewing the menu.
     * @return The title of the menu (color codes supported).
     */
    String getTitle(HomiesPlayer player);

    /**
     * Gets the size of the menu's inventory (must be a multiple of 9).
     * @return The size of the menu.
     */
    int getSize();

    /**
     * Gets the map of menu items for the current state of the menu.
     * The keys represent inventory slots, and values are the {@link HomiesMenuItem}s.
     * @param player The {@link HomiesPlayer} viewing the menu.
     * @return A map of slot to {@link HomiesMenuItem}.
     */
    Map<Integer, HomiesMenuItem> getItems(HomiesPlayer player);

    /**
     * Handles a click event within the menu.
     * @param player The {@link HomiesPlayer} who clicked.
     * @param slot The inventory slot that was clicked.
     */
    void handleClick(HomiesPlayer player, int slot);

    /**
     * Called when the menu is opened for a player.
     * @param player The {@link HomiesPlayer} for whom the menu was opened.
     */
    void handleOpen(HomiesPlayer player);

    /**
     * Called when the menu is closed for a player.
     * @param player The {@link HomiesPlayer} for whom the menu was closed.
     */
    void handleClose(HomiesPlayer player);

    /**
     * Returns the delay in ticks between placing each item during the menu opening animation.
     * A value of 0 or less indicates no animation.
     * @return The animation delay in ticks.
     */
    default long getAnimationDelayTicks() {
        return 0; // Default to no animation
    }

    /**
     * Returns the sound to play when an item is placed during the menu opening animation.
     * @return The name of the sound (platform-specific), or null if no sound is desired.
     */
    default String getAnimationSound() {
        return null; // Default to no sound
    }

    /**
     * Returns the delay in ticks between removing each item during the menu closing animation.
     * A value of 0 or less indicates no animation.
     * @return The animation out delay in ticks.
     */
    default long getAnimationOutDelayTicks() {
        return 0; // Default to no animation
    }

    /**
     * Called to signal the menu to refresh its contents, typically after a configuration reload.
     * Implementations should re-read necessary configuration and rebuild their internal state.
     */
    void refresh();
}
