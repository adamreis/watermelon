package watermelon.group1;

import java.util.ArrayList;
import java.util.LinkedList;

public class ColoringAlgos {
	
	/*
	 * Colors each seed such that it is least like the surrounding seeds.
	 */
	public static void colorGreedy(ArrayList<SeedNode> list) {
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
