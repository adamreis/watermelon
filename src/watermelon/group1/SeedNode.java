package watermelon.group1;

import java.util.ArrayList;

import watermelon.group1.Location;

public class SeedNode extends Location {
	public enum Ploidies {
		NONE, DIPLOID, TETRAPLOID
	};
	
	public ArrayList<SeedNode> adjacent;
	public Ploidies ploidy;
	
	public SeedNode(Location l) {
		super(l.x, l.y);
		
		adjacent = new ArrayList<SeedNode>();
		ploidy = Ploidies.NONE;
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
}
