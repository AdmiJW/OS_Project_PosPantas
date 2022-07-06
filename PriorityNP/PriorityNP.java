package PriorityNP;


import java.util.PriorityQueue;

import SJF.SJF;
import classes.*;



//* Even with Priority (Non-preemptive), it can reuse logics from SJF. */
public class PriorityNP extends SJF {

    @Override
    protected void initialize() {
        waitingQueue = new PriorityQueue<>( Postage::comparePriorityThenBurstAscending );

        printTitle();

        // //* Overwrite the waiting queue, such that it sorts by priority, then shortest burst */
        // arrivals = new TreeMap<>();
        // waitingQueue = new PriorityQueue<>( Postage::comparePriorityThenBurstAscending );

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
            int priority = Util.getInputOfRange(
                0,
                9999,
                String.format("Enter the priority (0 = Highest) for Postage #%d: ", i)
            );

            // arrivals.putIfAbsent(arrival, new ArrayList<>() );
            // arrivals.get(arrival).add( new Postage(i, arrival, burst, priority) );
            arrivals.offer( new Postage( i, arrival, burst, priority ) );
            System.out.println();
        }
    }


    @Override
    protected String postageToString(Postage p) {
        return String.format(
            "%-5d%-10d%-10d%-10d%-10d%-10d%-10d",
            p.id,
            p.arrivalTime,
            p.burstTime,
            p.priority,
            p.lastStarted,
            p.completionTime,
            p.totalWaited
        );
    }

    @Override
    protected String getHeader() {
        return String.format(
            "%-5s%-10s%-10s%-10s%-10s%-10s%-10s",
            "ID", 
            "Pickup", 
            "Delivery",
            "Priority",
            "Started",
            "Completed", 
            "Waited"
        );
    }


    @Override
    protected void printTitle() {
        Util.clearScreen();
        Util.printTitle("Priority Scheduling (Non-Preemptive) Postage");
        System.out.println();
    }
}
