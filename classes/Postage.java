package classes;


public class Postage {
    public int id;
    public int arrivalTime;
    public int burstTime;
    public int remainingBurst;
    public int completionTime;
    public int totalWaited;
    public int lastStarted;
    public int priority;

    public Postage(int id, int arrival, int burst) {
        this.id = id;
        this.arrivalTime = arrival;
        this.burstTime = burst;
        this.remainingBurst = burst;
        this.completionTime = -1;
        this.totalWaited = 0;
        this.lastStarted = -1;
        this.priority = 0;
    }

    public Postage(int id, int arrival, int burst, int priority) {
        this(id, arrival, burst);
        this.priority = priority;
    }


    public static int compareBurstAscending(Postage a, Postage b) {
        return a.remainingBurst - b.remainingBurst;
    }

    public static int compareIDAscending(Postage a, Postage b) {
        return a.id - b.id;
    }

    public static int compareArrivalAscending(Postage a, Postage b) {
        return a.arrivalTime - b.arrivalTime;
    }

    public static int comparePriorityAscending(Postage a, Postage b) {
        return a.priority - b.priority;
    }

    public static int comparePriorityThenBurstAscending(Postage a, Postage b) {
        if ( comparePriorityAscending(a, b) == 0 ) return compareArrivalAscending(a, b);
        return comparePriorityAscending(a, b);
    }
}