package lib.homies.framework.world;

/**
 * A platform-agnostic representation of a location within a game world.
 * This interface abstracts away platform-specific location objects (e.g., Bukkit's Location).
 * The concrete implementation will be provided by a platform-specific module,
 * which can be unwrapped to get the underlying platform object if needed.
 */
public interface HomiesLocation {
    /**
     * Gets the {@link HomiesWorld} this location is in.
     * @return The world of this location.
     */
    HomiesWorld getWorld();

    /**
     * Gets the X coordinate of this location.
     * @return The X coordinate.
     */
    double getX();

    /**
     * Gets the Y coordinate of this location.
     * @return The Y coordinate.
     */
    double getY();

    /**
     * Gets the Z coordinate of this location.
     * @return The Z coordinate.
     */
    double getZ();

    /**
     * Gets the yaw (horizontal rotation) of this location.
     * @return The yaw value.
     */
    float getYaw();

    /**
     * Gets the pitch (vertical rotation) of this location.
     * @return The pitch value.
     */
    float getPitch();
}
