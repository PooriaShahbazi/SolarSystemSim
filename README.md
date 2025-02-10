# SolarSystemSim
Solar System Simulator - SolarSystemSim

This application is a Java-based simulation of a simplified solar system. It models the physics of celestial bodies like planets, moons, stars, and comets using Newtonian gravitational mechanics.

The simulation is powered by a physics engine (SimEngine) that calculates and updates the motion of objects based on gravitational forces, their masses, and initial velocities. The graphical user interface (GUI) is built using the Standard Widget Toolkit (SWT), allowing users to visualize the system in real-time and interact with it.

Key Features:
•	Dynamic Simulation: Realistic simulation of planetary orbits and object interactions using Newton’s laws of motion and gravity.
•	Multiple Object Support: Supports planets, stars, moons, and comets with adjustable properties.
•	Real-Time Interaction: Start, pause, and stop the simulation, with dynamic speed control through a slider.
•	Scalable System: Easily extendable to include additional objects or improve physics accuracy.

Core Components:
•	AstronomicObject: Base class representing all celestial bodies.
•	Planet, Moon, Star, and Comet: Specialized objects inheriting from AstronomicObject.
•	SimEngine: Handles gravitational calculations and time-based updates.
•	ToplevelGUI: Graphical user interface allowing user interaction and real-time visualization.
•	AstronomySimulator: Manages object creation and orchestrates the simulation loop.

This project is ideal for those interested in learning about simulations, physics engines, and graphical Java applications.
