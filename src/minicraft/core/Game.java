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

import org.jetbrains.annotations.Nullable;

import minicraft.core.io.InputHandler;
import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.mob.Player;
import minicraft.level.Level;
import minicraft.level.tile.Tiles;
import minicraft.network.MinicraftClient;
import minicraft.network.MinicraftProtocol;
import minicraft.network.MinicraftServer;
import minicraft.saveload.Load;
import minicraft.saveload.Version;
import minicraft.screen.Display;
import minicraft.screen.MultiplayerDisplay;
import minicraft.screen.TitleDisplay;

//--------------------------------------------------------------------------------------------------------------------------------------------------

/*
 *This is the main class, where is all the important variables and
 * functions that make up the game at the beginning of the game. 
 */

public class Game {
	Game() {} // Can't instantiate the Game class.

	private static Random random = new Random();

	public static boolean debug = false;
	public static boolean packet_debug = false;
	public static boolean HAS_GUI = true; 
	
	public static boolean in_dev = false;
	
	// Game events (Shhhh is seeeecret)
	public static boolean IS_Christmas = false; 
	public static boolean IS_Halloween = false; 
	public static boolean IS_April_fools = false; 

	public static final String NAME = "Aircraft"; // This is the name on the application window
	public static final String BUILD = "0.4"; // Aircraft version
	public static final Version VERSION = new Version("2.0.7"); // Minicraft plus mod base version

	public static InputHandler input; // Input used in Game, Player, and just about all the *Menu classes.
	public static Player player;

	// The directory in which all the game files are stored
	public static String gameDir; 
	public static List<String> notifications = new ArrayList<>();

	public static int MAX_FPS = (int) Settings.get("fps");
	public static Level level;
	
	// Crash window titles
	private static String[] Splash = {
		"Who has put TNT?",
		"An error has occurred again??",
		"Unexpected error again??",
		"Oh. That hurts :(",
		"Sorry for the crash :(",
		"You can play our brother game, Minitale",
		"F, crash again??" 
	};
	
//--------------------------------------------------------------------------------------------------------------------------------------------------

	
	/**
	 * This specifies a custom port instead of default to server-side using
	 * --port parameter if something goes wrong in setting the new port
	 * it'll use the default one {@link MinicraftProtocol#PORT}
	 */
	public static int CUSTOM_PORT = MinicraftProtocol.PORT;

	static Display menu = null, newMenu = null; // The current menu you are on.

	// Sets the current menu.
	public static void setMenu(@Nullable Display display) {
		newMenu = display;
	}

	public static void exitMenu() {

		if (menu == null) {
			if (debug) {
				System.out.println("Game.exitMenu(): No menu found, returning!");
				
			}
			return; // No action required; cannot exit from no menu
		}

		Sound.GUI_back.play();
		newMenu = menu.getParent();
	}

	public static Display getMenu() {
		return newMenu;
	}

	public static boolean isMode(String mode) {
		return ((String) Settings.get("mode")).equalsIgnoreCase(mode);
	}

	// MULTIPLAYER
	public static boolean ISONLINE = false;
	public static boolean ISHOST = false;

	public static MinicraftClient client = null;

	public static boolean isValidClient() {
		return ISONLINE && client != null;
	}

	public static boolean isConnectedClient() {
		return isValidClient() && client.isConnected();
	}

	public static MinicraftServer server = null;

	/** Checks if you are a host and the game is a server */
	public static boolean isValidServer() {
		return ISONLINE && ISHOST && server != null;
	}

	public static boolean hasConnectedClients() {
		return isValidServer() && server.hasClients();
	}

	// LEVEL
	public static Level[] levels = new Level[6]; // This array stores the different levels.
	public static int currentLevel = 3; // This is the level the player is on. It defaults to 3, the surface.

	static boolean gameOver = false; // If the player wins this is set to true.

	static boolean running = true;

	@SuppressWarnings("unused")
	private static String errorSplash; // Store the splashes n the crash screen

	// Quit function
	public static void quit() {
		if (isConnectedClient()) {
			client.endConnection();
			
		}
		if (isValidServer()) {
			server.endConnection();
			
		}
		running = false;
	}

	// Main functions
	public static void main(String[] args) {

// Crash window log ------------------------------------------------------------------------------------------------------------------------------
		
		// Load the splashes 
		Game.errorSplash = Splash[random.nextInt(Splash.length)]; 

		// Load the time vars
		LocalDateTime time = LocalDateTime.now();
	
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			throwable.printStackTrace();

			StringWriter string = new StringWriter();
			PrintWriter printer = new PrintWriter(string);
			throwable.printStackTrace(printer);
			
			if(GraphicsEnvironment.isHeadless()) return;

			// Crash log Structure
			JTextArea CrashDisplay = new JTextArea();

			CrashDisplay.setForeground(Color.BLACK);
			CrashDisplay.setBackground(Color.WHITE);

			// Crash message
			CrashDisplay.setText(
					
                " " + Game.errorSplash + "\n" +
                " If the problem persists, send a screenshot to the author.\n" + "\n" + 

                "--- BEGIN ERROR REPORT ---------" + "\n" +
                "Generated: " + time.toLocalDate() + "\n\n" +		                     
					
                "-- System Details --" + "\n" +
                "Details: " + "\n" +
                "        Aircraft mod Version: " + Game.BUILD + "\n" +
                "        Minicraft plus base Version: " + Game.VERSION + "\n" +
		        "        Operting System: " + GameInfo.OS_Name + " (" + GameInfo.OS_Arch + ") " + GameInfo.OS_Version + "\n" +
		        "        Java Version: " + GameInfo.Java_Version + ", " + GameInfo.Java_Vendor + "\n" +
		        "        Java VM Version: " + GameInfo.JVM_Name + " (" + GameInfo.JVM_Info + "), " + GameInfo.JVM_Vendor + "\n" +
		        "        Memory: " + GameInfo.Memory_info + "\n\n" +
		            
                "~~ERROR~~ " + "\n" +
					
			     string.toString() + "\n" +
			        
                "--- END ERROR REPORT ---------"
			);
			
			CrashDisplay.setEditable(false);
			CrashDisplay.setFont(new Font("Consolas", Font.PLAIN, 12));
			JScrollPane errorPane = new JScrollPane(CrashDisplay);
			errorPane.setSize(600, 400);

			UIManager.put("OptionPane.background", Color.white);
			UIManager.put("Panel.background", Color.white);	 

			JOptionPane.showOptionDialog(null, errorPane, "Aircraft has crashed!", JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, args, thread);
		});
		



// Start events ------------------------------------------------------------------------------------------------------------------------------------

		Initializer.parseArgs(args);

		// Initialize game events
		if (time.getMonth() == Month.DECEMBER) { // Christmas 
			if (time.getDayOfMonth() == 24) {
				IS_Christmas = true;
			}
		} else {
			IS_Christmas = false;
		}

		if (time.getMonth() == Month.OCTOBER) { // Halloween
			if (time.getDayOfMonth() == 31) {
				IS_Halloween = true;
			}
		} else {
			IS_Halloween = false;
		}

		if (time.getMonth() == Month.APRIL) { // April Fools 
			if (time.getDayOfMonth() == 1) {
				IS_April_fools = true;
			}
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

		if (Network.autoclient) {
			setMenu(new MultiplayerDisplay("localhost"));
			
		} else if (!HAS_GUI) {
			Network.startMultiplayerServer();
			
		} else {
			setMenu(new TitleDisplay()); // Sets menu to the title screen.
		}

// Window events ----------------------------------------------------------------------------------------------------------------------------------

		// Create a game window
		Initializer.createAndDisplayFrame();

		// Display objects in the screen
		Renderer.initScreen();
		
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