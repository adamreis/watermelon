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
}
