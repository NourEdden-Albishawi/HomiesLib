package lib.homies.framework.spigot.texture;

import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

/**
 * Manages retrieving custom textured items from the generated registry.
 */
public class CustomTextureManager {

    private static Method getTextureMethod;

    static {
        try {
            Class<?> registryClass = Class.forName("lib.homies.framework.spigot.texture.CustomTextureRegistry");
            getTextureMethod = registryClass.getMethod("getTexture", String.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            // This is not an error. It just means no custom textures were defined and processed.
        }
    }

    /**
     * Retrieves a custom textured ItemStack by its registered name.
     *
     * @param name The name of the texture (typically the enum constant name).
     * @return The ItemStack with the custom texture, or null if not found.
     */
    public static ItemStack getTexture(String name) {
        if (getTextureMethod == null) {
            return null;
        }
        try {
            return (ItemStack) getTextureMethod.invoke(null, name);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Retrieves a custom textured ItemStack using an enum constant.
     *
     * @param textureEnum The enum constant representing the texture.
     * @return The ItemStack with the custom texture, or null if not found.
     */
    public static ItemStack getTexture(Enum<?> textureEnum) {
        return getTexture(textureEnum.name());
    }
}
