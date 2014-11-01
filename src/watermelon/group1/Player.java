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
			Solution solution = new Solution(PackAlgos.hexagonal(trees, width, height, PackAlgos.Corner.UL, PackAlgos.Direction.V));
			ColoringAlgos.colorMaxValue(solution.seedNodes, new Location(0,0));
			solution.coloringAlgo = "test";
			solution.packingAlgo = "test";
			solution.score(s);
			possibleSolutions.add(solution);
		}
		
		else {
			// Get all possible packings/colorings
			possibleSolutions = generateAllPossibleSolutions(trees, width, height, s);
		}
		
		// Now find the best one
		Solution bestSolution = new Solution();
		for (Solution solution : possibleSolutions) {
			if (solution.score > bestSolution.score) {
				bestSolution = solution;
			}
		}
		
		// Print which configuration was best
		System.out.println("Winning config:");
		System.out.println("\tPacking: " + bestSolution.packingAlgo);
		System.out.println("\tColoring: " + bestSolution.coloringAlgo);
		
		// Transform our output into the simulator classes and return it
		return bestSolution.simRepresentation();
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
		
		for (Solution solution : actualSolutions) {
			solution.score(s);

		}
		System.err.println("Scored all colorings");
		return actualSolutions;
	}
	
	private static ArrayList<Solution> generateAllPackings(ArrayList<Location> trees, double width, double height) {
		ArrayList<Solution> packings = new ArrayList<Solution>();
		
		Solution newSolution;
		
		// Rectilinear
		newSolution = new Solution(PackAlgos.rectilinear(trees, width, height, PackAlgos.Corner.UL));
		newSolution.packingAlgo = "rectilinear, UL corner";
		packings.add(newSolution);
		
		newSolution = new Solution(PackAlgos.rectilinear(trees, width, height, PackAlgos.Corner.UR));
		newSolution.packingAlgo = "rectilinear, UR corner";
		packings.add(newSolution);
		
		newSolution = new Solution(PackAlgos.rectilinear(trees, width, height, PackAlgos.Corner.BL));
		newSolution.packingAlgo = "rectilinear, BL corner";
		packings.add(newSolution);
		
		newSolution = new Solution(PackAlgos.rectilinear(trees, width, height, PackAlgos.Corner.BR));
		newSolution.packingAlgo = "rectilinear, BR corner";
		packings.add(newSolution);
		
		System.err.println("Generated all Rectilinear packings");
		
		// Hex
		newSolution = new Solution(PackAlgos.hexagonal(trees, width, height, PackAlgos.Corner.UL, PackAlgos.Direction.V));
		newSolution.packingAlgo = "hex, UL corner, V direction";
		packings.add(newSolution);

		newSolution = new Solution(PackAlgos.hexagonal(trees, width, height, PackAlgos.Corner.UL, PackAlgos.Direction.H));
		newSolution.packingAlgo = "hex, UL corner, H direction";
		packings.add(newSolution);
		
		newSolution = new Solution(PackAlgos.hexagonal(trees, width, height, PackAlgos.Corner.UR, PackAlgos.Direction.V));
		newSolution.packingAlgo = "hex, UR corner, V direction";
		packings.add(newSolution);

		newSolution = new Solution(PackAlgos.hexagonal(trees, width, height, PackAlgos.Corner.UR, PackAlgos.Direction.H));
		newSolution.packingAlgo = "hex, UR corner, H direction";
		packings.add(newSolution);
		
		newSolution = new Solution(PackAlgos.hexagonal(trees, width, height, PackAlgos.Corner.BL, PackAlgos.Direction.V));
		newSolution.packingAlgo = "hex, BL corner, V direction";
		packings.add(newSolution);
		
		newSolution = new Solution(PackAlgos.hexagonal(trees, width, height, PackAlgos.Corner.BL, PackAlgos.Direction.H));
		newSolution.packingAlgo = "hex, BL corner, H direction";
		packings.add(newSolution);
		
		newSolution = new Solution(PackAlgos.hexagonal(trees, width, height, PackAlgos.Corner.BR, PackAlgos.Direction.V));
		newSolution.packingAlgo = "hex, BR corner, V direction";
		packings.add(newSolution);
		
		newSolution = new Solution(PackAlgos.hexagonal(trees, width, height, PackAlgos.Corner.BR, PackAlgos.Direction.H));
		newSolution.packingAlgo = "hex, BR corner, H direction";
		packings.add(newSolution);
		
		System.err.println("Generated all Hex packings");
		
		// Best Known
		newSolution = new Solution(PackAlgos.bestKnown(trees, width, height));
		newSolution.packingAlgo = "bestKnown";
		packings.add(newSolution);

		System.err.println("Generated Best Known packing");
		
		// Physical
		newSolution = new Solution(PackAlgos.physical(trees, width, height));
		newSolution.packingAlgo = "physical";
		packings.add(newSolution);

		System.err.println("Generated Physical packing");
		
		return packings;
	}
	
	
	
}