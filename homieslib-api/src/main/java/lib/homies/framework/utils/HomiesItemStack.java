package lib.homies.framework.utils;

/**
 * A platform-agnostic representation of an item stack.
 * This interface serves as an abstraction for in-game items,
 * allowing framework code to manipulate items without direct dependency
 * on platform-specific classes (e.g., Bukkit's ItemStack).
 * The concrete implementation will be provided by a platform-specific module,
 * which can be unwrapped to get the underlying platform object if needed.
 */
public interface HomiesItemStack {
}
