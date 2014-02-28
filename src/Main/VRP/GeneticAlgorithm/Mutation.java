package Main.VRP.GeneticAlgorithm;
import Main.Utility;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.MutationOperators.IntraRouteRandomInsertion;
import Main.VRP.Individual.MutationOperators.IntraRouteGreedyInsertion;
import Main.VRP.Individual.MutationOperators.MutatePeriodAssignment;
import Main.VRP.Individual.MutationOperators.GreedyVehicleReAssignment;
import Main.VRP.Individual.MutationOperators.IntraRouteRandomSwap;
import Main.VRP.Individual.MutationOperators.OneOneExchange;
import Main.VRP.Individual.MutationOperators.OneZeroExchange;
import Main.VRP.Individual.MutationOperators.Two_Opt;


public class Mutation 
{	
	//ProblemInstance problemInstance;
	
	
	/*
	public Mutation(ProblemInstance problemInstance) 
	{
		// TODO Auto-generated constructor stub
		this.problemInstance = problemInstance;
	}
	*/
	
	
	public void applyMutation(Individual offspring)
	{
		int rand = 6;
		if(offspring.problemInstance.periodCount==1)rand--;
		
		int selectedMutationOperator = Utility.randomIntInclusive(rand);
		
		if(selectedMutationOperator==0)
		{
			IntraRouteGreedyInsertion.mutate(offspring);
		}
		else if (selectedMutationOperator == 1)
		{			
			GreedyVehicleReAssignment.mutate(offspring);
		}
		else if (selectedMutationOperator == 2)
		{
			OneZeroExchange.mutate(offspring);
//			offspring.mutateRouteWithInsertion();
		}
		else if (selectedMutationOperator == 3)
		{
			//offspring.mutateTwoDifferentRouteBySubstitution();
			IntraRouteRandomInsertion.mutate(offspring);
		}
		else if (selectedMutationOperator == 4)
		{
			Two_Opt.mutateRandomRoute(offspring);
		}
		else if (selectedMutationOperator == 5)
		{
			OneOneExchange.mutate(offspring);
		}
		else 
		{
		   MutatePeriodAssignment.mutatePeriodAssignment(offspring);
		}
		offspring.calculateCostAndPenalty();
		
	}

}
