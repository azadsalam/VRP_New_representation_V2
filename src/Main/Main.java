package Main;
import java.io.FileNotFoundException;

public class Main 
{
	
	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException 
	{
	//	System.out.println("Saikat!");
		
		final Solver solver = new Solver();
		solver.initialise();
		Thread thread1=new Thread() {			
			@Override
			public void run() {
				solver.solve();	
				
			}
		};
		//Thread thread2=new visualize();
		
		thread1.start();
		//thread2.start();
		
		
	}

}
