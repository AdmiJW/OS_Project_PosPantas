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
