package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Hello world!
 */
public class App {
    private static final Logger log = LogManager.getLogger();

    private static final RandomLockProvider lockProvider = new RandomLockProvider();

    public static void main(String[] args) throws InterruptedException {
        log.info("Hello World");
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2); // todo try with resources?
        executor.scheduleAtFixedRate(new LocksAcquiringTask(), 0, 10, TimeUnit.SECONDS);
        log.info("started a new task");
        executor.scheduleAtFixedRate(new LocksAcquiringTask(), 0, 5, TimeUnit.SECONDS);
        log.info("started a new task");

        while (true) {
            log.info("{} in queue", executor.getQueue().size());
            Thread.sleep(Duration.ofSeconds(10));
        }
    }


    private static class LocksAcquiringTask implements Runnable {
        Random r = new Random();

        @Override
        public void run() {
            log.info("acquiring locks");
            List<Lock> lockedLocks = lockProvider.lock(2);
            log.info("{} locks acquired", lockedLocks.size());

            int waitInterval = r.nextInt(3, 12);
            log.info("waiting for {} secs", waitInterval);
            
            try {
                Thread.sleep(Duration.ofSeconds(waitInterval));
            } catch (InterruptedException e) {
                log.error("interrupted");
            }
            
            log.info("finished waiting; releasing locks");
            lockProvider.unlock(lockedLocks);
        }
    }
}
