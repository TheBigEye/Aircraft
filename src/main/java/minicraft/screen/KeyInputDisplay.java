package minicraft.screen;

import minicraft.core.Game;
import minicraft.core.io.InputHandler;
import minicraft.graphic.Color;
import minicraft.graphic.Font;
import minicraft.graphic.Point;
import minicraft.graphic.Screen;
import minicraft.graphic.SpriteSheet;
import minicraft.screen.entry.KeyInputEntry;
import minicraft.screen.entry.StringEntry;

public class KeyInputDisplay extends Display {

	private boolean listeningForBind, confirmReset;
	
    private static final int minX = 15; 
    private static final int maxX = 15 + 12 * 32;
    private static final int minY = 8 ;
    private static final int maxY = 8 * 12 + 8 * 16;
    

	private static Menu.Builder builder;

	private static KeyInputEntry[] getEntries() {
		String[] prefs = Game.input.getKeyPrefs();
		KeyInputEntry[] entries = new KeyInputEntry[prefs.length];

		for (int i = 0; i < entries.length; i++) {
			entries[i] = new KeyInputEntry(prefs[i]);
		}

		return entries;
	}

	public KeyInputDisplay() {
		super(true);
		
		builder = new Menu.Builder(true, 0, RelPos.CENTER, getEntries());
		builder.setTitle("Controls");
		builder.setPositioning(new Point(Screen.w / 2, Screen.h - Font.textHeight() * 4), RelPos.TOP);
		builder.setSize(maxX - minX + SpriteSheet.boxWidth * 2, maxY - minY + SpriteSheet.boxWidth * 2);
		
		Menu.Builder popupBuilder = new Menu.Builder(true, 4, RelPos.CENTER);
		popupBuilder.setShouldRender(false);
		popupBuilder.setSelectable(false);

		menus = new Menu[] { 
			builder.createMenu(),
			popupBuilder.setEntries(StringEntry.useLines(Color.YELLOW, "Press the desired", "key sequence")).createMenu(),
			popupBuilder.setEntries(StringEntry.useLines(Color.RED, "Are you sure you want to reset all key bindings?", "enter to confirm", "escape to cancel")).setTitle("Confirm Action").createMenu() 
		};

		listeningForBind = false;
		confirmReset = false;
	}

	@Override
	public void tick(InputHandler input) {
		if (listeningForBind) {
			if (input.keyToChange == null) {
				// the key has just been set
				listeningForBind = false;
				menus[1].shouldRender = false;
				menus[0].updateSelectedEntry(new KeyInputEntry(input.getChangedKey()));
				selection = 0;
			}

			return;
		}

		if (confirmReset) {
			if (input.getKey("exit").clicked) {
				confirmReset = false;
				menus[2].shouldRender = false;
				selection = 0;
			} else if (input.getKey("select").clicked) {
				confirmReset = false;
				input.resetKeyBindings();
				menus[2].shouldRender = false;
				menus[0] = builder.setEntries(getEntries()).setSelection(menus[0].getSelection(), menus[0].getDispSelection()).createMenu();
				selection = 0;
			}

			return;
		}

		super.tick(input); // ticks menu

		if (input.keyToChange != null) {
			listeningForBind = true;
			selection = 1;
			menus[selection].shouldRender = true;
		} else if (input.getKey("shift-d").clicked && !confirmReset) {
			confirmReset = true;
			selection = 2;
			menus[selection].shouldRender = true;
		}
	}

	public void render(Screen screen) {
		if (selection == 0) { // not necessary to put in if statement now, but it's probably more efficient anyway
			screen.clear(0);
		}
		
		super.render(screen);

		if (!listeningForBind && !confirmReset) {
			String[] lines = { 
					"Press C/Enter to change key binding",
					"Press A to add key binding",
					"Shift-D to reset all keys to default",
					Game.input.getMapping("exit") + " to Return to menu" 
			};
			
			for (int i = 0; i < lines.length; i++) {
				Font.drawCentered(lines[i], screen, Screen.h - Font.textHeight() * (4 - i), Color.WHITE);
			}
		}
	}

}
