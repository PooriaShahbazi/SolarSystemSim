package com.example.solarSystemSim;

public class LocationXYZ {
	private double x,y,z;

	public LocationXYZ() {
		super();
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	public LocationXYZ(double x, double y, double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public String toString() {
		return "("+x+","+y+","+z+")";
	}
	public double getX() { return x; }
	public void setX(double x) { this.x = x; }
	public double getY() { return y; }
	public void setY(double y) { this.y = y; }
	public double getZ() { return z; }
	public void setZ(double z) { this.z = z; }
	
	public double distanceTo(LocationXYZ l) {
		return Math.sqrt((x-l.x)*(x-l.x) + (y-l.y)*(y-l.y) + (z-l.z)*(z-l.z));
	}
	public LocationXYZ differenceVector(LocationXYZ l) {
		return new LocationXYZ(l.x-x,l.y-y,l.z-z);
	}
	public LocationXYZ mul(double c) {
		return new LocationXYZ(c*x,c*y,c*z);
	}
	public void addTo(LocationXYZ v) {
		x+=v.x; y+=v.y; z+=v.z;
	}
	public double abs() {
		return Math.sqrt(x*x+y*y+z*z);
	}

}
