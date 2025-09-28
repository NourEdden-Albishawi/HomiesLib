package lib.homies.framework.command.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(Completers.class)
public @interface Completer {
    String value();
}