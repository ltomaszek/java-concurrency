package com.ltcode.threads;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.Random;

/**
 * Tests the performance of the class MatrixManipulator
 * No tests for correctness are included in this class - you can find them
 * in the class: MatrixManipulatorTest
 */
class MatrixManipulatorTestPerformance {

    private static int REPEATS ;
    private static Random random;

    @BeforeAll
    static void setUp() {
        REPEATS = 1;
        random = new Random();
    }

    /**
     * Tests the performance of the parallel implementation
     */
    @Test
    void multiplyPar256x256() {
        int N = 256;
        int M = 256;
        int L = 256;
        double speedup = parTest(N, M, L);

        int nCores = Runtime.getRuntime().availableProcessors();
        double minExpectedSpeedup = nCores * 0.6;

        assert speedup >= minExpectedSpeedup :
                String.format("Min expected speedup: %.2fx, actual speedup: %.2fx", speedup, minExpectedSpeedup);
    }

    /**
     * Tests the performance of the parallel implementation
     */
    @Test
    void multiplyPar512x512() {
        int N = 512;
        int M = 512;
        int L = 512;
        double speedup = parTest(N, M, L);

        int nCores = Runtime.getRuntime().availableProcessors();
        double minExpectedSpeedup = nCores * 0.6;

        assert speedup >= minExpectedSpeedup :
                String.format("Min expected speedup: %.2fx, actual speedup: %.2fx", minExpectedSpeedup, speedup);;
    }

    /**
     * A helper function for testing the performance of parallel implementation.
     *
     * @param N - number of rows in matrix A
     * @param M - number of columns in matrix A and rows in matrix B
     * @param L - number of columns in matrix B
     * @return The achieved speedup
     */
    private double parTest(final int N, final int M, final int L) {
        // Create a random input
        final int[][] A = createRandomMatrix(N, M);
        final int[][] B = createRandomMatrix(M, L);
        int[][] C = new int[N][L];

        //Run sequential and parallel versions to get an accurate measurement of parallel performance
        final long seqStartTime = System.currentTimeMillis();
        for (int r = 0; r < REPEATS; r++) {
            C = MatrixManipulator.multiplySeq(A, B);
        }
        final long seqRuntime = System.currentTimeMillis() - seqStartTime;

        final long parStartTime = System.currentTimeMillis();
        for (int r = 0; r < REPEATS; r++) {
            C = MatrixManipulator.multiplyPar(A, B);
        }
        final long parRuntime = System.currentTimeMillis() - parStartTime;

        return (double)seqRuntime / (double)parRuntime;
    }

    /**
     * Creates new random int[N][M] matrix to use as input for the tests
     *
     * @param N - number rows
     * @param M - number columns
     * @return Initialized int[N][M] array
     */
    private static int[][] createRandomMatrix(int N, int M) {
        int[][] matrix = new int[N][M];

        for (int n = 0; n < N; n++) {
            for (int m = 0; m < M; m++) {
                matrix[n][m] = random.nextInt(128);
            }
        }
        return matrix;
    }
}