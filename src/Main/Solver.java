package Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import Main.VRP.ProblemInstance;
import Main.VRP.GeneticAlgorithm.GeneticAlgorithm;
import Main.VRP.GeneticAlgorithm.Scheme6;
import Main.VRP.GeneticAlgorithm.TestAlgo.MutationTest;
import Main.VRP.GeneticAlgorithm.TestAlgo.TestDistance;
import Main.VRP.GeneticAlgorithm.TestAlgo.Tester_Initiator;
import Main.VRP.Individual.Individual;



public class Solver 
{
	static public Visualiser visualiser;
	public static boolean showViz=false;
	String inputFileName = "benchmark/MDVRP/p01";
	String outputFileName = "benchmark/MDVRP/out02.txt";
	int runSize=5;
	boolean singleRun = true;
	
	File inputFile,outputFile;	
	Scanner input;
	PrintWriter output;
	
	public static ExportToCSV exportToCsv;
		
	ProblemInstance problemInstance;
	
	public static boolean writeToExcel;
	public static boolean generateAggregatedReport;
	public static boolean outputToFile;
	public static int mutateRouteOfTwoDiefferentFailed=0;

	public void initialise() 
	{
		try
		{
			inputFile = new File(inputFileName);
			input = new Scanner(inputFile);
			
			outputFile = new File(outputFileName);
			//output = new PrintWriter(System.out);//for console output
			output = new PrintWriter(outputFile);//for file output
						
			
			int testCases = 1;			
			
			exportToCsv = new ExportToCSV(inputFileName);
			//exportToCsv.createCSV(10);
			
			if(inputFileName.startsWith("benchmark"))
				problemInstance = new ProblemInstance(input,output,true);
			else
			{
				testCases = input.nextInt(); 
				input.nextLine(); // escaping comment
				// FOR NOW IGNORE TEST CASES, ASSUME ONLY ONE TEST CASE
				output.println("Test cases (Now ignored): "+ testCases);
				output.flush();
				problemInstance = new ProblemInstance(input,output);
			}
		}
		catch (FileNotFoundException e)
		{
			System.out.println("FILE DOESNT EXIST !! EXCEPTION!!\n");
		}
		catch (Exception e) 
		{
			// TODO: handle exception
			System.out.println("EXCEPTION!!\n");
			e.printStackTrace();
		}
	}
	
	
	public void solve() 
	{
		// singlerun = true when excel needs to be generated or output checked for testing
		// sigleRun = false when aggregated report is to be generated
		
		long start,end;
		
		start = System.currentTimeMillis();
		
		writeToExcel = singleRun;
		outputToFile = singleRun;
		generateAggregatedReport = !singleRun;
		
		problemInstance.print();
	
		//if(true)return;
		
		if(showViz)
			visualiser = new Visualiser("original/"+inputFileName.substring(0, inputFileName.length()-4),problemInstance);
		
		
		GeneticAlgorithm ga = new Scheme6(problemInstance);		
		if(writeToExcel) 
		{
			Solver.exportToCsv.init(ga.getNumberOfGeeration()+1);	
			ga.run();
			exportToCsv.createCSV();
		}
		
		
		if(generateAggregatedReport)
			generateAggregatedReport(ga);
		
		
		System.out.println("mutateTwoDifferentRouteBySwapping Failed : "+mutateRouteOfTwoDiefferentFailed);
		output.close();
		
		end= System.currentTimeMillis();
		
		double minute = ((double)end-start) / 1000 / 60;
		System.out.println("ELAPSED TIME : " + minute);
	}
	
	
	/**
	 * gathers the min,avg and max cost \n
	 * Assumes all individuals cost+penalty is evaluated already
	 * Also assumes actual population is in [0,populationSize)
	 * @param population
	 * @param populationSize
	 * @param generation
	 */
	public static void gatherExcelData(Individual[] population,int populationSize,int generation)
	{
		if(writeToExcel)
		{
			
			double sum=0,avg,penalty;
			double min =0xFFFFFFF;
			double max = -1;
			int feasibleCount = 0;
			
			for(int i=0; i<populationSize; i++)
			{
				sum += population[i].costWithPenalty;
				if(population[i].costWithPenalty > max) max = population[i].costWithPenalty;
				if(population[i].costWithPenalty < min) min = population[i].costWithPenalty;
				if(population[i].isFeasible) feasibleCount++;
			}
			
			avg = sum / populationSize;

		
			exportToCsv.min[generation] = min;
			exportToCsv.avg[generation] = avg;
			exportToCsv.max[generation] = max;
			exportToCsv.feasibleCount[generation] = feasibleCount;
		}
	}
	
	public void generateAggregatedReport(GeneticAlgorithm ga)
	{
		double min = 0xFFFFFF;
		double max = -1;
		double sum = 0;
		double avg;
		int feasibleCount=0;

		for(int i=0; i<runSize; i++)
		{			
			Individual sol = ga.run();
			
			if(sol.isFeasible==true)
			{
				feasibleCount++;
				sum += sol.cost;
				if(sol.cost>max) max = sol.cost;
				if(sol.cost<min) min = sol.cost;
			}
				
		}
		avg = sum/feasibleCount;
		
		System.out.format("Min : %f Avg : %f  Max : %f Feasible : %d \n",min,avg,max,feasibleCount);
	}
	
	
}
