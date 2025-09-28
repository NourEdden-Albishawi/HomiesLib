package lib.homies.framework.utils;

/**
 * A platform-agnostic interface for building {@link HomiesItemStack}s.
 * This builder provides a fluent API to construct items with various properties.
 */
public interface ItemBuilder {
    /**
     * Sets the amount of the item stack.
     * @param amount The number of items in the stack.
     * @return The ItemBuilder instance for chaining.
     */
    ItemBuilder amount(int amount);

    /**
     * Sets the display name of the item.
     * Color codes are typically supported and handled by the underlying platform implementation.
     * @param displayName The display name of the item.
     * @return The ItemBuilder instance for chaining.
     */
    ItemBuilder name(String displayName);

    /**
     * Sets the lore (description lines) of the item.
     * Color codes are typically supported and handled by the underlying platform implementation.
     * @param lines An array of strings representing the lore lines.
     * @return The ItemBuilder instance for chaining.
     */
    ItemBuilder lore(String... lines);

    /**
     * Adds an enchantment to the item.
     * @param enchantment The name of the enchantment (e.g., "SHARPNESS", "PROTECTION").
     * @param level The level of the enchantment.
     * @return The ItemBuilder instance for chaining.
     */
    ItemBuilder enchant(String enchantment, int level);

    /**
     * Adds item flags to hide certain attributes (e.g., enchantments, attributes).
     * @param flags An array of strings representing the item flags (e.g., "HIDE_ENCHANTS", "HIDE_ATTRIBUTES").
     * @return The ItemBuilder instance for chaining.
     */
    ItemBuilder flag(String... flags);

    /**
     * Sets the player profile for skull items.
     * This is typically used for player heads to display a specific player's skin.
     * @param profile The {@link HomiesPlayerProfile} to set for the skull.
     * @return The ItemBuilder instance for chaining.
     */
    ItemBuilder setPlayerProfile(HomiesPlayerProfile profile);

    /**
     * Builds and returns the constructed {@link HomiesItemStack}.
     * @return The final {@link HomiesItemStack}.
     */
    HomiesItemStack build();
}
