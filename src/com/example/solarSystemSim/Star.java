package com.example.solarSystemSim;

public class Star extends AstronomicObject {
	private double temperature; // in Kelvin
	private double distanceFromCenterOfMilkyway; // in m
	private double luminosity; // in W
	private double age; // in years
	
	public Star(String name, double mass, double radius) {
		super(name, mass, radius, 0.0, null, 0.0, 0.0, 0.0);
		if (name.equals("Sun")) {
			temperature  =1.57e7;
			distanceFromCenterOfMilkyway = 2.7e20;
			luminosity = 3.846e26;
			age = 4.6e9;
		}
		locationXYZ=new LocationXYZ(); // 0,0,0 for center
		velocityVector=new LocationXYZ(); // 0,0,0 for no movement
	}
	
	public double getTemp() {
		return temperature;
	}
}
