package lib.homies.framework.spigot.world;

import lib.homies.framework.world.HomiesLocation;
import lib.homies.framework.world.HomiesWorld;
import org.bukkit.Location;

/**
 * Spigot-specific implementation of the {@link HomiesLocation} interface.
 * This class wraps a Bukkit {@link Location} object, providing a platform-specific
 * way to interact with locations while adhering to the platform-agnostic API.
 */
public class SpigotLocation implements HomiesLocation {
    private final Location location;

    /**
     * Constructs a new SpigotLocation wrapping a given Bukkit Location.
     * @param location The Bukkit {@link Location} to wrap.
     */
    public SpigotLocation(Location location) {
        this.location = location;
    }

    /**
     * Gets the underlying Bukkit {@link Location} object.
     * This method allows direct access to Bukkit's location functionality when needed
     * within the Spigot implementation module.
     * @return The wrapped Bukkit Location.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the {@link HomiesWorld} this location is in.
     * @return A {@link SpigotWorld} instance wrapping the Bukkit World of this location.
     */
    @Override
    public HomiesWorld getWorld() {
        return new SpigotWorld(location.getWorld());
    }

    /**
     * Gets the X coordinate of this location.
     * @return The X coordinate.
     */
    @Override
    public double getX() {
        return location.getX();
    }

    /**
     * Gets the Y coordinate of this location.
     * @return The Y coordinate.
     */
    @Override
    public double getY() {
        return location.getY();
    }

    /**
     * Gets the Z coordinate of this location.
     * @return The Z coordinate.
     */
    @Override
    public double getZ() {
        return location.getZ();
    }

    /**
     * Gets the yaw (horizontal rotation) of this location.
     * @return The yaw value.
     */
    @Override
    public float getYaw() {
        return location.getYaw();
    }

    /**
     * Gets the pitch (vertical rotation) of this location.
     * @return The pitch value.
     */
    @Override
    public float getPitch() {
        return location.getPitch();
    }

    @Override
    public <T> T getAs(Class<T> platformClass) {
        if (platformClass.isInstance(location)) {
            return platformClass.cast(location);
        }
        return null;
    }
}
