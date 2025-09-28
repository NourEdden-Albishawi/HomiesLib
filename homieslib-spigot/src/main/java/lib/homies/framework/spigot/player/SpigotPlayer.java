package lib.homies.framework.spigot.player;

import lib.homies.framework.player.HomiesPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Spigot-specific implementation of the {@link HomiesPlayer} interface.
 * This class wraps a Bukkit {@link Player} object, providing a platform-specific
 * way to interact with players while adhering to the platform-agnostic API.
 */
public class SpigotPlayer implements HomiesPlayer {
    private final Player player;

    /**
     * Constructs a new SpigotPlayer wrapping a given Bukkit Player.
     * @param player The Bukkit {@link Player} to wrap.
     */
    public SpigotPlayer(Player player) {
        this.player = player;
    }

    /**
     * Gets the underlying Bukkit {@link Player} object.
     * This method allows direct access to Bukkit's player functionality when needed
     * within the Spigot implementation module.
     * @return The wrapped Bukkit Player.
     */
    public Player getPlayer() {
        return player;
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }

    @Override
    public void setHealth(double health) {
        player.setHealth(health);
    }

    @Override
    public double getMaxHealth() {
        return player.getMaxHealth();
    }

    @Override
    public <T> T getAs(Class<T> platformClass) {
        if (platformClass.isInstance(player)) {
            return platformClass.cast(player);
        }
        return null;
    }
}
