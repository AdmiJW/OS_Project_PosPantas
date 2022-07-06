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
        while (!arrivals.isEmpty() || !waitingQueue.isEmpty() || currentPostage != null) {
            if (currentPostage == null) {
                handleArrivals();
                selectNewPostageFromWaitingQueue();
            } 
            else {
                while ( handleArrivals() ) {};
                deliverCurrentPostageUntilCompletion();
            }
        }



        // Keep iterating while we not all postage is delivered.
        //      Condition 1 - Still got pending arrivals
        //      Condition 2 - Got ongoing postage

        // while ( !arrivals.isEmpty() || currentPostage != null ) {
        //     //* If the current executing postage finish first */
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
                
        //         // Let postages arrive first if any, instead of directly selecting from the waiting queue
        //         if (!arrivals.isEmpty() && arrivals.peek().arrivalTime == currentTime) continue;

        //         // Select new postage, if there are still postages to be done
        //         selectNewPostageIfAvailable();
        //     }
        //     //* Otherwise another job arrives first */
        //     else {
        //         // Update the waiting time for all in waitingQueue
        //         for (Postage p: waitingQueue) p.totalWaited += arrivals.peek().arrivalTime - currentTime;
        //         // Update time
        //         currentTime = arrivals.peek().arrivalTime;
        //         // Add the new work into the queue.
        //         waitingQueue.offer( arrivals.poll() );

        //         // Select new postage, in case if no postage is currently being processed
        //         selectNewPostageIfAvailable();
        //     }
        // }
    }


    // The main purpose of this function is to handle arriving packages from the arrival queue
    // and push it into waiting queue as needed.
    //? Push to queue with condition:
    //? 1. Waiting queue is empty && curr is null (Eg: When algorithm first starts)
    //? 2. Completion time of curr is exceed the peek's arrival time. (When a package is currently being delivered)
    //? 3. Current time is exceed than arrival time. (Eg: After package delivery is completed)
    //
    //! For pickup packages of same time, they shall be added in bulk
    //! What happens when arrival === completion?
    //! Non-preemptive: The order doesn't matter.
    //! Preemptive: Package delivery shall complete first, preempting when a package is going to complete is unreasonable
    protected boolean handleArrivals() {
        if (arrivals.isEmpty()) return false;

        if (
            (waitingQueue.isEmpty() && currentPostage == null) ||
            (currentTime >= arrivals.peek().arrivalTime) ||
            (currentPostage != null && currentPostage.completionTime > arrivals.peek().arrivalTime)
        ) {
            int t = arrivals.peek().arrivalTime;

            // Update times
            for (Postage p: waitingQueue) p.totalWaited += t - currentTime;
            if (currentPostage != null) currentPostage.remainingBurst -= t - currentTime;
            currentTime = t;

            // Add the arriving packages in bulk
            while (!arrivals.isEmpty() && arrivals.peek().arrivalTime == t)
                waitingQueue.add( arrivals.poll() );

            return true;
        }
        return false;
    }



    // Use this when you are sure that the current postage will be delivered until complete.
    protected void deliverCurrentPostageUntilCompletion() {
        // Update the waiting time for all in waitingQueue
        for (Postage p: waitingQueue) p.totalWaited += currentPostage.completionTime - currentTime;
        // Push the postage to completed list
        completed.add( currentPostage );
        // Update time
        currentTime = currentPostage.completionTime;
        // The current postage is completed
        System.out.printf(">> ( t = %-4d): Parcel #%d delivered!\n", currentTime, currentPostage.id);
        currentPostage = null;
    }


    protected void selectNewPostageFromWaitingQueue() {
        currentPostage = waitingQueue.poll();
        currentPostage.lastStarted = currentTime;
        currentPostage.completionTime = currentTime + currentPostage.remainingBurst;

        System.out.printf(">> ( t = %-4d): Parcel #%d out for delivery!\n", currentTime, currentPostage.id);
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
            p.burstTime,
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
