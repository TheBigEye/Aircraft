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
		builder.setTitle("map");
		builder.setSize(148, 148);
		builder.setFrame(443, 1, 443);

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

		// player tile coords
		int ptx = (Game.player.x) / 16;
		int pty = (Game.player.y) / 16;

		// determines which 128x128 "chunk" the player is in
		if (level.w == 256) {
			if (ptx >= 128) {
				offset[0] = 1;
			}
			if (pty >= 128) {
				offset[1] = 1;
			}
		}	

			if (level.w == 512) {
				if (ptx >= 128 && ptx < 256) {
					offset[0] = 1;
				} else if (ptx >= 256 && ptx < 385) {
					offset[0] = 2;
				} else if (ptx >= 385 && ptx < 512) {
					offset[0] = 3;
				}
			}	

			if (pty >= 128 && pty < 256) {
				offset[1] = 1;
			} else if (pty >= 256 && pty < 385) {
				offset[1] = 2;
			} else if (pty >= 385 && pty < 512) {
				offset[1] = 3;
			}
		

		for (int i = 0; i < 136; i++) {
			for (int c = 0; c < 136; c++) {
				int color = 0;
				Tile tile = level.getTile(i, c);

				MapData mapData = MapData.getById(tile.id);
				if (mapData != null) {
					color = mapData.color;
				}
				// by drawing with only one pixel at a time we can draw with much more precision

				if (level.w == 128) {
					screen.setPixel(i + menuBounds.getLeft() + 10, c + menuBounds.getTop() + 10, color);
				} else {
					screen.setPixel(i + menuBounds.getLeft() + 6, c + menuBounds.getTop() + 6, color);
				}
			}
		}

		// render the marker for the player
		screen.render((Game.player.x - 8) / 16 + menuBounds.getLeft() + 2 - (offset[0] * 128), (Game.player.y - 8) / 16 + menuBounds.getTop() + 2 - (offset[1] * 128), 2 + 12 * 32,
				Color.get(-1, 255, 0, 0));
	}
}
