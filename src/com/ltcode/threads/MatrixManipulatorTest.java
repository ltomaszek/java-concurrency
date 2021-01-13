package com.ltcode.threads;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Random;

/**
 * Tests the correctness of the class MatrixManipulator
 */
class MatrixManipulatorTest {

    private static int REPEATS ;
    private static int MAX_MATRIX_SIDE;
    private static Random random;

    @BeforeAll
    static void setUp() {
        REPEATS = 20;
        random = new Random();
        MAX_MATRIX_SIDE = 512;
    }

    /**
     * Tests the correctness of the method: MatrixManipulator.multiplySeq(A, B)
     */
    @Test
    void multiplySeq() {
        for (int i = 0; i < REPEATS; i++) {
            // number of rows in A matrix
            int N = Math.max(1, random.nextInt(MAX_MATRIX_SIDE));
            // number of rows in B matrix and columns in A matrix
            int M = Math.max(1, random.nextInt(MAX_MATRIX_SIDE));
            // number of columns in B matrix
            int L = Math.max(1, random.nextInt(MAX_MATRIX_SIDE));

            int[][] A = createRandomMatrix(N, M);
            int[][] B = createRandomMatrix(M, L);

            int[][] safeMatrix = multiplySeq(A, B);
            int[][] testedMatrix = MatrixManipulator.multiplySeq(A, B);

            assert areMatricesEqual(safeMatrix, testedMatrix) : "Matrices are not equal";
        }
    }

    /**
     * Tests the correctness of the method: MatrixManipulator.multiplyPar(A, B)
     */
    @Test
    void multiplyPar() {
        for (int i = 0; i < REPEATS; i++) {
            // number of rows in A matrix
            int N = Math.max(1, random.nextInt(MAX_MATRIX_SIDE));
            // number of rows in B matrix and columns in A matrix
            int M = Math.max(1, random.nextInt(MAX_MATRIX_SIDE));
            // number of columns in B matrix
            int L = Math.max(1, random.nextInt(MAX_MATRIX_SIDE));

            int[][] A = createRandomMatrix(N, M);
            int[][] B = createRandomMatrix(M, L);

            int[][] safeMatrix = multiplySeq(A, B);
            int[][] testedMatrix = MatrixManipulator.multiplyPar(A, B);

            assert areMatricesEqual(safeMatrix, testedMatrix) : "Matrices are not equal";
        }
    }

    /**
     * Tests the correctness of the method: MatrixManipulator.isMultiplyPossible(A, B)
     */
    @Test
    void isMultiplyPossible() {
        // {matrix A, matrix B, expected return value}
        Object[][] outputs = {
                {new int[12][3], new int[3][3], true},
                {new int[22][13], new int[13][33], true},
                {new int[32][33], new int[33][1], true},
                {new int[42][7], new int[7][22], true},
                {new int[52][2], new int[2][32], true},
                {new int[12][31], new int[3][3], false},
                {new int[22][1], new int[2][33], false},
                {new int[32][3], new int[4][1], false},
                {new int[42][1], new int[2][22], false},
                {new int[1][1], new int[2][1], false}
        };

        for (Object[] o : outputs) {
            int[][] A = (int[][])o[0];
            int[][] B = (int[][])o[1];
            boolean expectedValue = (boolean)o[2];

            assert MatrixManipulator.isMultiplyPossible(A, B) == expectedValue;
        }
    }

    // HELPER METHODS

    /**
     * Performs sequentially a two-dimensional matrix multiply (A x B = C)
     * A reference implementation of multiplySeq in class MatrixManipulator,
     * in case the one in the main source file is accidentally modified.
     *
     * @param A An input matrix with dimensions NxM
     * @param B An input matrix with dimensions MxL
     * @return The output two-dimensional matrix with size NxL
     */
    private static int[][] multiplySeq(final int[][] A, final int[][] B) {
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
                matrix[n][m] = random.nextInt(2);
            }
        }
        return matrix;
    }

    /**
     * Checks matrices equality
     * @param A An input matrix with dimensions NxM
     * @param B An input matrix with dimensions MxL
     * @return true if all values are the same, otherwise false
     */
    private boolean areMatricesEqual(int[][] A, int[][] B) {
        return Arrays.deepEquals(A, B);
    }
}