package com.ltcode.liveness;

import java.util.Arrays;
import java.util.Random;

/**
 * Class shows liveness problem - deadlock, that can occur in parallel programming
 * with the example of 'Dining Philosophers'
 *
 *         C1  Aristotle  C2
 *       Plato   TABLE  Socrates
 *         C3  Confucius  C4
 */
public class DiningPhilosophers {

    /**
     * Deadlock occurs when all philosophers try to eat at the same time, and all
     * of them pick a chopstick from the same side.
     *
     * @return Thread array with all running Philosophers
     */
    public Thread[] runWithDeadlock() {
        Chopstick c1 = new Chopstick();
        Chopstick c2 = new Chopstick();
        Chopstick c3 = new Chopstick();
        Chopstick c4 = new Chopstick();

        // All philosophers start eating by picking their left chopstick
        Philosopher aristotle = new Philosopher("Aristotle", c2, c1);
        Philosopher plato = new Philosopher("Plato", c1, c3);
        Philosopher socrates = new Philosopher("Socrates", c4, c2);
        Philosopher confucius = new Philosopher("Confucius", c3, c4);

        Thread[] threads = new Thread[4];
        threads[0] = new Thread(aristotle);
        threads[1] = new Thread(plato);
        threads[2] = new Thread(socrates);
        threads[3] = new Thread(confucius);

        Arrays
                .stream(threads)
                .forEach(t -> t.start());

        return threads;
    }

    /**
     * To assure that Deadlock will not occurs when all philosophers try to eat at the same time,
     * at least one of them must pick a chopstick from the different side then other philosophers.
     *
     * @return Thread array with all running Philosophers
     */
    public Thread[] runWithNoDeadlock() {
        Chopstick c1 = new Chopstick();
        Chopstick c2 = new Chopstick();
        Chopstick c3 = new Chopstick();
        Chopstick c4 = new Chopstick();

        // 3 out of 4 philosophers start eating by picking their left chopstick
        Philosopher aristotle = new Philosopher("Aristotle", c2, c1);
        Philosopher plato = new Philosopher("Plato", c1, c3);
        Philosopher socrates = new Philosopher("Socrates", c4, c2);

        // One philosopher start eating by picking right (different) chopstick
        Philosopher confucius = new Philosopher("Confucius", c4, c3);

        Thread[] threads = new Thread[4];
        threads[0] = new Thread(aristotle);
        threads[1] = new Thread(plato);
        threads[2] = new Thread(socrates);
        threads[3] = new Thread(confucius);

        Arrays
                .stream(threads)
                .forEach(t -> t.start());

        return threads;
    }

    private static class Philosopher implements Runnable {

        static Random random = new Random();
        static int thinkingTime = 1000;
        static int takingChopstickTime = 1000;
        static int eatingTime = 3000;

        String name;
        Chopstick firstChopstick;
        Chopstick secondChopstick;

        Philosopher(String name, Chopstick firstChopstick, Chopstick secondChopstick) {
            this.name = name;
            this.firstChopstick = firstChopstick;
            this.secondChopstick = secondChopstick;
        }

        @Override
        public void run() {
            try {
                System.out.println(name + " is thinking...");
                Thread.sleep(thinkingTime);
                System.out.println(name + " is waiting for first chopstick");
                Thread.sleep(takingChopstickTime);
                synchronized (firstChopstick) {
                    System.out.println(name + " is taking first chopstick");
                    Thread.sleep(takingChopstickTime);
                    System.out.println(name + " is waiting for second chopstick");
                    Thread.sleep(takingChopstickTime);
                    synchronized (secondChopstick) {
                        System.out.println(name + " is taking second chopstick");
                        System.out.println(name + " is eating now");
                        Thread.sleep(eatingTime);
                        System.out.println(name + " ended eating");
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Chopstick {

    }
}
