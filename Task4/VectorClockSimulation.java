import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class VectorClockSimulation {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        VectorClockProcess.OutPrinter printer = new VectorClockProcess.OutPrinter();
        List<VectorClockProcess> pList = new ArrayList<>();

        System.out.print("How many processes? ");
        int numProcesses = sc.nextInt();

        // Generate events for each process first, before creating VectorClockProcess instances
        List<List<VectorClockProcess.Event>> allEvents = new ArrayList<>();
        for (int i = 0; i < numProcesses; i++) {
            System.out.printf("Generating events for process %d\n", i + 1);
            List<VectorClockProcess.Event> events = generateRandomEvents(numProcesses);
            allEvents.add(events);
        }

        // Now, create and initialize VectorClockProcess instances using the events
        for (int i = 0; i < numProcesses; i++) {
            System.out.printf("Creating process %d\n", i + 1);
            VectorClockProcess proc = new VectorClockProcess(i, pList,numProcesses,allEvents.get(i), printer);
            pList.add(proc);  // Now pList is populated with the processes
        }

        // Now, start all the processes
        for (VectorClockProcess p : pList) {
            p.start();
        }

        // Wait for all processes to finish
        for (VectorClockProcess p : pList) {
            try {
                p.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        sc.close();
    }

    private static List<VectorClockProcess.Event> generateRandomEvents(int numProcesses) {
        Random rand = new Random();
        List<VectorClockProcess.Event> events = new ArrayList<>();
        int numEvents = rand.nextInt(5) + 3; // Random number of events between 3 and 7
        for (int i = 0; i < numEvents; i++) {
            int action = rand.nextInt(3) + 1; // Random action between 1 and 3
            int target = 0;
            if (action == VectorClockProcess.Event.SEND || action == VectorClockProcess.Event.RECV) {
                target = rand.nextInt(numProcesses); // Random target process based on total processes
            }
            events.add(new VectorClockProcess.Event(action, target));
        }
        return events;
    }
}
