package minicraft.screen;

import minicraft.core.Game;
import minicraft.core.io.InputHandler;
import minicraft.gfx.Color;
import minicraft.gfx.Rectangle;
import minicraft.gfx.Screen;

public class MapDisplay extends Display {

	private static final int PLAYER_MARKER_SPRITE = 2 + 12 * 32;
	private static final int PLAYER_MARKER_COLOR = Color.get(-1, 255, 0, 0);

	public MapDisplay() {
		Menu.Builder builder = new Menu.Builder(true, 0, RelPos.CENTER);
		builder.setSize(140, 140);
		builder.setFrame(443, 1, 443);
		builder.setTitle("map");

		menus = new Menu[1];
		menus[0] = builder.createMenu();

		menus[0].shouldRender = true;
	}

	@Override
	public void tick(InputHandler input) {
		if (input.getKey("menu").clicked || input.getKey("attack").clicked || input.getKey("exit").clicked) {
			Game.exitMenu();
		}
	}

	@Override
	public void render(Screen screen) {
		Menu menu = menus[0];
		menu.render(screen);

		// Player Tile Coordinates
		int ptx = Game.player.x >> 4;
		int pty = Game.player.y >> 4;

		/*
		 * This is for worlds large than 128x128
		 * due that map can just display 128x128 pixels
		 * thus we can fix position to get tiles correctly
		 *
		 * To explain this, we divide by 128 to get an integer about how many tiles
		 * we have skipped, once done that, we can multiply 128 again to shift those
		 * coordinates, keeping in mind that, smx != ptx and smy != pty, due that those
		 * operations didn't use decimal part
		 *
		 * smx and smy mean Shift Map X and Shift Map Y
		 */
		int smx = (ptx >> 7) << 7;
		int smy = (pty >> 7) << 7;

		Rectangle menuBounds = menu.getBounds();

		for (int y = 0; y < 128; y++) {
			for (int x = 0; x < 128; x++) {
				MapData mapData = MapData.getById(Game.levels[Game.currentLevel].getTile(x + smx, y + smy).id);
				int color = mapData != null ? mapData.color : 0;

				// by drawing with only one pixel at a time we can draw with much more precision
				screen.setPixel(x + menuBounds.getLeft() + 6, y + menuBounds.getTop() + 6, color);
			}
		}

		// Render the marker for the player
		screen.render(ptx % 128 + menuBounds.getLeft() + 2, pty % 128 + menuBounds.getTop() + 2,
				MapDisplay.PLAYER_MARKER_SPRITE, MapDisplay.PLAYER_MARKER_COLOR);
	}
}
