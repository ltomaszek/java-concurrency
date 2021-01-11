package com.ltcode.forkjoin;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

/**
 * This class can change array's values in sequential and concurrent way
 */
public class ArrayManipulator {

    /**
     * Sequentially increments array's values using for-loop
     *
     * @param array               - array with int elements
     * @param incrementByValue    - value by witch each cell in range should be incremented
     * @param startIndexInclusive - first array's cell that should be incremented
     * @param endIndexExclusive   - last cell that should be incremented (exclusively)
     */
    public static void incrementSeq(int[] array, int startIndexInclusive, int endIndexExclusive, int incrementByValue) {
        for (int i = startIndexInclusive; i < endIndexExclusive; i++)
            array[i] += incrementByValue;
    }

    /**
     * Parallel increments array's values using RecursiveAction in class: IntArrayIncrementer
     *
     * @param array               - array with int elements
     * @param incrementByValue    - value by witch each cell in range should be incremented
     * @param startIndexInclusive - first array's cell that should be incremented
     * @param endIndexExclusive   - last cell that should be incremented (exclusively)
     */
    public static void incrementPar(int[] array, int startIndexInclusive, int endIndexExclusive, int incrementByValue) {
        // use common pool
        ForkJoinPool pool = ForkJoinPool.commonPool();

        // number of tasks that can run parallel, where +1 is current thread
        int numTask = pool.getParallelism() + 1;

        // array with all tasks
        IntArrayIncrementer[] taskArray = new IntArrayIncrementer[numTask];

        // chunk size for one task
        int chunkSize = (endIndexExclusive - startIndexInclusive + numTask - 1) / numTask;

        // create new tasks, fork them, and add them to the array
        for (int taskIdx = 0; taskIdx < numTask; taskIdx++) {

            // calculate chunk of the array that the task should accomplish
            int startChunkIdx = startIndexInclusive + taskIdx * chunkSize;
            int endChunkIdx = Math.min(startChunkIdx + chunkSize, endIndexExclusive);

            IntArrayIncrementer task = new IntArrayIncrementer(array, startChunkIdx, endChunkIdx, incrementByValue);
            taskArray[taskIdx] = task;

            // last task should not be forked - it will be run in current thread
            if (taskIdx < numTask - 1)
                task.fork();
        }
        // run last task in current thread
        taskArray[numTask - 1].compute();

        // wait for all forked tasks to complete
        for (int taskIdx = 0; taskIdx < numTask - 1; taskIdx++) {
            taskArray[taskIdx].join();
        }
    }

    /**
     *
     * @param array               - array with int elements
     * @param incrementByValue    - value by witch each cell in range should be incremented
     * @param startIndexInclusive - first array's cell that should be incremented
     * @param endIndexExclusive   - last cell that should be incremented (exclusively)
     * @param maxSeqentialWork    - max array range that should be worked on in one thread
     */
    public static void incrementParWithThreshold(int[] array, int startIndexInclusive, int endIndexExclusive,
                                                 int incrementByValue, int maxSeqentialWork) {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        pool.invoke(new IntArrayIncrementerWithThreshold(array, startIndexInclusive, endIndexExclusive,
                incrementByValue, maxSeqentialWork));
    }

    /**
     * Private class uses ForkJoin Framework for increasing/decreasing int array's values
     * It does not call itself recursively
     */
    private static class IntArrayIncrementer extends RecursiveAction {

        private final int[] array;
        private final int startIndexInclusive;
        private final int endIndexExclusive;
        private final int incrementByValue;

        private IntArrayIncrementer(final int[] array, final int startIndexInclusive,
                                   final int endIndexExclusive, final int incrementByValue) {
            this.array = array;
            this.startIndexInclusive = startIndexInclusive;
            this.endIndexExclusive = endIndexExclusive;
            this.incrementByValue = incrementByValue;
        }

        @Override
        protected void compute() {
            for (int i = startIndexInclusive; i < endIndexExclusive; i++) {
                array[i] += incrementByValue;
            }
        }
    }

    /**
     * Private class uses ForkJoin Framework for increasing/decreasing int array's values
     * It uses threshold to decide if computation should be executed directly or if
     * the range of computation should be divided in half and called in parallel
     */
    private static class IntArrayIncrementerWithThreshold extends RecursiveAction {

        private final int[] array;
        private final int lo;
        private final int hi;
        private final int incrementByValue;
        private final int THRESHOLD;

        private IntArrayIncrementerWithThreshold(int[] array, int lo, int hi, int incrementByValue, int maxSeqentialWork) {
            this.array = array;
            this.lo = lo;
            this.hi = hi;
            this.incrementByValue = incrementByValue;
            this.THRESHOLD = maxSeqentialWork;
        }

        @Override
        protected void compute() {
            //System.out.printf("Computing: lo = %d, hi = %d\n", lo, hi);
            if (hi - lo <= THRESHOLD) {
                for (int i = lo; i < hi; i++)
                    array[i] += incrementByValue;
            } else {
                int mid = (lo + hi) >>> 1;
                ForkJoinTask.invokeAll(new IntArrayIncrementerWithThreshold(array, lo, mid, incrementByValue, THRESHOLD),
                        new IntArrayIncrementerWithThreshold(array, mid, hi, incrementByValue, THRESHOLD));
            }
        }
    }
}

