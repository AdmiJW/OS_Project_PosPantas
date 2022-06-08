import SJF.SJF;
import SRTF.SRTF;
import PriorityNP.PriorityNP;
import PriorityP.PriorityP;
import classes.Util;

public class PosPantas {
    
    private static void mainMenu() {

        while (true) {
            Util.clearScreen();
            int choice = Util.printChoiceMenu(
                "PosPantas - Faster than you imagined", 
                "", 
                new String[] {
                    "Shortest-Job-First Scheduling (SJF)",
                    "Shortest-Remaining-Time-First Scheduling (SRTF)",
                    "Priority Scheduling (Non-preemptive)",
                    "Priority Scheduling (Preemptive)",
                    "Exit"
                }
            );
                
            if (choice == 1) new SJF();
            else if (choice == 2) new SRTF();
            else if (choice == 3) new PriorityNP();
            else if (choice == 4) new PriorityP();
            else exitApplication();

            System.out.println();
            Util.pressEnterToContinue();
        }
    }
    
    private static void exitApplication() {
        Util.clearScreen();
        System.out.println("Thank you for using the application!\n");
        Util.pressEnterToContinue();
        System.exit(0);
    }

    
    public static void main(String[] args) {
        mainMenu();
    }
}
