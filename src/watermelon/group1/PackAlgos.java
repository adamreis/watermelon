package watermelon.group1;

import java.util.*;
import watermelon.group1.Consts;
import watermelon.group1.Location;

public class PackAlgos {
	private static boolean closeToTree(double x, double y, ArrayList<Location> trees) {
		for (Location tree : trees) {
			if ( (tree.x - x)*(tree.x - x) + (tree.y - y)*(tree.y - y) < ((Consts.SEED_RADIUS + Consts.TREE_RADIUS) * (Consts.SEED_RADIUS + Consts.TREE_RADIUS)))
				return true;
		}
		
		return false;
	}
	
	public static ArrayList<Location> rectilinear(ArrayList<Location> trees, double width, double height) {
		double x = Consts.SEED_RADIUS;
		double y = Consts.SEED_RADIUS;
		ArrayList<Location> locations = new ArrayList<Location>();
		
		while (y <= height - Consts.SEED_RADIUS) {
			x = Consts.SEED_RADIUS;
			
			while (x <= width - Consts.SEED_RADIUS) {
				if (!closeToTree(x, y, trees))
					locations.add(new Location(x, y));
				
				x += 2*Consts.SEED_RADIUS;
			}
			
			y += 2*Consts.SEED_RADIUS;
		}
		
		return locations;
	}
	
	public static ArrayList<Location> hexagonal(ArrayList<Location> trees, double width, double height) {
		double x = Consts.SEED_RADIUS;
		double y = Consts.SEED_RADIUS;
		boolean offset = false;
		
		ArrayList<Location> locations = new ArrayList<Location>();
		
		while (y <= height - Consts.SEED_RADIUS) {
			x = Consts.SEED_RADIUS + (offset ? Consts.SEED_RADIUS : 0);
			
			while (x <= width - Consts.SEED_RADIUS) {
				if (!closeToTree(x, y, trees))
					locations.add(new Location(x, y));
				
				x += 2*Consts.SEED_RADIUS;
			}
			
			y += Consts.SQRT_3*Consts.SEED_RADIUS;
			offset = !offset;
		}
		
		return locations;
	}
}