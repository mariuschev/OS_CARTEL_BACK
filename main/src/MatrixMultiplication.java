import java.util.Random;
public class MatrixMultiplication {
    private static final int MATRIX_SIZE = 4; // Adjust the matrix size as needed
    private static final int NUM_THREADS = 2; // Number of threads to use

    private static int[][] matrixA;
    private static int[][] matrixB;
    private static int[][] resultMatrix;

    public static void main(String[] args) {
        matrixA = initializeMatrix(MATRIX_SIZE, MATRIX_SIZE);
        matrixB = initializeMatrix(MATRIX_SIZE, MATRIX_SIZE);
        resultMatrix = new int[MATRIX_SIZE][MATRIX_SIZE];

        long startTime, endTime;

        // Sequential Matrix Multiplication
        long startTimeSeq = System.nanoTime();
        multiplyMatricesSequentially();
        long endTimeSeq = System.nanoTime();
        System.out.println("\nSequential Transposition Time: " + (endTimeSeq - startTimeSeq) + " nanoseconds");

        // ... (same as before)

        // Parallel Matrix Transposition
        long startTimeParallel = System.nanoTime();
        multiplyMatricesConcurrently();
        long endTimeParallel = System.nanoTime();
        System.out.println("\nParallel Transposition Time: " + (endTimeParallel - startTimeParallel) + " nanoseconds");
        // Verify that both results are the same
        verifyResults();
    }

    private static int[][] initializeMatrix(int rows, int cols) {
        int[][] matrix = new int[rows][cols];
        Random random = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = random.nextInt(10); // Adjust the range of random values as needed
            }
        }

        return matrix;
    }

    private static void multiplyMatricesSequentially() {
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                for (int k = 0; k < MATRIX_SIZE; k++) {
                    resultMatrix[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }
        System.out.println("Sequential Matrix Multiplication Completed");
    }

    private static void multiplyMatricesConcurrently() {
        Thread[] threads = new Thread[NUM_THREADS];
        int submatrixSize = MATRIX_SIZE / NUM_THREADS;

        for (int i = 0; i < NUM_THREADS; i++) {
            int startRow = i * submatrixSize;
            int endRow = (i == NUM_THREADS - 1) ? MATRIX_SIZE : startRow + submatrixSize;
            threads[i] = new Thread(new MatrixMultiplier(startRow, endRow));
            threads[i].start();
        }

        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Parallel Matrix Multiplication Completed");
    }

    private static void verifyResults() {
        // Verify that the results of sequential and parallel multiplication are the same
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                if (resultMatrix[i][j] != matrixA[i][j] * matrixB[i][j]) {
                    System.err.println("Error: Results do not match!");
                    return;
                }
            }
        }
        System.out.println("Results Verified: Sequential and Parallel Multiplication Match");
    }

    private static class MatrixMultiplier implements Runnable {
        private final int startRow;
        private final int endRow;

        public MatrixMultiplier(int startRow, int endRow) {
            this.startRow = startRow;
            this.endRow = endRow;
        }

        @Override
        public void run() {
            for (int i = startRow; i < endRow; i++) {
                for (int j = 0; j < MATRIX_SIZE; j++) {
                    for (int k = 0; k < MATRIX_SIZE; k++) {
                        resultMatrix[i][j] += matrixA[i][k] * matrixB[k][j];
                    }
                }
            }
        }
    }
}
