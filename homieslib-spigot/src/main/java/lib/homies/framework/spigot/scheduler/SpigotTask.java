package lib.homies.framework.spigot.scheduler;

import lib.homies.framework.scheduler.HomiesTask;
import org.bukkit.scheduler.BukkitTask;

/**
 * Spigot-specific implementation of the {@link HomiesTask} interface.
 * This class wraps a Bukkit {@link BukkitTask} object, providing a platform-specific
 * way to manage scheduled tasks while adhering to the platform-agnostic API.
 */
public class SpigotTask implements HomiesTask {
    private final BukkitTask bukkitTask;

    /**
     * Constructs a new SpigotTask wrapping a given BukkitTask.
     * @param bukkitTask The Bukkit {@link BukkitTask} to wrap.
     */
    public SpigotTask(BukkitTask bukkitTask) {
        this.bukkitTask = bukkitTask;
    }

    /**
     * Cancels the underlying Bukkit {@link BukkitTask}.
     */
    @Override
    public void cancel() {
        bukkitTask.cancel();
    }
}
