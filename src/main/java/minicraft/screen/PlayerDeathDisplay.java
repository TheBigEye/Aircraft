package minicraft.screen;

import java.util.ArrayList;
import java.util.Arrays;

import minicraft.core.Game;
import minicraft.core.World;
import minicraft.graphic.Point;
import minicraft.graphic.SpriteSheet;
import minicraft.saveload.Save;
import minicraft.screen.entry.BlankEntry;
import minicraft.screen.entry.ListEntry;
import minicraft.screen.entry.SelectEntry;
import minicraft.screen.entry.StringEntry;

public class PlayerDeathDisplay extends Display {
	// This is an IMPORTANT bool, determines if the user should respawn or not. :)
	public static boolean shouldRespawn = true;

	private static final String[] deadSpashes = {
			"You died! Aww!", "Its humiliating!", "Luck for the next!",
			"That had to hurt!", "What a pity!", "In the end?!",
			"Don't mistake my silence!", "There are things we forget ...", "Great, you died!",
			"Unforeseen consequences", 
	};

	public PlayerDeathDisplay() {
		super(false, false);

		ArrayList<ListEntry> entries = new ArrayList<>(Arrays.asList(
				new StringEntry("Time: " + InfoDisplay.getTimeString()),
				new StringEntry("Score: " + Game.player.getScore()),
				new BlankEntry()
		));

		// Sets the menu to nothing
		if (!Game.isMode("Hardcore")) {
			entries.add(new SelectEntry("Respawn", () -> {
				World.resetGame();
				Game.setDisplay(null); 
			}));
		}

		// Save the death status
		entries.add(new SelectEntry("Save and Quit", () -> {
			new Save(WorldSelectDisplay.getWorldName());
			Game.setDisplay(new TitleDisplay());
		}));

		entries.add(new SelectEntry("Quit", () -> Game.setDisplay(new TitleDisplay())));

		menus = new Menu[] {
			new Menu.Builder(true, 0, RelPos.LEFT, entries)
			.setPositioning(new Point(SpriteSheet.boxWidth, SpriteSheet.boxWidth * 3), RelPos.BOTTOM_RIGHT)
			.setTitle(deadSpashes[random.nextInt(9)]).setTitlePos(RelPos.TOP_LEFT).createMenu() 
		};
	}
}
