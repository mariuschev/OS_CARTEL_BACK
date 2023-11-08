import java.util.Random;

public class MatrixTranspose {
    private static final int MATRIX_SIZE = 2000; // Taille de la matrice
    private static int NUM_THREADS = 4; // Utilisation du nombre de cœurs de processeur

    private static int[][] originalMatrix;
    private static int[][] transposedMatrix;

    public static void main(String[] args) {
        originalMatrix = initializeMatrix(MATRIX_SIZE, MATRIX_SIZE);
        transposedMatrix = new int[MATRIX_SIZE][MATRIX_SIZE];

        long startTime, endTime;

        // Transposition séquentielle de la matrice
        startTime = System.nanoTime();
        transposeMatrixSequentially();
        endTime = System.nanoTime();
        System.out.println("Temps de transposition séquentielle : " + (endTime - startTime) + " nanosecondes");

        // Réinitialise la matrice transposée pour la transposition parallèle
        transposedMatrix = new int[MATRIX_SIZE][MATRIX_SIZE];

        // Optimisation du nombre de threads en fonction des cœurs de processeur
        NUM_THREADS = Math.min(NUM_THREADS, MATRIX_SIZE);

        // Transposition de la matrice en parallèle
        startTime = System.nanoTime();
        transposeMatrixConcurrently();
        endTime = System.nanoTime();
        System.out.println("Temps de transposition parallèle : " + (endTime - startTime) + " nanosecondes");

        // Vérifie que les deux résultats sont identiques
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
        System.out.println("Transposition séquentielle de la matrice terminée");
    }

    private static void transposeMatrixConcurrently() {
        // Division de la matrice en segments et utilisation de threads pour la transposition
        int segmentSize = MATRIX_SIZE / NUM_THREADS;
        Thread[] threads = new Thread[NUM_THREADS];

        for (int t = 0; t < NUM_THREADS; t++) {
            final int startRow = t * segmentSize;
            final int endRow = (t == NUM_THREADS - 1) ? MATRIX_SIZE : (t + 1) * segmentSize;
            threads[t] = new Thread(() -> {
                for (int i = startRow; i < endRow; i++) {
                    for (int j = 0; j < MATRIX_SIZE; j++) {
                        transposedMatrix[j][i] = originalMatrix[i][j];
                    }
                }
            });
            threads[t].start();
        }

        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Transposition de la matrice en parallèle terminée");
    }

    private static void verifyResults() {
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                if (transposedMatrix[i][j] != originalMatrix[j][i]) {
                    System.err.println("Erreur : les résultats ne correspondent pas !");
                    return;
                }
            }
        }
        System.out.println("Résultats vérifiés : transposition séquentielle et parallèle identiques");
    }
}
