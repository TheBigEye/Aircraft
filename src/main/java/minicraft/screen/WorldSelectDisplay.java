package minicraft.screen;

import java.io.File;
import java.util.ArrayList;

import org.tinylog.Logger;

import minicraft.core.Game;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Localization;
import minicraft.graphic.Color;
import minicraft.graphic.Font;
import minicraft.graphic.Screen;
import minicraft.saveload.Load;
import minicraft.saveload.Save;
import minicraft.saveload.Version;
import minicraft.screen.WorldEditDisplay.Action;
import minicraft.screen.entry.SelectEntry;

public class WorldSelectDisplay extends Display {

	private static final ArrayList<String> worldNames = new ArrayList<>();
	private static final ArrayList<Version> worldVersions = new ArrayList<>();

	private static final String worldsDir = Game.gameDir + "/saves/";

	private static String worldName = "";
	private static boolean loadedWorld = true;

	static {
		updateWorlds();
	}

	public WorldSelectDisplay() {
		super(true);
	}
	
	@Override
	public void init(Display parent) {
	    // If the parent is a WorldEditDisplay and it has a parent itself, set the super class' parent to be the grandparent of the parent
	    if (parent instanceof WorldEditDisplay && parent.getParent() != null) {
	        super.init(parent.getParent().getParent());
	    } else {
	        // Otherwise, just set the super class' parent to be the parent
	        super.init(parent);
	    }

	    // Initialize instance variables
	    worldName = "";
	    loadedWorld = true;

	    // Update the list of available worlds
	    updateWorlds();
	    
	    // Create a new array of SelectEntry objects to be used in the menu
	    SelectEntry[] entries = new SelectEntry[worldNames.size()];
	    
	    // Iterate through each world and create a SelectEntry object for it
	    for (int i = 0; i < entries.length; i++) {
	        final String name = worldNames.get(i);
	        final Version version = worldVersions.get(i);
	        entries[i] = new SelectEntry(name, () -> {
	            // If the selected world was saved by a version of the game that is newer than the current version, return without doing anything
	            if (version.compareTo(Game.VERSION) > 0) {
	                return;
	            }
	            // Otherwise, set the worldName variable to the name of the selected world and switch to a loading screen
	            worldName = name;
	            Game.setDisplay(new LoadingDisplay());
	        }, false);
	    }

	    // Create a new menu with the SelectEntry array and set its display length and scrolling behavior
	    menus = new Menu[] {
	        new Menu.Builder(false, 0, RelPos.CENTER, entries)
	        .setDisplayLength(7)
	        .setScrollPolicies(1, true)
	        .createMenu()
	    };
	}
	
	@Override
	public void tick(InputHandler input) {
		super.tick(input);

		for (Action a : Action.values()) {
			if (input.getKey(a.key).clicked) {
				Game.setDisplay(new WorldEditDisplay(a));
				break;
			}
		}
	}
	
	@Override
	public void render(Screen screen) {
		super.render(screen);
		
		Font.drawCentered(Localization.getLocalized("Select World"), screen, 0, Color.WHITE);
		
		int currentSelection = menus[0].getSelection();
		if (currentSelection >= 0 && currentSelection < worldVersions.size()) {
			Version version = worldVersions.get(currentSelection);
			int textColor = Color.WHITE;
			if (version.compareTo(Game.VERSION) > 0) {
				textColor = Color.RED;
				Font.drawCentered(Localization.getLocalized("Higher version, cannot load world!"), screen, Font.textHeight() * 5, textColor);
			}
			Font.drawCentered(Localization.getLocalized("World Version:") + " " + (version.compareTo(new Version("1.9.2")) <= 0 ? "~" : "") + version, screen, Font.textHeight() * 7 / 2,textColor);
		}
		
		Font.drawCentered(Game.input.getMapping("select") + Localization.getLocalized(" to confirm"), screen, Screen.h - 60, Color.GRAY);
		Font.drawCentered(Game.input.getMapping("exit") + Localization.getLocalized(" to return"), screen, Screen.h - 40, Color.GRAY);

		int y = Screen.h - Font.textHeight() * Action.values().length;
		for (Action a : Action.values()) {
			Font.drawCentered(a.key + Localization.getLocalized(" to " + a), screen, y, a.color);
			y += Font.textHeight();
		}
	}

	public static void updateWorlds() {
		Logger.debug("Updating worlds list ...");

		// Get folder containing the worlds and load them.
		File worldSavesFolder = new File(worldsDir);

		// Try to create the saves folder if it doesn't exist.
		if (worldSavesFolder.mkdirs()) {
			Logger.trace("World save folder created");
		}

		// Get all the files (worlds) in the folder.
		File[] worlds = worldSavesFolder.listFiles();

		if (worlds == null) {
			Logger.error("Game location file folder is null, somehow...");
			return;
		}

		worldNames.clear();
		worldVersions.clear();

		// Check if there are no files in folder.
		if (worlds.length == 0) {
			Logger.debug("No worlds in folder, won't bother loading");
			return;
		}

		// Iterate between every file in worlds.
		for (File file : worlds) {
			if (file.isDirectory()) {
				String path = worldsDir + file.getName() + "/";
				File folder = new File(path);
				folder.mkdirs();
				String[] files = folder.list();
				if (files != null && files.length > 0 && files[0].endsWith(Save.saveExtension)) {
					String name = file.getName();
					worldNames.add(name);
					worldVersions.add(new Load(name, false).getWorldVersion());
				}
			}
		}
	}

	public static String getWorldName() {
		return worldName; 
	}
	
	public static void setWorldName(String world, boolean loaded) {
		worldName = world;
		loadedWorld = loaded;
	}

	public static boolean hasLoadedWorld() {
		return loadedWorld; 
	}

	public static ArrayList<String> getWorldNames() {
		return worldNames; 
	}
}
