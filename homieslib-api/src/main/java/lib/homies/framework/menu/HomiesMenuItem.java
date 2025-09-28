package lib.homies.framework.menu;

import lib.homies.framework.player.HomiesPlayer;
import lib.homies.framework.utils.HomiesItemStack;

import java.util.function.Consumer;

/**
 * A platform-agnostic interface representing an item within a {@link HomiesMenu}.
 * It defines the visual representation and the action to be performed when clicked.
 */
public interface HomiesMenuItem {
    /**
     * Gets the {@link HomiesItemStack} that represents this menu item visually.
     * The item stack can be dynamic based on the player viewing the menu.
     * @param player The {@link HomiesPlayer} viewing the menu.
     * @return The {@link HomiesItemStack} for this menu item.
     */
    HomiesItemStack getItemStack(HomiesPlayer player);

    /**
     * Gets the action to be performed when this menu item is clicked.
     * @return A {@link Consumer} that accepts the {@link HomiesPlayer} who clicked the item,
     *         or {@code null} if no action is associated with this item.
     */
    Consumer<HomiesPlayer> getAction();
}
