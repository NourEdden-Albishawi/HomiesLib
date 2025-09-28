package lib.homies.framework.menu;

import lib.homies.framework.player.HomiesPlayer;
import lib.homies.framework.utils.HomiesItemStack;

import java.util.function.Consumer;

/**
 * A simple, concrete implementation of {@link HomiesMenuItem}.
 * This class allows for easy creation of menu items with a specified {@link HomiesItemStack}
 * and an optional action to be performed when the item is clicked.
 */
public class SimpleHomiesMenuItem implements HomiesMenuItem {
    private final HomiesItemStack itemStack;
    private final Consumer<HomiesPlayer> action;

    /**
     * Constructs a new SimpleHomiesMenuItem with a visual item stack and no associated action.
     * @param itemStack The {@link HomiesItemStack} to display for this menu item.
     */
    public SimpleHomiesMenuItem(HomiesItemStack itemStack) {
        this(itemStack, null);
    }

    /**
     * Constructs a new SimpleHomiesMenuItem with a visual item stack and an action.
     * @param itemStack The {@link HomiesItemStack} to display for this menu item.
     * @param action A {@link Consumer} that accepts the {@link HomiesPlayer} who clicked the item.
     *               This action will be executed when the item is clicked.
     */
    public SimpleHomiesMenuItem(HomiesItemStack itemStack, Consumer<HomiesPlayer> action) {
        this.itemStack = itemStack;
        this.action = action;
    }

    /**
     * Gets the {@link HomiesItemStack} that represents this menu item visually.
     * @param player The {@link HomiesPlayer} viewing the menu (can be used for dynamic items).
     * @return The {@link HomiesItemStack} for this menu item.
     */
    @Override
    public HomiesItemStack getItemStack(HomiesPlayer player) {
        return itemStack;
    }

    /**
     * Gets the action to be performed when this menu item is clicked.
     * @return A {@link Consumer} that accepts the {@link HomiesPlayer} who clicked the item,
     *         or {@code null} if no action is associated with this item.
     */
    @Override
    public Consumer<HomiesPlayer> getAction() {
        return action;
    }
}
