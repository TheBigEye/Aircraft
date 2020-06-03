package minicraft.screen;

import minicraft.gfx.Color;
import minicraft.gfx.Font;
import minicraft.gfx.FontStyle;
import minicraft.gfx.Screen;

public class CreditsMenu extends Menu {
	private Menu parent; // Creates a parent object to go back to
	
	/** The about menu is a read menu about what the game was made for. Only contains text and a black background */
	public CreditsMenu(Menu parent) {
		this.parent = parent;
	}

	public void tick() {
		if (input.getKey("exit").clicked || input.getKey("select").clicked) {
			game.setMenu(parent); //goes back to parent if either above button is pressed
		}
	}
	
	/** Renders the text on the screen */
	public void render(Screen screen) {
		screen.clear(0); // clears the screen to make it black.
		
		Font.drawCentered("Credits", screen, 1 * 8, Color.get(-1, 555));
		FontStyle style = new FontStyle(Color.get(-1, 333));
		Font.drawParagraph("Textures of.. RiverOaken ", screen, 4, true, 5 * 8, false, style, 12);
		Font.drawParagraph("Help.. from Christopher and Chris J", screen, 4, true, 7 * 8, false, style, 12);
		Font.drawParagraph("Ideas.. from the community", screen, 4, true, 9 * 8, false, style, 12);
		Font.drawParagraph("Modified.. by TheBigEye", screen, 4, true, 11 * 8, false, style, 12);
		Font.drawParagraph("(This Mod is in continuous update phase)", screen, 4, true, 19 * 8, false, style, 12);
	}
}
