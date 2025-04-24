package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class App {
    private static final Logger log = LogManager.getLogger();

    private static final int LOCKS_NUMBER = 5;

    private static final RandomLockProvider lockProvider = new RandomLockProvider(LOCKS_NUMBER);

    public static void main(String[] args) {
        List<LocksAcquiringTask> tasks = initializeTasks();
        if (tasks.isEmpty()) return;

        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(tasks.size());
        for (LocksAcquiringTask task : tasks) {
            log.debug("starting new task with params: {}", task.toString());
            executor.scheduleAtFixedRate(task, 0, task.getPeriodTime(), TimeUnit.SECONDS);
        }
    }

    private static List<LocksAcquiringTask> initializeTasks() {
        List<LocksAcquiringTask> tasks = new ArrayList<>();
        try (Scanner scanner = new Scanner(App.class.getClassLoader().getResourceAsStream("threads_params.txt"))) {
            while (scanner.hasNextInt()) {
                int minWaitTime = scanner.nextInt();
                int maxWaitTime = scanner.nextInt();
                int periodTime = scanner.nextInt();
                int locksToAcquire = scanner.nextInt();
                tasks.add(new LocksAcquiringTask(minWaitTime, maxWaitTime, periodTime, locksToAcquire, lockProvider));
            }
        } catch (Exception e) {
            tasks.clear();
            log.error("couldn't properly initialize tasks", e);
        }

        return tasks;
    }
}
