package minicraft.screen;

import minicraft.core.Game;
import minicraft.core.io.InputHandler;
import minicraft.gfx.Color;
import minicraft.gfx.Rectangle;
import minicraft.gfx.Screen;
import minicraft.level.Level;
import minicraft.level.tile.Tile;

public class MapDisplay extends Display {

	public MapDisplay() {

		Menu.Builder builder = new Menu.Builder(true, 0, RelPos.CENTER);
		builder.setSize(148, 148);
		builder.setFrame(443, 1, 443);
		builder.setTitle("map");

		menus = new Menu[1];
		menus[0] = builder.createMenu();

		menus[0].shouldRender = true;
	}

	@Override
	public void tick(InputHandler input) {
		if (input.getKey("menu").clicked || input.getKey("attack").clicked || input.getKey("exit").clicked)
			Game.exitMenu();
	}

	@Override
	public void render(Screen screen) {
		menus[0].render(screen);

		Level level = Game.levels[Game.currentLevel];

		Rectangle menuBounds = menus[0].getBounds();

		// used for world sizes bigger than 128, since the map can only render 128x128
		// tiles
		int[] offset = new int[2];
		offset[0] = 0;
		offset[1] = 0;

		// used to indicate which directions can be traveled in
		// North : 0
		// West : 1
		// South : 2
		// East : 3
		boolean[] arrows = new boolean[4];
		for (int i = 0; i < 3; i++) {
			arrows[i] = false;
		}

		// player tile coords
		int ptx = (Game.player.x) / 16;
		int pty = (Game.player.y) / 16;

		// determines which 128x128 "chunk" the player is in
		if (level.w == 256) {
			if (ptx >= 128) {
				offset[0] = 1;
				arrows[3] = true;
			} else {
				arrows[1] = true;
			}
			if (pty >= 128) {
				offset[1] = 1;
				arrows[0] = true;
			} else {
				arrows[2] = true;
			}
		}

		if (level.w == 512) {
			if (ptx >= 128 && ptx < 256) {
				offset[0] = 1;
				arrows[3] = true;
				arrows[1] = true;
			} else if (ptx >= 256 && ptx < 385) {
				offset[0] = 2;
				arrows[3] = true;
				arrows[1] = true;
			} else if (ptx >= 385 && ptx < 512) {
				offset[0] = 3;
				arrows[3] = true;
			} else {
				arrows[1] = true;
			}

			if (pty >= 128 && pty < 256) {
				offset[1] = 1;
				arrows[0] = true;
				arrows[2] = true;
			} else if (pty >= 256 && pty < 385) {
				offset[1] = 2;
				arrows[0] = true;
				arrows[2] = true;
			} else if (pty >= 385 && pty < 512) {
				offset[1] = 3;
				arrows[0] = true;
			} else {
				arrows[2] = true;
			}
		}

		for (int i = 0; i < 128; i++) {
			for (int c = 0; c < 128; c++) {
				int color = 0;
				Tile tile = level.getTile(i + (offset[0] * 128), c + (offset[1] * 128));

				MapData mapData = MapData.getById(tile.id);
				if (mapData != null) {
					color = mapData.color;
				}
				// by drawing with only one pixel at a time we can draw with much more precision
				screen.setPixel(i + menuBounds.getLeft() + 6, c + menuBounds.getTop() + 6, color);
			}
		}

		// render the marker for the player
		screen.render((Game.player.x - 8) / 16 + menuBounds.getLeft() + 2 - (offset[0] * 128),
				(Game.player.y - 8) / 16 + menuBounds.getTop() + 2 - (offset[1] * 128), 2 + 12 * 32,
				Color.get(-1, 255, 0, 0));
	}
}
