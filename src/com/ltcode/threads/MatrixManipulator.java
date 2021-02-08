package com.ltcode.threads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Wrapper class for implementing matrix multiply sequentially and in parallel.
 */
public class MatrixManipulator {

    /**
     * Performs sequentially a two-dimensional matrix multiply (A x B = C)
     *
     * @param A An input matrix with dimensions NxM
     * @param B An input matrix with dimensions MxL
     * @return The output two-dimensional matrix with size NxL
     */
    public static int[][] multiplySeq(final int[][] A, final int[][] B) {
        checkIfMultiplyPossible(A, B);

        int N = A.length;
        int L = B[0].length;

        // matrix to return
        int[][] C = new int[N][L];

        for (int r = 0; r < N; r++) {
            for (int c = 0; c < L; c++) {
                for (int k = 0; k < A[0].length; k++) {
                    C[r][c] += A[r][k] * B[k][c];
                }
            }
        }
        return C;
    }

    /**
     * Performs in parallel a two-dimensional matrix multiply (A x B = C)
     * Uses number of CPUs for parallel work
     *
     * @param A An input matrix with dimensions NxM
     * @param B An input matrix with dimensions MxL
     * @return The output two-dimensional matrix with size NxL
     */
    public static int[][] multiplyPar(final int[][] A, final int[][] B) {
        checkIfMultiplyPossible(A, B);

        int N = A.length;
        int L = B[0].length;

        // matrix to return
        int[][] C = new int[N][L];

        int nCores = Runtime.getRuntime().availableProcessors();
        int TOTAL_WORK = N*L;
        int WORK_CHUNK = (TOTAL_WORK + nCores - 1) / nCores;

        List<Thread> threads = new ArrayList<>();

        // multiply in parallel threads (nThreads = nCores - 1) / -1 for current thread
        int parallelThreads = nCores - 1;
        for (int i = 0; i < parallelThreads; i ++) {
            // Math.min guarantee that indexes do not exceed the TOTAL_WORK,
            // cause it might happen with small matrices
            int startIdx = i * WORK_CHUNK;
            int endIdx = Math.min(startIdx + WORK_CHUNK, TOTAL_WORK);

            if (startIdx < TOTAL_WORK) {
                Thread t = new Thread(() -> multiply(A, B, C, startIdx, endIdx));
                t.start();
                threads.add(t);
            }
        }

        // last multiplication can run in current thread
        multiply(A, B, C, parallelThreads * WORK_CHUNK, TOTAL_WORK);

        // wait for all threads to end
        threads.stream().forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        return C;
    }

    /**
     * Multiply is possible only if M == M
     *
     * @param A An input matrix with dimensions NxM
     * @param B An input matrix with dimensions MxL
     * @return True/False
     */
    public static boolean isMultiplyPossible(final int[][] A, final int[][] B) {
        if (A == null || B == null)
            throw new NullPointerException("Matrix can not be null");
        return A[0].length == B.length;
    }

    /**
     * Multiply is possible only if M == M
     * Throws Exception if arguments are null or multiplication is not possible
     *
     * @param A An input matrix with dimensions NxM
     * @param B An input matrix with dimensions MxL
     */
    private static void checkIfMultiplyPossible(int[][] A, int[][] B) {
        if (!isMultiplyPossible(A, B))
            throw new IllegalArgumentException("Matrices can not be multiplied");
    }

    /**
     * Multiply sequentially given range
     *
     * @param A An input matrix with dimensions NxM
     * @param B An input matrix with dimensions MxL
     * @param C The output matrix
     * @param startIdx - work's range start (inclusive)
     * @param endIdx - work's range end (exclusive)
     */
    private static void multiply(final int [][] A, final int[][] B, final int[][] C,
                                final int startIdx, final int endIdx) {
        final int COLUMNS = C[0].length;
        final int M = B.length; // == A[0].length

        for (int i = startIdx; i < endIdx; i++) {
            int row = i / COLUMNS;
            int column = i % COLUMNS;
            C[row][column] = 0;
            for (int k = 0; k < M; k++) {
                C[row][column] += A[row][k] * B[k][column];
            }
        }
    }
}
