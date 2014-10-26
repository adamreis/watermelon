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
}
