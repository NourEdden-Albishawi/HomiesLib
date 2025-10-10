package lib.homies.framework.world;

public interface HomiesWorld {
    String getName();

    /**
     * Gets the underlying platform-specific world object.
     * This is an escape hatch to access platform-specific APIs.
     *
     * @param platformClass The class of the platform-specific world object (e.g., org.bukkit.World.class).
     * @param <T>           The type of the platform-specific world.
     * @return The platform-specific world object, or null if the type is incompatible.
     */
    <T> T getAs(Class<T> platformClass);
}
