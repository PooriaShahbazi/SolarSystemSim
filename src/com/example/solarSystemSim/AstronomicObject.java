package com.example.solarSystemSim;

import org.eclipse.swt.graphics.RGB;

public class AstronomicObject {
	final static double GRAVITATIONALCONSTANT = 6.67394e-11; // Nm^2/kg^2 // http://en.wikipedia.org/wiki/Gravity
	// ^ for calculating Force F=G*m1*m2/r^2
	private String name;
	private double mass; // in kg
	private double radius; // in m
	private double distanceFromCenter; // in m
	private AstronomicObject center; // my center object or null for sun
	private double startAngle; // where is this object relative to its center at time 0?
	private double initialSpeed; // in m/s
	private double elevationAngle; // in degrees. if orbit is not on the same plane as coordinate system (x,y)-plane
	private RGB color;
	protected LocationXYZ locationXYZ;
	protected LocationXYZ velocityVector;

	private boolean visible = true; // Default is visible

	public boolean isVisible() {
	    return visible;
	}

	public void setVisible(boolean visible) {
	    this.visible = visible;
	}

	public LocationXYZ getLocationXYZ() {
		return locationXYZ;
	}

	public void setLocationXYZ(LocationXYZ locationXYZ) {
		this.locationXYZ = locationXYZ;
	}

	public RGB getColor() {
		if (color==null) color=new RGB(105,105,105); // default grey
		return color;
	}

	public void setColor(RGB color) {
		this.color = color;
	}

	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getMass() {
		return mass;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public double getDistanceFromCenter() {
		return distanceFromCenter;
	}

	public void setDistanceFromCenter(double distanceFromCenter) {
		this.distanceFromCenter = distanceFromCenter;
	}

	public AstronomicObject getCenter() {
		return center;
	}

	public void setCenter(AstronomicObject center) {
		this.center = center;
	}

	public double getStartAngle() {
		return startAngle;
	}

	public void setStartAngle(double startAngle) {
		this.startAngle = startAngle;
	}

	public double getInitialSpeed() {
		return initialSpeed;
	}

	public void setInitialSpeed(double initialSpeed) {
		this.initialSpeed = initialSpeed;
	}

	public double getElevationAngle() {
		return elevationAngle;
	}

	public void setElevationAngle(double elevationAngle) {
		this.elevationAngle = elevationAngle;
	}

	public LocationXYZ getVelocityVector() {
		return velocityVector;
	}

	public AstronomicObject(String name, double mass, double radius,
			double distanceFromCenter, AstronomicObject center,
			double startAngle, double initialSpeed, double elevationAngle) {
		super();
		this.name = name;
		this.mass = mass;
		this.radius = radius;
		this.distanceFromCenter = distanceFromCenter;
		this.center = center;
		this.startAngle = startAngle;
		this.initialSpeed = initialSpeed;
		this.elevationAngle = elevationAngle;
	}

	public boolean checkOrbitStable() {
	    if (center == null || distanceFromCenter == 0) {
	        return true;
	    }

	    // Calculate the expected velocity for a stable orbit
	    double expectedVelocitySquared = GRAVITATIONALCONSTANT * center.getMass() / distanceFromCenter;
	    double currentVelocitySquared = velocityVector.abs() * velocityVector.abs();

	    // Allow a small tolerance for numerical inaccuracies (e.g., 1%)
	    double tolerance = 0.01;
	    boolean velocityMatches = Math.abs(currentVelocitySquared - expectedVelocitySquared) / expectedVelocitySquared < tolerance;

	    // Calculate the expected orbital period for a stable orbit
	    double expectedOrbitalPeriodSquared = Math.pow(distanceFromCenter, 3) * 4 * Math.PI * Math.PI /
	                                          (GRAVITATIONALCONSTANT * center.getMass());
	    double currentOrbitalPeriodSquared = Math.pow(2 * Math.PI * distanceFromCenter / velocityVector.abs(), 2);

	    boolean periodMatches = Math.abs(currentOrbitalPeriodSquared - expectedOrbitalPeriodSquared) /
	                            expectedOrbitalPeriodSquared < tolerance;

	    return velocityMatches && periodMatches;
	}

	public boolean checkOrbit() {
		// check distance to center object. Allow 10% deviation
		if (distanceFromCenter==0) return true; // Sun is always OK
		double deviationOrbit =
			(locationXYZ.distanceTo(center.locationXYZ) - distanceFromCenter) / distanceFromCenter;
		return (Math.abs(deviationOrbit)<0.1);
	}
	public boolean checkVelocity() {
		// check distance to center object. Allow 10% deviation
		if (distanceFromCenter==0) return true; // Sun is always OK
		double deviationVelocity =
				(velocityVector.abs() - initialSpeed) / initialSpeed;
		return (Math.abs(deviationVelocity)<0.1);
	}
}
