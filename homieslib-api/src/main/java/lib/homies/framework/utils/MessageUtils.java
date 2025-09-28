package lib.homies.framework.utils;

import lib.homies.framework.player.HomiesPlayer;

/**
 * A platform-agnostic interface for utility methods related to sending messages to players.
 * This service abstracts away platform-specific message sending mechanisms.
 */
public interface MessageUtils {
    /**
     * Sends a message to a specific player.
     * Color codes are typically supported and handled by the underlying platform implementation.
     * @param player The {@link HomiesPlayer} to send the message to.
     * @param message The message string to send.
     */
    void send(HomiesPlayer player, String message);

    /**
     * Broadcasts a message to all online players on the current platform.
     * Color codes are typically supported and handled by the underlying platform implementation.
     * @param message The message string to broadcast.
     */
    void broadcast(String message);

    /**
     * Sends an action bar message to a specific player.
     * If the platform does not support action bars, it may fall back to a regular chat message or be a no-op.
     * @param player The {@link HomiesPlayer} to send the action bar to.
     * @param message The action bar message string.
     */
    void actionBar(HomiesPlayer player, String message);

    /**
     * Sends a title and subtitle to a specific player.
     * If the platform does not support titles, it may fall back to regular chat messages or be a no-op.
     * @param player The {@link HomiesPlayer} to send the title to.
     * @param title The main title string.
     * @param subtitle The subtitle string.
     * @param fadeIn The fade-in time in ticks.
     * @param stay The stay time in ticks.
     * @param fadeOut The fade-out time in ticks.
     */
    void title(HomiesPlayer player, String title, String subtitle, int fadeIn, int stay, int fadeOut);

    /**
     * Replaces common placeholders in a message string for a given player.
     * Implementations should handle platform-specific placeholders (e.g., %player_name%).
     * @param player The {@link HomiesPlayer} for whom to replace placeholders.
     * @param message The message string containing placeholders.
     * @return The message string with placeholders replaced.
     */
    String replacePlaceholders(HomiesPlayer player, String message);
}
