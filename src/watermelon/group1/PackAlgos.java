package watermelon.group1;

import java.util.*;
import java.io.*;

import watermelon.group1.Consts;
import watermelon.group1.Location;
import watermelon.group1.Vector2D;

public class PackAlgos {
	public static enum Corner { UL, BL, UR, BR };
	public static enum Direction { H, V };
	
	private static final int MAX_JIGGLES = 50;
	private static final double MIN_JIGGLE_MOVE = 0.01;
	private static final double LOCATION_GRANULARITY = 0.25;
	
	private static boolean closeToTree(double x, double y, ArrayList<Location> trees) {
		for (Location tree : trees) {
			if (Location.distance(x,  y, tree.x, tree.y) < (Consts.SEED_RADIUS + Consts.TREE_RADIUS - Consts.EPSILON))
				return true;
		}
		
		return false;
	}
	
	public static ArrayList<Location> rectilinear(ArrayList<Location> trees, double width, double height, Corner corner, boolean spreadApart) {
		double x, y, xStart, yStart, extraX, extraY, xSpacing, ySpacing;
		int xSign, ySign, numX, numY;
		
		if (spreadApart) {
			numX = (int) Math.floor(width / (2 * Consts.SEED_RADIUS));
			numY = (int) Math.floor(height / (2 * Consts.SEED_RADIUS));
			
			extraX = width - numX * 2 * Consts.SEED_RADIUS;
			extraY = height - numY * 2 * Consts.SEED_RADIUS;
			
			xSpacing = extraX / (numX - 1);
			ySpacing = extraY / (numY - 1);
		} else {
			xSpacing = 0;
			ySpacing = 0;
		}
		
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
				
				x += (2*Consts.SEED_RADIUS + xSpacing) * xSign;
			}
			
			y += (2*Consts.SEED_RADIUS + ySpacing)* ySign;
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
		
		// Move each location by its vector and ensure its not a zero vector because it's balanced between trees
		for (int i = 0; i < locations.size(); i++) {
			Vector2D v = vectors.get(i);
			Location location = locations.get(i);
			
			if (!v.isNone()) {
				success = false;
				location.x += v.x;
				location.y += v.y;
			}
			
			if (location.x < Consts.SEED_RADIUS - Consts.EPSILON || location.x > width - (Consts.SEED_RADIUS - Consts.EPSILON) || location.y < Consts.SEED_RADIUS - Consts.EPSILON || location.y > height - (Consts.SEED_RADIUS - Consts.EPSILON))
				success = false;
			
			if (closeToTree(location.x, location.y, trees))
				success = false;
		}
		
		return success;
	}
	
	private static ArrayList<Location> physicalWithExisting(ArrayList<Location> trees, ArrayList<Location> existingLocations, double width, double height) {
		ArrayList<Location> locations = new ArrayList<Location>();
		
		if (existingLocations != null) {
			for (Location location : existingLocations)
				locations.add(location);
		}
		
		Location tryLocation = null;
		ArrayList<Location> tryLocations = null;
		
		while (true) {
			// Try various places to place a new seed on the field
			boolean success = false;
			
			for (double x = Consts.SEED_RADIUS; x < width - Consts.SEED_RADIUS; x += LOCATION_GRANULARITY) {
				for (double y = Consts.SEED_RADIUS; y < height - Consts.SEED_RADIUS; y += LOCATION_GRANULARITY) {
					tryLocation = new Location(x, y);
					boolean invalid = false;
					
					for (Location tree : trees) {
						if (Location.distance(tryLocation, tree) < (Consts.SEED_RADIUS + Consts.TREE_RADIUS) / 2)
							invalid = true;
					}
					
					for (Location location : locations) {
						if (Location.distance(tryLocation, location) < Consts.SEED_RADIUS)
							invalid = true;
					}
					
					// Do not try this location if it exactly overlaps an existing one (or is fairly close)
					if (invalid) continue;
					
					// Deep copy the existing best locations and add the new one to try
					tryLocations = new ArrayList<Location>();
					for (Location location : locations)
						tryLocations.add(new Location(location));
					
					tryLocations.add(tryLocation);
					
					for (int i = 0; i < MAX_JIGGLES; i++) {
						success = jiggleLocations(tryLocations, trees, width, height);
						if (success) break;
					}
					
					if (success) break;
				}
				
				if (success) break;
			}
			
			if (!success) break;
			
			locations = tryLocations;
		}
		
		return locations;
	}
	
	public static ArrayList<Location> physical(ArrayList<Location> trees, double width, double height) {
		return physicalWithExisting(trees, null, width, height);
	}
	
	// Returns number of circles and scaling factor
	private static double[] bestKnownNumCircles(double dimension) {
		int num, lastNum = 0;
		double scale, lastScale = 0;
		double[] res = {0.0, 0.0};
		
		// Search the radius file to find the highest number of circles a square of size dimension*dimension can accommodate
		File file = new File("watermelon/group1/bestKnown/radius.txt");
		if (!file.exists()) {
			System.err.printf("Error: Unable to open radius.txt\n");
			return null;
		}
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] tokens = line.split(" ");
				num = Integer.parseInt(tokens[0]);
				scale = 1.0 / Double.parseDouble(tokens[1]);
				if (scale > dimension)
					break;
				
				lastNum = num;
				lastScale = scale;
			}
			br.close();
		} catch (IOException e) {
			System.err.printf("Error: Unable to open radius.txt\n");
			return null;
		}

		if (lastScale > dimension)
			return null;
		
		res[0] = (double) lastNum;
		res[1] = lastScale;
		
		return res;
	}
	
	private static ArrayList<Location> getBestKnownLocations(int num, double scale, double dimension) {
		ArrayList<Location> locations = new ArrayList<Location>();
		
		// Search the radius file to find the highest number of circles a square of size dimension*dimension can accommodate
		File file = new File("watermelon/group1/bestKnown/csq" + Integer.toString(num) + ".txt");
		if (!file.exists()) {
			System.err.printf("Error: Unable to open square file.txt\n");
			return null;
		}
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				line.trim();
				String[] tokens = line.split("[ ]+");
				locations.add(new Location(dimension/2 + Double.parseDouble(tokens[2])*scale, dimension/2 + Double.parseDouble(tokens[3])*scale));
			}
			br.close();
		} catch (IOException e) {
			System.err.printf("Error: Unable to open square file.txt\n");
			return null;
		}
		
		return locations;
	}
	
	public static ArrayList<Location> bestKnown(ArrayList<Location> trees, double width, double height) {
		double dimension = Math.min(width, height);
		double best[] = bestKnownNumCircles(dimension);
		
		if (best == null)
			return null;
		
		int num = (int) best[0];
		double scale = best[1];
		
		// Open the corresponding coordinates file and create the locations
		ArrayList<Location> tentativeLocations = getBestKnownLocations(num, scale, dimension);
		
		if (tentativeLocations == null)
			return null;
		
		// Remove tree intersections
		ArrayList<Location> locations = new ArrayList<Location>();
		for (Location tentativeLocation : tentativeLocations) {
			if (!closeToTree(tentativeLocation.x, tentativeLocation.y, trees))
				locations.add(tentativeLocation);
		}
		
		// Fill in the remaining space with a physical packing
		locations = physicalWithExisting(trees, locations, width, height);
		
		return locations;
	}
}