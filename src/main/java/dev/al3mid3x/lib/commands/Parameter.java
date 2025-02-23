package dev.al3mid3x.lib.commands;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Parameter {
    String value();
    boolean requiresPlayer() default false;
    int minArgs() default 0;  // Minimum number of arguments required
    int maxArgs() default Integer.MAX_VALUE;  // Maximum number of arguments allowed
}