package com.ltcode.threads;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Concurrency example of the Fizz/Buzz game
 * Prints Fizz / Buzz / FizzBuzz / Number or returns array with those values depending on the number
 */
public class ConcurrentFizzBuzz {

    private int currNum;
    private final int startNumIncluding;
    private final int endNumIncluding;
    private String[] outputArray;

    public ConcurrentFizzBuzz(int startNumIncluding, int endNumIncluding) {
        this.currNum = startNumIncluding;
        this.startNumIncluding = startNumIncluding;
        this.endNumIncluding = endNumIncluding;
    }

    /**
     * Prints Fizz / Buzz / FizzBuzz / Number depending on the current number
     */
    public void print() {
        Consumer<Integer>[] toDo = new Consumer[]{
                n -> System.out.println("Fizz"),
                n -> System.out.println("Buzz"),
                n -> System.out.println("FizzBuzz"),
                n -> System.out.println(n)
        };

        Thread[] tasks = getTasks(toDo);
        startTask(tasks);
        waitForTaskTermination(tasks);
    }

    /**
     * Returns output array where:
     * array[0] - is output for startNumIncluding
     * array[1] - is output for startNumIncluding + 1
     * array[N-1] - is output for endNumIncluding
     *
     * @return array with output for each number
     */
    public String[] getOutput() {
        if (outputArray != null) {
            return outputArray;
        }

        outputArray = new String[endNumIncluding - startNumIncluding + 1];
        Consumer<Integer>[] toDo = new Consumer[]{
                n -> outputArray[(int)n - startNumIncluding] = "Fizz",
                n -> outputArray[(int)n - startNumIncluding] = "Buzz",
                n -> outputArray[(int)n - startNumIncluding] = "FizzBuzz",
                n -> outputArray[(int)n - startNumIncluding] = n.toString()
        };

        Thread[] tasks = getTasks(toDo);
        startTask(tasks);
        waitForTaskTermination(tasks);
        return outputArray;
    }

    /**
     * Starts all threads
     *
     * @param tasks - array with task threads
     */
    private void startTask(Thread[] tasks) {
        Arrays.stream(tasks)
                .forEach(t -> t.start());
    }

    /**
     * toDo[0] - work for %3
     * toDo[1] - work for %5
     * toDo[2] - work for %3 && %5
     * toDo[3] - else
     *
     * @param toDo - Consumer array with work to do
     * @return
     */
    private Thread[] getTasks(Consumer<Integer>[] toDo) {
        Thread fizzThread = new TaskThread(this, n -> n%3 == 0 && n%5 != 0, toDo[0]);
        Thread buzzThread = new TaskThread(this, n -> n%5 == 0 && n%3 != 0, toDo[1]);
        Thread fizzBuzzThread = new TaskThread(this, n -> n%3 == 0 && n%5 == 0, toDo[2]);
        Thread numThread = new TaskThread(this, n -> n%3 != 0 && n%5 != 0, toDo[3]);

        return new Thread[] {fizzThread, buzzThread, fizzBuzzThread, numThread};
    }

    /**
     * Waits for all threads to finish execution
     *
     * @param tasks - all tasks that should terminate execution
     */
    private void waitForTaskTermination(Thread[] tasks) {
        Arrays.stream(tasks)
                .forEach(t -> {
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
    }

    /**
     * Helper class for each task
     */
    private class TaskThread extends Thread {
        private final Object lock;
        private final Predicate<Integer> validate;
        private final Consumer consumer;

        private TaskThread(Object lock, Predicate<Integer> validate, Consumer<Integer> consumer) {
            this.lock = lock;
            this.validate = validate;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            while (currNum <= endNumIncluding) {
                synchronized (lock) {
                    if (currNum <= endNumIncluding && validate.test(currNum)) {
                        consumer.accept(currNum);
                        currNum++;
                    }
                }
            }
        }
    }
}
