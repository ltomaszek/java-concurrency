package com.ltcode.forkjoin;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.stream.IntStream;


class ArrayManipulatorTestPerformance {

    private static int arraySize;
    private static int startIdx;
    private static int endIdx;
    private static int incrementByValue;
    private static int REPEATS;

    private int[] originalArray;
    private int[] testedArray;

    @BeforeAll
    static void setUpBeforeAll() {
        arraySize = 10_000_000;
        startIdx = 0;
        endIdx = arraySize;
        incrementByValue = 3;
        REPEATS = 100;
    }

    @BeforeEach
    void setUp() {
        originalArray = new int[arraySize];
        testedArray = originalArray.clone();
    }

    @Test
    void testPerformanceWithManyApproaches() {
        int[] array = new int[arraySize];

        // calculate sum of all cells after test
        int sum = REPEATS * (endIdx - startIdx) * incrementByValue;

        // test sequentially runtime
        long timeStart = currTime();
        for (int i = 0; i < REPEATS; i ++)
            ArrayManipulator.incrementSeq(array, startIdx, endIdx, incrementByValue);
        long runtimeSeq = currTime() - timeStart;
        assert sum == IntStream.of(array).sum();

        // test parallel runtime
        array = new int[arraySize];
        timeStart = currTime();
        for (int i = 0; i < REPEATS; i ++)
            ArrayManipulator.incrementPar(array, startIdx, endIdx, incrementByValue);
        long runtimePar = currTime() - timeStart;
        assert sum == IntStream.of(array).sum();

        // test parallel runtime with threshold
        array = new int[arraySize];
        int cores = getNCores();
        int THRESHOLD = arraySize / cores + cores - 1;
        timeStart = currTime();
        for (int i = 0; i < REPEATS; i ++)
            ArrayManipulator.incrementParWithThreshold(array, startIdx, endIdx, incrementByValue, THRESHOLD);
        long runtimeParThreshold = currTime() - timeStart;
        assert sum == IntStream.of(array).sum();

        // test stream runtime
        int[] arrayStreamSerial = new int[arraySize];
        timeStart = currTime();
        for (int i = 0; i < REPEATS; i ++)
            IntStream.range(startIdx, endIdx)
                    .forEach(idx -> arrayStreamSerial[idx] += incrementByValue);
        long runtimeStream = currTime() - timeStart;
        assert sum == IntStream.of(arrayStreamSerial).sum();

        // test parallel stream
        int[] arrayStreamPar = new int[arraySize];
        timeStart = currTime();
        for (int i = 0; i < REPEATS; i ++)
            IntStream.range(startIdx, endIdx)
                    .parallel()
                    .forEach(idx -> arrayStreamPar[idx] += incrementByValue);
        long runtimeStreamPar = currTime() - timeStart;

        assert sum == IntStream.of(arrayStreamPar).sum();

        // compare times
        System.out.printf("Runtime sequential                     :%10d milliseconds\n", runtimeSeq);
        System.out.printf("Runtime RecursiveAction                :%10d milliseconds\n", runtimePar);
        System.out.printf("Runtime RecursiveAction with Threshold :%10d milliseconds\n", runtimeParThreshold);
        System.out.printf("Runtime serial stream                  :%10d milliseconds\n", runtimeStream);
        System.out.printf("Runtime parallel stream                :%10d milliseconds\n", runtimeStreamPar);
    }

    @Test
    void incrementPar() {
        double expectedSpeedup = getNCores() * 0.6;
        double speedup;

        long timeStart = currTime();
        for (int i = 0; i < REPEATS; i++) {
            ArrayManipulator.incrementSeq(originalArray, startIdx, endIdx, incrementByValue);
        }
        long runtimeSeq = currTime() - timeStart;

        timeStart = currTime();
        for (int i = 0; i < REPEATS; i++) {
            ArrayManipulator.incrementPar(originalArray, startIdx, endIdx, incrementByValue);
        }
        long runtimePar = currTime() - timeStart;

        speedup = (double)runtimeSeq / runtimePar;

        assert speedup >= expectedSpeedup :
                String.format("Expected speedup: %.2f, actual speedup %.2f", expectedSpeedup, speedup);
    }

    @Test
    void incrementParWithThreshold() {
        double expectedSpeedup = getNCores() * 0.6;
        double speedup;
        int cores = getNCores();
        int THRESHOLD = arraySize / cores + cores - 1;

        long timeStart = currTime();
        for (int i = 0; i < REPEATS; i++) {
            ArrayManipulator.incrementSeq(originalArray, startIdx, endIdx, incrementByValue);
        }
        long runtimeSeq = currTime() - timeStart;

        timeStart = currTime();
        for (int i = 0; i < REPEATS; i++) {
            ArrayManipulator.incrementParWithThreshold(originalArray, startIdx, endIdx, incrementByValue, THRESHOLD);
        }
        long runtimePar = currTime() - timeStart;

        speedup = (double)runtimeSeq / runtimePar;

        assert speedup >= expectedSpeedup :
                String.format("Expected speedup: %.2f, actual speedup %.2f", expectedSpeedup, speedup);
    }

    /* helper methods */
    private static long currTime() {
        return System.currentTimeMillis();
    }

    private static int getNCores() {
        return Runtime.getRuntime().availableProcessors();
    }
}