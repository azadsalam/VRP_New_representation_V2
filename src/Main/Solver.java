package Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

import javax.rmi.CORBA.Tie;

import Main.VRP.ProblemInstance;
import Main.VRP.GeneticAlgorithm.GeneticAlgorithm;
import Main.VRP.GeneticAlgorithm.Scheme6;
import Main.VRP.GeneticAlgorithm.Scheme6_With_Binary_Tournament;
import Main.VRP.GeneticAlgorithm.Scheme6_with_weighted_mutation;
import Main.VRP.GeneticAlgorithm.TotalCostCalculator;
import Main.VRP.GeneticAlgorithm.TestAlgo.MutationTest;
import Main.VRP.GeneticAlgorithm.TestAlgo.TestAlgo;
import Main.VRP.GeneticAlgorithm.TestAlgo.TestDistance;
import Main.VRP.GeneticAlgorithm.TestAlgo.Tester_Initiator;
import Main.VRP.Individual.Individual;


public class Solver 
{
	static public int numberOfmutationOperator=9;
	static public int episodeSize = 5;
	public static double loadPenaltyFactor = 10;
	public static double routeTimePenaltyFactor = 10;
	static public Visualiser visualiser;
	public static boolean showViz=false;
	public static boolean printProblemInstance= false;
	public static boolean onTest=false;
	String singleInputFileName = "benchmark/MDPVRP/pr03";
	String singleOutputFileName = "benchmark/MDPVRP/out.txt";
	String timeStamp = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(Calendar.getInstance().getTime());
	
	String reportFileName = "reports/report_"+timeStamp+".csv"; 
	//String[] instanceFiles={"benchmark/PVRP/p01"};
	/*String[] instanceFiles={"benchmark/PVRP/p01","benchmark/PVRP/p13","benchmark/PVRP/p32","benchmark/PVRP/pr06","benchmark/PVRP/pr10"
			,"benchmark/MDVRP/p01","benchmark/MDVRP/p14","benchmark/MDVRP/p23","benchmark/MDVRP/pr01","benchmark/MDVRP/pr05","benchmark/MDVRP/pr10"
			,"benchmark/MDPVRP/pr03","benchmark/MDPVRP/pr07","benchmark/MDPVRP/pr10"};
	
	*/
	
	//All mdpvrp
	String[] instanceFiles={"benchmark/MDPVRP/pr01","benchmark/MDPVRP/pr02","benchmark/MDPVRP/pr03"
			,"benchmark/MDPVRP/pr04","benchmark/MDPVRP/pr05","benchmark/MDPVRP/pr06"
			,"benchmark/MDPVRP/pr07","benchmark/MDPVRP/pr08","benchmark/MDPVRP/pr09"
			,"benchmark/MDPVRP/pr10"
			};
	int runSize=3;
	public static boolean singleRun = false;
	
	File inputFile,outputFile;	
	Scanner input;
	PrintWriter output;
	
	public static ExportToCSV exportToCsv;
		
	//ProblemInstance problemInstance;
	
	public static boolean writeToExcel;
	public static boolean generateAggregatedReport;
	public static boolean outputToFile;
	public static int mutateRouteOfTwoDiefferentFailed=0;

	
 	public ProblemInstance createProblemInstance(String inputFileName, String outputFileName)
	{
		ProblemInstance problemInstance=null;
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
				//output.println("Test cases (Now ignored): "+ testCases);
				//output.flush();
				problemInstance = new ProblemInstance(input,output);
				if(problemInstance.periodCount==1) numberOfmutationOperator--;
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
		return problemInstance;
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
		
		
		if(generateAggregatedReport)
			generateAggregatedReport();
		
		//single run
		if(writeToExcel) 
		{
			ProblemInstance problemInstance = createProblemInstance(singleInputFileName,singleOutputFileName);

			if(printProblemInstance)
				problemInstance.print();
			
			if(showViz)
				visualiser = new Visualiser("original/"+singleInputFileName.substring(0, singleInputFileName.length()-4),problemInstance);

			GeneticAlgorithm ga;
			if(!onTest)
				ga = new Scheme6_with_weighted_mutation(problemInstance);		
			else
				ga = new MutationTest(problemInstance);
			Solver.exportToCsv.init(ga.getNumberOfGeeration()+1);	
			ga.run();
			exportToCsv.createCSV();
		}
		
		//System.out.println("mutateTwoDifferentRouteBySwapping Failed : "+mutateRouteOfTwoDiefferentFailed);
		output.close();
		
		end= System.currentTimeMillis();
		
		long duration = (end-start) / 1000;
		long minute =  duration/ 60;
		long seconds = duration % 60;
		System.out.println("ELAPSED TIME : " + minute+ " minutes "+seconds+" seconds");
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
	
	public void generateAggregatedReport()
	{
	
		long start,end;
		
		start = System.currentTimeMillis();
		
		File reportFile = new File(reportFileName);
		PrintWriter reportOut=null;
		
		boolean once= true;
		try 
		{
			reportOut = new PrintWriter(reportFile);
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println(timeStamp );
		reportOut.println("Report Generation Time , "+timeStamp);
		
		for(int instanceNo=0;instanceNo<instanceFiles.length;instanceNo++)
		{
			ProblemInstance problemInstance = createProblemInstance(instanceFiles[instanceNo], singleOutputFileName);
			
			Scheme6_with_weighted_mutation ga = new Scheme6_with_weighted_mutation(problemInstance);
			
			if(once)
			{
				once=false;
				reportOut.format("Number Of Generation, Population Size, Offspring Population Size, LoadPenalty, RouteTime Penalty\n");
				reportOut.format("%d, %d, %d, %f, %f\n",ga.NUMBER_OF_GENERATION,ga.POPULATION_SIZE,ga.NUMBER_OF_OFFSPRING,loadPenaltyFactor,routeTimePenaltyFactor);
				reportOut.println();
				
				reportOut.println();
				reportOut.format("Instance Name, Min, Avg, Max, Feasible \n");

			}
			
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
				}
				sum += sol.costWithPenalty;
				if(sol.costWithPenalty>max) max = sol.costWithPenalty;
				if(sol.costWithPenalty<min) min = sol.costWithPenalty;
				
					
			}
			avg = sum/runSize;
		
			reportOut.format("%s, %f, %f, %f, %d \n",instanceFiles[instanceNo],min,avg,max,feasibleCount);
			reportOut.flush();
			System.out.format("%s, %f, %f, %f, %d \n",instanceFiles[instanceNo],min,avg,max,feasibleCount);
		}
		
		end= System.currentTimeMillis();
		
		long duration = (end-start) / 1000;
		long minute =  duration/ 60;
		long seconds = duration % 60;
		
		reportOut.println("\nELAPSED TIME : " + minute+ " minutes "+seconds+" seconds");
		reportOut.flush();
		reportOut.close();
	}
	
	
}
