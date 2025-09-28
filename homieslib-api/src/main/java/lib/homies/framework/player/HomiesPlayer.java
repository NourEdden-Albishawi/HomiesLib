package lib.homies.framework.player;

import java.util.UUID;

public interface HomiesPlayer {

    String getName();

    UUID getUniqueId();

    void sendMessage(String message);

    boolean hasPermission(String permission);

    void setHealth(double health);

    double getMaxHealth();

    /**
     * Gets the underlying platform-specific player object.
     * This is an escape hatch to access platform-specific APIs.
     *
     * @param platformClass The class of the platform-specific player object (e.g., org.bukkit.entity.Player.class).
     * @param <T> The type of the platform-specific player.
     * @return The platform-specific player object, or null if the type is incompatible.
     */
    <T> T getAs(Class<T> platformClass);
}
