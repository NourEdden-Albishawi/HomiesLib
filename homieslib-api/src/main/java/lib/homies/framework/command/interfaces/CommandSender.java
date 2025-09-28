package lib.homies.framework.command.interfaces;

import java.util.UUID;

/**
 * A platform-agnostic interface representing a command sender.
 * This abstracts away platform-specific sender types (e.g., Bukkit's CommandSender, BungeeCord's CommandSender).
 */
public interface CommandSender {
    /**
     * Sends a message to the command sender.
     * @param message The message to send.
     */
    void sendMessage(String message);

    /**
     * Checks if the command sender has a specific permission.
     * @param permission The permission node to check.
     * @return true if the sender has the permission, false otherwise.
     */
    boolean hasPermission(String permission);

    /**
     * Gets the name of the command sender.
     * @return The sender's name.
     */
    String getName();

    /**
     * Gets the unique ID of the command sender.
     * For console senders, this might return a consistent dummy UUID.
     * @return The sender's unique ID.
     */
    UUID getUniqueId();
}
