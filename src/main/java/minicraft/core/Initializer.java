package minicraft.core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.tinylog.Logger;

import de.jcm.discordgamesdk.Core;
import minicraft.core.io.FileHandler;

/*
 * Make the game window and ticks counter
 */
public class Initializer extends Game {
	private Initializer() {}

	/**
	 * Reference to actual frame, also it may be null.
	 */
	static JFrame frame;
	static LogoSplashCanvas logoSplash = new LogoSplashCanvas();
	static int fra, tik; // These store the number of frames and ticks in the previous second, used for fps, at least.

	public static int getCurFps() {
		return fra;
	}

	static void parseArgs(String[] args) {
		boolean debug = false;
		boolean dev = false;

		// parses command line arguments
		String saveDir = FileHandler.getSystemGameDir();
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--debug")) {
				debug = true;
			} else if (args[i].equals("--dev")) {
				dev = true;
			} else if (args[i].equals("--savedir") && i + 1 < args.length) {
				i++;
				saveDir = args[i];
			} else if (args[i].equals("--fullscreen")) {
				// Initializes fullscreen
				Updater.FULLSCREEN = true;
			}
		}

		Game.debug = debug;
		Game.dev = dev;

		FileHandler.determineGameDir(saveDir);
	}

	/**
	 * This is the main loop that runs the game. It: -keeps track of the amount of
	 * time that has passed -fires the ticks needed to run the game -fires the
	 * command to render out the screen.
	 */
	static void run(Core discordCore) {
	    long lastTick = System.nanoTime();
	    long lastRender = System.nanoTime();
	    double unprocessed = 0;
	    int frames = 0;
	    int ticks = 0;
	    long lastTimer1 = System.currentTimeMillis();

	    // calculate nanoseconds per tick
	    double nsPerTick = 1E9D / Updater.normSpeed;
	    if (display == null) {
	        nsPerTick /= Updater.gamespeed;
	    }

	    // main loop
	    while (running) {
	        long now = System.nanoTime();
	        unprocessed += (now - lastTick) / nsPerTick;
	        lastTick = now;

	        // update game state
	        while (unprocessed >= 1) {
	            ticks++;
	            Updater.tick();
	            unprocessed--;
	        }
	        
		/*try {
			Thread.sleep(2); // Makes a small pause for 2 milliseconds
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/

		if ((now - lastRender) / 1.0E9 > 1.0 / MAX_FPS) {
			frames++;
			lastRender = System.nanoTime();
			Renderer.render();
		}

		if (discordCore != null) {
			discordCore.runCallbacks();
		}

		if (System.currentTimeMillis() - lastTimer1 > 1000) { // updates every 1 second
			lastTimer1 += 1000; // adds a second to the timer

			fra = frames; // saves total frames in last second
			tik = ticks; // saves total ticks in last second
			frames = 0; // resets frames
			ticks = 0; // resets ticks; ie, frames and ticks only are per second
		}
	    }
	}

	/// Creates and displays the JFrame window that the game appears in.
	static void createAndDisplayFrame() {
		Renderer.canvas.setMinimumSize(new java.awt.Dimension(1, 1));
		Renderer.canvas.setPreferredSize(Renderer.getWindowSize());
		Renderer.canvas.setBackground(Color.BLACK);
		logoSplash.setMinimumSize(new java.awt.Dimension(1, 1));
		logoSplash.setPreferredSize(Renderer.getWindowSize());
		logoSplash.setBackground(Color.BLACK);
		JFrame frame = Initializer.frame = new JFrame(NAME);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout()); // sets the layout of the window
		frame.add(logoSplash, BorderLayout.CENTER);

		frame.pack(); // squishes everything into the preferredSize.

		try { // Loads the icon for the window.
			BufferedImage logo = ImageIO.read(Game.class.getResourceAsStream("/resources/logo.png"));
			frame.setIconImage(logo);
		} catch (IOException e) {
			e.printStackTrace();
		}

		frame.setLocationRelativeTo(null); // the window will pop up in the middle of the screen when launched.

		frame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				float w = frame.getWidth() - frame.getInsets().left - frame.getInsets().right;
				float h = frame.getHeight() - frame.getInsets().top - frame.getInsets().bottom;
				Renderer.SCALE = Math.min(w / Renderer.WIDTH, h / Renderer.HEIGHT);
			}
		});

		frame.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowOpened(WindowEvent e) {}
			
			public void windowClosed(WindowEvent e) {
				Logger.debug("Window closed");
			}

			public void windowClosing(WindowEvent e) {
				Logger.info("Window closing ...");
				quit();
			}
		});

		frame.setVisible(true);
		logoSplash.setDisplay(true);
		logoSplash.renderer.start();
	}

	@SuppressWarnings("serial")
	private static class LogoSplashCanvas extends JPanel {
		private Image logo;

		{
			try {
				logo = ImageIO.read(Objects.requireNonNull(Initializer.class.getResourceAsStream("/resources/title.png")));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private int transparency = 255;
		private boolean display = false;
		private boolean inAnimation = false;
		private boolean interruptWhenAnimated = false;

		public Thread renderer = new Thread(() -> {
			do {
				repaint();
				if (interruptWhenAnimated && !inAnimation) break;
			} while (!Initializer.logoSplash.renderer.isInterrupted());
		}, "Logo Splash Screen Renderer");

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			setBackground(Color.BLACK);  
			if (transparency < 255) g.drawImage(logo, getWidth()/2 - logo.getWidth(frame), getHeight()/2 - logo.getHeight(frame)*2, logo.getWidth(frame)*2, logo.getHeight(frame)*2, frame);
			if (transparency > 0) {
				g.setColor(new Color(0, 0, 0, transparency));
				g.fillRect(0, 0, g.getClipBounds().width, g.getClipBounds().height);
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
		final java.io.ByteArrayOutputStream bytestream = new java.io.ByteArrayOutputStream();
		final java.io.PrintStream printStream = new java.io.PrintStream(bytestream);
		throwable.printStackTrace(printStream);
		String exceptionStr;
		try {
			exceptionStr = bytestream.toString("UTF-8");
		} catch(Exception ex) {
			exceptionStr = "Unavailable";
		}
		return exceptionStr;
	}
}
