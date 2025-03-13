package minicraft.screen;

import minicraft.core.Game;
import minicraft.core.Updater;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Settings;
import minicraft.graphic.Color;
import minicraft.graphic.Point;
import minicraft.graphic.Screen;
import minicraft.graphic.SpriteSheet;
import minicraft.item.Items;
import minicraft.saveload.Save;
import minicraft.screen.entry.BlankEntry;
import minicraft.screen.entry.ListEntry;
import minicraft.screen.entry.SelectEntry;
import minicraft.screen.entry.StringEntry;

import java.util.ArrayList;
import java.util.Arrays;

public class EndGameDisplay extends Display {
	private static final String[] scoredItems = { "Oak Wood", "Spruce Wood", "Birch Wood", "Cloth", "Slime", "Bone", "Arrow", "Gunpowder", "Antidious" };
	private static final String[] titles = { "Game Over", "Win?", "Win!!", "Nice!", "The End!" };

	private int inputDelay; // variable to delay the input of the player, so they won't skip the won menu by accident.
	private int displayTimer;
	private int finalScore;

	static {
		int maxLength = 0;
		for (String s : scoredItems) {
			maxLength = Math.max(maxLength, s.length());
		}
	}

	public EndGameDisplay() {
		super(false, false);

		displayTimer = Updater.normalSpeed; // wait 3 seconds before rendering the menu.
		inputDelay = Updater.normalSpeed / 2; // wait a half-second after rendering before allowing user input.

		ArrayList<ListEntry> entries = new ArrayList<>();

		// calculate the score
		entries.add(new StringEntry("Player Score: " + Game.player.getScore(), Color.WHITE));
		entries.add(new StringEntry("<Bonuses>", Color.YELLOW));

		finalScore = Game.player.getScore();
		for (String item : scoredItems) {
			addBonus(item);
		}

		entries.add(new StringEntry("Final Score: " + finalScore));

		// add any unlocks
		entries.addAll(Arrays.asList(getAndWriteUnlocks()));
		entries.add(new BlankEntry());
		entries.add(new SelectEntry("Close", () -> Game.setDisplay(null)));
		entries.add(new SelectEntry("Exit to Menu", () -> Game.setDisplay(new TitleDisplay())));

		menus = new Menu[] {
			new Menu.Builder(true, 0, RelPos.LEFT, entries)
			.setPositioning(new Point(SpriteSheet.boxWidth, SpriteSheet.boxWidth * 3), RelPos.BOTTOM_RIGHT)
			.setTitle(titles[random.nextInt(titles.length)])
			.createMenu()
		};
	}

	private void addBonus(String item) {
		int count = Game.player.getInventory().count(Items.get(item));
		int score = count * (random.nextInt(2) + 1) * 10;
        finalScore += score;
	}

	@Override
	public void tick(InputHandler input) {
		if (displayTimer > 0) displayTimer--;
		else if (inputDelay > 0) inputDelay--;
		else super.tick(input);
	}

	@Override
	public void render(Screen screen) {
		if (displayTimer <= 0) {
			super.render(screen);
		}
	}

	private StringEntry[] getAndWriteUnlocks() {
		int scoreTime = (int) Settings.get("scoretime");
		ArrayList<Integer> unlocks = new ArrayList<>();

		if (scoreTime == 20 && !Settings.getEntry("scoretime").valueIs(10) && finalScore > 1000) {
			unlocks.add(10);
			Settings.getEntry("scoretime").setValueVisibility(10, true);
		}

		if (scoreTime == 60 && !Settings.getEntry("scoretime").valueIs(120) && finalScore > 100000) {
			unlocks.add(120);
			Settings.getEntry("scoretime").setValueVisibility(120, true);
		}

		StringEntry[] entries = new StringEntry[unlocks.size()];
		for (int i = 0; i < entries.length; i++) {
			entries[i] = new StringEntry("Unlocked! " + unlocks.get(i) + " Score Time");
		}

		new Save(); // Writes unlocks, and preferences

		return entries;
	}
}
