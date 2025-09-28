package lib.homies.framework.spigot.utils;

import lib.homies.framework.player.HomiesPlayer;
import lib.homies.framework.spigot.player.SpigotPlayer;
import lib.homies.framework.spigot.world.SpigotLocation;
import lib.homies.framework.utils.LocationUtils;
import lib.homies.framework.world.HomiesLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

/**
 * Spigot-specific implementation of the {@link LocationUtils} interface.
 * This class provides utility methods for serializing, deserializing, and checking properties of locations
 * using Bukkit's {@link Location} and {@link World} objects.
 */
public class SpigotLocationUtils implements LocationUtils {
    /**
     * Serializes a {@link HomiesLocation} object into a string representation.
     * The location must be a {@link SpigotLocation} to access the underlying Bukkit {@link Location}.
     * The format is "worldName;x;y;z;yaw;pitch".
     * @param location The {@link HomiesLocation} to serialize.
     * @return A string representation of the location, or {@code null} if the location is not a {@link SpigotLocation} or its world is null.
     */
    @Override
    public String serialize(HomiesLocation location) {
        if (location instanceof SpigotLocation) {
            Location bukkitLoc = ((SpigotLocation) location).getLocation();
            if (bukkitLoc.getWorld() == null) return null;
            return bukkitLoc.getWorld().getName() + ";" + bukkitLoc.getX() + ";" + bukkitLoc.getY() + ";" +
                    bukkitLoc.getZ() + ";" + bukkitLoc.getYaw() + ";" + bukkitLoc.getPitch();
        }
        return null;
    }

    /**
     * Deserializes a string representation back into a {@link HomiesLocation} object.
     * The string is expected to be in the format "worldName;x;y;z;yaw;pitch".
     * @param data The string data representing the location.
     * @return A {@link SpigotLocation} object, or {@code null} if deserialization fails (e.g., invalid format, world not found).
     */
    @Override
    public HomiesLocation deserialize(String data) {
        String[] parts = data.split(";");
        if (parts.length != 6) return null; // Ensure correct number of parts

        World world = Bukkit.getWorld(parts[0]);
        if (world == null) return null;
        try {
            Location bukkitLoc = new Location(world,
                    Double.parseDouble(parts[1]),
                    Double.parseDouble(parts[2]),
                    Double.parseDouble(parts[3]),
                    Float.parseFloat(parts[4]),
                    Float.parseFloat(parts[5]));
            return new SpigotLocation(bukkitLoc);
        } catch (NumberFormatException e) {
            // Log error or handle invalid number format
            return null;
        }
    }

    /**
     * Checks if a given {@link HomiesLocation} is considered "safe" for a player to teleport to.
     * A location is considered safe if the block at the location and the block above it are {@link Material#AIR}.
     * @param location The {@link HomiesLocation} to check. Must be a {@link SpigotLocation}.
     * @return {@code true} if the location is safe, {@code false} otherwise.
     */
    @Override
    public boolean isSafe(HomiesLocation location) {
        if (location instanceof SpigotLocation) {
            Location bukkitLoc = ((SpigotLocation) location).getLocation();
            // Check if the block at the location and the block above it are air
            return bukkitLoc.getBlock().getType() == Material.AIR &&
                    bukkitLoc.clone().add(0, 1, 0).getBlock().getType() == Material.AIR;
        }
        return false;
    }

    /**
     * Teleports a {@link HomiesPlayer} to a {@link HomiesLocation} only if the location is safe.
     * If the location is not safe, the teleportation is not performed.
     * @param player The {@link HomiesPlayer} to teleport. Must be a {@link SpigotPlayer}.
     * @param location The target {@link HomiesLocation}. Must be a {@link SpigotLocation}.
     */
    @Override
    public void safeTeleport(HomiesPlayer player, HomiesLocation location) {
        if (player instanceof SpigotPlayer && location instanceof SpigotLocation) {
            Location bukkitLoc = ((SpigotLocation) location).getLocation();
            if (isSafe(location)) {
                ((SpigotPlayer) player).getPlayer().teleport(bukkitLoc);
            }
        }
    }
}
