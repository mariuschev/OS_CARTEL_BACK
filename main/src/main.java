import java.util.Scanner;
import java.lang.Thread;

public class main {
    //fonction affichage du menu
    public static int displayMenu(){
        System.out.println("\n");
        System.out.println("\n");
        System.out.println("#-------------------------------------------------------------------------------------#");
        int choix;
        Scanner sc = new Scanner(System.in);
        System.out.println("Choose the operation you want to do : ");
        System.out.println("1- Matrix Addition");
        System.out.println("2- Matrix Multiplication");
        System.out.println("3- Matrix Transpose");
        System.out.println("4- quit");
        System.out.println("\n");
        System.out.println("#-------------------------------------------------------------------------------------#");
        System.out.println("Enter your choice : ");
        choix = sc.nextInt();
        return choix;
    }

    public static void main(String[] args) throws InterruptedException {
        int choix = displayMenu();

        while (choix != 4) {

            switch (choix) {
                case 1:
                    MatrixAddition.main(args);
                    Thread.sleep(10000);
                    break;
                case 2:
                    MatrixMultiplication.main(args);
                    Thread.sleep(10000);
                    break;
                case 3:
                    MatrixTranspose.main(args);
                    Thread.sleep(10000);
                    break;
                case 4:
                    System.out.println("Goodbye");
                    break;
                default:
                    System.out.println("Invalid choice");
                    break;
            }

            choix = displayMenu();
        }
    }
}
