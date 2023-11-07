import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

class MatrixTranspose {
    private static final int MATRIX_SIZE = 4;
    private static final int NUM_THREADS = 2;

    private static int[][] originalMatrix;
    private static int[][] transposedMatrix;

    private static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        originalMatrix = initializeMatrix(MATRIX_SIZE, MATRIX_SIZE);
        transposedMatrix = new int[MATRIX_SIZE][MATRIX_SIZE];

        long startTime, endTime;

        // Sequential Matrix Transposition
        startTime = System.nanoTime();
        transposeMatrixSequentially();
        endTime = System.nanoTime();
        System.out.println("Sequential Transposition Time: " + (endTime - startTime) + " nanoseconds");

        // Reset transposedMatrix for parallel transposition
        transposedMatrix = new int[MATRIX_SIZE][MATRIX_SIZE];

        // Parallel Matrix Transposition
        startTime = System.nanoTime();
        transposeMatrixConcurrently();
        endTime = System.nanoTime();
        System.out.println("Parallel Transposition Time: " + (endTime - startTime) + " nanoseconds");

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

    private static void transposeMatrixSequentially() {
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                transposedMatrix[j][i] = originalMatrix[i][j];
            }
        }
        System.out.println("Sequential Matrix Transposition Completed");
    }

    private static void transposeMatrixConcurrently() {
        Thread[] threads = new Thread[NUM_THREADS];
        int submatrixSize = MATRIX_SIZE / NUM_THREADS;

        for (int i = 0; i < NUM_THREADS; i++) {
            int startRow = i * submatrixSize;
            int endRow = (i == NUM_THREADS - 1) ? MATRIX_SIZE : startRow + submatrixSize;
            threads[i] = new Thread(new MatrixTransposer(startRow, endRow));
            threads[i].start();
        }

        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Parallel Matrix Transposition Completed");
    }

    private static void verifyResults() {
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                if (transposedMatrix[i][j] != originalMatrix[j][i]) {
                    System.err.println("Error: Results do not match!");
                    return;
                }
            }
        }
        System.out.println("Results Verified: Sequential and Parallel Transposition Match");
    }

    private static class MatrixTransposer implements Runnable {
        private final int startRow;
        private final int endRow;

        public MatrixTransposer(int startRow, int endRow) {
            this.startRow = startRow;
            this.endRow = endRow;
        }

        @Override
        public void run() {
            for (int i = startRow; i < endRow; i++) {
                for (int j = 0; j < MATRIX_SIZE; j++) {
                    // Use lock to protect the critical section
                    lock.lock();
                    try {
                        transposedMatrix[j][i] = originalMatrix[i][j];
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }
    }
}
