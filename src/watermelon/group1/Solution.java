package watermelon.group1;

import java.util.ArrayList;

import watermelon.sim.seed;

public class Solution {
	public ArrayList<SeedNode> seedNodes;
	public String packingAlgo;
	public String coloringAlgo;
	public String jiggleAlgo;
	public double score;

	public Solution(ArrayList<Location> seeds) {
		this.seedNodes = generateSeedGraph(seeds);
		this.packingAlgo = "";
		this.coloringAlgo = "";
		this.jiggleAlgo = "";
		this.score = -1.0;
	}
	
	public Solution() {
		this.seedNodes = new ArrayList<SeedNode>();
		this.packingAlgo = "";
		this.coloringAlgo = "";
		this.jiggleAlgo = "none";
		this.score = -1.0;
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
		newSolution.score = this.score;
		
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

	public void score(double s) {
		ArrayList<seed> seedList = this.simRepresentation();
		
		this.score = scoreSeeds(seedList, s);
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
	
	private double scoreSeeds(ArrayList<seed> seedlist, double s) {
		double total = 0;
		
		for (int i = 0; i < seedlist.size(); i++) {
			double score;
			double chance = 0.0;
			double totaldis = 0.0;
			double difdis = 0.0;
			for (int j = 0; j < seedlist.size(); j++) {
				if (j != i) {
					totaldis = totaldis
							+ Math.pow(
									distanceseed(seedlist.get(i),
											seedlist.get(j)), -2);
				}
			}
			for (int j = 0; j < seedlist.size(); j++) {
				if (j != i
						&& ((seedlist.get(i).tetraploid && !seedlist.get(j).tetraploid) || (!seedlist
								.get(i).tetraploid && seedlist.get(j).tetraploid))) {
					difdis = difdis
							+ Math.pow(
									distanceseed(seedlist.get(i),
											seedlist.get(j)), -2);
				}
			}
			//System.out.println(totaldis);
			//System.out.println(difdis);
			chance = difdis / totaldis;
			score = chance + (1 - chance) * s;
			total = total + score;
		}
		return total;
	}
	
	private static double distanceseed(seed a, seed b) {
		return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
	}

}