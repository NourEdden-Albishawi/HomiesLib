package lib.homies.framework.spigot.utils;

import lib.homies.framework.player.HomiesPlayer;
import lib.homies.framework.spigot.player.SpigotPlayer;
import lib.homies.framework.utils.MessageUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

/**
 * Spigot-specific implementation of the {@link MessageUtils} interface.
 * This class uses Bukkit's API to send various types of messages to players,
 * including chat, action bars, and titles.
 */
public class SpigotMessageUtils implements MessageUtils {
    private final SpigotTextUtils textUtils = new SpigotTextUtils();

    /**
     * Sends a message to a specific player.
     * Color codes are translated using the internal {@link SpigotTextUtils}.
     * @param player The {@link HomiesPlayer} to send the message to. Must be a {@link SpigotPlayer}.
     * @param message The message string to send.
     */
    @Override
    public void send(HomiesPlayer player, String message) {
        if (player instanceof SpigotPlayer) {
            ((SpigotPlayer) player).getPlayer().sendMessage(textUtils.colorize(message));
        }
    }

    /**
     * Broadcasts a message to all online players on the Spigot server.
     * Color codes are translated using the internal {@link SpigotTextUtils}.
     * @param message The message string to broadcast.
     */
    @Override
    public void broadcast(String message) {
        Bukkit.broadcastMessage(textUtils.colorize(message));
    }

    /**
     * Sends an action bar message to a specific player.
     * Color codes are translated using the internal {@link SpigotTextUtils}.
     * @param player The {@link HomiesPlayer} to send the action bar to. Must be a {@link SpigotPlayer}.
     * @param message The action bar message string.
     */
    @Override
    public void actionBar(HomiesPlayer player, String message) {
        if (player instanceof SpigotPlayer) {
            ((SpigotPlayer) player).getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent(textUtils.colorize(message)));
        }
    }

    /**
     * Sends a title and subtitle to a specific player.
     * Color codes are translated using the internal {@link SpigotTextUtils}.
     * @param player The {@link HomiesPlayer} to send the title to. Must be a {@link SpigotPlayer}.
     * @param title The main title string.
     * @param subtitle The subtitle string.
     * @param fadeIn The fade-in time in ticks.
     * @param stay The stay time in ticks.
     * @param fadeOut The fade-out time in ticks.
     */
    @Override
    public void title(HomiesPlayer player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (player instanceof SpigotPlayer) {
            ((SpigotPlayer) player).getPlayer().sendTitle(textUtils.colorize(title), textUtils.colorize(subtitle), fadeIn, stay, fadeOut);
        }
    }

    /**
     * Replaces common placeholders in a message string for a given player.
     * Currently supports %player% and %player_name% with the player's name.
     * For more advanced placeholders (e.g., PlaceholderAPI), further integration would be needed.
     * @param player The {@link HomiesPlayer} for whom to replace placeholders.
     * @param message The message string containing placeholders.
     * @return The message string with placeholders replaced.
     */
    @Override
    public String replacePlaceholders(HomiesPlayer player, String message) {
        String processedMessage = message;
        if (player != null) {
            processedMessage = processedMessage.replace("%player%", player.getName())
                                               .replace("%player_name%", player.getName());
            // Add more basic placeholders here if needed
        }
        return textUtils.colorize(processedMessage); // Ensure color codes are processed after placeholders
    }
}
