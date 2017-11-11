package task4;
import CharStackExceptions.*;

public class StackManager
{
	// The Stack
	private static CharStack stack = new CharStack();
	private static final int NUM_ACQREL = 4; // Number of Producer/Consumer threads
	private static final int NUM_PROBERS = 1; // Number of threads dumping stack
	private static int iThreadSteps = 3; // Number of steps they take
	private static Semaphore mutex = new Semaphore(1);
    private static Semaphore full = new Semaphore(0);
    private static Semaphore empty = new Semaphore(stack.getSize());
    private static Semaphore consumer = new Semaphore(-1);
    private static Semaphore producer = new Semaphore(1);

	// The main()
	public static void main(String[] argv)
	{
		// Some initial stats...
		try
		{
			System.out.println("Main thread starts executing.");
			System.out.println("Initial value of top = " + stack.getTop() + ".");
			System.out.println("Initial value of stack top = " + stack.pick() + ".");
			System.out.println("Main thread will now fork several threads.");
		}
		catch(CharStackEmptyException e)
		{
			System.out.println("Caught exception: StackCharEmptyException");
			System.out.println("Message : " + e.getMessage());
			System.out.println("Stack Trace : ");
			e.printStackTrace();
		}
		/*
		 * The birth of threads
		 */
		Consumer ab1 = new Consumer();
		Consumer ab2 = new Consumer();
		System.out.println ("Two Consumer threads have been created.");
		Producer rb1 = new Producer();
		Producer rb2 = new Producer();
		System.out.println ("Two Producer threads have been created.");
		CharStackProber csp = new CharStackProber();
		System.out.println ("One CharStackProber thread has been created.");
		/*
		 * start executing
		 */
		ab1.start();
		rb1.start();
		ab2.start();
		rb2.start();
		csp.start();
		/*
		 * Wait by here for all forked threads to die
		 */
		try
		{
			ab1.join();
			ab2.join();
			rb1.join();
			rb2.join();
			csp.join();
			// Some final stats after all the child threads terminated...
			System.out.println("System terminates normally.");
			System.out.println("Final value of top = " + stack.getTop() + ".");
			System.out.println("Final value of stack top = " + stack.pick() + ".");
			System.out.println("Final value of stack top-1 = " + stack.getAt(stack.getTop() - 1) + ".");
			//System.out.println("Stack access count = " + stack.getAccessCounter());
		}
		catch(InterruptedException e)
		{
			System.out.println("Caught InterruptedException: " + e.getMessage());
			System.exit(1);
		}
		catch(Exception e)
		{
			System.out.println("Caught exception: " + e.getClass().getName());
			System.out.println("Message : " + e.getMessage());
			System.out.println("Stack Trace : ");
			e.printStackTrace();
		}
	} // main()
	/*
	 * Inner Consumer thread class
	 */
	static class Consumer extends BaseThread
	{
		private char copy; // A copy of a block returned by pop()
		public void run()
		{
			consumer.Wait();
			System.out.println ("Consumer thread [TID=" + this.iTID + "] starts executing.");
			for (int i = 0; i < StackManager.iThreadSteps; i++)  {
				full.Wait();
				mutex.Wait();
				try {
                    copy = stack.pop();
                }
                catch(CharStackEmptyException e){
                    System.out.println ("The stack is empty");
                }
				System.out.println("Consumer thread [TID=" + this.iTID + "] pops character = " + this.copy);
				mutex.Signal();
				empty.Signal();
			}
			System.out.println ("Consumer thread [TID=" + this.iTID + "] terminates.");
			consumer.Signal();
		}
	} // class Consumer
	
	/*
	 * Inner class Producer
	 */
	static class Producer extends BaseThread
	{
		private char block; // block to be returned
		public void run()
		{
			producer.Wait();
			System.out.println ("Producer thread [TID=" + this.iTID + "] starts executing.");
			for (int i = 0; i < StackManager.iThreadSteps; i++)  {
				empty.Wait();
				mutex.Wait();
				try {
                    block = stack.pick();
                }
                catch(CharStackEmptyException e){
                    System.out.println ("The stack is empty");
                    block = 'a';
                }
                try {
                    stack.push((char)(block + 1));
                }
                catch(CharStackFullException e){
                    System.out.println ("The stack is full");
                }
                mutex.Signal();
                full.Signal();
                System.out.println("Producer thread [TID=" + this.iTID + "] pushes character = " + this.block);
			}
			//consumer.Signal();
			System.out.println("Producer thread [TID=" + this.iTID + "] terminates.");
			consumer.Signal();
			System.out.println("Post increment");
			producer.Signal();
		}
	} // class Producer
	
	/*
	 * Inner class CharStackProber to dump stack contents
	 */
	static class CharStackProber extends BaseThread
	{
		public void run()
		{
			System.out.println("CharStackProber thread [TID=" + this.iTID + "] starts executing.");
			String result = new String();
			for (int i = 0; i < 2 * StackManager.iThreadSteps; i++)
			{
				result = "Stack S =(";
				for (int y = 0; y < stack.getSize(); y++) {
					try {
						result += "[" + stack.getAt(y) + "]";
						if (y + 1 < stack.getSize()) {
							result += ",";
						} else {
							//Do nothing
						}
					} catch (CharStackInvalidAceessException e) {
						System.out.println ("Trying to access an index out of bounds");
					}
				}
				result += ")";
			}
			System.out.println(result);
		}
	} // class CharStackProber
} // class StackManager