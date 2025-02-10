package com.example.solarSystemSim;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;

public class ToplevelGUI {
	private static final int INTERVAL = 100; // in ms between timer events
	private boolean debug;
	private Display display;
	private Shell shell;
	private Canvas myCanvas;
	private Point myOrigin;
	private Point myCenter;
	private String timeString;
	private Label labelSimTime;
	private SimEngine simEngine;
	private Slider sliderSpeed;
	private Label labelSpeed;
	private Runnable updateRunnable;
	private MenuItem visibilityMenuItem;

	public ToplevelGUI(Display display, Shell shell, SimEngine simEngine, boolean debug) {
		super();
		this.display = display;
		this.shell = shell;
		this.simEngine = simEngine; // handle to our simulator
		this.debug = debug;
		timeString = ""; // will display the simulated time later
	}

	public Display getDisplay() {
		return display;
	}

	public Shell getShell() {
		return shell;
	}
	private void constructMenu() { // MENU widget
		Menu myBar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(myBar); // attach menu to GUI
		// "File"
		MenuItem fileMenuItem = new MenuItem(myBar, SWT.CASCADE);
		fileMenuItem.setText("&File");
		Menu subMenuItem = new Menu(shell, SWT.DROP_DOWN);
		fileMenuItem.setMenu(subMenuItem);
		// "New"
		MenuItem selectMenuItem = new MenuItem(subMenuItem, SWT.NULL);
		selectMenuItem.setText("&New\tCtrl+N");
		selectMenuItem.setAccelerator(SWT.CTRL + 'N');
		selectMenuItem.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						System.err.println("Menu item New selected. Sorry. Not implemented.\n"+event);
					}
				});
		// "Open"
		MenuItem openMenuItem = new MenuItem(subMenuItem, SWT.NULL);
		openMenuItem.setText("&Open\tCtrl+O");
		openMenuItem.setAccelerator(SWT.CTRL + 'O');
		openMenuItem.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						System.err.println("Menu item Open selected. Sorry. Not implemented.\n"+event);
					}
				});
		// "Save"
		MenuItem saveMenuItem = new MenuItem(subMenuItem, SWT.NULL);
		saveMenuItem.setText("&Save\tCtrl+S");
		saveMenuItem.setAccelerator(SWT.CTRL + 'S');
		saveMenuItem.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						System.err.println("Menu item Save selected. Sorry. Not implemented.\n"+event);
					}
				});
		// "SaveAs"
		MenuItem saveAsMenuItem = new MenuItem(subMenuItem, SWT.NULL);
		saveAsMenuItem.setText("Save &as");
		saveAsMenuItem.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						System.err.println("Menu item SaveAs selected. Sorry. Not implemented.\n"+event);
					}
				});
		@SuppressWarnings("unused") // thisIsSeperator is not used anymore later
		MenuItem thisIsSeperator = new MenuItem(subMenuItem, SWT.SEPARATOR);
		// "Exit"
		MenuItem exitMenuItem = new MenuItem(subMenuItem, SWT.NULL);
		exitMenuItem.setText("E&xit\tCtrl+X");
		exitMenuItem.setAccelerator(SWT.CTRL + 'X');
		exitMenuItem.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event) {
				shell.dispose();
			}
		});
		// "Simulate"
		MenuItem simMenuItem = new MenuItem(myBar, SWT.CASCADE);
		simMenuItem.setText("&Simulate");
		Menu simSubMenuItem = new Menu(shell, SWT.DROP_DOWN);
		simMenuItem.setMenu(simSubMenuItem);
		// "Begin"
		MenuItem startMenuItem = new MenuItem(simSubMenuItem, SWT.NULL);
		startMenuItem.setText("&Begin\tCtrl+B");
		startMenuItem.setAccelerator(SWT.CTRL + 'B');
		startMenuItem.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						System.err.println("Menu item Begin selected. Sorry. Not implemented.\n"+event);
					}
				});
		MenuItem pauseMenuItem = new MenuItem(simSubMenuItem, SWT.NULL);
		pauseMenuItem.setText("&Pause\tCtrl+Z");
		pauseMenuItem.setAccelerator(SWT.CTRL + 'Z');
		pauseMenuItem.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						System.err.println("Menu item Pause selected. Sorry. Not implemented.\n"+event);
					}
				});
		// "End"
		MenuItem stopMenuItem = new MenuItem(simSubMenuItem, SWT.NULL);
		stopMenuItem.setText("&End\tCtrl+E");
		stopMenuItem.setAccelerator(SWT.CTRL + 'E');
		stopMenuItem.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						System.err.println("Menu item End selected. Sorry. Not implemented.\n"+event);
					}
				});
		
	    // Visibility menu
	    visibilityMenuItem = new MenuItem(myBar, SWT.CASCADE);
	    visibilityMenuItem.setText("Visibility");
		
	    recreateVisibilityMenu(visibilityMenuItem);

/*		// "Visibility"
		 MenuItem visibilityMenuItem = new MenuItem(myBar, SWT.CASCADE);
		    visibilityMenuItem.setText("Visibility");
		    
		    // Create a dropdown menu for toggling visibility
		    Menu visibilitySubMenu = new Menu(shell, SWT.DROP_DOWN);
		    visibilityMenuItem.setMenu(visibilitySubMenu);

		    for (AstronomicObject obj : simEngine.myAstronomicObjects) {
		        MenuItem objectMenuItem = new MenuItem(visibilitySubMenu, SWT.CHECK);
		        objectMenuItem.setText(obj.getName());
		        objectMenuItem.setSelection(obj.isVisible()); // Reflect current visibility state
		        objectMenuItem.addSelectionListener(new SelectionAdapter() {
		            @Override
		            public void widgetSelected(SelectionEvent e) {
		                obj.setVisible(!obj.isVisible()); // Toggle visibility
		                simEngine.updateVisibility();    // Update the canvas and scaling
		            }
		        });
		    }
		    */
		
		    
			// "Help"
			MenuItem helpMenuItem = new MenuItem(myBar, SWT.NULL);
			helpMenuItem.setText("&Help");
			helpMenuItem.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent event) {
					System.err.println("Menu item Help selected. Sorry. Not implemented.\n"+event);
				}
			});
		
	} // MENU widget
	
	private void recreateVisibilityMenu(MenuItem visibilityMenuItem) {
	    // Remove old items
	    Menu visibilitySubMenu = visibilityMenuItem.getMenu();
	    if (visibilitySubMenu != null) {
	        for (MenuItem item : visibilitySubMenu.getItems()) {
	            item.dispose();
	        }
	    } else {
	        visibilitySubMenu = new Menu(shell, SWT.DROP_DOWN);
	        visibilityMenuItem.setMenu(visibilitySubMenu);
	    }

	    // Add new items for the current astronomic objects
	    for (AstronomicObject obj : simEngine.myAstronomicObjects) {
	        MenuItem objectMenuItem = new MenuItem(visibilitySubMenu, SWT.CHECK);
	        objectMenuItem.setText(obj.getName());
	        objectMenuItem.setSelection(obj.isVisible()); // Reflect current visibility state
	        objectMenuItem.addSelectionListener(new SelectionAdapter() {
	            @Override
	            public void widgetSelected(SelectionEvent e) {
	                obj.setVisible(!obj.isVisible()); // Toggle visibility
	                simEngine.updateVisibility();    // Update the canvas and scaling
	            }
	        });
	    }
	}

	
	public void constructLeftBar() {
	    Composite composite = new Composite(shell, SWT.BORDER);
	    GridData gridData = new GridData(GridData.BEGINNING, GridData.FILL, false, false);
	    gridData.horizontalSpan = 1;
	    composite.setLayoutData(gridData);

	    // Start Button
	    final Button buttonStart = new Button(composite, SWT.PUSH);
	    buttonStart.setBounds(0, 0, 80, 30);
	    buttonStart.setText("Start");
	    buttonStart.setToolTipText("Click me to start a new simulation at t=0");

	    // Pause Button
	    final Button buttonPause = new Button(composite, SWT.PUSH);
	    buttonPause.setBounds(0, 30, 80, 30);
	    buttonPause.setText("Pause");
	    buttonPause.setToolTipText("Click me to interrupt/pause and later resume");

	    // Stop Button
	    final Button buttonStop = new Button(composite, SWT.PUSH);
	    buttonStop.setBounds(0, 60, 80, 30);
	    buttonStop.setText("Stop");
	    buttonStop.setToolTipText("Click me to end the simulation and cleanup");

	    // Add listeners
	    buttonStart.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent event) {
	            simEngine.resetSimulation();

	            simEngine = new SimEngine(debug, display, shell);
	            simEngine.setCanvas(myCanvas);

	            labelSimTime.setText("Time: 0d:00h");
	            sliderSpeed.setSelection(50);
	            labelSpeed.setText("Speed: 24h/s");
	            buttonPause.setText("Pause");

	            if (updateRunnable != null) {
	                display.timerExec(-1, updateRunnable);
	            }
	            display.timerExec(INTERVAL, updateRunnable = new Runnable() {
	                @Override
	                public void run() {
	                    if (myCanvas.isDisposed()) return;
	                    simEngine.update();
	                    GC gc = new GC(myCanvas);
	                    redrawSpace(gc);
	                    gc.dispose();
	                    display.timerExec(INTERVAL, this);
	                }
	            });

	            // Update visibility menu
	            recreateVisibilityMenu(visibilityMenuItem);

	            System.out.println("Simulation restarted.");
	        }
	    });



	    buttonPause.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent event) {
	            if (simEngine.isPaused()) {
	                // Resume the simulation
	                simEngine.setPaused(false);
	                buttonPause.setText("Pause"); // Update button text

	                // Restart periodic updates
	                if (updateRunnable != null) {
	                    display.timerExec(INTERVAL, updateRunnable = new Runnable() {
	                        @Override
	                        public void run() {
	                            if (myCanvas.isDisposed()) return;
	                            simEngine.update();
	                            GC gc = new GC(myCanvas);
	                            redrawSpace(gc);
	                            gc.dispose();
	                            display.timerExec(INTERVAL, this);
	                        }
	                    });
	                }

	                System.out.println("Simulation resumed.");
	            } else {
	                // Pause the simulation
	                simEngine.setPaused(true);
	                buttonPause.setText("Resume"); // Update button text

	                // Cancel the periodic updates
	                if (updateRunnable != null) {
	                    display.timerExec(-1, updateRunnable); // Stop updates
	                }

	                System.out.println("Simulation paused.");
	            }
	        }
	    });

	    
	    buttonStop.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent event) {
	            simEngine.stopSimulation(); // Clear simulation objects
	            labelSimTime.setText("Time: 0d:00h");
	            sliderSpeed.setSelection(50);
	            labelSpeed.setText("Speed: 24h/s");
	            buttonPause.setText("Pause");

	            if (updateRunnable != null) {
	                display.timerExec(-1, updateRunnable);
	                updateRunnable = null;
	            }

	            if (myCanvas != null && !myCanvas.isDisposed()) {
	                GC gc = new GC(myCanvas);
	                Rectangle canvasRect = myCanvas.getClientArea();
	                gc.fillRectangle(canvasRect);
	                gc.dispose();
	            }

	            // Update visibility menu
	            recreateVisibilityMenu(visibilityMenuItem);

	            System.out.println("Simulation stopped.");
	        }
	    });




	    // Label for simulation time
	    labelSimTime = new Label(composite, SWT.CENTER);
	    labelSimTime.setBounds(0, 150, 80, 30);
	    labelSimTime.setText("Time");
	    labelSimTime.setToolTipText("This is the current simulated time");
	    labelSpeed = new Label(composite, SWT.CENTER);
		labelSpeed.setBounds(0, 120, 80, 30);
		labelSpeed.setText("Speed: 24h/s");
		labelSpeed.setToolTipText("This is the rate at which the simulated time progresses per second");
		sliderSpeed = new Slider(composite, SWT.VERTICAL);
		sliderSpeed.setValues(0, 0, 110, 10, 1, 10);
		Rectangle clientArea = composite.getClientArea ();
		//sliderSpeed.setBounds (0, 150, 80, clientArea.y-150);
		sliderSpeed.setBounds(0, 150, 80, 400);
		sliderSpeed.setSelection(50);
		//sliderSpeed.addSelectionListener(listener);
		sliderSpeed.addListener (SWT.Selection, new Listener () {
			@Override
			public void handleEvent (Event event) {
				String string = "SWT.NONE";
				switch (event.detail) {
					case SWT.DRAG: string = "SWT.DRAG"; break;
					case SWT.HOME: string = "SWT.HOME"; break;
					case SWT.END: string = "SWT.END"; break;
					case SWT.ARROW_DOWN: string = "SWT.ARROW_DOWN"; break;
					case SWT.ARROW_UP: string = "SWT.ARROW_UP"; break;
					case SWT.PAGE_DOWN: string = "SWT.PAGE_DOWN"; break;
					case SWT.PAGE_UP: string = "SWT.PAGE_UP"; break;
				}
				if (debug) System.out.println ("Scroll detail -> " + string);
				int sliderValue = sliderSpeed.getSelection(); // top=0, bottom=100
				// translate int to double: (linear to exponential)
				// 0 -> 1/F*24*60*60
				// 50 -> 24*60*60
				// 100 -> F*24*60*60
				final double F = 10;
				double exponent = -(sliderValue-50.0)/50.0; // -1 .. 1
				double simTimeFactor = 24.0*60.0*60.0*Math.pow(10, exponent);
				if (debug) System.out.println ("sliderSpeed value = "+sliderValue+" => simTimeFactor="+(simTimeFactor/60.0/60.0)+"h/s");
				labelSpeed.setText(String.format("Speed: %.1fh/s",simTimeFactor/60.0/60.0));
				simEngine.setSimTimeFactor(simTimeFactor);
			}
		});
	    
	}
	@SuppressWarnings("unused")
	public void constructCanvas() {
		final Point origin = new Point (0, 0);
		myOrigin = origin;  // set member;
		//final Canvas canvas = new Canvas (shell, SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.V_SCROLL | SWT.H_SCROLL);
		final Canvas canvas = new Canvas (shell, SWT.NO_BACKGROUND | SWT.V_SCROLL | SWT.H_SCROLL);
		myCanvas=canvas; // set member;
		simEngine.setCanvas(canvas); // needed there to draw things
		Rectangle canvasRect = myCanvas.getClientArea();
		int sizeX = canvasRect.width;
		int sizeY = canvasRect.height;
		myCenter = new Point (sizeX/2, sizeY/2);;
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = 1;
		canvas.setLayoutData(gridData);
		canvas.setBackground(display.getSystemColor (SWT.COLOR_BLACK));
		if (true) canvas.addListener (SWT.Resize,  new Listener () {
			@Override
			public void handleEvent (Event e) {
				simEngine.updateCanvasSize();
			}
		});
		if (true) canvas.addListener (SWT.Paint, new Listener () {
			@Override
			public void handleEvent (Event e) {
				// if (debug) System.out.println("SWT.Paint Event: "+e); // happens often
				GC gc = e.gc;
				redrawSpace(gc);
			}
		});
		// periodic updates (every 100ms). Extra thread:
		display.timerExec(INTERVAL, updateRunnable = new Runnable() {
		    @Override
		    public void run() {
		        if (canvas.isDisposed()) return;
		        simEngine.update();
		        GC gc = new GC(canvas);
		        redrawSpace(gc);
		        gc.dispose();
		        display.timerExec(INTERVAL, this); // Schedule next update
		    }
		});

	} // constructCanvas()
	public void redrawSpace(GC gc) {
		Rectangle canvasRect = myCanvas.getClientArea();
		int sizeX = canvasRect.width;
		int sizeY = canvasRect.height;
		gc.fillRectangle (0, 0, sizeX, sizeY);
		myCenter = new Point (sizeX/2, sizeY/2);;
		timeString = String.valueOf (simEngine.getSimTimeFormattedString());
		//gc.drawString (timeString, 10, 10, true); // current simulated time
		labelSimTime.setText(timeString);
		//simEngine.update(); // time leap
		simEngine.drawAll(gc); // graphics
	}
	public void constructMainArea() {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		//RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		//layout.wrap = true; layout.fill = false;layout.justify = true;
		shell.setLayout(layout);
		constructLeftBar();
	    constructCanvas();
	}
	public void enlargeGUI() {
		Monitor primary = display.getPrimaryMonitor ();
		Rectangle bounds = primary.getBounds ();
		int borderFree = 200;
		int sizeX = bounds.width - borderFree;
		int sizeY = bounds.height - borderFree;
		if (debug) System.out.println("size(X,Y)=("+sizeX+","+sizeY+")");
		shell.setSize(sizeX,sizeY);
	}
	public void centerGUI() {
		Monitor primary = display.getPrimaryMonitor ();
		Rectangle bounds = primary.getBounds ();
		Rectangle rect = shell.getBounds ();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation (x, y);
	}
	public void constructGUI() {
		shell.setText("Solar System Simulator"); // window title
		shell.setSize(700,600); // default size in (X,Y)
	    //shell.setBounds(100, 100, 600, 600); // absolute position
	    shell.setLayout(new FillLayout());
	    constructMenu();
	    constructMainArea();
		//Rectangle rect = myCanvas.getClientArea();
		//myCanvas.drawOval(0, 0, rect.width - 1, rect.height - 1);
	} // constructGUI()
} // class ToplevelGUI
