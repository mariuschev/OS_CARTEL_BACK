import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class MatrixTranspose {
    private static final int MATRIX_SIZE = 1000;
    private static final int NUM_THREADS = 100;

    private static int[][] originalMatrix;
    private static int[][] transposedMatrix;

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
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);

        for (int i = 0; i < NUM_THREADS; i++) {
            final int threadIndex = i;
            executorService.submit(() -> {
                int startRow = threadIndex * (MATRIX_SIZE / NUM_THREADS);
                int endRow = (threadIndex == NUM_THREADS - 1) ? MATRIX_SIZE : (threadIndex + 1) * (MATRIX_SIZE / NUM_THREADS);
                transposeSubmatrix(startRow, endRow);
            });
        }

        executorService.shutdown();

        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Parallel Matrix Transposition Completed");
    }

    private static void transposeSubmatrix(int startRow, int endRow) {
        for (int i = startRow; i < endRow; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                transposedMatrix[j][i] = originalMatrix[i][j];
            }
        }
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
}
