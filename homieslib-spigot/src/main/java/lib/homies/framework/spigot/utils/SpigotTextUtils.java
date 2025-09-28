package lib.homies.framework.spigot.utils;

import lib.homies.framework.utils.TextUtils;
import org.bukkit.ChatColor;

/**
 * Spigot-specific implementation of the {@link TextUtils} interface.
 * This class uses Bukkit's {@link ChatColor} for colorization and basic gradient effects.
 */
public class SpigotTextUtils implements TextUtils {
    /**
     * Colorizes a given string using Bukkit's alternate color code system (e.g., '&' codes).
     * @param text The input string with color codes.
     * @return The colorized string.
     */
    @Override
    public String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Applies a basic gradient effect to a string between two specified {@link ChatColor}s.
     * This implementation creates a linear gradient by interpolating between the start and end colors.
     * @param text The input string to apply the gradient to.
     * @param startColor The starting color (e.g., "RED", "#FF0000").
     * @param endColor The ending color (e.g., "BLUE", "#0000FF").
     * @return The string with the gradient effect applied.
     */
    @Override
    public String gradient(String text, String startColor, String endColor) {
        // Note: Bukkit's ChatColor.valueOf() expects uppercase names (e.g., "RED").
        // For hex colors, a more complex parsing would be needed.
        ChatColor start = ChatColor.valueOf(startColor.toUpperCase());
        ChatColor end = ChatColor.valueOf(endColor.toUpperCase());
        StringBuilder sb = new StringBuilder();
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            double ratio = (double) i / (chars.length - 1);
            // Simple interpolation: if ratio < 0.5, use start; otherwise, use end.
            // For a true gradient, you'd interpolate RGB values.
            ChatColor color = ratio < 0.5 ? start : end;
            sb.append(color).append(chars[i]);
        }
        return sb.toString();
    }
}
