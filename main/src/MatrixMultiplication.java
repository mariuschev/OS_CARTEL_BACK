import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

class MatrixMultiplication {
    private static final int MATRIX_SIZE = 4;
    private static final int NUM_THREADS = 2;

    private static int[][] matrixA;
    private static int[][] matrixB;
    private static int[][] resultMatrix;

    private static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        matrixA = initializeMatrix(MATRIX_SIZE, MATRIX_SIZE);
        matrixB = initializeMatrix(MATRIX_SIZE, MATRIX_SIZE);
        resultMatrix = new int[MATRIX_SIZE][MATRIX_SIZE];

        long startTime, endTime;

        // Sequential Matrix Multiplication
        startTime = System.nanoTime();
        multiplyMatricesSequentially();
        endTime = System.nanoTime();
        System.out.println("Sequential Multiplication Time: " + (endTime - startTime) + " nanoseconds");

        // Reset resultMatrix for parallel multiplication
        resultMatrix = new int[MATRIX_SIZE][MATRIX_SIZE];

        // Parallel Matrix Multiplication
        startTime = System.nanoTime();
        multiplyMatricesConcurrently();
        endTime = System.nanoTime();
        System.out.println("Parallel Multiplication Time: " + (endTime - startTime) + " nanoseconds");
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
                        // Use lock to protect the critical section
                        lock.lock();
                        try {
                            resultMatrix[i][j] += matrixA[i][k] * matrixB[k][j];
                        } finally {
                            lock.unlock();
                        }
                    }
                }
            }
        }
    }
}