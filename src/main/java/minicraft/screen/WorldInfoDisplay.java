package minicraft.screen;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import minicraft.core.Game;
import minicraft.core.io.Localization;
import minicraft.core.io.Settings;
import minicraft.gfx.Color;
import minicraft.gfx.Font;
import minicraft.gfx.Screen;
import minicraft.screen.entry.BlankEntry;
import minicraft.screen.entry.SelectEntry;
import minicraft.screen.entry.StringEntry;

public class WorldInfoDisplay extends Display {

	public WorldInfoDisplay() {
		super(true);

		Menu worldInfo = new Menu.Builder(true, 3, RelPos.CENTER,
				new BlankEntry(), new BlankEntry(), new BlankEntry(), new BlankEntry(), new BlankEntry(), new BlankEntry(), new BlankEntry(), new BlankEntry(),
				new StringEntry("                               "),
				new SelectEntry("Open World Folder", () -> {
					try {
						Desktop.getDesktop().open(new File(Game.gameDir + "/saves/" + WorldSelectDisplay.getWorldName()));
					} catch (IOException exception) {
						exception.printStackTrace();
					}
				}),
				new BlankEntry())
				.setTitle(Localization.getLocalized("World info"))
				.createMenu();

		menus = new Menu[]{
			worldInfo
		};
	}

	public void render(Screen screen) {
		super.render(screen);
		Font.draw(Localization.getLocalized("Name:") + " " + WorldSelectDisplay.getWorldName(), screen, 120, 90, Color.GREEN);
		Font.draw(Localization.getLocalized("Seed:") + " " + Game.levels[selection].getSeed(), screen, 120, 100, Color.GREEN);

		Font.draw(Settings.getEntry("size") + "", screen, 120, 115, Color.GRAY);
		Font.draw(Settings.getEntry("theme") + "", screen, 120, 125, Color.GRAY);
		Font.draw(Settings.getEntry("type") + "", screen, 120, 135, Color.GRAY);

		Font.draw(Settings.getEntry("mode") + "", screen, 120, 150, Color.GRAY);
		Font.draw(Settings.getEntry("cheats") + "", screen, 120, 160, Color.GRAY);
	}
}
