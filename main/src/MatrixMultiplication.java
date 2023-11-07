import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class MatrixMultiplication {
    private static final int MATRIX_SIZE = 1000;
    private static final int NUM_THREADS = 100;

    private static int[][] matrixA;
    private static int[][] matrixB;
    private static int[][] resultMatrix;

    public static void main(String[] args) {
        matrixA = initializeMatrix(MATRIX_SIZE, MATRIX_SIZE);
        matrixB = initializeMatrix(MATRIX_SIZE, MATRIX_SIZE);
        resultMatrix = new int[MATRIX_SIZE][MATRIX_SIZE];

        System.out.println("Sequential Matrix Multiplication:");
        long startTimeSeq = System.nanoTime();
        multiplyMatricesSequentially();
        long endTimeSeq = System.nanoTime();
        //printMatrix(resultMatrix);
        System.out.println("Sequential Multiplication Time: " + (endTimeSeq - startTimeSeq) + " nanoseconds");

        // Reset resultMatrix for parallel multiplication
        resultMatrix = new int[MATRIX_SIZE][MATRIX_SIZE];

        System.out.println("\nParallel Matrix Multiplication:");
        long startTimeParallel = System.nanoTime();
        multiplyMatricesConcurrently();
        long endTimeParallel = System.nanoTime();
        //printMatrix(resultMatrix);
        System.out.println("Parallel Multiplication Time: " + (endTimeParallel - startTimeParallel) + " nanoseconds");

        // Verify that both results are the same
        verifyResults();
    }

    private static int[][] initializeMatrix(int rows, int cols) {
        int[][] matrix = new int[rows][cols];
        Random random = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = random.nextInt(10);
            }
        }

        return matrix;
    }

    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }

    private static void multiplyMatricesSequentially() {
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                for (int k = 0; k < MATRIX_SIZE; k++) {
                    resultMatrix[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }
    }

    private static void multiplyMatricesConcurrently() {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                final int row = i;
                final int col = j;
                executor.submit(() -> multiplyElement(row, col));
            }
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void multiplyElement(int row, int col) {
        for (int k = 0; k < MATRIX_SIZE; k++) {
            resultMatrix[row][col] += matrixA[row][k] * matrixB[k][col];
        }
    }

    private static void verifyResults() {
        // Verify that the results of parallel multiplication match the sequential multiplication
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                if (resultMatrix[i][j] != getSequentialMultiplicationResult(i, j)) {
                    System.err.println("Error: Results do not match!");
                    return;
                }
            }
        }
        System.out.println("Results Verified: Sequential and Parallel Multiplication Match");
    }

    private static int getSequentialMultiplicationResult(int row, int col) {
        int result = 0;
        for (int k = 0; k < MATRIX_SIZE; k++) {
            result += matrixA[row][k] * matrixB[k][col];
        }
        return result;
    }
}
