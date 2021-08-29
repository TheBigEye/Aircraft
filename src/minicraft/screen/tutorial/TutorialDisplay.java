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
		super(true, new Menu.Builder(false, 6, RelPos.CENTER,
				new BlankEntry(),
				new StringEntry(Localization.getLocalized("Welcome to the tutorial!,"), Color.YELLOW),
				new StringEntry(Localization.getLocalized("are you new? Don't worry,"), Color.YELLOW),
				new StringEntry(Localization.getLocalized("here you will learn the"), Color.YELLOW),
				new StringEntry(Localization.getLocalized("basics of the game,"), Color.YELLOW),
				new StringEntry(Localization.getLocalized("have fun!"), Color.YELLOW),
				new BlankEntry(),
				new SelectEntry("Moving the character", () -> Game.setMenu(new ControlsTutorial())),
				new SelectEntry("Mobs?, What's that", () -> Game.setMenu(new MobsTutorial())),
				new SelectEntry("How to Attack", () -> Game.setMenu(new CombatTutorial())),
				new SelectEntry("The Inventory", () -> Game.setMenu(new CombatTutorial())),
				new SelectEntry("The Villagers", () -> Game.setMenu(new CombatTutorial())),
				new SelectEntry("Furnitures", () -> Game.setMenu(new CombatTutorial())),
				new SelectEntry("Crafting", () -> Game.setMenu(new CombatTutorial())),
				new SelectEntry("Potions", () -> Game.setMenu(new CombatTutorial())),
				new SelectEntry("Farming", () -> Game.setMenu(new CombatTutorial())),
				new SelectEntry("Mining", () -> Game.setMenu(new CombatTutorial())),
				new SelectEntry("Bosses", () -> Game.setMenu(new CombatTutorial())),
				new BlankEntry()			
			)
			.setTitle("Tutorial", Color.YELLOW)
			.createMenu()
		);
	}
	
	@Override
	public void onExit() {

	}
}
