package watermelon.group1;

import java.util.*;

import watermelon.sim.Pair;
import watermelon.sim.seed;
import watermelon.group1.Consts;
import watermelon.group1.Solution;

public class Player extends watermelon.sim.Player {
	public void init() {

	}

	@Override
	public ArrayList<seed> move(ArrayList<Pair> treelist, double width, double height, double s) {
		long startTime = System.nanoTime(); 
		
		// Transform input parameters from simulator classes into our preferred class
		ArrayList<Location> trees = new ArrayList<Location>();
		for (Pair p : treelist)
			trees.add(new Location(p.x, p.y));
		
		boolean testMethod = false;
		ArrayList<Solution> possibleSolutions;
		
		// use this variable for testing a particular method
		if (testMethod) {
			possibleSolutions = new ArrayList<Solution>();
			// choose a packing method
			Solution solution = new Solution(PackAlgos.rectilinear(trees, width, height, PackAlgos.Corner.UL, false), trees, width, height);
			ColoringAlgos.colorMaxValue(solution.seedNodes, new Location(width/2, height/2));
			solution.coloringAlgo = "test";
			solution.packingAlgo = "test";
			possibleSolutions.add(solution);
		} else {
			// Get all possible packings/colorings
			possibleSolutions = generateAllPossibleSolutions(trees, width, height, s);
		}
		
		// Now find the best one
		Solution bestSolution = new Solution();
		double bestScore = 0;
		for (Solution solution : possibleSolutions) {
			double newScore = solution.getScore(s);
			if (newScore > bestScore) {
				bestScore = newScore;
				bestSolution = solution;
			}
		}
		System.err.println("Best score before jiggling is " + bestScore);
		
		// Now try jiggling it
		Solution jiggledSolution = jiggleSolution(bestSolution, s);
		System.err.println("Best score after jiggling is " + jiggledSolution.getScore(s));
		
		// Print which configuration was best
		System.out.println("Winning config:");
		System.out.println("\tPacking: " + jiggledSolution.packingAlgo);
		System.out.println("\tColoring: " + jiggledSolution.coloringAlgo);
		System.out.println("\tJiggling: " + jiggledSolution.jiggleAlgo);
		
		double estimatedTime = System.nanoTime() - startTime;
		System.out.println("Total time: " + estimatedTime/1000000000 + "s");
		
		// Transform our output into the simulator classes and return it
		return jiggledSolution.simRepresentation();
	}
	
	private static Solution jiggleSolution(Solution baseSolution, double s) {
		Solution newSolution;
		baseSolution.jiggleAlgo = "none";
		Solution bestSolution = baseSolution;
		ArrayList<Solution> jiggledSolutions = new ArrayList<Solution>();
		jiggledSolutions.add(baseSolution);
		
		newSolution = baseSolution.deepDuplicate();
		JiggleAlgos.jiggleIterative(newSolution, s);
		newSolution.jiggleAlgo = "iterative";
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
		System.err.println("generateAllPossibleSolutions called");
		ArrayList<Solution> packings = generateAllPackings(trees, width, height);
		System.err.println("Generated all packings");
		ArrayList<Solution> actualSolutions = new ArrayList<Solution>();
		
		Solution newSolution;
		for (Solution packing : packings) {
			newSolution = packing.deepDuplicate();
			ColoringAlgos.colorAdjacent(newSolution.seedNodes);
			newSolution.coloringAlgo = "adjacent";
			actualSolutions.add(newSolution);
			
			newSolution = packing.deepDuplicate();
			ColoringAlgos.colorConcentric(newSolution.seedNodes, new Location(width/2, height/2));
			newSolution.coloringAlgo = "concentric, center";
			actualSolutions.add(newSolution);
			
			newSolution = packing.deepDuplicate();
			ColoringAlgos.colorConcentric(newSolution.seedNodes, new Location(0, 0));
			newSolution.coloringAlgo = "concentric, UL corner";
			actualSolutions.add(newSolution);
			
			newSolution = packing.deepDuplicate();
			ColoringAlgos.colorMaxValue(newSolution.seedNodes, new Location(width/2, height/2));
			newSolution.coloringAlgo = "max value, center";
			actualSolutions.add(newSolution);
			
			newSolution = packing.deepDuplicate();
			ColoringAlgos.colorMaxValue(newSolution.seedNodes, new Location(0,0));
			newSolution.coloringAlgo = "max value, UL corner";
			actualSolutions.add(newSolution);
		}
		
		System.err.println("Generated all colorings");
		return actualSolutions;
	}
	
	private static ArrayList<Solution> generateAllPackings(ArrayList<Location> trees, double width, double height) {
		ArrayList<Solution> packings = new ArrayList<Solution>();
		
		Solution newSolution;
		
		// Rectilinear
		newSolution = new Solution(PackAlgos.rectilinear(trees, width, height, PackAlgos.Corner.UL, false), trees, width, height);
		newSolution.packingAlgo = "rectilinear, UL corner";
		packings.add(newSolution);
		
		newSolution = new Solution(PackAlgos.rectilinear(trees, width, height, PackAlgos.Corner.UR, false), trees, width, height);
		newSolution.packingAlgo = "rectilinear, UR corner";
		packings.add(newSolution);
		
		newSolution = new Solution(PackAlgos.rectilinear(trees, width, height, PackAlgos.Corner.BL, false), trees, width, height);
		newSolution.packingAlgo = "rectilinear, BL corner";
		packings.add(newSolution);
		
		newSolution = new Solution(PackAlgos.rectilinear(trees, width, height, PackAlgos.Corner.BR, false), trees, width, height);
		newSolution.packingAlgo = "rectilinear, BR corner";
		packings.add(newSolution);
		
		newSolution = new Solution(PackAlgos.rectilinear(trees, width, height, PackAlgos.Corner.UL, true), trees, width, height);
		newSolution.packingAlgo = "rectilinear, UL corner, spread out";
		packings.add(newSolution);
		
		System.err.println("Generated all Rectilinear packings");
		
		// Hex
		newSolution = new Solution(PackAlgos.hexagonal(trees, width, height, PackAlgos.Corner.UL, PackAlgos.Direction.V), trees, width, height);
		newSolution.packingAlgo = "hex, UL corner, V direction";
		packings.add(newSolution);

		newSolution = new Solution(PackAlgos.hexagonal(trees, width, height, PackAlgos.Corner.UL, PackAlgos.Direction.H), trees, width, height);
		newSolution.packingAlgo = "hex, UL corner, H direction";
		packings.add(newSolution);
		
		newSolution = new Solution(PackAlgos.hexagonal(trees, width, height, PackAlgos.Corner.UR, PackAlgos.Direction.V), trees, width, height);
		newSolution.packingAlgo = "hex, UR corner, V direction";
		packings.add(newSolution);

		newSolution = new Solution(PackAlgos.hexagonal(trees, width, height, PackAlgos.Corner.UR, PackAlgos.Direction.H), trees, width, height);
		newSolution.packingAlgo = "hex, UR corner, H direction";
		packings.add(newSolution);
		
		newSolution = new Solution(PackAlgos.hexagonal(trees, width, height, PackAlgos.Corner.BL, PackAlgos.Direction.V), trees, width, height);
		newSolution.packingAlgo = "hex, BL corner, V direction";
		packings.add(newSolution);
		
		newSolution = new Solution(PackAlgos.hexagonal(trees, width, height, PackAlgos.Corner.BL, PackAlgos.Direction.H), trees, width, height);
		newSolution.packingAlgo = "hex, BL corner, H direction";
		packings.add(newSolution);
		
		newSolution = new Solution(PackAlgos.hexagonal(trees, width, height, PackAlgos.Corner.BR, PackAlgos.Direction.V), trees, width, height);
		newSolution.packingAlgo = "hex, BR corner, V direction";
		packings.add(newSolution);
		
		newSolution = new Solution(PackAlgos.hexagonal(trees, width, height, PackAlgos.Corner.BR, PackAlgos.Direction.H), trees, width, height);
		newSolution.packingAlgo = "hex, BR corner, H direction";
		packings.add(newSolution);
		
		System.err.println("Generated all Hex packings");
		
		// Best Known
		ArrayList<Location> packing = PackAlgos.bestKnown(trees, width, height);
		if (packing != null) {
			newSolution = new Solution(packing, trees, width, height);
			newSolution.packingAlgo = "bestKnown";
			packings.add(newSolution);
			System.err.println("Generated Best Known packing");
		} else {
			System.err.println("Failed to generate Best Known packing");
		}
		
		// Physical
		newSolution = new Solution(PackAlgos.physical(trees, width, height), trees, width, height);
		newSolution.packingAlgo = "physical";
		packings.add(newSolution);

		System.err.println("Generated Physical packing");
		
		return packings;
	}
}