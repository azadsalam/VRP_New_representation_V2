package Main.VRP.GeneticAlgorithm;
import java.util.Random;

import Main.VRP.ProblemInstance;
import Main.VRP.Individual.Individual;
import Main.VRP.Individual.RandomInitialisation;

public class PopulationInitiator 
{
	public static void initialisePopulation(Individual[] population,int populationSize,ProblemInstance problemInstance)
	{
		//	out.print("Initial population : \n");
		for(int i=0; i<populationSize; i++)
		{
			population[i] = new Individual(problemInstance);
			if(i%5 == 0)
				RandomInitialisation.initialiseRandom(population[i]);
			else if(i%5 == 1)
				population[i].initialise_Closest_Depot_Uniform_Cut();
			else 
				population[i].initialise_Closest_Depot_Greedy_Cut();
			//problemInstance.out.println("Printing individual "+ i +" : \n");
			//population[i].print();
		}
	}
}
