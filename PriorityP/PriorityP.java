package PriorityP;

import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.TreeMap;

import PriorityNP.PriorityNP;
import classes.*;



public class PriorityP extends PriorityNP {

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
                // Update the remaining time for the current executing job, if it is not null
                if (currentPostage != null) currentPostage.remainingBurst -= arrivals.firstKey() - currentTime;
                // Update time
                currentTime = arrivals.firstKey();
                // Add the new work into the queue.
                for (Postage p: arrivals.pollFirstEntry().getValue() )
                    waitingQueue.offer( p );

                //* Two conditions result in current job being replaced:
                //* 1. Current postage is null
                //* 2. Preemption: Arrived job has less remaining time
                preemptPostageIfAvailable();
                selectNewPostageIfAvailable();
            }
        }
    }


    private void preemptPostageIfAvailable() {
        if (
            currentPostage != null &&
            !waitingQueue.isEmpty() &&
            Postage.comparePriorityThenBurstAscending(currentPostage, waitingQueue.peek() ) > 0
        ) {
            //* Preemption happens by placing the current postage back into the waiting queue.
            //* The rest of selecting new job will be handled by selectNewPostageIfAvailable()
            System.out.printf(
                ">> ( t = %-4d): Parcel #%d (Priority #%d) preempted. Remaining burst = %d\n", 
                currentTime, 
                currentPostage.id,
                currentPostage.priority,
                currentPostage.remainingBurst
            );

            waitingQueue.offer( currentPostage );
            currentPostage = null;
        }
    }



    @Override
    protected String postageToString(Postage p) {
        return String.format(
            "%-5d%-10d%-10d%-10d%-10d%-10d",
            p.id,
            p.arrivalTime,
            p.burstTime,
            p.priority,
            p.completionTime,
            p.totalWaited
        );
    }

    @Override
    protected String getHeader() {
        return String.format(
            "%-5s%-10s%-10s%-10s%-10s%-10s",
            "ID", 
            "Arrival", 
            "Burst",
            "Priority",
            "Completed", 
            "Waited"
        );
    }


    @Override
    protected void printTitle() {
        Util.clearScreen();
        Util.printTitle("Priority Scheduling (Preemptive) Postage");
        System.out.println();
    }
}
