package PriorityP;

import PriorityNP.PriorityNP;
import classes.*;



public class PriorityP extends PriorityNP {
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
        if (!waitingQueue.isEmpty() && Postage.comparePriorityThenBurstAscending(currentPostage, waitingQueue.peek() ) > 0) {
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
            "Pickup", 
            "Delivery",
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
