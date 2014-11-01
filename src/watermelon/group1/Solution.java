package watermelon.group1;

import java.util.ArrayList;

import watermelon.sim.seed;

public class Solution {
	public ArrayList<SeedNode> seedNodes;
	public String packingAlgo;
	public String coloringAlgo;
	public String jiggleAlgo;

	public Solution(ArrayList<Location> seeds) {
		this.seedNodes = generateSeedGraph(seeds);
		this.packingAlgo = "";
		this.coloringAlgo = "";
		this.jiggleAlgo = "";
	}
	
	public Solution() {
		this.seedNodes = new ArrayList<SeedNode>();
		this.packingAlgo = "";
		this.coloringAlgo = "";
		this.jiggleAlgo = "none";
	}
	
	public Solution deepDuplicate() {
		Solution newSolution = new Solution();
		ArrayList<Location> locations = new ArrayList<Location>();
		for (SeedNode seed: this.seedNodes) {
			locations.add(new Location(seed));
		}
		newSolution.seedNodes = generateSeedGraph(locations);
		
		for (int i = 0; i < this.seedNodes.size(); i++) {
			newSolution.seedNodes.get(i).ploidy = this.seedNodes.get(i).ploidy;
		}
		
		newSolution.packingAlgo = this.packingAlgo;
		newSolution.coloringAlgo = this.coloringAlgo;
		newSolution.jiggleAlgo = this.jiggleAlgo;
		
		return newSolution;
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
	
	public ArrayList<seed> simRepresentation() {
		ArrayList<seed> seeds = new ArrayList<seed>();
		
		for (SeedNode seedNode : this.seedNodes) {
			if (seedNode.ploidy != SeedNode.Ploidies.NONE) {
				double x = seedNode.x;
				double y = seedNode.y;
				seeds.add(new seed(x, y, seedNode.ploidy == SeedNode.Ploidies.TETRAPLOID));
			}
		}
		
		return seeds;
	}
	
	public double getScore(double scoringMultiplier) {
		double total = 0;
		for (SeedNode node : seedNodes) {
			double nodeScore;
			double chance = 0.0;
			double allInfluences = 0.0;
			double oppositeInfluences = 0.0;
			for (SeedNode comparisonNode : seedNodes) {
				if (comparisonNode == node)
					continue;
				allInfluences += 1/node.distanceSquared(comparisonNode);
				if (node.ploidy != SeedNode.Ploidies.NONE && node.ploidy != comparisonNode.ploidy)
					oppositeInfluences += 1/node.distanceSquared(comparisonNode);
			}
			chance = oppositeInfluences/allInfluences;
			nodeScore = chance + (1 - chance) * scoringMultiplier;
			total += nodeScore;
		}
		return total;
	}

}