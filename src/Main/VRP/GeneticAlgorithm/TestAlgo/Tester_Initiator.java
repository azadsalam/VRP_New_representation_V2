package Main.VRP.GeneticAlgorithm.TestAlgo;


import java.io.PrintWriter;

import Main.Solver;
import Main.Utility;
import Main.VRP.ProblemInstance;
import Main.VRP.GeneticAlgorithm.GeneticAlgorithm;
import Main.VRP.GeneticAlgorithm.PopulationInitiator;
import Main.VRP.GeneticAlgorithm.TotalCostCalculator;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.Initialise_ClosestDepot_withNoLoadViolation_Uniform_cut;


public class Tester_Initiator  implements GeneticAlgorithm
{
	PrintWriter out; 
	
	int POPULATION_SIZE = 1;
	int NUMBER_OF_OFFSPRING = 1;
	int NUMBER_OF_GENERATION = 1;
	
	ProblemInstance problemInstance;
	Individual population[];

	//for storing new generated offsprings
	Individual offspringPopulation[];

	//for temporary storing
	Individual temporaryPopulation[];

	// for selection - roulette wheel
	double fitness[];
	double cdf[];

	double loadPenaltyFactor;
	double routeTimePenaltyFactor;
	
	
	public Tester_Initiator(ProblemInstance problemInstance) 
	{
		// TODO Auto-generated constructor stub
		this.problemInstance = problemInstance;
		out = problemInstance.out;
		
		population = new Individual[POPULATION_SIZE];
		offspringPopulation = new Individual[NUMBER_OF_OFFSPRING];
		temporaryPopulation = new Individual[NUMBER_OF_GENERATION];
		
		fitness = new double[POPULATION_SIZE];
		cdf = new double[POPULATION_SIZE];
		
		loadPenaltyFactor = 10;
		routeTimePenaltyFactor = 10;
		
	}

	public Individual run() 
	{
		//problemInstance.print();
		// INITIALISE POPULATION
		
		//initialisePopulation();
		
		System.out.println("IN INITIATOR TESTER");
		initialisePopulation();
		TotalCostCalculator.calculateCostofPopulation(population,0,POPULATION_SIZE, loadPenaltyFactor, routeTimePenaltyFactor);
		Utility.sort(population);

		double min = population[0].costWithPenalty;
		double max = population[0].costWithPenalty;
		double total=0;
		
		for(int i=0;i<POPULATION_SIZE;i++)
		{
			if(population[i].costWithPenalty<min)
				min = population[i].costWithPenalty;
			if(population[i].costWithPenalty>max)
				max = population[i].costWithPenalty;
			
			total +=  population[i].costWithPenalty;
			
		}
		double infeasiblePercent=0;

		int invalid = 0;
		int infeasible = 0;
		for(int i=0; i<POPULATION_SIZE; i++)
		{
			if(population[i].validationTest()==false)
			{
				System.out.println("INVALID INDIVIDUAL");

				invalid++;
			}
			if(population[i].isFeasible==false)
			{
				infeasible++;
			}
			out.println("Printing individual "+ i +" : \n");
			population[i].print();
		}
		
		 infeasiblePercent= ((double)infeasible*100/POPULATION_SIZE);
		
		//System.out.printf("Max : %d Avg : %f Count : %d \n",Individual.max,(double)Individual.total/Individual.count,Individual.count);
		System.out.println("Best : "+min +" avg : "+(total/POPULATION_SIZE)+" worst : "+max+ "Infeasible : "+infeasiblePercent+"%  invalid : "+invalid);

		if(Solver.showViz==true)
		{
			Solver.visualiser.drawIndividual(population[0], "Best Initial");
			Solver.visualiser.drawIndividual(population[POPULATION_SIZE-1], "Worst Initial");
		}
		return population[0];
	}
	
	
	void initialisePopulation()
	{
		Individual.calculateAssignmentProbalityForDiefferentDepot(problemInstance);
		Individual.calculateProbalityForDiefferentVehicle(problemInstance);
		//out.print("Initial population : \n");
		for(int i=0; i<POPULATION_SIZE; i++)
		{
			population[i] = new Individual(problemInstance);
			//population[i].initialise_Closest_Depot_Uniform_Cut();
			Initialise_ClosestDepot_withNoLoadViolation_Uniform_cut.initiialise(population[i]);
			out.println("Printing Initial individual "+ i +" : \n");
			TotalCostCalculator.calculateCost(population[i], loadPenaltyFactor, routeTimePenaltyFactor);
			population[i].print();
		}
	}

	
	public int getNumberOfGeeration() {
		// TODO Auto-generated method stub
		return NUMBER_OF_GENERATION;
	}

}
