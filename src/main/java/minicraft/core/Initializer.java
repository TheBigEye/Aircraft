package minicraft.core;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.GameSDKException;
import de.jcm.discordgamesdk.activity.Activity;
import minicraft.core.io.FileHandler;
import org.tinylog.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Objects;

/*
 * Make the game window and ticks counter
 */
public class Initializer extends Game {
	private Initializer() {}

	/**
	 * Reference to actual frame, also it may be null.
	 */
	public static JFrame frame;
	static LogoSplashCanvas logoSplash = new LogoSplashCanvas();

	// These store the number of frames and ticks in the previous second, used for fps, at least.
	static int fra;
	static int tik;

	public static int getCurrentFPS() {
		return fra;
	}

	static void parseArgs(String[] args) {
		boolean debug = false;
		// Parses command line arguments
		String saveDir = FileHandler.getSystemGameDir();
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--debug")) {
				debug = true;
			} else if (args[i].equals("--savedir") && i + 1 < args.length) {
				i++;
				saveDir = args[i];
			} else if (args[i].equals("--fullscreen")) {
				Updater.FULLSCREEN = true;
			}
		}

		Game.debug = debug;

		FileHandler.determineGameDir(saveDir);
	}

	/**
	 * This is the main loop that runs the game. It:
	 * - keeps track of the amount of time that has passed
	 * - fires the ticks needed to run the game
	 * - fires the command to render out the screen.
	 * - update the discord rpc
	 */
	static void run() {

        // Discord rich presence
        Core discordCore = null;

		try {
			if (debug) Logger.debug("Initializing discord RPC ...");

			final long CLIENT_ID = 981764521616093214L;
			final String LARGE_TEXT = "Aircraft " + BUILD + ", Nice!";
			final String SMALL_TEXT = "Minicraft+ mod";

			Core.initDownload();
			CreateParams params = new CreateParams();
			params.setClientID(CLIENT_ID);
			params.setFlags(CreateParams.getDefaultFlags());
            params.setFlags(CreateParams.Flags.NO_REQUIRE_DISCORD);
			discordCore = new Core(params);

			Activity activity = new Activity();
			activity.assets().setLargeImage("logo");
            activity.assets().setLargeText(LARGE_TEXT);
            activity.assets().setSmallImage("small-logo");
            activity.assets().setSmallText(SMALL_TEXT);
			activity.timestamps().setStart(Instant.now());

			discordCore.activityManager().updateActivity(activity);

		} catch (GameSDKException exception) {
			Logger.warn("Failed to initialize Discord SDK, no discord running!");
			exception.printStackTrace();

		} catch (UnknownHostException exception) {
			Logger.warn("Failed to download Discord SDK, no internet connection!");
			exception.printStackTrace();

		} catch (Exception exception) {
			Logger.error("Unknown error");
			exception.printStackTrace();
		}

	    long lastTick = System.nanoTime();
	    long lastRender = System.nanoTime();
	    double unprocessed = 0; // delta?
	    int frames = 0;
	    int ticks = 0;
	    long lastTimer = System.currentTimeMillis();

	    // Game main loop
	    while (running) {
	    	long now = System.nanoTime();
	    	
		    // Calculate nanoseconds per tick (updates)
		    double nsPerTick = 1E9D / Updater.normalSpeed;
		    if (display == null) {
		        nsPerTick /= Updater.gameSpeed;
		    }

	        unprocessed += (now - lastTick) / nsPerTick;
	        lastTick = now;

	        // Updates the game state
	        while (unprocessed >= 1) {
	            ticks++;
	            Updater.tick();
	            
				if (discordCore != null) {
					discordCore.runCallbacks();
				}
				
	            unprocessed--;
	        }

	        // Refresh the screen
	        now = System.nanoTime();
			if (now >= lastRender + 1E9D / maxFPS / 1.01) {
				frames++;
				lastRender = now;
				Renderer.render();
			}

			try {
				long curNano = System.nanoTime();
				long untilNextTick = (long) (lastTick + nsPerTick - curNano);
				long untilNextFrame = (long) (lastRender + 1E9D / maxFPS - curNano);
				if (untilNextTick > 1E3 && untilNextFrame > 1E3) {
					double timeToWait = Math.min(untilNextTick, untilNextFrame) / 1.2; // in nanosecond
					//noinspection BusyWait
					Thread.sleep((long) Math.floor(timeToWait / 1E6), (int) ((timeToWait - Math.floor(timeToWait)) % 1E6));
				}
			} catch (InterruptedException ignored) {}


			if (System.currentTimeMillis() - lastTimer > 1000) { // updates every 1 second
				long interval = System.currentTimeMillis() - lastTimer;
				lastTimer = System.currentTimeMillis(); // adds a second to the timer

				fra = (int) Math.round(frames * 1000D / interval); // saves total frames in last second
				tik = (int) Math.round(ticks * 1000D / interval); // saves total ticks in last second
				frames = 0; // resets frames
				ticks = 0; // resets ticks; ie, frames and ticks only are per second
			}
	    }

	    // Finalize discord rich presence
	    //discordCore.activityManager().clearActivity(Core.DEFAULT_CALLBACK);
	}


	// Creates and displays the JFrame window that the game appears in.
	static java.awt.Dimension dimension;
	static void createAndDisplayFrame() {
		dimension = Renderer.getWindowSize();
	    Renderer.canvas.setMinimumSize(new java.awt.Dimension(1, 1));
	    Renderer.canvas.setPreferredSize(dimension);
	    Renderer.canvas.setBackground(Color.BLACK);
	    logoSplash.setMinimumSize(new java.awt.Dimension(1, 1));
	    logoSplash.setPreferredSize(dimension);
	    logoSplash.setBackground(Color.BLACK);

	    JFrame frame = Initializer.frame = new JFrame(NAME);
	    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    frame.setLayout(new BorderLayout());
	    frame.add(logoSplash, BorderLayout.CENTER);
	    frame.pack();

		try { // Load the window logo
			BufferedImage logo = ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/resources/logo.png")));
			frame.setIconImage(logo);
		} catch (IOException exception) {
			exception.printStackTrace();
		}

		frame.setLocationRelativeTo(null); // The window will pop up in the middle of the screen when launched.

		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				float w = frame.getWidth() - frame.getInsets().left - frame.getInsets().right;
				float h = frame.getHeight() - frame.getInsets().top - frame.getInsets().bottom;
				Renderer.SCALE = Math.min(w / Renderer.WIDTH, h / Renderer.HEIGHT);
			}
		});

	    frame.addWindowListener(windowListener);
	    frame.setBackground(Color.BLACK);
	    frame.setVisible(true);
	    logoSplash.setDisplay(true);
	    logoSplash.renderer.start();
	}


	private static final WindowListener windowListener = new WindowListener() {
	    public void windowActivated(WindowEvent e) {} // not used
	    public void windowDeactivated(WindowEvent e) {} // not used
	    public void windowIconified(WindowEvent e) {} // not used
	    public void windowDeiconified(WindowEvent e) {} // not used
	    public void windowOpened(WindowEvent e) {} // not used

	    public void windowClosed(WindowEvent e) {
	        Logger.debug("Game window closed!");
	    }

	    public void windowClosing(WindowEvent e) {
	        quit();
	    }
	};


	private static class LogoSplashCanvas extends JPanel {
		private static final Image LOGO;
		static {
			try {
				LOGO = ImageIO.read(Objects.requireNonNull(Initializer.class.getResourceAsStream("/resources/title.png")));
			} catch (IOException exception) {
				throw new RuntimeException(exception);
			}
		}

		private static final Color[] COLORS = new Color[256];
		static {
			for (int i = 0; i < COLORS.length; i++) {
				COLORS[i] = new Color(0, 0, 0, i);
			}
		}

		private int transparency = 255;
		private boolean display = false;
		private boolean inAnimation = false;
		private boolean interruptWhenAnimated = false;

		public LogoSplashCanvas() {
			setBackground(Color.BLACK);
		}

		public final Thread renderer = new Thread(() -> {
			do {
				repaint();
				if (interruptWhenAnimated && !inAnimation) break;
			} while (!Initializer.logoSplash.renderer.isInterrupted());
		}, "Splash Renderer Thread");

		@Override
		public void paintComponent(Graphics graphics) {
			super.paintComponent(graphics);

			int lx = (getWidth() / 2) - LOGO.getWidth(frame);
			int ly = (getHeight() / 2) - (LOGO.getHeight(frame) + 12);
			int lw = LOGO.getWidth(frame) << 1;
			int lh = LOGO.getHeight(frame) << 1;

			if (transparency < 255) {
				graphics.drawImage(LOGO, lx, ly, lw, lh, frame);
			}

			if (transparency > 0) {
				graphics.setColor(COLORS[transparency]);
				graphics.fillRect(0, 0, graphics.getClipBounds().width, graphics.getClipBounds().height);
			}

			if (inAnimation) {
				if (display) {
					if (transparency > 0) transparency -= 5;
					else inAnimation = false;
				} else {
					if (transparency < 255) transparency += 1;
					else inAnimation = false;
				}
			}
		}

		public void setDisplay(boolean display) {
			this.display = display;
			inAnimation = true;
		}
	}

	/** Remove the logo splash screen and start canvas rendering. */
	static void startCanvasRendering() {
		logoSplash.setDisplay(false);
		logoSplash.interruptWhenAnimated = true;
		try {
			logoSplash.renderer.join();
		} catch (InterruptedException ignored) {}
		logoSplash.renderer.interrupt();
		frame.remove(logoSplash);
		frame.add(Renderer.canvas, BorderLayout.CENTER); // Adds the game (which is a canvas) to the center of the screen.
		frame.pack();
		frame.revalidate();
		logoSplash = null; // Discard the canvas.
	}


	/**
	 * Provides a String representation of the provided Throwable's stack trace
	 * that is extracted via PrintStream.
	 *
	 * @param throwable Throwable/Exception from which stack trace is to be extracted.
	 * @return String with provided Throwable's stack trace.
	 */
	public static String getExceptionTrace(final Throwable throwable) {
		final java.io.ByteArrayOutputStream byteStream = new java.io.ByteArrayOutputStream();
		final java.io.PrintStream printStream = new java.io.PrintStream(byteStream);
		throwable.printStackTrace(printStream);

		String exceptionString;

		try {
			exceptionString = byteStream.toString("UTF-8");
		} catch(Exception exception) {
			exceptionString = "Unavailable";
		}

		return exceptionString;
	}
}
