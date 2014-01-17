package Main.VRP.GeneticAlgorithm;
import Main.VRP.Individual.Individual;


public interface GeneticAlgorithm 
{
	
	public Individual run();
	public int getNumberOfGeeration();
}
