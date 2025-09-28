package lib.homies.framework.scheduler;

import java.util.concurrent.TimeUnit;

/**
 * A platform-agnostic service for scheduling tasks.
 * This service allows framework developers to run tasks synchronously, asynchronously,
 * with delays, or repeatedly, abstracting away platform-specific scheduler APIs.
 */
public interface SchedulerService {

    /**
     * Runs a task once after a specified delay on the main server thread.
     * @param task The {@link Runnable} to execute.
     * @param delay The delay before the task runs, in server ticks (20 ticks = 1 second).
     * @return A {@link HomiesTask} representing the scheduled task.
     */
    HomiesTask runLater(Runnable task, long delay);

    /**
     * Runs a task repeatedly on the main server thread after an initial delay.
     * @param task The {@link Runnable} to execute.
     * @param delay The initial delay before the first execution, in server ticks.
     * @param period The period between subsequent executions, in server ticks.
     * @return A {@link HomiesTask} representing the scheduled task.
     */
    HomiesTask runRepeating(Runnable task, long delay, long period);

    /**
     * Runs a task asynchronously (off the main server thread) immediately.
     * @param task The {@link Runnable} to execute.
     * @return A {@link HomiesTask} representing the scheduled task.
     */
    HomiesTask runAsync(Runnable task);

    /**
     * Adds a scheduled task to a named group. Tasks in a group can be cancelled together.
     * @param group The name of the task group.
     * @param task The {@link HomiesTask} to add to the group.
     */
    void addToGroup(String group, HomiesTask task);

    /**
     * Cancels all tasks belonging to a specific group.
     * @param group The name of the task group to cancel.
     */
    void cancelGroup(String group);

    /**
     * Cancels all tasks scheduled by the framework for the current plugin.
     */
    void cancelAll();

    /**
     * Provides a builder for creating more complex scheduled tasks.
     * @return A {@link SchedulerTaskBuilder} instance.
     */
    SchedulerTaskBuilder task();

    /**
     * Builder interface for constructing and scheduling tasks with various options.
     */
    interface SchedulerTaskBuilder {
        /**
         * Sets the {@link Runnable} to be executed by the task.
         * @param runnable The task logic.
         * @return The builder instance.
         */
        SchedulerTaskBuilder run(Runnable runnable);

        /**
         * Configures the task to run asynchronously (off the main server thread).
         * @return The builder instance.
         */
        SchedulerTaskBuilder async();

        /**
         * Sets an initial delay for the task before its first execution.
         * @param ticks The delay in server ticks.
         * @return The builder instance.
         */
        SchedulerTaskBuilder delay(long ticks);

        /**
         * Configures the task to repeat with a specified period.
         * @param ticks The period between executions in server ticks.
         * @return The builder instance.
         */
        SchedulerTaskBuilder repeat(long ticks);

        /**
         * Assigns the task to a named group, allowing for group-based cancellation.
         * @param group The name of the task group.
         * @return The builder instance.
         */
        SchedulerTaskBuilder group(String group);

        /**
         * Builds and starts the scheduled task.
         * @return A {@link HomiesTask} representing the newly scheduled task.
         */
        HomiesTask start();
    }
}
