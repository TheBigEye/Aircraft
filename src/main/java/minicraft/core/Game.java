package minicraft.core;

import java.awt.GraphicsEnvironment;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.GameSDKException;
import de.jcm.discordgamesdk.activity.Activity;
import minicraft.core.io.FileHandler;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.mob.Player;
import minicraft.level.Level;
import minicraft.level.tile.Tiles;
import minicraft.saveload.Load;
import minicraft.saveload.Version;
import minicraft.screen.Display;
import minicraft.screen.TitleDisplay;
import minicraft.util.Utils;

/*
 *This is the main class, where is all the important variables and
 * functions that make up the game at the beginning of the game.
 */
public class Game {

	protected Game() {} // Can't instantiate the Game class.

	/** Random values used for some Game class logic **/
	protected static final Random random = new Random(); // Create a Random object to generate random numbers  
	
	public static boolean debug = false; // --debug arg

	public static final String NAME = "Aircraft"; // This is the name on the application window
	public static final String BUILD = "0.5"; // Aircraft version
	
	// TODO: not use anymore Minicraft plus versioning
	public static final Version VERSION = new Version("2.2.0-dev2"); // Minicraft plus mod base version

	public static InputHandler input; // Input used in Game, Player, and just about all the *Menu classes*.
	public static Player player;

	public static List<String> notifications = new ArrayList<>();

	public static int maxFPS;
	public static Level level;

	// DISPLAY
	static Display display = null;
	static Display newDisplay = null;
	
	public static void setDisplay(@Nullable Display display) {
		newDisplay = display;
	}

	public static void exitDisplay() {
		if (display == null) {
			Logger.warn("Game tried to exit display, but no menu is open.");
			return;
		}
		Sound.Menu_back.playOnDisplay();
		newDisplay = display.getParent();
	}

	public static void toTitle() {
		setDisplay(new TitleDisplay());
	}

	@Nullable
	public static Display getDisplay() {
		return newDisplay;
	}

	// GAMEMODE
	public static boolean isMode(String mode) {
		return ((String) Settings.get("mode")).equalsIgnoreCase(mode);
	}

	// LEVEL
	public static Level[] levels = new Level[7]; // This array stores the different levels.
	public static int currentLevel = 3; // This is the level the player is on. It defaults to 3, the surface.

	// GAME
	public static String gameDir; // The directory in which all the game files are stored
	public static boolean gameOver = false; // If the player wins this is set to true.

	protected static boolean running = true;

	// Quit function.
	public static void quit() {
		running = false;
	}
	
    /**
     * Gets a random witty comment for a possible crash report
     */
    private static String getWittyComment() {
        String[] comment = new String[] { 
    		"Terra SUS",
    		"Looks like the game just couldn't handle the awesomeness you were dishing out.",
    		"Don't worry, it's not you, it's definitely the game's fault.",
    		"Looks like we hit a bug, better call the exterminator.",
    		"Well, that was unexpected. But hey, at least you're not stuck in a cave.",
    		"It's not a crash, it's a feature! (Well, it was supposed to be, at least).",
    		"I'm sorry, Dave.",
    		"Looks like the game just couldn't keep up with your epic mining skills.",
    		"The game has reached the end... of its stability.",
    		"Looks like the game has hit a roadblock... in the code",
    		"The game has crashed, we don't need a blue screen to say it"
        };
        
        // TODO: put this "The game has gone offline... just like your internet connection." when the user haven't internet :)

        try {
            return comment[(int)(System.nanoTime() % (long)comment.length)];
        } catch (Throwable exception) {
            return "Sorry i ran out of ideas :(";
        }
    }

	// Main functions
	public static void main(String[] args) {
		// Crash report log
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			throwable.printStackTrace();
		
			StringWriter crash = new StringWriter(8192);
			PrintWriter printer = new PrintWriter(crash);
			throwable.printStackTrace(printer);
			
			String crashString = (
					"" 																										+ "\n\n" +
					"         Aircraft has crashed! " 																		+ "\n" +
					"         --------------------- " 																		+ "\n\n" +
					
					"The game was stopped running because it encountered a problem.	" 										+ "\n\n" +
					
					"" + getWittyComment() 																					+ "\n\n" +
					
					"If you wish to report this, please copy this entire text and send to the developer. " 					+ "\n" +
					"Please include a description of what you did, to replicate the error. " 								+ "\n\n" +
					
			        "--------- BEGIN ERROR REPORT ---------	" 																+ "\n" +
			        "Generated " + (new SimpleDateFormat()).format(new Date()) 												+ "\n\n" +
			
			        "-- System Details -- " 																				+ "\n" +
			        "Details: " 																							+ "\n" +
			        "        Aircraft version: " + Game.BUILD + " (" + Game.VERSION + ")" 									+ "\n" +
			        "        Operting System: " + Utils.OS_NAME + " (" + Utils.OS_ARCH + ") version " + Utils.OS_VERSION 	+ "\n" +
			        "        Java Version: " + Utils.JAVA_VERSION + ", " + Utils.JAVA_VENDOR 								+ "\n" +
			        "        Java VM Version: " + Utils.JVM_NAME + " (" + Utils.JVM_INFO + "), " + Utils.JVM_VENDOR 		+ "\n" +
			        "        Memory: " + Utils.memoryInfo() 																+ "\n\n" +
			        
					"Flags: " 	                                                                                            + "\n" +
					"        isAudioOutputAvailible: " + Sound.isAudioOutputAvailable()         							+ "\n\n" +
			
			        "~~ ERROR ~~ " 																							+ "\n" +
			
			        crash.toString() 																						+ "\n" +
			
			        "--------- END ERROR REPORT --------- "
			);
		
			// If the OS not have a desktop or graphic interface
			if (GraphicsEnvironment.isHeadless()) {
				System.out.println(crashString);
				return;
			} else {
				Logger.error(crash.toString());
			}
		
			Renderer.canvas.setVisible(false);
			Initializer.frame.add(new CrashReport(crashString));
			Initializer.frame.pack();
			Initializer.frame.setVisible(true);
		});

		
		// START EVENTS

		// Clean previously downloaded native files
		FileHandler.cleanNativesFiles();
		
        // Discord rich presence
        Core discordCore = null;
		try {
			final long CLIENT_ID = 981764521616093214L;
			final String LARGE_TEXT = "Aircraft " + BUILD + ", Nice!";
			final String SMALL_TEXT = "Minicraft+ mod";
			
			Core.initDownload(); // download java-discord SDK
			CreateParams params = new CreateParams();
			params.setClientID(CLIENT_ID); // Discord APP ID
			params.setFlags(CreateParams.getDefaultFlags());
            params.setFlags(CreateParams.Flags.NO_REQUIRE_DISCORD);
			discordCore = new Core(params);

			Activity activity = new Activity();
			activity.assets().setLargeImage("logo"); // Big image
            activity.assets().setLargeText(LARGE_TEXT); // Big image text
            activity.assets().setSmallImage("small-logo"); // Small image
            activity.assets().setSmallText(SMALL_TEXT); // Small image text
			activity.timestamps().setStart(Instant.now()); // Start timer

			discordCore.activityManager().updateActivity(activity);
			if (debug) Logger.debug("Initializing discord RPC ...");
			
		} catch (GameSDKException exception) {
			Logger.error("Failed to initialize Discord SDK, no discord detected!");
			exception.printStackTrace();
			
		} catch (UnknownHostException exception) {
			Logger.error("Failed to download Discord SDK, no internet connection!");
			exception.printStackTrace();
			
		} catch (Exception exception) {
			Logger.error("Unknown error");
			exception.printStackTrace();
		}
		
		// Parses the command line arguments
		Initializer.parseArgs(args); 

		// Initialize input handler
		input = new InputHandler(Renderer.canvas);

		Settings.initialize();

		World.resetGame(); // "half"-starts a new game, to set up initial variables
		player.eid = 0;
		new Load(true); // This loads any saved preferences.
		maxFPS = (int) Settings.get("fps"); // DO NOT put this above.
		
		// WINDOW EVENTS

		// Create a game window
		Initializer.createAndDisplayFrame();
		
		// Initialize the game modules
		Sound.initialize();
		Tiles.initialize();
		
		// Display objects in the screen
		Renderer.initScreen();

		// Update fullscreen frame if Updater.FULLSCREEN was updated previously
		if (Updater.FULLSCREEN) {
			Updater.updateFullscreen();
		}
		
		// Sets menu to the title screen.
		setDisplay(new TitleDisplay());

		// Start tick() count and start the game
		Initializer.run(discordCore);

		
		// EXIT EVENTS

		Logger.debug("Main game loop ended; Terminating application...");
		
		System.exit(0);
	}
}
