import java.util.Random;

class MatrixTranspose {
    private static final int MATRIX_SIZE = 4; // Adjust the matrix size as needed
    private static final int NUM_THREADS = 2; // Number of threads to use

    private static int[][] originalMatrix;
    private static int[][] transposedMatrix;

    public static void main(String[] args) {
        originalMatrix = initializeMatrix(MATRIX_SIZE, MATRIX_SIZE);
        transposedMatrix = new int[MATRIX_SIZE][MATRIX_SIZE];

        long startTime, endTime;

        System.out.println("Original Matrix:");
        printMatrix(originalMatrix);

        // Sequential Matrix Transposition
        startTime = System.currentTimeMillis();
        transposeMatrixSequentially();
        endTime = System.currentTimeMillis();
        System.out.println("\nSequential Transposition Time: " + (endTime - startTime) + " milliseconds");

        System.out.println("\nTransposed Matrix (Sequential):");
        printMatrix(transposedMatrix);

        // Parallel Matrix Transposition
        startTime = System.currentTimeMillis();
        transposeMatrixConcurrently();
        endTime = System.currentTimeMillis();
        System.out.println("\nParallel Transposition Time: " + (endTime - startTime) + " milliseconds");

        System.out.println("\nTransposed Matrix (Parallel):");
        printMatrix(transposedMatrix);
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

    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }

    private static void transposeMatrixSequentially() {
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                transposedMatrix[j][i] = originalMatrix[i][j];
            }
        }
        System.out.println("\nSequential Matrix Transposition Completed");
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

        System.out.println("\nParallel Matrix Transposition Completed");
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
                    transposedMatrix[j][i] = originalMatrix[i][j];
                }
            }
        }
    }
}

