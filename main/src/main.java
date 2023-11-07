import java.util.Scanner;
public class main {
    public static void main(String[] args) {
        // donner le choix a l utilisateur de choisir si il veut faire matrice d addition,multiplication ou transposé
        int choix;
        Scanner sc = new Scanner(System.in);
        System.out.println("Choose the operation you want to do : ");
        System.out.println("1- Matrix Addition");
        System.out.println("2- Matrix Multiplication");
        System.out.println("3- Matrix Transpose");
        System.out.println("4- quit");

        System.out.println("#-------------------------------------------------------------------------------------#");
        System.out.println("Enter your choice : ");
        choix = sc.nextInt();


        switch(choix){
            case 1:
                System.out.println("#-----------------------------------Matrix Addition-----------------------------------#");
                MatrixAddition.main(args);
                break;
            case 2:
                MatrixMultiplication.main(args);
                break;
            case 3:
                MatrixTranspose.main(args);
                break;
            case 4:
                System.out.println("Goodbye");
                break;
            default:
                System.out.println("Invalid choice");
                break;
        }
    }
}
