package com.example.solarSystemSim;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;

public class AstronomySimulator {
	static Display display;
	static Shell shell;
	static SimEngine simEngine;
	static ToplevelGUI toplevel;
	static boolean debug=true; // change to false for production code, after debugging
	/* main function */
	public static void main(String[] args) {
		display  = new Display(); // SWT
		shell    = new Shell();   // SWT
		simEngine= new SimEngine(debug, display, shell); // our class
		toplevel = new ToplevelGUI(display, shell, simEngine, debug); // HAW
		toplevel.constructGUI(); // outsourced into separate class
		shell.open(); // display the GUI
		if (!debug) toplevel.enlargeGUI();
		if (!debug) toplevel.centerGUI();
		// event loop:
		while (!shell.isDisposed()) // endless loop as long as window is alive
			if (display.readAndDispatch()) // event handling
				display.sleep(); // do nothing. All done in readAndDispatch() above
		display.dispose(); // at the end: cleanup
	}
}