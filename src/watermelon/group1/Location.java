package watermelon.group1;

public class Location {
    public double x;
    public double y;

    public Location(double x, double y) {
    	this.x = x;
    	this.y = y;
    }
    
    public Location(Location location) {
    	this.x = location.x;
    	this.y = location.y;
    }
    
    static public double distance(Location l1, Location l2) {
    	return Math.sqrt((l1.x - l2.x)*(l1.x - l2.x) + (l1.y - l2.y)*(l1.y - l2.y));
    }
}
