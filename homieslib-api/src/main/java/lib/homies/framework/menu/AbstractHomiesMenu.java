package lib.homies.framework.menu;

import lib.homies.framework.player.HomiesPlayer;

import java.util.Map;

/**
 * An abstract base class for implementing {@link HomiesMenu}.
 * This class provides default empty implementations for event handlers ({@link #handleClick(HomiesPlayer, int)}, {@link #handleOpen(HomiesPlayer)}, {@link #handleClose(HomiesPlayer)}),
 * allowing subclasses to override only the methods they need.
 * Subclasses are required to implement {@link #getTitle(HomiesPlayer)}, {@link #getSize()}, and {@link #getItems(HomiesPlayer)}.
 */
public abstract class AbstractHomiesMenu implements HomiesMenu {

    /**
     * Default empty implementation. Subclasses can override to add custom click handling logic.
     * @param player The {@link HomiesPlayer} who clicked.
     * @param slot The inventory slot that was clicked.
     */
    @Override
    public void handleClick(HomiesPlayer player, int slot) {
        // Default: Do nothing, subclasses can override
    }

    /**
     * Default empty implementation. Subclasses can override to add custom logic when the menu is opened.
     * @param player The {@link HomiesPlayer} for whom the menu was opened.
     */
    @Override
    public void handleOpen(HomiesPlayer player) {
        // Default: Do nothing, subclasses can override
    }

    /**
     * Default empty implementation. Subclasses can override to add custom logic when the menu is closed.
     * @param player The {@link HomiesPlayer} for whom the menu was closed.
     */
    @Override
    public void handleClose(HomiesPlayer player) {
        // Default: Do nothing, subclasses can override
    }

    // Subclasses must implement getTitle, getSize, and getItems
    @Override
    public abstract String getTitle(HomiesPlayer player);

    @Override
    public abstract int getSize();

    @Override
    public abstract Map<Integer, HomiesMenuItem> getItems(HomiesPlayer player);
}
