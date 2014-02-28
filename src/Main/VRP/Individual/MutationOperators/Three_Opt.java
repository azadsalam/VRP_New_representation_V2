package Main.VRP.Individual.MutationOperators;
import java.lang.reflect.Array;
import java.util.ArrayList;
import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.RouteUtilities;

public class Three_Opt {

	public static void mutateRandomRoute(Individual individual)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		//boolean success = false;
		//do
		//{
			int period = Utility.randomIntInclusive(problemInstance.periodCount-1);
			int vehicle = Utility.randomIntInclusive(problemInstance.vehicleCount-1);
			mutateRouteBy3_Opt(individual, period, vehicle);
			
			//success = mutateRouteBy2_Opt(individual,period, vehicle);
		//}while(success==false);
	}
	
	public static void onAllROute(Individual individual)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		
	//	mutateRouteBy2_Opt(individual, 0, 1);if(true)return;
		for(int period=0;period<problemInstance.periodCount;period++)
		{
			for(int vehicle = 0;vehicle<problemInstance.vehicleCount;vehicle++)
			{
				mutateRouteBy3_Opt(individual, period, vehicle);
			}
		}
	}
	
	/**
	 * 
	 * @param individual
	 * @param period
	 * @param vehicle
	 * @return false if cost is not decreased
 	 */
	public static boolean mutateRouteBy3_Opt(Individual individual, int period, int vehicle)
	{
		ProblemInstance problemInstance = individual.problemInstance;
		ArrayList<Integer> route;
		

		for(int k=3;k>=1;k--)
		{
			route = individual.routes.get(period).get(vehicle);
			for(int clientIndex=0;clientIndex<route.size();clientIndex++)
			{
				int client = route.get(clientIndex);
			}
		}
	}

	
}
