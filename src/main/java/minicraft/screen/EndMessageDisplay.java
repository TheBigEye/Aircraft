package minicraft.screen;

import java.util.ArrayList;

import minicraft.core.Game;
import minicraft.core.Updater;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Settings;
import minicraft.entity.mob.Player;
import minicraft.gfx.Color;
import minicraft.gfx.Point;
import minicraft.gfx.Screen;
import minicraft.gfx.SpriteSheet;
import minicraft.saveload.Save;
import minicraft.screen.entry.BlankEntry;
import minicraft.screen.entry.ListEntry;
import minicraft.screen.entry.SelectEntry;
import minicraft.screen.entry.StringEntry;

public class EndMessageDisplay extends Display {
	private static final String[] titles = { "Game Over?", "You Win!", "Well played, boy!", "You Win, Awww Man", "Well played!" };

	private int inputDelay; // variable to delay the input of the player, so they won't skip the won menu by accident.
	private int displayTimer;
	private int finalscore;

	public EndMessageDisplay(Player player) {
		super(false, false);

		displayTimer = Updater.normalSpeed; // wait 3 seconds before rendering the menu.
		inputDelay = Updater.normalSpeed / 2; // wait a half-second after rendering before allowing user input.

		ArrayList<ListEntry> entries = new ArrayList<>();

		entries.add(new StringEntry("lol", Color.GREEN));
		entries.add(new BlankEntry());
		entries.add(new SelectEntry("Close", () -> Game.setDisplay(null)));
		entries.add(new SelectEntry("Exit to Menu", () -> Game.setDisplay(new TitleDisplay())));

		menus = new Menu[] {
			new Menu.Builder(true, 1, RelPos.LEFT, entries)
			.setPositioning(new Point(SpriteSheet.boxWidth, SpriteSheet.boxWidth * 4), RelPos.BOTTOM_RIGHT)
			.setTitle(titles[random.nextInt(titles.length)])
			.createMenu() 
		};
	}

	@Override
	public void tick(InputHandler input) {
		if (displayTimer > 0) {
			displayTimer--;
		} else if (inputDelay > 0) {
			inputDelay--;
		} else {
			super.tick(input);
		}
	}

	@Override
	public void render(Screen screen) {
		if (displayTimer <= 0) {
			super.render(screen);
		}
	}

	@SuppressWarnings("unused")
	private StringEntry[] getAndWriteUnlocks() {
		int scoreTime = (int) Settings.get("scoretime");
		ArrayList<Integer> unlocks = new ArrayList<>();

		if (scoreTime == 20 && !Settings.getEntry("scoretime").valueIs(10) && finalscore > 1000) {
			unlocks.add(10);
			Settings.getEntry("scoretime").setValueVisibility(10, true);
		}

		if (scoreTime == 60 && !Settings.getEntry("scoretime").valueIs(120) && finalscore > 100000) {
			unlocks.add(120);
			Settings.getEntry("scoretime").setValueVisibility(120, true);
		}

		StringEntry[] entries = new StringEntry[unlocks.size()];
		for (int i = 0; i < entries.length; i++) {
			entries[i] = new StringEntry("Unlocked! " + unlocks.get(i) + " Score Time");
		}

		new Save(); // writes unlocks and preferences

		return entries;
	}
}
