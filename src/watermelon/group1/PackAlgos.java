package watermelon.group1;

import java.util.*;
import java.io.*;

import watermelon.group1.Consts;
import watermelon.group1.Location;

public class PackAlgos {
	public static enum Corner { UL, BL, UR, BR };
	public static enum Direction { H, V };
	
	private static final int MAX_PHYSICAL_ITERATIONS = 1000;
	private static final double MIN_PHYSICAL_MOVE = 0.01;
	private static final double PHYSICAL_SEARCH_GRANULARITY = (2 * Consts.SQRT_3) / (2 + Consts.SQRT_3);
	private static final int MAX_PHYSICAL_FAILURES_PER_LOCATION = 3;
	
	private static boolean closeToTree(Location location, ArrayList<Location> trees) {
		return Location.nearAny(location, trees, Consts.SEED_RADIUS + Consts.TREE_RADIUS - Consts.EPSILON);
	}
	
	private static boolean closeToTree(double x, double y, ArrayList<Location> trees) {
		return closeToTree(new Location(x, y), trees);
	}
	
	private static void pruneForTrees(ArrayList<Location> locations, ArrayList<Location> trees) {
		int i;
		Location location;
		
		for (i = locations.size() - 1; i >= 0; i--) {
			location = locations.get(i);
			if (closeToTree(location, trees))
				locations.remove(i);
		}
	}
	
	public static ArrayList<Location> rectilinear(ArrayList<Location> trees, double width, double height, Corner corner, boolean spreadApart, Location treeToAvoid) {
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
		
		if (treeToAvoid != null) {
			xStart = corner == Corner.UL || corner == Corner.BL ? 
					xStart + (treeToAvoid.x + Consts.SEED_RADIUS) % 2*Consts.SEED_RADIUS : xStart - (treeToAvoid.x + Consts.SEED_RADIUS) % 2*Consts.SEED_RADIUS;
			yStart = corner == Corner.UL || corner == Corner.UR ? 
					yStart + (treeToAvoid.y + Consts.SEED_RADIUS) % 2*Consts.SEED_RADIUS : yStart - (treeToAvoid.y + Consts.SEED_RADIUS) % 2*Consts.SEED_RADIUS;
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
	
	public static ArrayList<Location> hexagonal(ArrayList<Location> trees, double width, double height, Corner corner, Direction direction, boolean spreadApart, Location treeToAvoid) {
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
		
		ArrayList<Location> treeIntersectors = new ArrayList<Location>();
		ArrayList<Location> locations = new ArrayList<Location>();
		
		// numLines is the number of rows/columns that we have along the given direction.  If direction is horizontal, it means the seeds are side by side horizontally, and the number of rows relates to the height of the field.  It is analogous if the direction is vertical
		int numLines;
		if (direction == Direction.H)
			numLines = (int) Math.floor((height - 2*Consts.SEED_RADIUS) / (Consts.SQRT_3*Consts.SEED_RADIUS)) + (height - 2*Consts.SEED_RADIUS > 0 ? 1 : 0);
		else
			numLines = (int) Math.floor((width - 2*Consts.SEED_RADIUS) / (Consts.SQRT_3*Consts.SEED_RADIUS)) + (width - 2*Consts.SEED_RADIUS > 0 ? 1 : 0);
		
		// With the number of lines, if spreading is on, we can calculate the additional offset needed on that dimension to spread
		double spreadAmount = Consts.SQRT_3*Consts.SEED_RADIUS;
		
		if (spreadApart) {
			if (direction == Direction.H)
				spreadAmount += (height - (2*Consts.SEED_RADIUS + (numLines - 1)*spreadAmount)) / (numLines - 1) - Consts.EPSILON;
			else
				spreadAmount += (width - (2*Consts.SEED_RADIUS + (numLines - 1)*spreadAmount)) / (numLines - 1) - Consts.EPSILON;
		}
		
		x = xStart;
		y = yStart;
		offset = false;
		
		if (direction == Direction.H) {
			while (y >= Consts.SEED_RADIUS && y <= height - Consts.SEED_RADIUS) {
				x = xStart + (offset ? Consts.SEED_RADIUS * xSign : 0);
				
				while (x >= Consts.SEED_RADIUS && x <= width - Consts.SEED_RADIUS) {
					if (!closeToTree(x, y, trees))
						locations.add(new Location(x, y));
					else
						treeIntersectors.add(new Location(x, y));
					
					x += 2*Consts.SEED_RADIUS * xSign;
				}
				
				y += spreadAmount * ySign;
				offset = !offset;
			}
		} else {
			while (x >= Consts.SEED_RADIUS && x <= width - Consts.SEED_RADIUS) {
				y = yStart + (offset ? Consts.SEED_RADIUS * ySign : 0);
				
				while (y >= Consts.SEED_RADIUS && y <= height - Consts.SEED_RADIUS) {
					if (!closeToTree(x, y, trees))
						locations.add(new Location(x, y));
					else
						treeIntersectors.add(new Location(x, y));
					
					y += 2*Consts.SEED_RADIUS * ySign;
				}
				
				x += spreadAmount * xSign;
				offset = !offset;
			}
		}
		if (treeToAvoid != null) {
			ArrayList<Location> newLocations = new ArrayList<Location>();
			double xOff = Double.MAX_VALUE;
			double yOff = Double.MAX_VALUE;
			for (Location loc : treeIntersectors) {
				double newXOff = treeToAvoid.x - loc.x;
				double newYOff = treeToAvoid.y - loc.y;
				if (newXOff + newYOff < xOff + yOff) {
					xOff = newXOff;
					yOff = newYOff;
				}
			}
			for (Location loc : locations) {
				loc.x += xOff;
				loc.y += yOff;

				if (!closeToTree(loc.x, loc.y, trees) && isInBounds(loc, width, height))
					newLocations.add(loc);
			}
			for (Location loc : treeIntersectors) {
				loc.x += xOff;
				loc.y += yOff;
				if (!closeToTree(loc.x, loc.y, trees) && isInBounds(loc, width, height))
						newLocations.add(loc);
			}
			locations = newLocations;
		}
		
		return locations;
	}
	
	private static boolean simulateForces(ArrayList<Location> locations, ArrayList<Vector2D> vectors, ArrayList<Location> trees, double width, double height) {
		for (Vector2D vector : vectors) {
			vector.x = 0;
			vector.y = 0;
		}
		
		// For each location, calculate its vector
		for (int i = 0; i < locations.size(); i++) {
			Location location = locations.get(i);
			Vector2D vector = vectors.get(i);
			double d;
			
			// Test against the walls
			if (location.x < Consts.SEED_RADIUS)
				vector.x += Math.max((Consts.SEED_RADIUS - location.x) / 2, Math.min(Consts.SEED_RADIUS - location.x, MIN_PHYSICAL_MOVE));
			
			if (location.y < Consts.SEED_RADIUS)
				vector.y += Math.max((Consts.SEED_RADIUS - location.y) / 2, Math.min(Consts.SEED_RADIUS - location.y, MIN_PHYSICAL_MOVE));
			
			if (location.x > width - Consts.SEED_RADIUS)
				vector.x -= Math.max((location.x - (width - Consts.SEED_RADIUS)) / 2, Math.min(location.x - (width - Consts.SEED_RADIUS), MIN_PHYSICAL_MOVE));
			
			if (location.y > height - Consts.SEED_RADIUS)
				vector.y -= Math.max((location.y - (height - Consts.SEED_RADIUS)) / 2, Math.min(location.y - (height - Consts.SEED_RADIUS), MIN_PHYSICAL_MOVE));
			
			// Test against the trees
			for (Location tree : trees) {
				if ((d = Location.distance(location, tree)) < Consts.SEED_RADIUS + Consts.TREE_RADIUS) {
					double m = Math.max((Consts.SEED_RADIUS + Consts.TREE_RADIUS - d) / 2, Math.min(Consts.SEED_RADIUS + Consts.TREE_RADIUS - d, MIN_PHYSICAL_MOVE));
					
					vector.x += Math.sqrt(Math.abs(location.x - tree.x)) * m * Math.signum(location.x - tree.x);
					vector.y += Math.sqrt(Math.abs(location.y - tree.y)) * m * Math.signum(location.y - tree.y);
				}
			}
			
			// Test against the other locations
			for (int j = i + 1; j < locations.size(); j++) {
				Location testLocation = locations.get(j);
				
				if ((d = Location.distance(location, testLocation)) < 2*Consts.SEED_RADIUS) {
					double m = Math.max((2*Consts.SEED_RADIUS - d) / 2, Math.min((2*Consts.SEED_RADIUS - d), MIN_PHYSICAL_MOVE));
					
					double x = Math.sqrt(Math.abs(location.x - testLocation.x)) * m * Math.signum(location.x - testLocation.x);
					double y = Math.sqrt(Math.abs(location.y - testLocation.y)) * m * Math.signum(location.y - testLocation.y);
					
					vector.add(x, y);
					vectors.get(j).add(-x, -y);
				}
			}
		}
		
		boolean success = true;
		
		// Move each location by its vector and ensure it's not a zero vector because it's balanced between trees
		for (int i = 0; i < locations.size(); i++) {
			Vector2D v = vectors.get(i);
			Location location = locations.get(i);
			
			if (!v.isNone()) {
				success = false;
				location.x += v.x;
				location.y += v.y;
			}
			
			if (!isInBounds(location, width, height))
				success = false;
			
			if (closeToTree(location, trees))
				success = false;
		}
		
		return success;
	}
	
	private static ArrayList<Location> physicalWithExisting(ArrayList<Location> trees, ArrayList<Location> existingLocations, double width, double height) {
		ArrayList<Location> locationsPacked = new ArrayList<Location>();			// Final locations to return
		ArrayList<Location> locationsToTry = new ArrayList<Location>();				// List of locations to try for the simulation; the front of the list has higher priority locations to try than the end of the list
		ArrayList<Location> locationsToSimulate = null;								// The current set of locations being simulated; locations will be set to this if the simulation succeeds in finding a valid field
		Location locationToTry = null;
		HashMap<String, Integer> numFailures = new HashMap<String, Integer>();		// Stores the number of times the simulation has failed for a particular location, so that we can avoid repeating those failures
		
		// Loose upper bound on the maximum number of seeds
		int maxSeeds = (int) (width * height / (Math.PI * Math.pow(Consts.SEED_RADIUS, 2)));
		
		// We set up the vector list now to avoid expensive object creation and garbage collection during each simulation
		ArrayList<Vector2D> vectors = new ArrayList<Vector2D>(maxSeeds);
		for (int i = 0; i < maxSeeds; i++)
			vectors.add(new Vector2D());
		
		if (existingLocations != null) {
			for (Location l : existingLocations)
				locationsPacked.add(l);
		}
		
		for (double x = Consts.SEED_RADIUS; x <= width - Consts.SEED_RADIUS; x += PHYSICAL_SEARCH_GRANULARITY) {
			for (double y = Consts.SEED_RADIUS; y <= height - Consts.SEED_RADIUS; y += PHYSICAL_SEARCH_GRANULARITY) {
				Location l = new Location(x, y);
				boolean isValid = true;
						
				for (Location tree : trees) {
					if (Location.equals(l, tree)) {
						isValid = false;
						break;
					}
				}
				
				if (!isValid)
					continue;
					
				locationsToTry.add(l);
				numFailures.put(l.toString(), 0);
			}
		}
		
		while (true) {
			// Try various places to place a new seed on the field.  We shuffle the locations to avoid repeatedly running likely fruitless simulations in each iteration
			Collections.shuffle(locationsToTry);
			boolean success = false;
			
			for (Location locationToTryOrig : locationsToTry) {
				locationToTry = new Location(locationToTryOrig);
								
				if (numFailures.get(locationToTry.toString()) >= MAX_PHYSICAL_FAILURES_PER_LOCATION)
					continue;
				
				boolean isValid = true;
				
				for (Location location : locationsPacked)
					if (Location.equals(locationToTry, location))
						isValid = false;
								
				if (!isValid)
					continue;
				
				// Deep copy the existing best locations and add the new one to try
				locationsToSimulate = new ArrayList<Location>();
				for (Location location : locationsPacked)
					locationsToSimulate.add(new Location(location));
				
				locationsToSimulate.add(locationToTry);
				
				for (int i = 0; i < MAX_PHYSICAL_ITERATIONS; i++) {
					success = simulateForces(locationsToSimulate, vectors, trees, width, height);
					if (success) {
						numFailures.put(locationToTryOrig.toString(), 0);
						break;
					} else {
						numFailures.put(locationToTryOrig.toString(), numFailures.get(locationToTryOrig.toString()) + 1);
					}
				}
				
				if (success) break;
			}
			
			if (!success) break;
			
			locationsPacked = locationsToSimulate;
		}
		
		return locationsPacked;
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
		ArrayList<Location> locations = getBestKnownLocations(num, scale, dimension);
		
		if (locations == null)
			return null;
		
		// Remove tree intersections
		pruneForTrees(locations, trees);
		
		// Fill in the remaining space with a physical packing
		locations = physicalWithExisting(trees, locations, width, height);
		
		return locations;
	}
	
	private static boolean isInBounds(Location location, double width, double height) {
		return !(location.x < Consts.SEED_RADIUS - Consts.EPSILON || location.x > width - (Consts.SEED_RADIUS - Consts.EPSILON) || location.y < Consts.SEED_RADIUS - Consts.EPSILON || location.y > height - (Consts.SEED_RADIUS - Consts.EPSILON));
	}
}