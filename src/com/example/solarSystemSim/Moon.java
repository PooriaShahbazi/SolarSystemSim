package com.example.solarSystemSim;

public class Moon extends AstronomicObject {

	public Moon(String name, double mass, double radius,
			double distanceFromCenter, AstronomicObject center,
			double startAngle, double initialSpeed, double elevationAngle) {
		super(name, mass, radius, distanceFromCenter, center, startAngle,
				initialSpeed, elevationAngle);
		if (center!=null) {
			double x = center.locationXYZ.getX() + distanceFromCenter*Math.cos(startAngle*Math.PI/180.0);
			double y = center.locationXYZ.getY() + distanceFromCenter*Math.sin(startAngle*Math.PI/180.0);
			double z = center.locationXYZ.getZ() + distanceFromCenter*Math.sin(elevationAngle*Math.PI/180.0);
			locationXYZ = new LocationXYZ(x,y,z);
			z = 0; // speed vector:
			x = -initialSpeed*Math.sin(startAngle*Math.PI/180.0);
			y = initialSpeed*Math.cos(startAngle*Math.PI/180.0);
			velocityVector=new LocationXYZ(x,y,z);
			velocityVector.addTo(center.getVelocityVector()); // must add orbital speed of center object because its own orbital speed is relative to the center
		}
	}

}
