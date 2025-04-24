package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomLockProvider {
    private static final Logger log = LogManager.getLogger();

    private final List<ReentrantLock> list;

    public RandomLockProvider(int locksNumber) {
        this.list = new ArrayList<>(locksNumber);
        for (int i = 0; i < locksNumber; i++) {
            list.add(new ReentrantLock());
        }
    }

    public List<Lock> lock(int locksNum) {
        if (locksNum > list.size()) {
            throw new IllegalArgumentException("locks number can't exceed " + list.size());
        }

        Set<Integer> locksIndexes = generateLockIndexes(locksNum);
        List<Lock> acquiredLocks = new ArrayList<>();

        boolean success = true;
        log.debug("trying to acquire locks with indexes: {}", locksIndexes);
        for (Integer lockIndex : locksIndexes) {
            Lock l = list.get(lockIndex);
            if (!l.tryLock()) {
                log.debug("couldn't acquire lock {}", lockIndex);
                success = false;
                break;
            } else {
                acquiredLocks.add(l);
            }
        }

        if (success) {
            log.info("all locks acquired");
            return acquiredLocks;
        } else {
            log.info("not all locks acquired; releasing {}", acquiredLocks);
            for (Lock lock : acquiredLocks) {
                lock.unlock();
            }
            return Collections.emptyList();
        }
    }

    private Set<Integer> generateLockIndexes(int locksNum) {
        if (locksNum == list.size()) {
            return IntStream.range(0, list.size()).boxed().collect(Collectors.toSet());
        }

        Set<Integer> indexes = new HashSet<>(locksNum);
        while (indexes.size() != locksNum) {
            indexes.add(ThreadLocalRandom.current().nextInt(list.size()));
        }
        return indexes;
    }

    public void unlock(List<Lock> locks) {
        for (Lock lock : locks) {
            lock.unlock();
        }
    }
}

