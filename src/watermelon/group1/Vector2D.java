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
    
    public Vector2D add(double x, double y) {
    	this.x += x;
    	this.y += y;
    	
    	return this;
    }
    
    public Vector2D add(Vector2D v) {
    	return add(v.x, v.y);
    }
    
    public Vector2D negate() {
    	x = -x;
    	y = -y;
    	
    	return this;
    }
    
    public boolean isNone() {
    	return Math.abs(this.x) <= Consts.EPSILON && Math.abs(this.y) <= Consts.EPSILON;
    }
}
