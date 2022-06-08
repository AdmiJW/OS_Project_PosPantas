package PriorityNP;


import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.TreeMap;

import SJF.SJF;
import classes.*;



//* Even with Priority (Non-preemptive), it can reuse logics from SJF. */
public class PriorityNP extends SJF {

    protected TreeMap<Integer, List<Postage>> arrivals;

    @Override
    protected void initialize() {
        printTitle();

        //* Overwrite the waiting queue, such that it sorts by priority, then shortest burst */
        arrivals = new TreeMap<>();
        waitingQueue = new PriorityQueue<>( Postage::comparePriorityThenBurstAscending );

        //* Prompt input for postage jobs */
        int n = Util.getInputOfRange(1, 999, "Enter the number of postages: ");
        System.out.println();

        for (int i = 1; i <= n; ++i) {
            int arrival = Util.getInputOfRange(
                0,
                9999,
                String.format("Enter the arrival time for Postage #%d: ", i)
            );
            int burst = Util.getInputOfRange(
                0,
                9999,
                String.format("Enter the burst time for Postage #%d: ", i)
            );
            int priority = Util.getInputOfRange(
                0,
                9999,
                String.format("Enter the priority (0 = Highest) for Postage #%d: ", i)
            );

            arrivals.putIfAbsent(arrival, new ArrayList<>() );
            arrivals.get(arrival).add( new Postage(i, arrival, burst, priority) );
            System.out.println();
        }
    }


    
    @Override
    protected void compute() {
        // Keep iterating while we not all postage is delivered.
        //      Condition 1 - Still got pending arrivals
        //      Condition 2 - Got ongoing postage
        while ( !arrivals.isEmpty() || currentPostage != null ) {
            //* If the current executing postage finish first */
            //! Important: For postages that come at same time, we must consider the one with highest priority */
            if ( 
                arrivals.isEmpty() || 
                (currentPostage != null && currentPostage.completionTime <= arrivals.firstKey() )
            ) {
                // Update the waiting time for all in waitingQueue
                for (Postage p: waitingQueue) p.totalWaited += currentPostage.completionTime - currentTime;
                // Push the postage to completed list
                completed.add( currentPostage );
                // Update time
                currentTime = currentPostage.completionTime;
                // The current postage is completed
                System.out.printf(">> ( t = %-4d): Parcel #%d (Priority %d) delivered!\n", currentTime, currentPostage.id, currentPostage.priority);
                currentPostage = null;

                // Select new postage, if there are still postages to be done
                selectNewPostageIfAvailable();
            }
            //* Otherwise another job arrives first */
            else {
                // Update the waiting time for all in waitingQueue
                for (Postage p: waitingQueue) p.totalWaited += arrivals.firstKey() - currentTime;
                // Update time
                currentTime = arrivals.firstKey();
                // Add the new work into the queue.
                for (Postage p: arrivals.pollFirstEntry().getValue() )
                    waitingQueue.offer( p );

                // Select new postage, in case if no postage is currently being processed
                selectNewPostageIfAvailable();
            }
        }
    }


    @Override
    protected void selectNewPostageIfAvailable() {
        if (currentPostage == null && !waitingQueue.isEmpty()) {
            currentPostage = waitingQueue.poll();
            currentPostage.lastStarted = currentTime;
            currentPostage.completionTime = currentTime + currentPostage.remainingBurst;

            System.out.printf(">> ( t = %-4d): Parcel #%d (Priority %d) out for delivery!\n", currentTime, currentPostage.id, currentPostage.priority);
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
            "Arrival", 
            "Burst",
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
