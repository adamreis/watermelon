package watermelon.group1;

import java.util.ArrayList;

import watermelon.group1.Location;

public class SeedNode extends Location {
	public enum Ploidies {
		NONE, DIPLOID, TETRAPLOID
	};
	
	public ArrayList<SeedNode> adjacent;
	public Ploidies ploidy;
	
	public SeedNode(Location loc) {
		super(loc.x, loc.y);
		
		this.adjacent = new ArrayList<SeedNode>();
		this.ploidy = Ploidies.NONE;
	}
	
	public Ploidies getMostCommonNeighbor() {
		int diploidNeighbors = 0;
		int tetraploidNeighbors = 0;
		for (SeedNode neighbor : adjacent) {
			if (neighbor.ploidy.equals(Ploidies.DIPLOID))
				diploidNeighbors++;
			else if (neighbor.ploidy.equals(Ploidies.TETRAPLOID))
				tetraploidNeighbors++;
		}
		if (tetraploidNeighbors > diploidNeighbors)
			return Ploidies.TETRAPLOID;
		else if (diploidNeighbors > tetraploidNeighbors)
			return Ploidies.DIPLOID;
		return Ploidies.NONE;
	}
	
	public double distanceTo(SeedNode otherNode) {
		return Math.sqrt(Math.pow((this.x - otherNode.x), 2) + Math.pow((this.y - otherNode.y), 2));
	}
	
	public double distanceTo(double x, double y) {
		return Math.sqrt(Math.pow((this.x - x), 2) + Math.pow((this.y - y), 2));
	}
}
