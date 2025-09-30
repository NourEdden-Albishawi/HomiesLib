package lib.homies.framework.texture;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define a custom texture for an item.
 * This should be applied to fields, typically in an enum, to associate a texture with a CustomModelData value.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CustomTexture {
    /**
     * The base Material for the item (e.g., "PAPER").
     * This should match the Bukkit Material enum name.
     * @return The material name.
     */
    String material();

    /**
     * The CustomModelData value.
     * This integer corresponds to a model in the server's resource pack.
     * @return The CustomModelData value.
     */
    int value();
}
