package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RandomLockProvider {
    private static final int CAPACITY = 5; // todo extract as a parameter

    private final Random r = new Random(); // todo check if static is better

    private final List<ReentrantLock> list = new ArrayList<>(CAPACITY);

    {
        for (int i = 0; i < CAPACITY; i++) {
            list.add(new ReentrantLock());
        }
    }

    public List<Lock> lock(int locksNum) {
        List<Lock> acquiredLocks = new ArrayList<>();

        boolean success = true;
        for (int i = 0; i < locksNum; i++) {
            Lock l = list.get(r.nextInt(list.size() - 1));
            if (l.tryLock()) {
                acquiredLocks.add(l);
            } else {
                success = false;
                break;
            }
        }

        if (!success) {
            for (Lock lock : acquiredLocks) {
                lock.unlock();
            }
            return Collections.emptyList();
        } else {
            return acquiredLocks;
        }
    }

    public void unlock(List<Lock> locks) {
        for (Lock lock : locks) {
            lock.unlock();
        }
    }
}

