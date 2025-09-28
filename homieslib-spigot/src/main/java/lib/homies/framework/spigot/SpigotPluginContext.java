package lib.homies.framework.spigot;

import lib.homies.framework.PluginContext;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;

/**
 * Spigot-specific implementation of the {@link PluginContext} interface.
 * This class wraps a Bukkit {@link Plugin} instance to provide platform-specific
 * access to plugin resources in a platform-agnostic manner.
 */
public class SpigotPluginContext implements PluginContext {

    private final Plugin plugin;

    public SpigotPluginContext(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return plugin.getName();
    }

    @Override
    public File getDataFolder() {
        return plugin.getDataFolder();
    }

    @Override
    public InputStream getResource(String filename) {
        return plugin.getResource(filename);
    }

    @Override
    public void saveResource(String resourcePath, boolean replace) {
        plugin.saveResource(resourcePath, replace);
    }
}
