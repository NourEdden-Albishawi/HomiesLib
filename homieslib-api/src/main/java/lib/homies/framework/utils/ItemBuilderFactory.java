package lib.homies.framework.utils;

/**
 * A functional interface for creating {@link ItemBuilder} instances.
 * This factory abstracts the creation of platform-specific ItemBuilder implementations.
 */
@FunctionalInterface
public interface ItemBuilderFactory {
    /**
     * Creates a new {@link ItemBuilder} instance for a given material.
     * @param material The name of the material (e.g., "STONE", "DIAMOND_SWORD").
     * @return A new {@link ItemBuilder} instance.
     */
    ItemBuilder create(String material);
}
