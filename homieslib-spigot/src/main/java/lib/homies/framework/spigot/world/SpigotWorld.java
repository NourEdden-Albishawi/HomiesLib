package lib.homies.framework.spigot.world;

import lib.homies.framework.world.HomiesWorld;
import org.bukkit.World;

/**
 * Spigot-specific implementation of the {@link HomiesWorld} interface.
 * This class wraps a Bukkit {@link World} object, providing a platform-specific
 * way to interact with game worlds while adhering to the platform-agnostic API.
 */
public class SpigotWorld implements HomiesWorld {
    private final World world;

    /**
     * Constructs a new SpigotWorld wrapping a given Bukkit World.
     * @param world The Bukkit {@link World} to wrap.
     */
    public SpigotWorld(World world) {
        this.world = world;
    }

    /**
     * Gets the underlying Bukkit {@link World} object.
     * This method allows direct access to Bukkit's world functionality when needed
     * within the Spigot implementation module.
     * @return The wrapped Bukkit World.
     */
    public World getWorld() {
        return world;
    }

    /**
     * Gets the name of the wrapped Bukkit World.
     * @return The world's name.
     */
    @Override
    public String getName() {
        return world.getName();
    }
}
