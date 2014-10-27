package watermelon.group1;

import java.util.*;
import watermelon.group1.Consts;
import watermelon.group1.Location;
import watermelon.group1.Vector2D;

public class PackAlgos {
	public static final int MAX_JIGGLES = 50000;
	public static final double MIN_JIGGLE_MOVE = 0.001;
	
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
				vector.x += Math.max((Consts.SEED_RADIUS - location.x) / 2, MIN_JIGGLE_MOVE);
			
			if (location.y < Consts.SEED_RADIUS)
				vector.y += Math.max((Consts.SEED_RADIUS - location.y) / 2, MIN_JIGGLE_MOVE);
			
			if (location.x > width - Consts.SEED_RADIUS)
				vector.x -= Math.max((location.x - (width - Consts.SEED_RADIUS)) / 2, MIN_JIGGLE_MOVE);
			
			if (location.y > height - Consts.SEED_RADIUS)
				vector.y -= Math.max((location.y - (height - Consts.SEED_RADIUS)) / 2, MIN_JIGGLE_MOVE);
			
			// Test against the trees
			for (Location tree : trees) {
				if ((d = Location.distance(location, tree)) < Consts.SEED_RADIUS + Consts.TREE_RADIUS) {
					Vector2D v = new Vector2D();
					double m = Math.max(Consts.SEED_RADIUS + Consts.TREE_RADIUS - d, MIN_JIGGLE_MOVE);
					
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
					double m = Math.max(2*Consts.SEED_RADIUS - d, MIN_JIGGLE_MOVE);
					
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
		
		Random random = new Random();
		
		Location tryLocation = null;
		while (true) {
			// Deep copy the existing best locations
			ArrayList<Location> tryLocations = new ArrayList<Location>();
			for (Location location : locations)
				tryLocations.add(new Location(location));
			
			// Place a seed randomly on the field
			tryLocation = new Location(random.nextDouble()*width, random.nextDouble()*height);
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
		
		locations.add(tryLocation);
		return locations;
	}
}