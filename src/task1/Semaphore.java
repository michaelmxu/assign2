// Source code for semaphore class:
package task1;
class Semaphore 
{
         private int value;
         public Semaphore(int value)
         {
                  this.value = value;
         }
        public Semaphore()
        {
                 this(0);
         }
        public synchronized void Wait()
        {
                  while (this.value <= 0)
                  {
                         try
                        {
                        	 this.value--;
                             wait();
                             this.value++;
                         }
                        catch(InterruptedException e)
                        {
                                 System.out.println ("Semaphore::Wait() - caught InterruptedException: " + e.getMessage() );
                                 e.printStackTrace();
                            }
                    }
                    this.value--;    
           }
           public synchronized void Signal()
           {
                   ++this.value;
                   notify();
           }
           public synchronized void P()
           {
                   this.Wait();
           }
          public synchronized void V()
          {
                   this.Signal();
          }
}

// Source code for character stack:

