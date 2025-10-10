package lib.homies.framework.spigot.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.logging.Level;

public class SpigotEventManager {

    private final Plugin mainPlugin;

    public SpigotEventManager(Plugin mainPlugin) {
        this.mainPlugin = mainPlugin;
    }

    public void registerAllListeners() {
        String registryPath = "META-INF/homieslib/event-listeners.list";
        mainPlugin.getLogger().info("Discovering and registering event listeners from all plugins...");

        int totalRegisteredCount = 0;

        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getClass().getClassLoader() == null) {
                continue;
            }

            try {
                Enumeration<URL> urls = plugin.getClass().getClassLoader().getResources(registryPath);
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    try (InputStream is = url.openStream();
                         BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

                        String line;
                        while ((line = reader.readLine()) != null) {
                            String className = line.trim();
                            if (className.isEmpty()) {
                                continue;
                            }

                            try {
                                Class<?> listenerClass = Class.forName(className, true, plugin.getClass().getClassLoader());
                                if (Listener.class.isAssignableFrom(listenerClass)) {
                                    Listener listener = (Listener) listenerClass.getDeclaredConstructor().newInstance();
                                    Bukkit.getPluginManager().registerEvents(listener, plugin);
                                    totalRegisteredCount++;
                                } else {
                                    mainPlugin.getLogger().warning("Class " + className + " from plugin " + plugin.getName() + " is annotated with @EventListener but does not implement org.bukkit.event.Listener.");
                                }
                            } catch (Exception e) {
                                mainPlugin.getLogger().log(Level.SEVERE, "Failed to register event listener " + className + " from plugin " + plugin.getName(), e);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                mainPlugin.getLogger().log(Level.WARNING, "Could not read listener registry for plugin " + plugin.getName(), e);
            }
        }

        if (totalRegisteredCount > 0) {
            mainPlugin.getLogger().info("Successfully registered " + totalRegisteredCount + " event listeners from all plugins.");
        } else {
            mainPlugin.getLogger().info("No external event listeners found for automatic registration.");
        }
    }
}
