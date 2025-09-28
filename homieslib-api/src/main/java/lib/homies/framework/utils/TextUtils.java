package lib.homies.framework.utils;

/**
 * A platform-agnostic interface for utility methods related to text manipulation.
 * This service provides methods for colorizing and creating gradients in text.
 */
public interface TextUtils {
    /**
     * Colorizes a given string using platform-specific color codes (e.g., '&' codes for Minecraft).
     * @param text The input string with color codes.
     * @return The colorized string.
     */
    String colorize(String text);

    /**
     * Applies a gradient effect to a string between two specified colors.
     * The implementation will determine how the gradient is applied based on platform capabilities.
     * @param text The input string to apply the gradient to.
     * @param startColor The starting color (e.g., hex code, color name).
     * @param endColor The ending color (e.g., hex code, color name).
     * @return The string with the gradient effect applied.
     */
    String gradient(String text, String startColor, String endColor);
}
