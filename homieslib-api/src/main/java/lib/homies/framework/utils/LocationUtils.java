package lib.homies.framework.utils;

import lib.homies.framework.player.HomiesPlayer;
import lib.homies.framework.world.HomiesLocation;

/**
 * A platform-agnostic interface for utility methods related to locations.
 * This service provides methods for serializing, deserializing, and checking properties of locations.
 */
public interface LocationUtils {
    /**
     * Serializes a {@link HomiesLocation} object into a string representation.
     * This string can be stored and later deserialized to reconstruct the location.
     * @param location The {@link HomiesLocation} to serialize.
     * @return A string representation of the location.
     */
    String serialize(HomiesLocation location);

    /**
     * Deserializes a string representation back into a {@link HomiesLocation} object.
     * @param data The string data representing the location.
     * @return A {@link HomiesLocation} object, or {@code null} if deserialization fails.
     */
    HomiesLocation deserialize(String data);

    /**
     * Checks if a given {@link HomiesLocation} is considered "safe" (e.g., not inside a block).
     * The definition of "safe" is platform-dependent.
     * @param location The {@link HomiesLocation} to check.
     * @return {@code true} if the location is safe, {@code false} otherwise.
     */
    boolean isSafe(HomiesLocation location);

    /**
     * Teleports a {@link HomiesPlayer} to a {@link HomiesLocation} only if the location is safe.
     * If the location is not safe, the teleportation may be cancelled or a message sent to the player.
     * @param player The {@link HomiesPlayer} to teleport.
     * @param location The target {@link HomiesLocation}.
     */
    void safeTeleport(HomiesPlayer player, HomiesLocation location);
}
