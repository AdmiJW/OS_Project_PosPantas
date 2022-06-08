package classes;


import java.util.Scanner;
import java.io.IOException;
import java.util.InputMismatchException;




public class Util {
    // Prompts user to enter a number in range [from, to].
    // Performs exception handling if invalid input is entered.
    public static int getInputOfRange(int from, int to) {
        return getInputOfRange(from, to, String.format("Enter your choice [%d - %d]: ", from, to));
    }


    public static int getInputOfRange(int from, int to, String prompt) {
        Integer input = null;
        Scanner scan = new Scanner(System.in);
        while (input == null) {
            System.out.print(prompt);
        
            try {
                int value = scan.nextInt();
                if (value < from || value > to) throw new InputMismatchException();
                else input = value;
            } catch (InputMismatchException e) {
                System.out.printf("Invalid input. Only value from %d to %d is allowed!\n", from, to);
            }
            scan.nextLine();        // Skip the \n character
        }
        return input;
    }



    public static void clearScreen() {
        // System.out.print("\033[H\033[2J");  
        // System.out.flush();  
        final String os = System.getProperty("os.name");
        try {
            if (os.contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        } catch (IOException e) {}
        catch (InterruptedException e) {}
    }


    public static void printTitle(String title) {
        System.out.println("*".repeat( title.length() + 4 ));
        System.out.printf("| %s |\n", title);
        System.out.println("*".repeat( title.length() + 4 ));
    }



    public static int printChoiceMenu(String title,  String description, String[] choices) {
        clearScreen();
        printTitle(title);
        System.out.println();

        if (description.length() != 0) System.out.printf("%s\n\n", description);

        for (int i = 0; i < choices.length; ++i)
            System.out.printf("%d. %s\n", i+1, choices[i]);
        System.out.println();

        return getInputOfRange(1, choices.length);
    }
    


    public static void pressEnterToContinue() {
        System.out.println("Press Enter key to continue...");
        try { System.in.read(); }  
        catch(Exception e) {}  
    }
}
