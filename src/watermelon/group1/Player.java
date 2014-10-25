package watermelon.group1;

import java.util.*;

import watermelon.sim.Pair;
import watermelon.sim.seed;
import watermelon.group1.Consts;

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

		// Set the ploidies (every other for now)
		ArrayList<SeedNode> seedNodes = generateSeedGraph(locations);
		System.out.println(seedNodes.get(55).adjacent);
		
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
	
	private static ArrayList<SeedNode> generateSeedGraph(ArrayList<Location> seeds) {
		ArrayList<SeedNode> nodes = new ArrayList<SeedNode>();
		
		for (Location loc: seeds) {
			nodes.add(new SeedNode(loc));
		}
		
		for (int i = 0; i < nodes.size(); i++) {
			SeedNode nodeA = nodes.get(i);
			for (int j = i + 1; j < nodes.size(); j++) {
				SeedNode nodeB = nodes.get(j);
				if (distance(nodeA, nodeB) <= 2*Consts.SEED_RADIUS + .000001) { // Need a little fudge factor here
					nodeA.adjacent.add(nodeB);
					nodeB.adjacent.add(nodeA);
				}
			}
		}
		
		return nodes;
	}
	
	private static double distance(Location locA, Location locB) {
			return Math.sqrt(Math.pow((locA.x - locB.x), 2) + Math.pow((locA.y - locB.y), 2));
	}
}