package dev.al3mid3x.lib.tasks;

import dev.al3mid3x.lib.utils.UnitOfWork;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WriteBehindTask extends BukkitRunnable {
    private final UnitOfWork unitOfWork;
    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();

    public WriteBehindTask(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    public void addTask(Runnable task) {
        taskQueue.add(task);
    }

    @Override
    public void run() {
        while (!taskQueue.isEmpty()) {
            Runnable task = taskQueue.poll();
            if (task != null) {
                task.run();
            }
        }
    }
}