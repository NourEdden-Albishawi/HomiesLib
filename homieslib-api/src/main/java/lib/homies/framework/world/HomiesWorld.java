package lib.homies.framework.world;

/**
 * A platform-agnostic interface representing a game world.
 * This abstracts away platform-specific world objects (e.g., Bukkit's World).
 * The concrete implementation will be provided by a platform-specific module,
 * which can be unwrapped to get the underlying platform object if needed.
 */
public interface HomiesWorld {
    /**
     * Gets the name of the world.
     * @return The world's name.
     */
    String getName();
}
