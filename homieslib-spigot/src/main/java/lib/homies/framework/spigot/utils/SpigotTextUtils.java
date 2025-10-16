package lib.homies.framework.spigot.utils;

import lib.homies.framework.utils.TextUtils;
import net.md_5.bungee.api.ChatColor;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Spigot-specific implementation of the {@link TextUtils} interface.
 * This class uses Bukkit's {@link ChatColor} for colorization, including modern hex color support ({@code &#RRGGBB}).
 */
public class SpigotTextUtils implements TextUtils {

    // Pattern to find hex color codes in the format &#RRGGBB
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    /**
     * Colorizes a given string using both standard Bukkit color codes ({@code &}) and modern hex codes ({@code &#RRGGBB}).
     * @param text The input string with color codes.
     * @return The colorized string.
     */
    @Override
    public String colorize(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder buffer = new StringBuilder(text.length() + 4 * 8); // Pre-allocate buffer

        while (matcher.find()) {
            String hexCode = matcher.group(1);
            // Replace the matched hex code pattern with the BungeeCord API's ChatColor
            String bungeecordHex = ChatColor.of("#" + hexCode).toString();
            matcher.appendReplacement(buffer, bungeecordHex);
        }

        String processedHex = matcher.appendTail(buffer).toString();

        // Append the rest of the string and translate legacy color codes
        String finalColoredText = ChatColor.translateAlternateColorCodes('&', processedHex);

        return finalColoredText;
    }

    /**
     * Applies a true linear gradient effect to a string between two specified colors.
     * The colors can be provided as standard color names (e.g., "RED") or hex codes (e.g., "#FF0000").
     * @param text The input string to apply the gradient to.
     * @param startColor The starting color.
     * @param endColor The ending color.
     * @return The string with the gradient effect applied.
     */
    @Override
    public String gradient(String text, String startColor, String endColor) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        Color start = parseColor(startColor);
        Color end = parseColor(endColor);

        StringBuilder sb = new StringBuilder();
        char[] chars = text.toCharArray();
        int length = chars.length;

        for (int i = 0; i < length; i++) {
            double ratio = (length == 1) ? 0.0 : (double) i / (length - 1);

            int red = (int) (start.getRed() * (1 - ratio) + end.getRed() * ratio);
            int green = (int) (start.getGreen() * (1 - ratio) + end.getGreen() * ratio);
            int blue = (int) (start.getBlue() * (1 - ratio) + end.getBlue() * ratio);

            Color stepColor = new Color(red, green, blue);
            sb.append(ChatColor.of(stepColor)).append(chars[i]);
        }

        return sb.toString();
    }

    /**
     * Parses a color from a string, which can be a hex code or a standard ChatColor name.
     *
     * @param colorString The color string to parse.
     * @return The parsed {@link Color} object.
     */
    private Color parseColor(String colorString) {
        if (colorString.startsWith("#")) {
            try {
                return Color.decode(colorString);
            } catch (NumberFormatException e) {
                return Color.WHITE; // Default on invalid hex
            }
        }
        try {
            return ChatColor.valueOf(colorString.toUpperCase()).getColor();
        } catch (IllegalArgumentException e) {
            return Color.WHITE; // Default on invalid name
        }
    }
}
