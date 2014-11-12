package watermelon.group1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import watermelon.sim.Pair;
import watermelon.sim.seed;
import watermelon.group1.Solution;

public class Player extends watermelon.sim.Player {
	public void init() {

	}

	@Override
	public ArrayList<seed> move(ArrayList<Pair> treelist, double width, double height, double s) {
		long startTime = System.currentTimeMillis();
		long timeLimit = startTime + 60 * 60 * 1000;
		long lastIterationLength = 0;
		
		int maxIterations = 10;
		int numIterations = 0;
		Solution bestIteration = null;
		int bestIterationNum = 0;
		
		// Transform input parameters from simulator classes into our preferred class
		ArrayList<Location> trees = new ArrayList<Location>();
		for (Pair p : treelist)
			trees.add(new Location(p.x, p.y));

		while (numIterations < maxIterations && timeLimit - System.currentTimeMillis() > lastIterationLength) {
			System.out.printf("Iteration #%d\n", numIterations);
			System.out.printf("-------------\n");
			
			long iterationStartTime = System.currentTimeMillis();
			String mapName = "CHANGE THIS TO MAP NAME";
			boolean testMethod = false;
			ArrayList<Solution> possibleSolutions;
			
			// use this variable for testing a particular method
			if (testMethod) {
				possibleSolutions = new ArrayList<Solution>();
				// choose a packing method
				Solution solution = new Solution(PackAlgos.hexagonal(trees, width, height, PackAlgos.Corner.BR, PackAlgos.Direction.V, true, null), trees, width, height);
				ColoringAlgos.colorMaxValue(solution.seedNodes, new Location(width/2, height/2));
				solution.coloringAlgo = "test";
				solution.packingAlgo = "test";
				possibleSolutions.add(solution);
			} else {
				// Get all possible packings/colorings
				possibleSolutions = generateAllPossibleSolutions(trees, width, height, s);
			}
			
			// Now find the best one
			StringBuffer resultsLog = new StringBuffer();
			resultsLog.append("Map, Iteration, Packing Algo, Coloring Algo, score");
			Solution bestSolution = new Solution();
			double bestScore = 0;
			for (Solution solution : possibleSolutions) {
				double newScore = solution.getScore(s);
				String resultLine = String.format("%s, %d, %s, %s, %f\n", mapName, numIterations, solution.packingAlgo, solution.coloringAlgo, newScore);
				resultsLog.append(resultLine);
				if (newScore > bestScore) {
					bestScore = newScore;
					bestSolution = solution;
				}
			}
			
			// Write the log to a file
			BufferedWriter out = null;
			String logFileName = "ResultsLog.txt";
			try  
			{
			    FileWriter fstream = new FileWriter(logFileName, true);
			    out = new BufferedWriter(fstream);
			    out.write(resultsLog.toString());
			    out.close();
			}
			catch (IOException e)
			{
			    System.err.println("Could not write log file.");
			}
			
			System.out.println("Best score before jiggling is " + bestScore);
			
			// Now try jiggling it
			Solution jiggledSolution = jiggleSolution(bestSolution, s, timeLimit);
			System.out.println("Best score after jiggling is " + jiggledSolution.getScore(s));
			
			// Print which configuration was best
			System.out.println("Best config in this iteration:");
			System.out.println("\tPacking: " + jiggledSolution.packingAlgo);
			System.out.println("\tColoring: " + jiggledSolution.coloringAlgo);
			System.out.println("\tJiggling: " + jiggledSolution.jiggleAlgo);
			
			long currentTime = System.currentTimeMillis();
			lastIterationLength = currentTime - iterationStartTime;
			System.out.println("Total iteration time: " + (lastIterationLength)/1000 + "s");
			
			if (bestIteration == null || jiggledSolution.getScore(s) > bestIteration.getScore(s)) {
				bestIteration = jiggledSolution;
				bestIterationNum = numIterations;
				System.out.printf("Iteration %d beats previous best iteration\n", numIterations);
			}
			
			if (testMethod)
				break;
			
			numIterations++;
			
			System.out.printf("\n\n");
		}
	
		System.out.printf("Final Result\n");
		System.out.printf("------------\n");
		System.out.printf("Iteration #%d\n", bestIterationNum);
		System.out.println("\tPacking: " + bestIteration.packingAlgo);
		System.out.println("\tColoring: " + bestIteration.coloringAlgo);
		System.out.println("\tJiggling: " + bestIteration.jiggleAlgo);
		System.out.println("\tScore: " + bestIteration.getScore(s));
		
		System.out.printf("\n");
		System.out.println("Total execution time: " + (System.currentTimeMillis() - startTime)/1000 + "s");
		
		// Transform our output into the simulator classes and return it
		return bestIteration.simRepresentation();
	}
	
	private static Solution jiggleSolution(Solution baseSolution, double s, long timeLimit) {
		Solution newSolution;
		baseSolution.jiggleAlgo = "none";
		Solution bestSolution = baseSolution;
		ArrayList<Solution> jiggledSolutions = new ArrayList<Solution>();
		jiggledSolutions.add(baseSolution);
		
		newSolution = baseSolution.deepDuplicate();
		JiggleAlgos.jiggleIterative(newSolution, s, timeLimit);
		jiggledSolutions.add(newSolution);
		
		// return whichever "jiggle solution" has the highest score
		double bestScore = 0;
		for (Solution solution : jiggledSolutions) {
			double newScore = solution.getScore(s);
			if (newScore > bestScore) {
				bestScore = newScore;
				bestSolution = solution;
			}
		}
		
		return bestSolution;
	}
	
	private static ArrayList<Solution> generateAllPossibleSolutions(ArrayList<Location> trees, double width, double height, double s) {
		System.out.println("generateAllPossibleSolutions called");
		ArrayList<Solution> packings = generateAllPackings(trees, width, height);
		System.out.println("Generated all packings");
		ArrayList<Solution> actualSolutions = new ArrayList<Solution>();
		
		Solution newSolution;
		for (Solution packing : packings) {
			newSolution = packing.deepDuplicate();
			ColoringAlgos.colorAdjacent(newSolution.seedNodes);
			newSolution.coloringAlgo = "adjacent";
			actualSolutions.add(newSolution);
			
			newSolution = packing.deepDuplicate();
			ColoringAlgos.colorConcentric(newSolution.seedNodes, new Location(width/2, height/2));
			newSolution.coloringAlgo = "concentric from center";
			actualSolutions.add(newSolution);
			
			newSolution = packing.deepDuplicate();
			ColoringAlgos.colorConcentric(newSolution.seedNodes, new Location(0, 0));
			newSolution.coloringAlgo = "concentric from UL corner";
			actualSolutions.add(newSolution);
			
			newSolution = packing.deepDuplicate();
			ColoringAlgos.colorMaxValue(newSolution.seedNodes, new Location(width/2, height/2));
			newSolution.coloringAlgo = "max value from center";
			actualSolutions.add(newSolution);
			
			newSolution = packing.deepDuplicate();
			ColoringAlgos.colorMaxValue(newSolution.seedNodes, new Location(0,0));
			newSolution.coloringAlgo = "max value from UL corner";
			actualSolutions.add(newSolution);
		}
		
		System.out.println("Generated all colorings");
		return actualSolutions;
	}
	
	private static ArrayList<Solution> generateAllPackings(ArrayList<Location> trees, double width, double height) {
		ArrayList<Solution> packings = new ArrayList<Solution>();
		
		Solution newSolution;
		
		// Rectilinear
		for (PackAlgos.Corner corner : PackAlgos.Corner.values()) {
			newSolution = new Solution(PackAlgos.rectilinear(trees, width, height, corner, false, null), trees, width, height);
			newSolution.packingAlgo = "rectilinear from " + corner + " corner";
			if (newSolution.seedNodes.size() > 0)
				packings.add(newSolution);
			
			for (Location tree : trees) {
				newSolution = new Solution(PackAlgos.rectilinear(trees, width, height, corner, false, tree), trees, width, height);
				newSolution.packingAlgo = "rectilinear from " + corner + " corner around tree at " + tree.x + ", " + tree.y;
				if (newSolution.seedNodes.size() > 0)
					packings.add(newSolution);
			}
		}
		
		System.out.println("Generated all Rectilinear packings");
		
		// Hex
		for (PackAlgos.Corner corner : PackAlgos.Corner.values()) {
			for (PackAlgos.Direction dir : PackAlgos.Direction.values()) {
				for (int i = 0; i < 2; i++) {
					newSolution = new Solution(PackAlgos.hexagonal(trees, width, height, corner, dir, i == 0, null), trees, width, height);
					newSolution.packingAlgo = "hex from " + corner + " corner, " + dir + " direction" + (i == 0 ? " with spread" : "");
					if (newSolution.seedNodes.size() > 0)
						packings.add(newSolution);
					
					for (Location tree : trees) {
						newSolution = new Solution(PackAlgos.hexagonal(trees, width, height, corner, dir, i == 0, tree), trees, width, height);
						newSolution.packingAlgo = "hexagonal around tree at " + tree.x + ", " + tree.y  + " from " + corner + " corner in " + dir + " direction" + (i == 0 ? " with spread" : "");;
						if (newSolution.seedNodes.size() > 0)
							packings.add(newSolution);
					}
				}
			}
		}
		
		System.out.println("Generated all Hex packings");
		
		// Best Known
		ArrayList<Location> packing = PackAlgos.bestKnown(trees, width, height);
		if (packing != null) {
			newSolution = new Solution(packing, trees, width, height);
			newSolution.packingAlgo = "bestKnown";
			if (newSolution.seedNodes.size() > 0)
				packings.add(newSolution);
			System.out.println("Generated Best Known packing");
		} else {
			System.out.println("Failed to generate Best Known packing");
		}
		
		// Physical
		newSolution = new Solution(PackAlgos.physical(trees, width, height), trees, width, height);
		newSolution.packingAlgo = "physical";
		if (newSolution.seedNodes.size() > 0)
			packings.add(newSolution);

		System.out.println("Generated Physical packing");
		
		return packings;
	}
}