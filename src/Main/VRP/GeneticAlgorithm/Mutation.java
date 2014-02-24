package Main.VRP.GeneticAlgorithm;
import Main.Utility;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.MutationOperators.InsertionMutation;
import Main.VRP.Individual.MutationOperators.InsertionMutationGreedy;
import Main.VRP.Individual.MutationOperators.MutatePeriodAssignment;
import Main.VRP.Individual.MutationOperators.MutateVehicleAssignmentGreedy;
import Main.VRP.Individual.MutationOperators.SwapMutation;
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
		int rand = 5;
		if(offspring.problemInstance.periodCount==1)rand--;
		
		int selectedMutationOperator = Utility.randomIntInclusive(rand);
		
		if(selectedMutationOperator==0)
		{
			SwapMutation.mutate(offspring);
		}
		else if (selectedMutationOperator == 1)
		{			
			InsertionMutationGreedy.mutate(offspring);
		}
		else if (selectedMutationOperator == 2)
		{
			InsertionMutation.mutateRouteWithInsertion(offspring);
//			offspring.mutateRouteWithInsertion();
		}
		else if (selectedMutationOperator == 3)
		{
			//offspring.mutateTwoDifferentRouteBySubstitution();
			MutateVehicleAssignmentGreedy.mutate(offspring);
		}
		else if (selectedMutationOperator == 4)
		{
			//offspring.mutateTwoDifferentRouteBySubstitution();
			Two_Opt.onAllROute(offspring);
		}
		
		else 
		{
		   MutatePeriodAssignment.mutatePeriodAssignment(offspring);
		}
		
		offspring.calculateCostAndPenalty();
		
	}

}
