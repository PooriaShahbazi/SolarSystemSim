package com.example.solarSystemSim;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SimEngine {
	private boolean debug;
	private Display display;
	private Shell shell;
	private Canvas canvas;
	ArrayList<AstronomicObject> myAstronomicObjects;
	private double maximumRadius;
	private double distanceScaling; // pixel per meter
	private double sizeScaling; // pixel per meter (different from distanceScaling otherwise objects would look too small)
	private int canvasSizeX;
	private int canvasSizeY;
	private double startRealTime;
	private double resumeRealTime;
	private double resumeSimulatedTime;
	private double simTimeFactor; // >1, ratio of simulated time / real time on computer
	private double lastStepSimTime;
	private boolean paused=false;
	private boolean zooming = false;
	private double targetScaling;
	private double zoomDuration = 1.0; // in seconds


	// constructor: Initialize all objects
	public SimEngine(boolean debug, Display display, Shell shell) {
		super();
		this.debug = debug;
		this.display = display;
		this.shell = shell;
		if (debug) System.out.println("SimEngine() constructor:");
		myAstronomicObjects = new ArrayList<AstronomicObject>();
		Star   sun     = new Star("Sun",       1.98855e30, 696342000.0); // http://en.wikipedia.org/wiki/Sun
		Planet mercury = new Planet("Mercury", 3.30220e23, 2439700.0,  57909050000.0, sun, 0.0, 47362.0, 7.0); // http://en.wikipedia.org/wiki/Mercury_%28planet%29
		Planet venus   = new Planet("Venus",   4.86760e24, 6051800.0, 108208000000.0, sun, 0.0, 35020.0, 3.4); // http://en.wikipedia.org/wiki/Venus
		Planet earth   = new Planet("Earth",   5.97219e24, 6378800.0, 149598261000.0, sun, 0.0, 29780.0, 1.57); // http://en.wikipedia.org/wiki/Earth
		Planet mars    = new Planet("Mars",    6.41850e23, 3376200.0, 227939100000.0, sun, 0.0, 24077.0, 1.67); // http://en.wikipedia.org/wiki/Mars
		Moon   luna    = new Moon("Luna",      7.34770e22, 1735970.0,    384405000.0, earth, 0.0, 1022.0, 5.145); // http://en.wikipedia.org/wiki/Moon
		
		// TODO: Planet Jupiter .. Neptun
		Planet Jupiter = new Planet("Jupiter", 1.89820e27, 69911000.0,  777920000000.0, sun, 0.0, 13060.0, 1.30);
		Planet Saturn = new Planet("Saturn", 	 5.683e26, 58232000.0, 1433530000000.0, sun, 0.0,  9680.0, 2.49);
		Planet Uranus = new Planet("Uranus",     8.681e25, 25362000.0, 2870972000000.0, sun, 0.0,  6800.0, 0.77);
		Planet Neptun = new Planet("Neptun",   1.02409e26, 24622000.0, 4500000000000.0, sun, 0.0,  5430.0, 1.77);
		
		// TODO: Comet halley = new Comet("Halley", ...);
		Comet Halley = new Comet("Halley", 		   2.2e14,    11000.0, 5250000000000.0, sun, 0.0, 54600.0,  0.0);
		
		sun.setColor(new RGB(255,255,0)); // yellow
		mercury.setColor(new RGB(139,137,112)); // greyish
		venus.setColor(new RGB(238,220,130)); // ocker
		earth.setColor(new RGB(0,0,255)); // blue 
		mars.setColor(new RGB(210,105,30)); // red
		luna.setColor(new RGB(105,105,105)); // grey 
		Jupiter.setColor(new RGB(106,81,119));
		Saturn.setColor(new RGB(201,180,140));
		Uranus.setColor(new RGB(173,216,230));
		Neptun.setColor(new RGB(38,92,148));
		Halley.setColor(new RGB(230, 230, 230));
		addAstronomicObjects(sun,mercury,venus,earth,luna,mars,Jupiter,Saturn,Uranus,Neptun,Halley);

		if (debug) System.out.println("SimEngine() constructed.");
		//startTimeMS = System.currentTimeMillis();
		startRealTime = System.currentTimeMillis() / 1000.0;
		resumeRealTime = startRealTime;
		simTimeFactor = 24.0 * 60.0 * 60.0; // one day per second
		lastStepSimTime = 0.0;
		resumeSimulatedTime = 0.0;
	} // constructor

	public void resetSimulation() {
	    myAstronomicObjects.clear(); // Clear existing objects
	}
	
	public void stopSimulation() {
		    myAstronomicObjects.clear();
	}

	public void animateZoom(double newScaling) {
	    if (zooming) return; // Avoid overlapping animations
	    zooming = true;
	    targetScaling = newScaling;

	    double startScaling = distanceScaling;
	    double startTime = System.currentTimeMillis() / 1000.0;

	    display.timerExec(10, new Runnable() {
	        @Override
	        public void run() {
	            double currentTime = System.currentTimeMillis() / 1000.0;
	            double elapsed = currentTime - startTime;
	            if (elapsed >= zoomDuration) {
	                distanceScaling = targetScaling;
	                zooming = false;
	                if (canvas != null && !canvas.isDisposed()) {
	                    canvas.redraw();
	                }
	                return;
	            }
	            // Interpolate scaling value
	            double t = elapsed / zoomDuration;
	            distanceScaling = startScaling + t * (targetScaling - startScaling);

	            if (canvas != null && !canvas.isDisposed()) {
	                canvas.redraw(); // Redraw with updated scaling
	            }

	            // Continue the animation
	            display.timerExec(10, this);
	        }
	    });
	}
	
	public boolean isPaused() {	return paused; }
	public void setPaused(boolean paused) {	this.paused = paused; }
	public void togglePaused() {	this.paused = !paused; }

	public double getSimTimeFactor() {
		return simTimeFactor;
	}

	public void setSimTimeFactor(double simTimeFactor) {
		this.simTimeFactor = simTimeFactor;
		resumeRealTime = System.currentTimeMillis() / 1000.0;
		resumeSimulatedTime = lastStepSimTime;
	}

	public double getSimTime() { // in seconds
		double now = System.currentTimeMillis() / 1000.0;
		// convert computer time (real time) to simulated time, e.g. 1 second -> 1 day
		double simTime = (now - resumeRealTime) * simTimeFactor + resumeSimulatedTime;
		return simTime;
	}
	public String getSimTimeFormattedString() {
		double simTime = getSimTime();
		int days = (int) Math.floor(simTime/24.0/60.0/60.0);
		int hours = (int) ((simTime - (double)days*24.0*60.0*60.0)/3600.0);
		return String.format("%dd:%02dh",days,hours);
	}

	public void addAstronomicObject(AstronomicObject o) {
		myAstronomicObjects.add(o);
		double r = o.getDistanceFromCenter();
		if (r>maximumRadius) maximumRadius=r; // should fit into canvas
	}
	public void addAstronomicObjects(AstronomicObject... olist) {
		for (AstronomicObject o : olist)
			addAstronomicObject(o);
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
		if (debug) System.out.println("SimEngine::setCanvas()");
		updateCanvasSize();
	}
	
	public void updateCanvasSize() {
	    Rectangle canvasRect = canvas.getClientArea();
	    canvasSizeX = canvasRect.width;
	    canvasSizeY = canvasRect.height;

	    // Recalculate the maximum radius based on visible objects
	    maximumRadius = 0;
	    for (AstronomicObject o : myAstronomicObjects) {
	        if (o.isVisible()) {
	            double r = o.getDistanceFromCenter();
	            if (r > maximumRadius) maximumRadius = r;
	        }
	    }

	    // Update distance scaling (zoom)
	    distanceScaling = Math.min(canvasSizeX, canvasSizeY) / 2.0 / maximumRadius;

	    // Update size scaling for real sizes
	    double maxObjectRadiusInMeters = 696342000.0; // Example: Sun's radius (adjust as needed)
	    double canvasSizeInPixels = Math.min(canvasSizeX, canvasSizeY) / 10.0; // Arbitrary reference
	    sizeScaling = canvasSizeInPixels / maxObjectRadiusInMeters;

	    if (debug) {
	        System.out.println("SimEngine::updateCanvasSize(" + canvasSizeX + "," + canvasSizeY +
	                "): distanceScaling=" + distanceScaling + ", sizeScaling=" + sizeScaling);
	    }
	}


	public void update() {
	    if (paused) return; // Skip updates when paused
		double simTime = getSimTime();
		double deltaT=simTime-lastStepSimTime;
		if (debug) System.out.format("SimEngine::update(): deltaT=%.3fh\n",deltaT/60.0/60.0);
		for (AstronomicObject o : myAstronomicObjects) {
			LocationXYZ velocityVector = o.getVelocityVector();
			if (debug) System.out.println("Object "+o+" @ "+o.getLocationXYZ());
			if (debug) System.out.println("   old velocityVector="+velocityVector);
			double gravityFactor = AstronomicObject.GRAVITATIONALCONSTANT * o.getMass();
			for (AstronomicObject other : myAstronomicObjects) {
				if (other == o) continue; // <- no gravity from myself
				double distance = o.locationXYZ.distanceTo(other.getLocationXYZ());
				double gravitationalForce = gravityFactor * other.getMass() / distance / distance; 
				double acceleration = gravitationalForce/o.getMass(); // F=m*a <=> a=F/m
				//LocationXYZ accelerationVector = o.locationXYZ.differenceVector(other.locationXYZ).mul(acceleration/distance);
				LocationXYZ velocityChangeVector = o.locationXYZ.differenceVector(other.locationXYZ).mul(deltaT*acceleration/distance); // integration
				if (debug) System.out.format("   interacting with "+other+ " at distance=%.0fkm: %5.0fN, a=%fm/s�\n",distance/1000.0,gravitationalForce,acceleration);
				if (debug) System.out.println("   velocityChangeVector="+velocityChangeVector);
				velocityVector.addTo(velocityChangeVector);
			}
			if (debug) System.out.println("   new velocityVector="+velocityVector);
			LocationXYZ moveVector = velocityVector.mul(deltaT); // integration
			o.locationXYZ.addTo(moveVector);
			if (debug) System.out.println("   new location="+o.getLocationXYZ());
			if (!o.getClass().getName().endsWith("Moon")) { // don't check moons
				assert(o.checkOrbit()==true);
				if (debug && !o.checkOrbitStable()) System.err.println(o+" unstable orbit!");
				if (debug && !o.checkOrbit()) System.err.println(o+" left orbit!");
				if (debug && !o.checkVelocity()) System.err.println(o+" changed velocity!");
			}
		}
		lastStepSimTime = simTime;
	}
	
	public void updateVisibility() {
	    // Recalculate the new maximum radius based on visible objects
	    double newMaximumRadius = 0;
	    for (AstronomicObject o : myAstronomicObjects) {
	        if (o.isVisible()) {
	            double r = o.getDistanceFromCenter();
	            if (r > newMaximumRadius) newMaximumRadius = r;
	        }
	    }
	    double newScaling = Math.min(canvasSizeX, canvasSizeY) / 2 / newMaximumRadius;
	    animateZoom(newScaling); // Trigger zoom animation
	}

	public void drawAll(GC gc) {
		if (debug) System.out.println("SimEngine::drawAll()");
		
		for (AstronomicObject o : myAstronomicObjects) {
	        if (!o.isVisible()) continue; // Skip invisible objects
	        
			int centerX=canvasSizeX/2, centerY=canvasSizeY/2; // center of the screen == position of sun
			int pixelRadius = (int) Math.ceil(o.getRadius() * sizeScaling);
			if (pixelRadius>10) pixelRadius=10; // avoid the sun getting too large
			int pixelDistance = (int) Math.ceil(o.getDistanceFromCenter() * distanceScaling);
			LocationXYZ locationXYZ = o.getLocationXYZ();
			String typeString = o.getClass().getName();
			if (typeString.endsWith("Moon")) {
				// TODO: make orbit larger
			}
			int orbitX = centerX + (int) Math.ceil(locationXYZ.getX() * distanceScaling);
			int orbitY = centerY - (int) Math.ceil(locationXYZ.getY() * distanceScaling);
			//                   ^ "-" because higher Y means up but on display it is counted from top to bottom.
			RGB colorRGB = o.getColor();
			if (debug) System.out.println(typeString+" "+o+": Radius="+o.getRadius()+"m="+pixelRadius+"px. OrbitXY=("+orbitX+","+orbitY+")");
			//gc.setLineWidth(centerObjectSize); // 4 would be thick
			// draw orbit:
			gc.setForeground(display.getSystemColor (SWT.COLOR_DARK_GRAY));
			gc.drawOval(centerX-pixelDistance, centerY-pixelDistance, pixelDistance*2, pixelDistance*2);
			// draw object itself:
			Color color = new Color(display,colorRGB);
			gc.setForeground(color);			
			gc.setBackground(color); // background fill color
			gc.fillOval(orbitX-pixelRadius, orbitY-pixelRadius, pixelRadius*2, pixelRadius*2);
			gc.drawOval(orbitX-pixelRadius, orbitY-pixelRadius, pixelRadius*2, pixelRadius*2);
			
			gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE)); // Label color
			
			if (!(o instanceof Moon)) {
				gc.drawText(o.getName(), orbitX + pixelRadius + 5, orbitY - 10, true);
			}
			
	        // Additional logic for moons
	        if (o instanceof Moon) {
	            AstronomicObject center = o.getCenter();
	            if (center != null && center.isVisible()) {
	                gc.drawText(o.getName(), orbitX + 5, orbitY + 10, true);
	            }
	        }
	        if (o instanceof Star) {
	        	Star star = (Star) o;
	        	String tempText = String.format("Temp: %.2f K", star.getTemp());
	        	gc.drawText(tempText, orbitX + pixelRadius + 5, orbitY + 5, true);
	        }
		}		
	}
}
