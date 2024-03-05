package minicraft.core.io;

import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.util.HashMap;

import org.tinylog.Logger;

import minicraft.core.Game;
import minicraft.screen.entry.ArrayEntry;
import minicraft.screen.entry.BooleanEntry;
import minicraft.screen.entry.RangeEntry;

public class Settings {

	@SuppressWarnings("rawtypes")
	private static HashMap<String, ArrayEntry> options = new HashMap<>();

	static {
		options.put("diff", new ArrayEntry<>("Difficulty", "Peaceful", "Easy", "Normal", "Hard"));
		options.get("diff").setSelection(2);
		options.put("mode", new ArrayEntry<>("Game Mode", "Survival", "Creative", "Hardcore", "Score"));

		options.put("scoretime", new ArrayEntry<>("Time (Score Mode)", 10, 20, 40, 60, 120));
		options.get("scoretime").setValueVisibility(10, false);
		options.get("scoretime").setValueVisibility(120, false);

		options.put("sound", new BooleanEntry("Sound", true));
		options.put("autosave", new BooleanEntry("Autosave", true));

		options.put("ambient", new ArrayEntry<>("Ambient", "Nice", "Normal", "Scary"));

		options.put("cheats", new BooleanEntry("Cheats", true));
		
		options.put("size", new ArrayEntry<>("World Size", 256, 512));
		options.get("size").setSelection(0);
		
		options.put("theme", new ArrayEntry<>("World Theme", "Normal", "Forest", "Desert", "Plain", "Hell", "Snow"));
		options.put("type", new ArrayEntry<>("Terrain Type", "Island", "Box", "Mountain", "Irregular"));

		options.put("unlockedskin", new BooleanEntry("Wear Suit", false));
		options.put("skinon", new BooleanEntry("Wear Suit", false));

		options.put("language", new ArrayEntry<>("Language", true, false, Localization.getLanguages()));
		options.get("language").setValue(Localization.getSelectedLanguage());

		options.get("mode").setChangeAction(value -> options.get("scoretime").setVisible("Score".equals(value)));

		options.get("unlockedskin").setChangeAction(value -> 
			options.get("skinon").setVisible((boolean) value)
		);

		options.get("skinon").setChangeAction(value -> {
			if (Game.player != null) {
				Game.player.suitOn = (boolean) value;
			}
		});
		
		options.put("textures", new ArrayEntry<>("Textures", "Original", "Custom"));
		options.get("textures").setSelection(0);
        
        // Video options
        options.put("fps", new RangeEntry("Max FPS", 30, 300, getRefreshRate()));
        options.put("vsync", new BooleanEntry("V.Sync", false));
       
        options.put("bossbar", new ArrayEntry<>("Bossbar type", "On screen", "On entity", "Percent"));
        options.get("bossbar").setSelection(0);
        
        options.put("particles", new BooleanEntry("Particles", true));
        options.put("shadows", new BooleanEntry("Shadows", true));
        
	}

	public static void initialize() {
		Logger.debug("Initializing game settings ...");
	}

	// Returns the value of the specified option
	public static Object get(String option) {
		return options.get(option.toLowerCase()).getValue();
	}

	// Returns the boolean value of the specified option
	public static boolean getBoolean(String option) {
		return ((boolean) (options.get(option.toLowerCase())).getValue());
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getUnsafe(String option) {
		return (T)get(option);
	}

	// Returns the index of the value in the list of values for the specified option
	public static int getIndex(String option) {
		return options.get(option.toLowerCase()).getSelection();
	}

	@SuppressWarnings("rawtypes")
	// Return the ArrayEntry object associated with the given option name.
	public static ArrayEntry getEntry(String option) {
		return options.get(option.toLowerCase());
	}

	// Sets the value of the given option name, to the given value, provided it is a valid value for that option.
	public static void set(String option, Object value) {
		options.get(option.toLowerCase()).setValue(value);
	}

	// Sets the index of the value of the given option, provided it is a valid index
	public static void setIndex(String option, int idx) {
		options.get(option.toLowerCase()).setSelection(idx);
	}

	public static int getRefreshRate() {
		// If the graphics environment is headless, we cannot determine the refresh rate, so return 60
		if (GraphicsEnvironment.isHeadless()){
			return 60;
		}

		int hz;
		try {
			// Get the refresh rate of the default screen device
			hz = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getRefreshRate();
		} catch (HeadlessException exception) {
			// If there is an error getting the refresh rate, return 60
			return 60;
		}

		// If the refresh rate is unknown, return 60
		if (hz == DisplayMode.REFRESH_RATE_UNKNOWN) return 60;
		// If the refresh rate is greater than 300 or less than 10, return 60
		if (hz > 300 || hz < 10) return 60;

		// Otherwise, return the refresh rate
		return hz;
	}
}