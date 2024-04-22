package minicraft.screen.tutorial;

import minicraft.core.Game;
import minicraft.core.io.Localization;
import minicraft.graphic.Color;
import minicraft.screen.Display;
import minicraft.screen.Menu;
import minicraft.screen.RelPos;
import minicraft.screen.entry.BlankEntry;
import minicraft.screen.entry.SelectEntry;
import minicraft.screen.entry.StringEntry;

public class TutorialDisplay extends Display {

	public TutorialDisplay() {
		super(true, new Menu.Builder(true, 1, RelPos.CENTER,
			new BlankEntry(),
			
			new StringEntry(Localization.getLocalized("Welcome to the tutorial!,"), Color.YELLOW),
			new StringEntry(Localization.getLocalized("are you new? Don't worry,"), Color.YELLOW),
			new StringEntry(Localization.getLocalized("here you will learn the"), Color.YELLOW),
			new StringEntry(Localization.getLocalized("basics of the game, have fun!"), Color.YELLOW),
			
			new BlankEntry(),
			
			new SelectEntry("Meet the Mobs", () -> Game.setDisplay(new MobsTutorial())),
			new SelectEntry("How to Move", () -> Game.setDisplay(new ControlsTutorial())),
			new SelectEntry("How to Attack", () -> Game.setDisplay(new CombatTutorial())),
			new SelectEntry("How to Craft", () -> Game.setDisplay(new CombatTutorial())),
			new SelectEntry("How to Farm", () -> Game.setDisplay(new CombatTutorial())),
			new SelectEntry("How to Mine", () -> Game.setDisplay(new CombatTutorial())),
			new SelectEntry("The bosses", () -> Game.setDisplay(new CombatTutorial())),
			
			new BlankEntry())
			
			.setTitle("Tutorial", Color.YELLOW).createMenu()
		);
	}
}
