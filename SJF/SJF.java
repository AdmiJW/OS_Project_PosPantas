package SJF;



import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;

import classes.*;



public class SJF {
    protected PriorityQueue<Postage> arrivals = new PriorityQueue<>( Postage::compareArrivalAscending );
    protected PriorityQueue<Postage> waitingQueue = new PriorityQueue<>( Postage::compareBurstAscending );
    protected List<Postage> completed = new ArrayList<>();

    protected int currentTime = 0;
    protected Postage currentPostage = null;


    public SJF() {
        initialize();
        System.out.println();

        Util.printTitle("The postage had begun!");
        compute();
        Util.printTitle("The postage had ended!");

        System.out.println();


        completed.sort( Postage::compareIDAscending );
        System.out.println( getHeader() );
        for (Postage p: completed ) System.out.println( postageToString(p) );

        System.out.println();

        computeAverages();
    }


    protected void initialize() {
        printTitle();

        //* Prompt input for postage jobs */
        int n = Util.getInputOfRange(1, 999, "Enter the number of postages: ");
        System.out.println();

        for (int i = 1; i <= n; ++i) {
            int arrival = Util.getInputOfRange(
                0,
                9999,
                String.format("Enter the pickup time for Postage #%d: ", i)
            );
            int burst = Util.getInputOfRange(
                0,
                9999,
                String.format("Enter the delivery time for Postage #%d: ", i)
            );

            arrivals.offer( new Postage( i, arrival, burst ) );
            System.out.println();
        }
    }


    protected void compute() {
        // Keep iterating while we not all postage is delivered.
        //      Condition 1 - Still got pending arrivals
        //      Condition 2 - Got ongoing postage
        while ( !arrivals.isEmpty() || currentPostage != null ) {
            //* If the current executing postage finish first */
            if ( 
                arrivals.isEmpty() || 
                (currentPostage != null && currentPostage.completionTime <= arrivals.peek().arrivalTime )
            ) {
                // Update the waiting time for all in waitingQueue
                for (Postage p: waitingQueue) p.totalWaited += currentPostage.completionTime - currentTime;
                // Push the postage to completed list
                completed.add( currentPostage );
                // Update time
                currentTime = currentPostage.completionTime;
                // The current postage is completed
                System.out.printf(">> ( t = %-4d): Parcel #%d delivered!\n", currentTime, currentPostage.id);
                currentPostage = null;

                // Select new postage, if there are still postages to be done
                selectNewPostageIfAvailable();
            }
            //* Otherwise another job arrives first */
            else {
                // Update the waiting time for all in waitingQueue
                for (Postage p: waitingQueue) p.totalWaited += arrivals.peek().arrivalTime - currentTime;
                // Update time
                currentTime = arrivals.peek().arrivalTime;
                // Add the new work into the queue.
                waitingQueue.offer( arrivals.poll() );

                // Select new postage, in case if no postage is currently being processed
                selectNewPostageIfAvailable();
            }
        }
    }


    protected void selectNewPostageIfAvailable() {
        if (currentPostage == null && !waitingQueue.isEmpty()) {
            currentPostage = waitingQueue.poll();
            currentPostage.lastStarted = currentTime;
            currentPostage.completionTime = currentTime + currentPostage.remainingBurst;

            System.out.printf(">> ( t = %-4d): Parcel #%d out for delivery!\n", currentTime, currentPostage.id);
        }
    }


    protected void computeAverages() {
        double totalWaited = 0;
        double totalTurnaround = 0;

        for (Postage p: completed) {
            totalTurnaround += p.completionTime - p.arrivalTime;
            totalWaited += p.totalWaited;
        }

        System.out.printf("Average turnaround: %.2f\n", totalTurnaround / completed.size() );
        System.out.printf("Average waiting time: %.2f\n", totalWaited / completed.size() );
    }


    protected String postageToString(Postage p) {
        return String.format(
            "%-5d%-10d%-10d%-10d%-10d%-10d",
            p.id,
            p.arrivalTime,
            p.remainingBurst,
            p.lastStarted,
            p.completionTime,
            p.totalWaited
        );
    }


    protected String getHeader() {
        return String.format(
            "%-5s%-10s%-10s%-10s%-10s%-10s",
            "ID", 
            "Pickup", 
            "Delivery", 
            "Started",
            "Completed", 
            "Waited"
        );
    }


    protected void printTitle() {
        Util.clearScreen();
        Util.printTitle("Shortest-Job-First Postage");
        System.out.println();
    }
}
