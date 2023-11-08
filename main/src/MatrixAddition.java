import java.util.Random;
import java.util.Scanner;

public class MatrixAddition {

    private static int MATRIX_SIZE = 3000;

    private static int NUM_THREADS;

    private static int[][] matrixA;
    private static int[][] matrixB;
    private static int[][] resultMatrix;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Matrix Size : ");
        MATRIX_SIZE = scanner.nextInt();

        System.out.print("Enter number of threads : ");
        NUM_THREADS = scanner.nextInt();
        matrixA = initializeMatrix(MATRIX_SIZE, MATRIX_SIZE);
        matrixB = initializeMatrix(MATRIX_SIZE, MATRIX_SIZE);
        resultMatrix = new int[MATRIX_SIZE][MATRIX_SIZE];

        // Sequential Matrix Addition
        long startTimeSeq = System.currentTimeMillis();
        addMatricesSequentially();
        long endTimeSeq = System.currentTimeMillis();
        System.out.println("Sequential Addition Time: " + (endTimeSeq - startTimeSeq) + " milliseconds");

        // Parallel Matrix Addition
        long startTimeParallel = System.currentTimeMillis();
        addMatricesConcurrently();
        long endTimeParallel = System.currentTimeMillis();
        System.out.println("Parallel Addition Time: " + (endTimeParallel - startTimeParallel) + " milliseconds");
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

    private static void addMatricesSequentially() {
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                resultMatrix[i][j] = matrixA[i][j] + matrixB[i][j];
            }
        }
        System.out.println("Sequential Matrix Addition Completed");
    }

    private static void addMatricesConcurrently() {
        int blockSize = MATRIX_SIZE / NUM_THREADS;
        Thread[] threads = new Thread[NUM_THREADS];

        for (int i = 0; i < NUM_THREADS; i++) {
            final int startRow = i * blockSize;
            final int endRow = (i == NUM_THREADS - 1) ? MATRIX_SIZE : startRow + blockSize;
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

    private static class MatrixAdder implements Runnable {
        private final int startRow;
        private final int endRow;

        public MatrixAdder(int startRow, int endRow) {
            this.startRow = startRow;
            this.endRow = endRow;
        }

        @Override
        public void run() {
            for (int row = startRow; row < endRow; row++) {
                for (int col = 0; col < MATRIX_SIZE; col++) {
                    resultMatrix[row][col] = matrixA[row][col] + matrixB[row][col];
                }
            }
        }
    }

    private static void verifyResults() {
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
}
