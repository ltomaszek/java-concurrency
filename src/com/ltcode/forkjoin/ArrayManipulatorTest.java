package com.ltcode.forkjoin;

import java.util.Arrays;
import java.util.Random;

class ArrayManipulatorTest {

    private static Random random;

    @org.junit.jupiter.api.BeforeAll
    static void setUp() {
        random = new Random();
    }

    /**
     * Checks if after many random increment operations the array's values are still correct
     */
    @org.junit.jupiter.api.Test
    void increment() {
        int ARRAY_SIZE = 1_000_000;
        int[] safeArray = new int[ARRAY_SIZE];
        int[] testedArray = safeArray.clone();

        int NUM_RANDOM_TESTS = 1_000;

        for(int i = 0; i < NUM_RANDOM_TESTS; i++) {
            int startIdx = random.nextInt(ARRAY_SIZE);
            int endIdx = startIdx + random.nextInt(ARRAY_SIZE - startIdx + 1);
            int incrementByValue = random.nextInt(Integer.MAX_VALUE);

            // randomly change or keep sing (+ / -)
            incrementByValue = random.nextBoolean() ? incrementByValue : -incrementByValue;

            // increment safely
            incrementSafely(safeArray, startIdx, endIdx, incrementByValue);

            // increment using testing method
            ArrayManipulator.incrementPar(testedArray, startIdx, endIdx, incrementByValue);
        }

        assert Arrays.equals(testedArray, safeArray) : "Tested array has false values!";
    }

    /**
     * Checks if after many random increment operations the array's values are still correct
     */
    @org.junit.jupiter.api.Test
    void incrementWithThreshold() {
        int ARRAY_SIZE = 1_000_000;
        int[] safeArray = new int[ARRAY_SIZE];
        int[] testedArray = safeArray.clone();

        int NUM_RANDOM_TESTS = 1_000;
        int cores = Runtime.getRuntime().availableProcessors();
        int THRESHOLD = ARRAY_SIZE / cores + cores - 1;

        for(int i = 0; i < NUM_RANDOM_TESTS; i++) {
            int startIdx = random.nextInt(ARRAY_SIZE);
            int endIdx = startIdx + random.nextInt(ARRAY_SIZE - startIdx + 1);
            int incrementByValue = random.nextInt(Integer.MAX_VALUE);

            // randomly change or keep sing (+ / -)
            incrementByValue = random.nextBoolean() ? incrementByValue : -incrementByValue;

            // increment safely
            incrementSafely(safeArray, startIdx, endIdx, incrementByValue);

            // increment using testing method
            ArrayManipulator.incrementParWithThreshold(testedArray, startIdx, endIdx, incrementByValue, THRESHOLD);
        }

        assert Arrays.equals(testedArray, safeArray) : "Tested array has false values!";
    }

    /**
     * helper method for incrementing array's values
     */
    private static void incrementSafely(int[] array, int startIdx, int endIdx, int incrementByValue) {
        for (int i = startIdx; i < endIdx; i++)
            array[i] += incrementByValue;
    }
}