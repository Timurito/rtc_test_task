package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;

public class LocksAcquiringTask implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger();

    private final int waitTimeMin;
    private final int waitTimeMax;
    private final int periodTime;

    private final int locksToAcquire;

    private final RandomLockProvider lockProvider;

    private final Random r = new Random();

    public LocksAcquiringTask(int waitTimeMin, int waitTimeMax, int periodTime, int locksToAcquire, RandomLockProvider lockProvider) {
        this.waitTimeMin = waitTimeMin;
        this.waitTimeMax = waitTimeMax;
        this.periodTime = periodTime;
        this.locksToAcquire = locksToAcquire;
        this.lockProvider = lockProvider;
    }

    @Override
    public void run() {
        LOGGER.debug("started");

        List<Lock> lockedLocks = lockProvider.lock(locksToAcquire);

        try {
            int waitInterval = r.nextInt(waitTimeMin, waitTimeMax);
            LOGGER.info("waiting for {} secs", waitInterval);

            Thread.sleep(Duration.ofSeconds(waitInterval));

            LOGGER.info("finished waiting; releasing locks");
        } catch (InterruptedException e) {
            LOGGER.warn("interrupted");
        } finally {
            lockProvider.unlock(lockedLocks);
        }
    }

    public int getPeriodTime() {
        return periodTime;
    }

    @Override
    public String toString() {
        return "{" +
                "waitTimeMin=" + waitTimeMin +
                ", waitTimeMax=" + waitTimeMax +
                ", periodTime=" + periodTime +
                '}';
    }
}

