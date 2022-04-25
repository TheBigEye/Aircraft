package minicraft.screen.tutorial;

import minicraft.core.Game;
import minicraft.core.io.Localization;
import minicraft.gfx.Color;
import minicraft.screen.Display;
import minicraft.screen.Menu;
import minicraft.screen.RelPos;
import minicraft.screen.entry.BlankEntry;
import minicraft.screen.entry.SelectEntry;
import minicraft.screen.entry.StringEntry;

public class TutorialDisplay extends Display {

	public TutorialDisplay() {
		super(true, new Menu.Builder(true, 6, RelPos.CENTER,
				new BlankEntry(),
				new StringEntry(Localization.getLocalized("Welcome to the tutorial!,"), Color.YELLOW),
				new StringEntry(Localization.getLocalized("are you new? Don't worry,"), Color.YELLOW),
				new StringEntry(Localization.getLocalized("here you will learn the"), Color.YELLOW),
				new StringEntry(Localization.getLocalized("basics of the game, have fun!"), Color.YELLOW),
				new BlankEntry(),
				new SelectEntry("Moving the character", () -> Game.setDisplay(new ControlsTutorial())),
				new SelectEntry("Mobs?, What's that", () -> Game.setDisplay(new MobsTutorial())),
				new SelectEntry("How to Attack", () -> Game.setDisplay(new CombatTutorial())),
				new SelectEntry("The Inventory", () -> Game.setDisplay(new CombatTutorial())),
				new SelectEntry("The Villagers", () -> Game.setDisplay(new CombatTutorial())),
				new SelectEntry("The Furnitures", () -> Game.setDisplay(new CombatTutorial())),
				new SelectEntry("The Crafting", () -> Game.setDisplay(new CombatTutorial())),
				new SelectEntry("The Potions", () -> Game.setDisplay(new CombatTutorial())),
				new SelectEntry("Farming", () -> Game.setDisplay(new CombatTutorial())),
				new SelectEntry("Mining", () -> Game.setDisplay(new CombatTutorial())),
				new SelectEntry("The Bosses", () -> Game.setDisplay(new CombatTutorial())),
				new BlankEntry())
				.setTitle("Tutorial", Color.YELLOW).createMenu());
	}

	@Override
	public void onExit() {

	}
}
