package watermelon.group1;

import java.util.ArrayList;

public class ColoringAlgos {
	
	/*
	 * Colors each seed such that it is least like the surrounding seeds.
	 */
	public static void colorGreedy(ArrayList<SeedNode> list) {
		for (SeedNode node : list) {
			colorOppositeFromNeighbors(node);
		}
	}
	
	public static void colorAround(ArrayList<SeedNode> list) {
		for (SeedNode node : list) {
			if (node.ploidy.equals(SeedNode.Ploidies.NONE)) {
				colorOppositeFromNeighbors(node);
			}
			for (SeedNode neighbor : node.adjacent) {
				if (neighbor.ploidy.equals(SeedNode.Ploidies.NONE))
					colorOppositeFromSource(node, neighbor);
			}
		}
	}
	
	private static void colorOppositeFromNeighbors(SeedNode node) {
		if (node.getMostCommonNeighbor().equals(SeedNode.Ploidies.DIPLOID))
			node.ploidy = SeedNode.Ploidies.TETRAPLOID;
		else
			node.ploidy = SeedNode.Ploidies.DIPLOID;
	}
	
	private static void colorOppositeFromSource(SeedNode source, SeedNode target) {
		if (source.ploidy.equals(SeedNode.Ploidies.DIPLOID))
			target.ploidy = SeedNode.Ploidies.TETRAPLOID;
		else if (source.ploidy.equals(SeedNode.Ploidies.TETRAPLOID))
			target.ploidy = SeedNode.Ploidies.DIPLOID;
	}
}
