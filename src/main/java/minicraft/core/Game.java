package minicraft.core;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
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
import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

//--------------------------------------------------------------------------------------------------------------------------------------------------

/*
 *This is the main class, where is all the important variables and
 * functions that make up the game at the beginning of the game.
 */

public class Game {

	protected Game() {} // Can't instantiate the Game class.

	private static Random random = new Random();

	public static boolean debug = false; // --debug arg
	public static boolean dev = false; // --dev arg

	public static boolean in_dev = false; // development version?

	// Game events (Shhhh is seeeecret)
	public static boolean IS_Christmas = false;
	public static boolean IS_Halloween = false;
	public static boolean IS_April_fools = false;

	public static final String NAME = "Aircraft"; // This is the name on the application window
	public static final String BUILD = "0.5"; // Aircraft version
	public static final Version VERSION = new Version("2.1.0-dev3"); // Minicraft plus mod base version

	public static InputHandler input; // Input used in Game, Player, and just about all the *Menu classes*.
	public static Player player;

	public static List<String> notifications = new ArrayList<>();

	public static int MAX_FPS = (int) Settings.get("fps");
	public static Level level;

	// Crash splashes
	private static final String[] Splash = {
			"Who has put TNT?",
			"An error has occurred again??",
			"A nice cup of coffee?",
			"Unexpected error again??",
			"Oh. That hurts :(",
			"Sorry for the crash :(",
			"You can play our brother game, Minitale",
			"F, crash again??", "Interesting, hmmmmm...",
			"ok, i messed it up"
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
		Sound.Menu_back.play();
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

	// Quit function
	public static void quit() {
		running = false;
	}

	// Main functions
	public static void main(String[] args) {

		// Crash window log ------------------------------------------------------------------------------------------------------------------------------

		// Load the splashes
		String errorSplash = Splash[random.nextInt(Splash.length)];

		// Load the time vars
		LocalDateTime time = LocalDateTime.now();

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
				" If the problem persists, send a screenshot to the author.\n" + "\n" +

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

			// Log it
			Logger.error(crash.toString());
		});

		// Start events ------------------------------------------------------------------------------------------------------------------------------------

		Initializer.parseArgs(args);

		// Initialize game events

		// Christmas
		if ((time.getMonth() == Month.DECEMBER) && (time.getDayOfMonth() == 24)) {
			IS_Christmas = true;
		} else {
			IS_Christmas = false;
		}

		// Halloween
		if ((time.getMonth() == Month.OCTOBER) && (time.getDayOfMonth() == 31)) {
			IS_Halloween = true;
		} else {
			IS_Halloween = false;
		}

		// April Fools
		if ((time.getMonth() == Month.APRIL) && (time.getDayOfMonth() == 1)) {
			IS_April_fools = true;
		} else {
			IS_April_fools = false;
		}

		// Initialize input handler
		input = new InputHandler(Renderer.canvas);

		// Load events
		Tiles.initTileList();
		Sound.init();
		Settings.init();

		World.resetGame(); // "half"-starts a new game, to set up initial variables
		player.eid = 0;
		new Load(true); // This loads any saved preferences.

		MAX_FPS = (int) Settings.get("fps"); // Load FPS

		// Window events ----------------------------------------------------------------------------------------------------------------------------------

		// Create a game window
		Initializer.createAndDisplayFrame();

		// Display objects in the screen
		Renderer.initScreen();

		// Sets menu to the title screen.
		setDisplay(new TitleDisplay());

		// Update fullscreen frame if Updater.FULLSCREEN was updated previously
		if (Updater.FULLSCREEN) {
			Updater.updateFullscreen();
		}

		// Start tick() count
		Initializer.run();

		// Exit events -------------------------------------------------------------------------------------------------------------------------------------

		if (debug) {
			System.out.println("Main game loop ended; Terminating application...");
		}

		System.exit(0);
	}

}