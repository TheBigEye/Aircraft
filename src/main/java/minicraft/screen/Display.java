package minicraft.screen;

import java.util.Random;

import org.jetbrains.annotations.Nullable;

import minicraft.core.Game;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Sound;
import minicraft.graphic.Screen;
import minicraft.screen.entry.ArrayEntry;

public class Display {
	
	/** Random values used for all the display instances **/
	protected static final Random random = new Random();
	
	private Display parent = null;

	protected Menu[] menus;
	int selection;

	private final boolean canExit;
	private final boolean clearScreen;

	private boolean setParent = false;

	public Display() {
		this(new Menu[0]);
	}

	public Display(boolean clearScreen) {
		this(clearScreen, true, new Menu[0]);
	}

	public Display(boolean clearScreen, boolean canExit) {
		this(clearScreen, canExit, new Menu[0]);
	}

	public Display(boolean clearScreen, boolean canExit, Menu... menus) {
		this.menus = menus;
		this.canExit = canExit;
		this.clearScreen = clearScreen;
		selection = 0;
	}

	public Display(boolean clearScreen, Menu... menus) {
		this(clearScreen, true, menus);
	}

	public Display(Menu... menus) {
		this(false, true, menus);
	}

	// called during setMenu()
	public void init(@Nullable Display parent) {
		if (!setParent) {
			setParent = true;
			this.parent = parent;
		}
	}

	public void onExit() {
	}
	
	public Display getParent() {
		return parent;
	}

	public void tick(InputHandler input) {

		if (canExit && input.getKey("exit").clicked) {
			Game.exitDisplay();
			return;
		}
		if (menus.length == 0) {
			return;
		}

		boolean changedSelection = false;

		// if menu set is unselectable, it must have been intentional, so prevent the user from setting it back.
		if (menus.length > 1 && menus[selection].isSelectable()) { 
			int previousSelection = selection;

			String shift = menus[selection].getCurEntry() instanceof ArrayEntry ? "shift-" : "";

			if (input.getKey(shift + "left").clicked) selection--;
			if (input.getKey(shift + "right").clicked) selection++;

			if (previousSelection != selection) {
				Sound.Menu_select.playOnDisplay();

				int delta = selection - previousSelection;
				selection = previousSelection;
				do {
					selection += delta;
					if (selection < 0) {
						selection = menus.length - 1;
					}
					selection = selection % menus.length;
				} while (!menus[selection].isSelectable() && selection != previousSelection);

				changedSelection = previousSelection != selection;
			}

			if (changedSelection) {
				onSelectionChange(previousSelection, selection);
			}
		}

		if (!changedSelection) {
			menus[selection].tick(input);
		}
	}
	
	protected void onSelectionChange(int oldSel, int newSel) {
		selection = newSel;
	}
	
	// sub-classes can do extra rendering here; this renders each menu that should be rendered
	// in the order of the array, such that the currently selected menu is rendered last, so it
	// appears on top (if they even overlap in the first place).
	public void render(Screen screen) {
		if (clearScreen) {
			screen.clear(0);
		}

		if (menus.length == 0) {
			return;
		}

		int idx = selection;
		do {
			idx++;
			idx = idx % menus.length;
			if (menus[idx].shouldRender()) {
				menus[idx].render(screen);
			}
		} while (idx != selection);
	}


}
