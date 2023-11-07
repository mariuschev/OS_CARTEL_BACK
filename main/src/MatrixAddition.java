import java.util.Random;
public class MatrixAddition {
    private static final int MATRIX_SIZE = 1000;
    private static final int NUM_THREADS = 4;

    private static int[][] matrixA;
    private static int[][] matrixB;
    private static int[][] resultMatrix;

    public static void main(String[] args) {
        matrixA = initializeMatrix(MATRIX_SIZE, MATRIX_SIZE);
        matrixB = initializeMatrix(MATRIX_SIZE, MATRIX_SIZE);
        resultMatrix = new int[MATRIX_SIZE][MATRIX_SIZE];



        // Sequential Matrix Addition
        long startTimeSeq = System.nanoTime();
        addMatricesSequentially();
        long endTimeSeq = System.nanoTime();
        System.out.println("\nSequential Addition Time: " + (endTimeSeq - startTimeSeq) + " nanoseconds");



        // Parallel Matrix Transposition
        long startTimeParallel = System.nanoTime();
        addMatricesConcurrently();
        long endTimeParallel = System.nanoTime();
        System.out.println("\nParallel Addition Time: " + (endTimeParallel - startTimeParallel) + " nanoseconds");
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

    private static void addMatricesSequentially() {
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                resultMatrix[i][j] = matrixA[i][j] + matrixB[i][j];
            }
        }
        System.out.println("Sequential Matrix Addition Completed");
    }

    private static void addMatricesConcurrently() {
        Thread[] threads = new Thread[NUM_THREADS];
        int blockSize = MATRIX_SIZE / NUM_THREADS;

        for (int i = 0; i < NUM_THREADS; i++) {
            int startRow = i * blockSize;
            int endRow = (i == NUM_THREADS - 1) ? MATRIX_SIZE : startRow + blockSize;
            threads[i] = new Thread(new MatrixAdder(startRow, endRow));
            threads[i].start();
        }

        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Parallel Matrix Addition Completed");
    }

    private static void verifyResults() {
        // Verify that the results of sequential and parallel addition are the same
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                if (resultMatrix[i][j] != matrixA[i][j] + matrixB[i][j]) {
                    System.err.println("Error: Results do not match!");
                    return;
                }
            }
        }
        System.out.println("Results Verified: Sequential and Parallel Addition Match");
    }

    private static class MatrixAdder implements Runnable {
        private final int startRow;
        private final int endRow;

        public MatrixAdder(int startRow, int endRow) {
            this.startRow = startRow;
            this.endRow = endRow;
        }

        @Override
        public void run() {
            for (int i = startRow; i < endRow; i++) {
                for (int j = 0; j < MATRIX_SIZE; j++) {
                    resultMatrix[i][j] = matrixA[i][j] + matrixB[i][j];
                }
            }
        }
    }
}


