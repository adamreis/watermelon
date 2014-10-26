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
	 * Creates concentric hexagons (theoretically)
	 */
	public static void colorConcentric(ArrayList<SeedNode> list, Location boardCenter) {
		SeedNode closestToCenter = list.get(list.size()/2);
		double closestDistanceToCenter = closestToCenter.distanceTo(boardCenter);
		
		for (SeedNode node: list) {
			if (node.distanceTo(boardCenter) < closestDistanceToCenter) {
				closestDistanceToCenter = node.distanceTo(boardCenter);
				closestToCenter = node;
			}
		}
		
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
	
	private static SeedNode.Ploidies oppositePloidy(SeedNode.Ploidies ploidy) {
		if (ploidy == SeedNode.Ploidies.DIPLOID) {
			return SeedNode.Ploidies.TETRAPLOID;
		} else {
			return SeedNode.Ploidies.DIPLOID;
		}
	}
}
