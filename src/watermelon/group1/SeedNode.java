package watermelon.group1;

import java.util.ArrayList;

import watermelon.group1.Location;

public class SeedNode extends Location {
	public ArrayList<SeedNode> adjacent;
	public enum Ploidy {
		NONE, DIPLOID, TETRAPLOID
	};
	
	public SeedNode(Location l) {
		super(l.x, l.y);
		
		adjacent = new ArrayList<SeedNode>();
	}
}
