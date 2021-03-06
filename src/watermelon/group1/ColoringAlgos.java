package watermelon.group1;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class ColoringAlgos {
	
	private static final int MAX_RECOLORS = 50;
	private static final int SIMULATED_ANNEALS = 50;
	
	/*
	 * Colors each seed such that it is least like the surrounding seeds.
	 */
	public static void colorAdjacent(ArrayList<SeedNode> list) {
		for (SeedNode node : list) {
			if (node.getMostCommonNeighbor().equals(SeedNode.Ploidies.DIPLOID))
				node.ploidy = SeedNode.Ploidies.TETRAPLOID;
			else
				node.ploidy = SeedNode.Ploidies.DIPLOID;
		}
	}
	
	/*
	 * Creates concentric hexagons
	 */
	public static void colorConcentric(ArrayList<SeedNode> list, Location startPoint) {
		SeedNode closestToCenter = findClosestSeedToLocation(list, startPoint);
		
		closestToCenter.ploidy = SeedNode.Ploidies.DIPLOID;
		LinkedList<SeedNode> queue = new LinkedList<SeedNode>();
		queue.add(closestToCenter);
		
		while (queue.size() > 0) {
			SeedNode node = queue.remove();
			SeedNode.Ploidies oppositePloidy = oppositePloidy(node.ploidy);
			for (SeedNode adj : node.adjacent) {
				if (adj.ploidy == SeedNode.Ploidies.NONE) {
					adj.ploidy = oppositePloidy;
					queue.add(adj);
				}
			}
		}
	}
	
	/*
	 * Colors each seed randomly
	 */
	public static void colorRandom(ArrayList<SeedNode> list) {
		Random rand = new Random();
		for (SeedNode node : list) {
			if (rand.nextBoolean())
				node.ploidy = SeedNode.Ploidies.TETRAPLOID;
			else
				node.ploidy = SeedNode.Ploidies.DIPLOID;
		}
	}
	
	/*
	 * Colors each seed such that it attains the maximum value based on the ploidies
	 * and distances of other colored seeds.
	 */
	public static void colorMaxValue(ArrayList<SeedNode> list, Location startPoint) {
		SeedNode closestToStart = findClosestSeedToLocation(list, startPoint);
		LinkedList<SeedNode> queue = new LinkedList<SeedNode>();
		queue.add(closestToStart);
		
		while (queue.size() > 0) {
			double diploidInfluence = 0;
			double tetraploidInfluence = 0;
			SeedNode nodeToColor = queue.remove();
			
			for (SeedNode comparisonNode : list) {
				if (comparisonNode.ploidy == SeedNode.Ploidies.DIPLOID) {
					diploidInfluence += influenceFromSeed(nodeToColor, comparisonNode);
				}
				else if (comparisonNode.ploidy == SeedNode.Ploidies.TETRAPLOID) {
					tetraploidInfluence += influenceFromSeed(nodeToColor, comparisonNode);
				}
			}
			
			if (diploidInfluence > tetraploidInfluence)
				nodeToColor.ploidy = SeedNode.Ploidies.TETRAPLOID;
			else
				nodeToColor.ploidy = SeedNode.Ploidies.DIPLOID;
			
			for (SeedNode neighbor : nodeToColor.adjacent) {
				if (neighbor.ploidy == SeedNode.Ploidies.NONE && !queue.contains(neighbor))
					queue.addFirst(neighbor);
			}
		}
		boolean localMax = true;
		int i = 0;
		while (localMax && i < MAX_RECOLORS) {
			localMax = reColorIfNecessary(list);
			i++;
		}
		// scoring multiplier is irrelevant in this situation, so we just pass in a random number
		double score = Solution.getScore(list, 0.01);
		for (i = 0; i < SIMULATED_ANNEALS; i++) {
			ArrayList<SeedNode> potentialList = new ArrayList<SeedNode>();
			for (SeedNode node : list) {
				potentialList.add(node);
				if (node.getMostCommonNeighbor().equals(SeedNode.Ploidies.DIPLOID))
					node.ploidy = SeedNode.Ploidies.TETRAPLOID;
				else
					node.ploidy = SeedNode.Ploidies.DIPLOID;
			}
			boolean localInnerMax = true;
			int j = 0;
			while (localInnerMax && j < MAX_RECOLORS) {
				localMax = reColorIfNecessary(potentialList);
				j++;
			}
			double potentialScore = Solution.getScore(potentialList, 0.01);
			if (potentialScore > score) {
				list = potentialList;
				score = potentialScore;
			}
			else {
				break;
			}
		}
	}
	
	public static boolean reColorIfNecessary(ArrayList<SeedNode> list) {
		boolean reColored = false;
		for (SeedNode nodeToColor : list) {
			double diploidInfluence = 0;
			double tetraploidInfluence = 0;
			for (SeedNode comparisonNode : list) {
				if (comparisonNode == nodeToColor)
					continue;
				if (comparisonNode.ploidy == SeedNode.Ploidies.DIPLOID) {
					diploidInfluence += influenceFromSeed(nodeToColor, comparisonNode);
				}
				else if (comparisonNode.ploidy == SeedNode.Ploidies.TETRAPLOID) {
					tetraploidInfluence += influenceFromSeed(nodeToColor, comparisonNode);
				}
			}
			
			if (diploidInfluence > tetraploidInfluence && nodeToColor.ploidy != SeedNode.Ploidies.TETRAPLOID) {
				nodeToColor.ploidy = SeedNode.Ploidies.TETRAPLOID;
				reColored = true;
			}
			else if (tetraploidInfluence > diploidInfluence && nodeToColor.ploidy != SeedNode.Ploidies.DIPLOID) {
				nodeToColor.ploidy = SeedNode.Ploidies.DIPLOID;
				reColored = true;
			}
		}
		return reColored;
	}
	
	private static double influenceFromSeed(Location influencee, Location influencer) {
		return 1/influencee.distanceSquared(influencer);
	}
	
	private static SeedNode findClosestSeedToLocation(ArrayList<SeedNode> list, Location loc) {
		SeedNode closestToCenter = list.get(list.size()/2);
		double closestDistanceToCenter = closestToCenter.distanceTo(loc);
		
		for (SeedNode node: list) {
			if (node.distanceTo(loc) < closestDistanceToCenter) {
				closestDistanceToCenter = node.distanceTo(loc);
				closestToCenter = node;
			}
		}
		
		return closestToCenter;
	}
	
	private static SeedNode.Ploidies oppositePloidy(SeedNode.Ploidies ploidy) {
		if (ploidy == SeedNode.Ploidies.DIPLOID) {
			return SeedNode.Ploidies.TETRAPLOID;
		} else {
			return SeedNode.Ploidies.DIPLOID;
		}
	}
}
