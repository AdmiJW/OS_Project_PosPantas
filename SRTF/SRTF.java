package SRTF;


import SJF.SJF;
import classes.*;


//* Considering some methods for SRTF is same as SJF, I simply extends it for reusability */
public class SRTF extends SJF {

    @Override
    protected void compute() {

        while (!arrivals.isEmpty() || !waitingQueue.isEmpty() || currentPostage != null) {
            if (currentPostage == null) {
                handleArrivals();
                selectNewPostageFromWaitingQueue();
            } 
            else {
                while ( handleArrivals() ) {
                    if (checkPostagePreemption() ) selectNewPostageFromWaitingQueue();
                }
                deliverCurrentPostageUntilCompletion();
            }
        }



        // Keep iterating while we not all postage is delivered.
        //      Condition 1 - Still got pending arrivals
        //      Condition 2 - Got ongoing postage

        // while ( !arrivals.isEmpty() || currentPostage != null ) {
        //     //* If the current executing postage will finish (Not pre-empted) */
        //     if ( 
        //         arrivals.isEmpty() || 
        //         (currentPostage != null && currentPostage.completionTime <= arrivals.peek().arrivalTime )
        //     ) {
        //         // Update the waiting time for all in waitingQueue
        //         for (Postage p: waitingQueue) p.totalWaited += currentPostage.completionTime - currentTime;
        //         // Push the postage to completed list
        //         completed.add( currentPostage );
        //         // Update time
        //         currentTime = currentPostage.completionTime;
        //         // The current postage is completed
        //         System.out.printf(">> ( t = %-4d): Parcel #%d delivered!\n", currentTime, currentPostage.id);
        //         currentPostage = null;

        //         // Select new postage, if there are still postages to be done
        //         selectNewPostageIfAvailable();
        //     }
        //     //* Otherwise another job arrives first. We will need to check if pre-empting happens or not */
        //     else {
        //         // Update the waiting time for all in waitingQueue
        //         for (Postage p: waitingQueue) p.totalWaited += arrivals.peek().arrivalTime - currentTime;
        //         // Update the remaining time for the current executing job, if it is not null
        //         if (currentPostage != null) currentPostage.remainingBurst -= arrivals.peek().arrivalTime - currentTime;
        //         // Update time
        //         currentTime = arrivals.peek().arrivalTime;
        //         // Add the new work into the queue.
        //         waitingQueue.offer( arrivals.poll() );

        //         //* Two conditions result in current job being replaced:
        //         //* 1. Current postage is null
        //         //* 2. Preemption: Arrived job has less remaining time
        //         preemptPostageIfAvailable();
        //         selectNewPostageIfAvailable();
        //     }
        // }
    }



    // Returns true if the delivery is indeed preempted.
    protected boolean checkPostagePreemption() {
        if (!waitingQueue.isEmpty() && currentPostage.remainingBurst > waitingQueue.peek().remainingBurst) {
            System.out.printf(
                ">> ( t = %-4d): Parcel #%d preempted. Remaining burst = %d\n", 
                currentTime, 
                currentPostage.id,
                currentPostage.remainingBurst
            );

            waitingQueue.offer( currentPostage );
            currentPostage = null;
            return true;
        }
        return false;
    }


    private void preemptPostageIfAvailable() {
        if (
            currentPostage != null &&
            !waitingQueue.isEmpty() &&
            currentPostage.remainingBurst > waitingQueue.peek().remainingBurst
        ) {
            //* Preemption happens by placing the current postage back into the waiting queue.
            //* The rest of selecting new job will be handled by selectNewPostageIfAvailable()
            System.out.printf(
                ">> ( t = %-4d): Parcel #%d preempted. Remaining burst = %d\n", 
                currentTime, 
                currentPostage.id,
                currentPostage.remainingBurst
            );

            waitingQueue.offer( currentPostage );
            currentPostage = null;
        }
    }


    @Override
    protected String postageToString(Postage p) {
        return String.format(
            "%-5d%-10d%-10d%-10d%-10d",
            p.id,
            p.arrivalTime,
            p.burstTime,
            p.completionTime,
            p.totalWaited
        );
    }

    @Override
    protected String getHeader() {
        return String.format(
            "%-5s%-10s%-10s%-10s%-10s",
            "ID", 
            "Pickup", 
            "Delivery",
            "Completed", 
            "Waited"
        );
    }


    @Override
    protected void printTitle() {
        Util.clearScreen();
        Util.printTitle("Shortest-Remaining-Time-First Postage");
        System.out.println();
    }
}
