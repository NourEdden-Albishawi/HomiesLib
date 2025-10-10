package lib.homies.framework.world;

public interface HomiesLocation {

    HomiesWorld getWorld();
    double getX();
    double getY();
    double getZ();
    float getYaw();
    float getPitch();

    /**
     * Gets the underlying platform-specific location object.
     * This is an escape hatch to access platform-specific APIs.
     *
     * @param platformClass The class of the platform-specific location object (e.g., org.bukkit.Location.class).
     * @param <T>           The type of the platform-specific location.
     * @return The platform-specific location object, or null if the type is incompatible.
     */
    <T> T getAs(Class<T> platformClass);
}
