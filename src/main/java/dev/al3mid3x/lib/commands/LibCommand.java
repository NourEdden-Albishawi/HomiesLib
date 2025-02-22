package dev.al3mid3x.lib.entities;

import dev.al3mid3x.lib.annotations.Parameter;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class LibCommand implements CommandExecutor {

    private final Map<String, Method> methods = new HashMap<>();
    @Getter
    private CommandSender commandSender;

    public LibCommand() {
        findMethods();
    }

    private void findMethods() {
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Parameter.class)) {
                Parameter parameter = method.getDeclaredAnnotation(Parameter.class);
                methods.put(parameter.value(), method);
            }
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        this.commandSender = commandSender;

        if (args.length == 0) {
            commandSender.sendMessage("Usage: /" + label + " <" + String.join("|", methods.keySet()) + ">");
            return true;
        }

        String param = args[0].toLowerCase();
        Method method = methods.get(param);

        if (method == null) {
            commandSender.sendMessage("Unknown command. Usage: /" + label + " <" + String.join("|", methods.keySet()) + ">");
            return true;
        }

        Parameter parameter = method.getDeclaredAnnotation(Parameter.class);

        if (parameter.requiresPlayer() && !(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can execute this command!");
            return false;
        }

        int numArgs = args.length - 1;  // Subtract 1 to exclude the command itself
        if (numArgs < parameter.minArgs() || numArgs > parameter.maxArgs()) {
            commandSender.sendMessage("Invalid number of arguments. Expected between " + parameter.minArgs() + " and " + parameter.maxArgs() + ".");
            return false;
        }

        try {
            // Pass the arguments to the method (excluding the command itself)
            method.invoke(this, (Object[]) Arrays.copyOfRange(args, 1, args.length));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}