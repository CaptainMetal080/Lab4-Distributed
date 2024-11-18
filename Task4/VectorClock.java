public class VectorClock {
   int[] v;
   int myId;
   int N;

   // Initialize the vector clock for a process
   public VectorClock(int numProc, int id) {
      myId = id;
      N = numProc;
      v = new int[numProc];
      for (int i = 0; i < N; i++) {
         v[i] = 0;
      }
      v[myId] = 1;
   }

   // Internal step for the process
   public void localStep() {
      v[myId]++;
   }

   // Send event: increment the local timestamp and send the vector clock
   public void sendEvent() {
      v[myId]++;
   }

   // Receive event: update the vector clock based on the received vector clock
   public void receiveEvent(int[] sentValue) {
      for (int i = 0; i < N; i++) {
         v[i] = max(v[i], sentValue[i]);
      }
      v[myId]++;
   }

   // Get the value of the clock for process i
   public int getValue(int i) {
      return v[i];
   }

   // Maximum of two integers
   public int max(int a, int b) {
      return a > b ? a : b;
   }

   // Convert the vector clock to a string for printing
   public String toString(){
      StringBuilder s = new StringBuilder();
      for (int j = 0; j < v.length; j++) {
         s.append(v[j] + " ");
      }
      return s.toString();
   }
}
