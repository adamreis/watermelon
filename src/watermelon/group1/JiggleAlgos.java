package watermelon.group1;

import java.util.ArrayList;

import watermelon.group1.Solution;

public class JiggleAlgos {

	
	public static void dumbJiggle(Solution baseSolution) {
		Solution newSolution = baseSolution.deepDuplicate();
		newSolution.jiggleAlgo = "dumbJiggle";
		
		// JIGGLE
	}
	
	private static boolean jiggleAllDirections(Solution solution, SeedNode seedNode, double s) {
		boolean improved = false;
		
		double angleGranularity = 1;
		double locationGranularity = 0.01;
		
		double origScore = solution.getScore(s);
		double bestScore = origScore;
		
		Location origLocation = new Location(seedNode.x, seedNode.y);
		Location bestLocation = new Location(seedNode.x, seedNode.y);
		
		for (double angle = 0; angle < 360; angle += angleGranularity) {
			seedNode.x = origLocation.x;
			seedNode.y = origLocation.y;
			
			double score;
			double x = Math.cos(Math.toRadians(angle)) * locationGranularity;
			double y = Math.sin(Math.toRadians(angle)) * locationGranularity;
			
			while (true) {
				seedNode.x += x;
				seedNode.y += y;
				
				if (!solution.isValidFor(seedNode))
					break;
				
				score = solution.getScore(s);
				
				if (score > bestScore) {
					improved = true;
					bestScore = score;
					bestLocation.x = seedNode.x;
					bestLocation.y = seedNode.y;
				}
			}
		}
		
		seedNode.x = bestLocation.x;
		seedNode.y = bestLocation.y;
		
		return improved;
	}
	
	public static void jiggleIterative(Solution solution, double s) {
		int i = 0;
		int maxIterations = 100;
		boolean improved;
		
		solution.jiggleAlgo = "iterative";
		
		do {
			improved = false;
			for (SeedNode seedNode : solution.seedNodes)
				improved = improved || jiggleAllDirections(solution, seedNode, s);
		} while (i++ < maxIterations && improved);
	}
}