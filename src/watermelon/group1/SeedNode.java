package watermelon.group1;

import java.util.ArrayList;

import watermelon.sim.Point;

public class SeedNode extends Point {
	public ArrayList<SeedNode> adjacent;
	public enum Ploidy {
		NONE, DIPLOID, TETRAPLOID
	};
	
	public SeedNode(Point p) {
		super(p.x, p.y, false);
		
		adjacent = new ArrayList<SeedNode>();
	}

}
