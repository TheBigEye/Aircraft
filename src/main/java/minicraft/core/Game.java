package minicraft.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

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
import minicraft.util.Info;

//--------------------------------------------------------------------------------------------------------------------------------------------------

/*
 *This is the main class, where is all the important variables and
 * functions that make up the game at the beginning of the game.
 */
public class Game {

	protected Game() {} // Can't instantiate the Game class.

	private static final Random random = new Random();

	public static boolean debug = false; // --debug arg
	public static boolean dev = false; // --dev arg

	public static boolean in_dev = false; // development version?

	// TODO: remove these vars, are redoundants :d
	// Game events (Shhhh is seeeecret)
	public static boolean IS_Christmas = false;
	public static boolean IS_Halloween = false;
	public static boolean IS_April_fools = false;

	public static final String NAME = "Aircraft"; // This is the name on the application window
	public static final String BUILD = "0.5"; // Aircraft version
	public static final Version VERSION = new Version("2.2.0-dev2"); // Minicraft plus mod base version

	public static InputHandler input; // Input used in Game, Player, and just about all the *Menu classes*.
	public static Player player;

	public static List<String> notifications = new ArrayList<>();

	public static int MAX_FPS;
	public static Level level;

	// Crash splashes
	private static final String[] Splash = {
		"Who has put TNT?",
		"An error has occurred!",
		"A nice cup of coffee?",
		"Unexpected error!",
		"Oh. That hurts :(",
		"Sorry for the crash :(",
		"You can play our brother game, Minitale",
		"F, the game was crashed!",
		"Interesting, hmmmmm...",
		"Ok, i messed it up"
	};

	//--------------------------------------------------------------------------------------------------------------------------------------------------

	// DISPLAY
	static Display display = null, newDisplay = null;
	public static void setDisplay(@Nullable Display display) {
		newDisplay = display;
	}

	public static void exitDisplay() {
		if (display == null) {
			Logger.warn("Game tried to exit display, but no menu is open.");
			return;
		}
		Sound.Menu_back.playOnGui();
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
	static boolean gameOver = false; // If the player wins this is set to true.

	static boolean running = true;

	// Quit function.
	public static void quit() {
		running = false;
	}

	// Main functions
	public static void main(String[] args) {
		
		// Load the time vars
		LocalDateTime time = LocalDateTime.now();
		
        // Initialize game events
        IS_Christmas = (time.getMonth() == Month.DECEMBER) && (time.getDayOfMonth() == 24); // Christmas
        IS_Halloween = (time.getMonth() == Month.OCTOBER) && (time.getDayOfMonth() == 31); // Halloween
        IS_April_fools = (time.getMonth() == Month.APRIL) && (time.getDayOfMonth() == 1); // April Fools

		// Crash window log ------------------------------------------------------------------------------------------------------------------------------

		// Load the splashes
		String errorSplash = Splash[random.nextInt(Splash.length)];

		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			throwable.printStackTrace();

			StringWriter crash = new StringWriter();
			PrintWriter printer = new PrintWriter(crash);
			throwable.printStackTrace(printer);

			// If the OS not have a desktop or graphic interface
			if (GraphicsEnvironment.isHeadless()) {
				return;
			}

			// Crash log Structure
			JTextArea crashDisplay = new JTextArea();

			crashDisplay.setForeground(Color.BLACK);
			crashDisplay.setBackground(Color.WHITE);

			// Crash message
			crashDisplay.setText(

				" " + errorSplash + "\n" +
				" If the problem persists, send a screenshot to the developer.\n" + "\n" +

                "--- BEGIN ERROR REPORT ---------" + "\n" +
                "Generated: " + time.toLocalDate() + "\n\n" +

                "-- System Details --" + "\n" +
                "Details: " + "\n" +
                "        Aircraft Version: " + Game.BUILD + "\n" +
                "        Minicraft Plus Version: " + Game.VERSION + "\n" +
                "        Operting System: " + Info.OS_Name + " (" + Info.OS_Arch + ") " + Info.OS_Version + "\n" +
                "        Java Version: " + Info.Java_Version + ", " + Info.Java_Vendor + "\n" +
                "        Java VM Version: " + Info.JVM_Name + " (" + Info.JVM_Info + "), " + Info.JVM_Vendor + "\n" +
                "        Memory: " + Info.Memory_info + "\n\n" +

                "~~ERROR~~ " + "\n" +

                crash.toString() + "\n" +

                "--- END ERROR REPORT ---------"
			);

			// Not editable
			crashDisplay.setEditable(false);

			// Font
			crashDisplay.setFont(new Font("Consolas", Font.PLAIN, 12));

			// Create the scroll control and the window size
			JScrollPane errorPane = new JScrollPane(crashDisplay);
			errorPane.setSize(600, 400);

			// all white, is better
			UIManager.put("OptionPane.background", Color.white);
			UIManager.put("Panel.background", Color.white);

			// Display the window
			JOptionPane.showOptionDialog(null, errorPane, "Aircraft has crashed!", JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, args, thread);
			Logger.error(crash.toString());

            // Stop and close the game window
            quit();
		});

		// Start events ------------------------------------------------------------------------------------------------------------------------------------

		// Clean previously downloaded native files
		FileHandler.cleanNativesFiles();
		
        // Discord rich presence
        Core discordCore = null;
		try {
			Core.initDownload(); // download java-discord SDK
			CreateParams params = new CreateParams();
			params.setClientID(981764521616093214L); // Discord APP ID
			params.setFlags(CreateParams.getDefaultFlags());
            params.setFlags(CreateParams.Flags.NO_REQUIRE_DISCORD);
			discordCore = new Core(params);

			Activity activity = new Activity();
			activity.assets().setLargeImage("logo"); // Big image
            activity.assets().setLargeText("Aircraft " + BUILD + ", Nice!"); // Big image text
            activity.assets().setSmallImage("small-logo"); // Small image
            activity.assets().setSmallText("Minicraft+ mod"); // Small image text
			activity.timestamps().setStart(Instant.now()); // Start timer

			discordCore.activityManager().updateActivity(activity);
			Logger.debug("Initializing discord rich presence ...");
		} catch(Exception e) {
			e.printStackTrace();
			if (e instanceof UnknownHostException) Logger.error("Failed to download Discord SDK, no internet connection!");
			if (e instanceof GameSDKException) Logger.error("Failed to initialize Discord SDK, no discord detected!");
		} 

		Initializer.parseArgs(args); // Parses the command line arguments

		// Initialize input handler
		input = new InputHandler(Renderer.canvas);

		// Load events
		Tiles.initTileList();
		Sound.init();
		Settings.init();

		World.resetGame(); // "half"-starts a new game, to set up initial variables
		player.eid = 0;
		new Load(true); // This loads any saved preferences.
		MAX_FPS = (int) Settings.get("fps");  // DO NOT put this above.

		// Window events ----------------------------------------------------------------------------------------------------------------------------------

		// Create a game window
		Initializer.createAndDisplayFrame();

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

		// Exit events -------------------------------------------------------------------------------------------------------------------------------------

		Logger.debug("Main game loop ended; Terminating application...");
		
		System.exit(0);
	}
}
