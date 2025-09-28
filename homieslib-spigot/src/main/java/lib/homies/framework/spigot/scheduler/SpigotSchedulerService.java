package lib.homies.framework.spigot.scheduler;

import lib.homies.framework.scheduler.HomiesTask;
import lib.homies.framework.scheduler.SchedulerService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spigot-specific implementation of the {@link SchedulerService} interface.
 * This class leverages Bukkit's {@link BukkitScheduler} to manage and execute tasks
 * synchronously and asynchronously, with support for delays and repetitions.
 */
public class SpigotSchedulerService implements SchedulerService {

    private final Plugin plugin;
    private final BukkitScheduler scheduler = Bukkit.getScheduler();
    private final Map<String, List<HomiesTask>> taskGroups = new ConcurrentHashMap<>();

    /**
     * Constructs a new SpigotSchedulerService.
     * @param plugin The {@link Plugin} instance of the framework, used for scheduling tasks.
     */
    public SpigotSchedulerService(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs a task once after a specified delay on the main server thread.
     * @param task The {@link Runnable} to execute.
     * @param delay The delay before the task runs, in server ticks (20 ticks = 1 second).
     * @return A {@link HomiesTask} representing the scheduled task.
     */
    @Override
    public HomiesTask runLater(Runnable task, long delay) {
        return new SpigotTask(scheduler.runTaskLater(plugin, task, delay));
    }

    /**
     * Runs a task repeatedly on the main server thread after an initial delay.
     * @param task The {@link Runnable} to execute.
     * @param delay The initial delay before the first execution, in server ticks.
     * @param period The period between subsequent executions, in server ticks.
     * @return A {@link HomiesTask} representing the scheduled task.
     */
    @Override
    public HomiesTask runRepeating(Runnable task, long delay, long period) {
        return new SpigotTask(scheduler.runTaskTimer(plugin, task, delay, period));
    }

    /**
     * Runs a task asynchronously (off the main server thread) immediately.
     * @param task The {@link Runnable} to execute.
     * @return A {@link HomiesTask} representing the scheduled task.
     */
    @Override
    public HomiesTask runAsync(Runnable task) {
        return new SpigotTask(scheduler.runTaskAsynchronously(plugin, task));
    }

    /**
     * Adds a scheduled task to a named group. Tasks in a group can be cancelled together.
     * @param group The name of the task group.
     * @param task The {@link HomiesTask} to add to the group.
     */
    @Override
    public void addToGroup(String group, HomiesTask task) {
        taskGroups.computeIfAbsent(group, k -> new ArrayList<>()).add(task);
    }

    /**
     * Cancels all tasks belonging to a specific group.
     * @param group The name of the task group to cancel.
     */
    @Override
    public void cancelGroup(String group) {
        if (taskGroups.containsKey(group)) {
            taskGroups.get(group).forEach(HomiesTask::cancel);
            taskGroups.remove(group);
        }
    }

    /**
     * Cancels all tasks scheduled by the framework for the current plugin.
     */
    @Override
    public void cancelAll() {
        scheduler.cancelTasks(plugin);
        taskGroups.clear(); // Clear internal tracking as well
    }

    /**
     * Provides a builder for creating more complex scheduled tasks.
     * @return A {@link SchedulerTaskBuilder} instance.
     */
    @Override
    public SchedulerTaskBuilder task() {
        return new SpigotSchedulerTaskBuilder();
    }

    /**
     * Spigot-specific implementation of the {@link SchedulerTaskBuilder} interface.
     * This builder allows for constructing and scheduling tasks with various options
     * using Bukkit's scheduler.
     */
    private class SpigotSchedulerTaskBuilder implements SchedulerTaskBuilder {
        private Runnable runnable;
        private boolean async = false;
        private long delay = 0;
        private long period = -1;
        private String group = null;

        /**
         * Sets the {@link Runnable} to be executed by the task.
         * @param runnable The task logic.
         * @return The builder instance.
         */
        @Override
        public SchedulerTaskBuilder run(Runnable runnable) {
            this.runnable = runnable;
            return this;
        }

        /**
         * Configures the task to run asynchronously (off the main server thread).
         * @return The builder instance.
         */
        @Override
        public SchedulerTaskBuilder async() {
            this.async = true;
            return this;
        }

        /**
         * Sets an initial delay for the task before its first execution.
         * @param ticks The delay in server ticks.
         * @return The builder instance.
         */
        @Override
        public SchedulerTaskBuilder delay(long ticks) {
            this.delay = ticks;
            return this;
        }

        /**
         * Configures the task to repeat with a specified period.
         * @param ticks The period between executions in server ticks.
         * @return The builder instance.
         */
        @Override
        public SchedulerTaskBuilder repeat(long ticks) {
            this.period = ticks;
            return this;
        }

        /**
         * Assigns the task to a named group, allowing for group-based cancellation.
         * @param group The name of the task group.
         * @return The builder instance.
         */
        @Override
        public SchedulerTaskBuilder group(String group) {
            this.group = group;
            return this;
        }

        /**
         * Builds and starts the scheduled task using the configured options.
         * @return A {@link HomiesTask} representing the newly scheduled task.
         * @throws IllegalStateException if no runnable is provided.
         */
        @Override
        public HomiesTask start() {
            if (runnable == null) {
                throw new IllegalStateException("Runnable must be provided for the task.");
            }

            HomiesTask task;
            if (async) {
                if (period > 0) {
                    task = new SpigotTask(scheduler.runTaskTimerAsynchronously(plugin, runnable, delay, period));
                } else if (delay > 0) {
                    task = new SpigotTask(scheduler.runTaskLaterAsynchronously(plugin, runnable, delay));
                } else {
                    task = runAsync(runnable);
                }
            } else {
                if (period > 0) {
                    task = runRepeating(runnable, delay, period);
                } else {
                    task = runLater(runnable, delay);
                }
            }

            if (group != null) {
                addToGroup(group, task);
            }
            return task;
        }
    }
}
