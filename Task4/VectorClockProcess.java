import java.util.List;

public class VectorClockProcess extends Thread {
    private VectorClock clock;
    private int pid;
    private List<VectorClockProcess> pList;
    private List<Event> events;
    private OutPrinter print;

    static class Event {
        public static final int SEND = 1;
        public static final int RECV = 2;
        public static final int LOCAL = 3;

        public int action;
        public int target;

        public Event(int action) {
            this.action = action;
        }

        public Event(int action, int target) {
            this.action = action;
            this.target = target;
        }
    }

    public static class OutPrinter {
        public synchronized void printEvent(int procNum, int eventNum, VectorClock clock, Event event) {
            System.out.printf("Process %d: Event %d: clock=%s", procNum, eventNum, clock.toString());
            switch (event.action) {
                case Event.SEND:
                    System.out.printf(", Action=SEND, To Process=%d\n", event.target);
                    break;
                case Event.RECV:
                    System.out.printf(", Action=RECV, From Process=%d\n", event.target);
                    break;
                case Event.LOCAL:
                    System.out.println(", Action=LOCAL");
                    break;
                default:
                    System.out.println(", Unknown Action");
            }
        }
    }

    public VectorClockProcess(int pid, List<VectorClockProcess> pList,int pnumber, List<Event> events, OutPrinter print) {
        this.pid = pid;
        this.pList = pList;
        this.events = events;
        this.print = print;

        // Initialize the clock with the number of processes (pList.size()) and the process ID (pid)
        this.clock = new VectorClock(pnumber, pid);
    }

    @Override
    public void run() {
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);

            switch (event.action) {
                case Event.LOCAL:
                    clock.localStep();
                    break;
                case Event.SEND:
                    clock.sendEvent();
                    sendClockTo(event.target);
                    break;
                case Event.RECV:
                    int[] receivedClock = receiveClockFrom(event.target);
                    clock.receiveEvent(receivedClock);
                    break;
                default:
                    System.out.println("Unknown Event");
            }

            print.printEvent(pid, i + 1, clock, event);
        }
    }

    private void sendClockTo(int targetPid) {
        for (VectorClockProcess proc : pList) {
            if (proc.pid == targetPid) {
                proc.onClockReceived(clock.v);
                return;
            }
        }
    }

    private int[] receiveClockFrom(int targetPid) {
        while (true) {
            for (VectorClockProcess proc : pList) {
                if (proc.pid == targetPid) {
                    return proc.clock.v;
                }
            }
        }
    }

    public synchronized void onClockReceived(int[] receivedClock) {
        clock.receiveEvent(receivedClock);
    }
}
