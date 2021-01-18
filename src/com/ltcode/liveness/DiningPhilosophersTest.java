package com.ltcode.liveness;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


class DiningPhilosophersTest {

    static int philosophers;
    static int workloadMillis;

    @BeforeAll
    static void setup() {
        philosophers = 4;
        workloadMillis = 5000;
    }

    /**
     * Tests if all parallel running philosophers' threads are blocked
     */
    @Test
    void runWithDeadlock() throws InterruptedException {
        // Threads are running (Runnable) philosophers
        Thread[] threads = new DiningPhilosophers().runWithDeadlock();
        boolean allThreadsBlocked = false;

        while (! allThreadsBlocked) {
            allThreadsBlocked = true;

            // Check every second if all threads are blocked
            Thread.sleep(1000);

            for (Thread thread : threads) {
                if (thread.getState() != Thread.State.BLOCKED) {
                    allThreadsBlocked = false;
                }
            }
        }

        assert allThreadsBlocked : "All threads were supposed to be blocked.";
    }

    /**
     * Tests if all parallel running philosophers' threads terminated
     */
    @Test
    void runWithNoDeadlock() throws InterruptedException {
        // Threads are running (Runnable) philosophers
        Thread[] threads = new DiningPhilosophers().runWithNoDeadlock();
        int maxExpectedRunningTime = (int)(philosophers * workloadMillis * 1.1);
        boolean allThreadsTerminated = false;

        long startTime = System.currentTimeMillis();

        while (! allThreadsTerminated) {
            allThreadsTerminated = true;

            // Check every second if all threads terminated
            Thread.sleep(1000);

            // Check if running time exceeded maxExpectedRunningTime
            long runtime = System.currentTimeMillis() - startTime;
            if (runtime > maxExpectedRunningTime)
                assert false : "Philosophers should have terminated already, but are still running.";

            for (Thread thread : threads) {
                if (thread.getState() != Thread.State.TERMINATED) {
                    allThreadsTerminated = false;
                }
            }
        }

        assert allThreadsTerminated;
    }
}