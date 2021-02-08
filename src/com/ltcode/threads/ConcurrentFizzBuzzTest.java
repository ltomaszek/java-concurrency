package com.ltcode.threads;

import org.junit.jupiter.api.Test;
import java.util.Arrays;

/**
 * Tests the ConcurrentFizzBuzz class
 */
class ConcurrentFizzBuzzTest {

    @Test
    void getOutput() {
        int startInclusive = 2;
        int endInclusive = 15;

        String[] expectedOutput = getOutput(startInclusive, endInclusive);

        ConcurrentFizzBuzz cfb = new ConcurrentFizzBuzz(startInclusive, endInclusive);
        String[] testedOutput = cfb.getOutput();

        assert Arrays.equals(testedOutput, expectedOutput);
    }

    /**
     * Helper method for getting fizz/buzz output array
     */
    private String[] getOutput(int startInclusive, int endInclusive) {
        String[] output = new String[endInclusive - startInclusive + 1];
        for (int i = startInclusive; i <= endInclusive; i++) {
            String result;
            if (i % 3 == 0 && i % 5 == 0)
                result = "FizzBuzz";
            else if (i % 3 == 0)
                result = "Fizz";
            else if (i % 5 == 0)
                result = "Buzz";
            else
                result = Integer.toString(i);
            output[i - startInclusive] = result;
        }
        return output;
    }
}