package watermelon.group1;

import java.util.*;

import watermelon.sim.Pair;
import watermelon.sim.seed;

public class Player extends watermelon.sim.Player {
	public void init() {

	}

	@Override
	public ArrayList<seed> move(ArrayList<Pair> treelist, double width, double height, double s) {
		// Transform input parameters from simulator classes into our preferred class
		ArrayList<Location> trees = new ArrayList<Location>();
		for (Pair p : treelist)
			trees.add(new Location(p.x, p.y));
		
		// Get a packing using some algorithm
		ArrayList<Location> locations = PackAlgos.hexagonal(trees, width, height);

		// Set the ploidies
		ArrayList<SeedNode> seedNodes = new ArrayList<SeedNode>();
		
		for (Location location : locations) {
			SeedNode seedNode = new SeedNode(location);
			seedNode.ploidy = SeedNode.Ploidies.TETRAPLOID;
			seedNodes.add(seedNode);
		}
		
		// Transform our output into the simulator classes
		ArrayList<seed> seeds = new ArrayList<seed>();
		
		for (SeedNode seedNode : seedNodes) {
			if (seedNode.ploidy != SeedNode.Ploidies.NONE) {
				double x = seedNode.x;
				double y = seedNode.y;
				seeds.add(new seed(x, y, seedNode.ploidy == SeedNode.Ploidies.TETRAPLOID));
			}
		}
		
		return seeds;
	}
}