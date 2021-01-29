package minicraft.screen;


import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import minicraft.core.Game;
import minicraft.core.io.Localization;
import minicraft.core.io.Settings;
import minicraft.saveload.Save;
import minicraft.screen.entry.BlankEntry;
import minicraft.screen.entry.SelectEntry;

public class OptionsDisplay extends Display {
	
	public OptionsDisplay() {
		super(true, new Menu.Builder(false, 6, RelPos.LEFT,
				Settings.getEntry("diff"),
				Settings.getEntry("fps"),
				Settings.getEntry("sound"),
				Settings.getEntry("ambient"),
				Settings.getEntry("autosave"),
				Settings.getEntry("skinon"),
				new BlankEntry(),
				new SelectEntry("Change Key Bindings", () -> Game.setMenu(new KeyInputDisplay())),
				Settings.getEntry("language"),
				Settings.getEntry("textures"),
				new BlankEntry(),
				new SelectEntry("Open Game Folder", () -> {
	                try {
	                  Desktop.getDesktop().open(new File(Game.gameDir));
	                } catch (IOException e) {
	                  e.printStackTrace();
	                } 
	              })
				
			)
			.setTitle("Options")
			.createMenu()
		);
	}
	
	@Override
	public void onExit() {
		Localization.changeLanguage((String)Settings.get("language"));
		new Save();
		Game.MAX_FPS = (int)Settings.get("fps");
	}
}
