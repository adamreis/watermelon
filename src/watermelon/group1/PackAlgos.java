package watermelon.group1;

import java.util.*;
import watermelon.group1.Consts;
import watermelon.group1.Location;
import watermelon.group1.Vector2D;

public class PackAlgos {
	public static enum Corner { UL, BL, UR, BR };
	public static enum Direction { H, V };
	
	public static final int MAX_JIGGLES = 50000;
	public static final double MIN_JIGGLE_MOVE = 0.001;
	
	private static boolean closeToTree(double x, double y, ArrayList<Location> trees) {
		for (Location tree : trees) {
			if (Location.distanceSquared(x,  y, tree.x, tree.y) < ((Consts.SEED_RADIUS + Consts.TREE_RADIUS) * (Consts.SEED_RADIUS + Consts.TREE_RADIUS)))
				return true;
		}
		
		return false;
	}
	
	public static ArrayList<Location> rectilinear(ArrayList<Location> trees, double width, double height, Corner corner) {
		double x, y, xStart, yStart;
		int xSign, ySign;
		
		if (corner == Corner.UL || corner == Corner.BL) {
			xStart = Consts.SEED_RADIUS;
			xSign = 1;
		} else {
			xStart = width - Consts.SEED_RADIUS;
			xSign = -1;
		}
		
		if (corner == Corner.UL || corner == Corner.UR) {
			yStart = Consts.SEED_RADIUS;
			ySign = 1;
		} else {
			yStart = height - Consts.SEED_RADIUS;
			ySign = -1;
		}
				
		ArrayList<Location> locations = new ArrayList<Location>();
		
		x = xStart;
		y = yStart;
		
		while (y >= Consts.SEED_RADIUS && y <= height - Consts.SEED_RADIUS) {
			x = xStart;
			
			while (x >= Consts.SEED_RADIUS && x <= width - Consts.SEED_RADIUS) {
				if (!closeToTree(x, y, trees))
					locations.add(new Location(x, y));
				
				x += 2*Consts.SEED_RADIUS * xSign;
			}
			
			y += 2*Consts.SEED_RADIUS * ySign;
		}
		
		return locations;
	}
	
	public static ArrayList<Location> hexagonal(ArrayList<Location> trees, double width, double height, Corner corner, Direction direction) {
		double x, y, xStart, yStart;
		int xSign, ySign;
		boolean offset;
		
		if (corner == Corner.UL || corner == Corner.BL) {
			xStart = Consts.SEED_RADIUS;
			xSign = 1;
		} else {
			xStart = width - Consts.SEED_RADIUS;
			xSign = -1;
		}
		
		if (corner == Corner.UL || corner == Corner.UR) {
			yStart = Consts.SEED_RADIUS;
			ySign = 1;
		} else {
			yStart = height - Consts.SEED_RADIUS;
			ySign = -1;
		}
				
		ArrayList<Location> locations = new ArrayList<Location>();
		
		x = xStart;
		y = yStart;
		offset = false;
		
		if (direction == Direction.H) {
			while (y >= Consts.SEED_RADIUS && y <= height - Consts.SEED_RADIUS) {
				x = xStart + (offset ? Consts.SEED_RADIUS * xSign : 0);
				
				while (x >= Consts.SEED_RADIUS && x <= width - Consts.SEED_RADIUS) {
					if (!closeToTree(x, y, trees))
						locations.add(new Location(x, y));
					
					x += 2*Consts.SEED_RADIUS * xSign;
				}
				
				y += Consts.SQRT_3*Consts.SEED_RADIUS * ySign;
				offset = !offset;
			}
		} else {
			while (x >= Consts.SEED_RADIUS && x <= width - Consts.SEED_RADIUS) {
				y = yStart + (offset ? Consts.SEED_RADIUS * ySign : 0);
				
				while (y >= Consts.SEED_RADIUS && y <= height - Consts.SEED_RADIUS) {
					if (!closeToTree(x, y, trees))
						locations.add(new Location(x, y));
					
					y += 2*Consts.SEED_RADIUS * ySign;
				}
				
				x += Consts.SQRT_3*Consts.SEED_RADIUS * xSign;
				offset = !offset;
			}
		}
		
		return locations;
	}
	
	private static Location findSparseLocation(ArrayList<Location> locations, ArrayList<Location> trees, double width, double height) {
		Location bestLocation = new Location(0,0);
		double minDistance = Double.MAX_VALUE;
		
		for (double x = Consts.SEED_RADIUS; x < width - Consts.SEED_RADIUS; x += 0.1) {
			for (double y = Consts.SEED_RADIUS; y < height - Consts.SEED_RADIUS; y += 0.1) {
				double d = 0;
				double ds = 0;
				
				boolean overlap = false;
				
				for (Location tree : trees) {
					if ((ds = Location.distance(x, y, tree.x, tree.y)) != 0)
						d += Math.max(0, Consts.SEED_RADIUS + Consts.TREE_RADIUS - ds);
					else
						overlap = true;
				}
				
				for (Location location : locations) {
					if ((ds = Location.distance(x, y, location.x, location.y)) != 0)
						d += Math.max(0, 2*Consts.SEED_RADIUS - ds);
					else
						overlap = true;
				}
				
				if (!overlap && d < minDistance) {
					minDistance = d;
					bestLocation.x = x;
					bestLocation.y = y;
				}
			}	
		}

		return bestLocation;
	}
	
	private static boolean jiggleLocations(ArrayList<Location> locations, ArrayList<Location> trees, double width, double height) {
		// Set up the vectors that will move each location
		ArrayList<Vector2D> vectors = new ArrayList<Vector2D>(locations.size());
		for (int i = 0; i < locations.size(); i++)
			vectors.add(new Vector2D());
		
		// For each location, calculate its vector
		for (int i = 0; i < locations.size(); i++) {
			Location location = locations.get(i);
			Vector2D vector = vectors.get(i);
			double d;
			
			// Test against the walls
			if (location.x < Consts.SEED_RADIUS)
				vector.x += Math.max((Consts.SEED_RADIUS - location.x) / 2, Math.min(Consts.SEED_RADIUS - location.x, MIN_JIGGLE_MOVE));
			
			if (location.y < Consts.SEED_RADIUS)
				vector.y += Math.max((Consts.SEED_RADIUS - location.y) / 2, Math.min(Consts.SEED_RADIUS - location.y, MIN_JIGGLE_MOVE));
			
			if (location.x > width - Consts.SEED_RADIUS)
				vector.x -= Math.max((location.x - (width - Consts.SEED_RADIUS)) / 2, Math.min(location.x - (width - Consts.SEED_RADIUS), MIN_JIGGLE_MOVE));
			
			if (location.y > height - Consts.SEED_RADIUS)
				vector.y -= Math.max((location.y - (height - Consts.SEED_RADIUS)) / 2, Math.min(location.y - (height - Consts.SEED_RADIUS), MIN_JIGGLE_MOVE));
			
			// Test against the trees
			for (Location tree : trees) {
				if ((d = Location.distance(location, tree)) < Consts.SEED_RADIUS + Consts.TREE_RADIUS) {
					Vector2D v = new Vector2D();
					double m = Math.max((Consts.SEED_RADIUS + Consts.TREE_RADIUS - d) / 2, Math.min(Consts.SEED_RADIUS + Consts.TREE_RADIUS - d, MIN_JIGGLE_MOVE));
					
					v.x = Math.sqrt(Math.abs(location.x - tree.x)) * m * Math.signum(location.x - tree.x);
					v.y = Math.sqrt(Math.abs(location.y - tree.y)) * m * Math.signum(location.y - tree.y);
					
					vector.add(v);
				}
			}
			
			// Test against the other locations
			for (int j = 0; j < locations.size(); j++) {
				Location testLocation = locations.get(j);
				
				if (i != j && (d = Location.distance(location, testLocation)) < 2*Consts.SEED_RADIUS) {
					Vector2D v = new Vector2D();
					double m = Math.max((2*Consts.SEED_RADIUS - d) / 2, Math.min((2*Consts.SEED_RADIUS - d), MIN_JIGGLE_MOVE));
					
					v.x = Math.sqrt(Math.abs(location.x - testLocation.x)) * m * Math.signum(location.x - testLocation.x);
					v.y = Math.sqrt(Math.abs(location.y - testLocation.y)) * m * Math.signum(location.y - testLocation.y);
					
					vector.add(v);
				}
			}
		}
		
		boolean success = true;
		
		// Move each location by its vector
		for (int i = 0; i < locations.size(); i++) {
			Vector2D v = vectors.get(i);
			if (!v.isNone()) {
				success = false;
				locations.get(i).x += vectors.get(i).x;
				locations.get(i).y += vectors.get(i).y;
			}
		}
		
		return success;
	}
	
	public static ArrayList<Location> physical(ArrayList<Location> trees, double width, double height) {
		ArrayList<Location> locations = new ArrayList<Location>();
				
		Location tryLocation = null;
		while (true) {
			// Deep copy the existing best locations
			ArrayList<Location> tryLocations = new ArrayList<Location>();
			for (Location location : locations)
				tryLocations.add(new Location(location));
			
			// Place a new seed on the field
			tryLocation = findSparseLocation(tryLocations, trees, width, height);
			tryLocations.add(tryLocation);
			
			// Jiggle the locations until they all fit or we fail
			boolean success = false;
			for (int i = 0; i < MAX_JIGGLES; i++) {
				success = jiggleLocations(tryLocations, trees, width, height);
				
				if (success)
					break;
			}
			
			if (!success)
				break;
			
			locations = tryLocations;			
		}
		
		return locations;
	}
}