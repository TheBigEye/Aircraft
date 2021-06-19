package minicraft.level.tile;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.gfx.Color;
import minicraft.gfx.ConnectorSprite;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;

public class SkyDirtTile extends Tile {
	private static ConnectorSprite sprite = new ConnectorSprite(SkyDirtTile.class, new Sprite(44, 9, 3, 3, 1, 3),
			new Sprite(47, 9, 2, 2, 1)) {
		public boolean connectsTo(Tile tile, boolean isSide) {
			if (!isSide)
				return true;
			return tile.connectsToSkyDirt;
		}
	};

	protected SkyDirtTile(String name) {
		super(name, sprite);
		connectsToSkyGrass = true;
		connectsToSkyDirt = true;
		maySpawn = true;
	}

	protected static int dCol(int depth) {
		switch (depth) {
		case 0:
			return Color.get(1, 129, 105, 83); // surface.
		case -4:
			return Color.get(1, 76, 30, 100); // dungeons.
		default:
			return Color.get(1, 102); // caves.
		}
	}

	protected static int dIdx(int depth) {
		switch (depth) {
		case 0:
			return 0; // surface
		case -4:
			return 2; // dungeons
		default:
			return 1; // caves
		}
	}

	public void render(Screen screen, Level level, int x, int y) {
		Tiles.get("sky grass").render(screen, level, x, y);
		sprite.render(screen, level, x, y);
	}

	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Shovel) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					level.setTile(xt, yt, Tiles.get("cloud"));
					Sound.Tile_generic_hurt.play();
					level.dropItem(xt * 16 + 8, yt * 16 + 8, Items.get("Sky dirt"));
					
					if (random.nextInt(64) == 0) { // 2% chance to drop bones
						level.dropItem(xt * 16 + 8, yt * 16 + 8, Items.get("bone"));
					}
					
					return true;
				}
			}
			if (tool.type == ToolType.Hoe) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					level.setTile(xt, yt, Tiles.get("sky farmland"));
					Sound.Tile_generic_hurt.play();
					return true;
				}
			}
		}
		return false;
	}
}
