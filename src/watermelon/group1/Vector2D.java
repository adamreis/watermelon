package watermelon.group1;

public class Vector2D {
    public double x;
    public double y;

    public Vector2D() {
    	this.x = 0;
    	this.y = 0;
    }
    
    public Vector2D(double x, double y) {
    	this.x = x;
    	this.y = y;
    }
    
    public Vector2D(Vector2D v) {
    	this.x = v.x;
    	this.y = v.y;
    }
    
    public void add(Vector2D v) {
    	this.x += v.x;
    	this.y += v.y;
    }
    
    public boolean isNone() {
    	return this.x == 0 && this.y == 0;
    }
}
