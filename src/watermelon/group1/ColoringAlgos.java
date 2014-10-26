package watermelon.group1;

import java.util.ArrayList;

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
}
