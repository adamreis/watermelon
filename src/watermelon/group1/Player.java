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
		ArrayList<SeedNode> seedNodes = generateSeedGraph(locations);
		
		// Change the ploidies based on some algorithm
		ColoringAlgos.colorConcentric(seedNodes, new Location(width/2, height/2));
//		ColoringAlgos.colorGreedy(seedNodes);
//		ColoringAlgos.colorRandom(seedNodes);
		
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
				if (nodeA.distanceTo(nodeB) <= 2*Consts.SEED_RADIUS + Consts.ADJACENCY_FUDGE_FACTOR) { // Need a little fudge factor here
					nodeA.adjacent.add(nodeB);
					nodeB.adjacent.add(nodeA);
				}
			}
		}
		
		return nodes;
	}
}