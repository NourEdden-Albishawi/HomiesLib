package lib.homies.framework.scheduler;

/**
 * A platform-agnostic representation of a scheduled task.
 * This interface allows for controlling (e.g., cancelling) a task scheduled by the {@link SchedulerService}.
 */
public interface HomiesTask {
    /**
     * Cancels this scheduled task.
     * If the task is currently running, it will be interrupted or allowed to finish its current execution,
     * depending on the underlying platform's scheduler implementation.
     */
    void cancel();
}
