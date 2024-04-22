package minicraft.level.tile;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.graphic.ConnectorSprite;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.item.Item;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;

public class MagmaTile extends Tile {
	private static ConnectorSprite sprite = new ConnectorSprite(IceTile.class, new Sprite(27, 16, 3, 3, 1), new Sprite(30, 16, 2, 2, 1)) {

		@Override
		public boolean connectsTo(Tile tile, boolean isSide) {
			if (!isSide) {
				return true;
			}
			return tile.connectsToMagma;
		}
	};

	protected MagmaTile(String name) {
		super(name, sprite);
		connectorSprite.sides = connectorSprite.sparse;
		connectsToMagma = true;
		connectsToFluid = true;
		maySpawn = false;
	}

	@Override
	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Pickaxe) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					Sound.playAt("genericHurt", xt << 4, yt << 4);
					level.setTile(xt, yt, Tiles.get("Lava"));
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		Tiles.get("Lava").render(screen, level, x, y);
		sprite.render(screen, level, x, y);
	}

	@Override
	public boolean tick(Level level, int xt, int yt) {
		int xn = xt;
		int yn = yt;

		if (random.nextBoolean()) {
			xn += random.nextInt(2) * 2 - 1;
		} else {
			yn += random.nextInt(2) * 2 - 1;
		}

		if (level.getTile(xn, yn) instanceof HoleTile) {
			level.setTile(xn, yn, "Lava");
		}

		for (int x = -1; x < 2; x++) {
			if (level.getTile(xt + x, yt) instanceof WaterTile) {
				level.setTile(xt + x, yt, Tiles.get("Raw Obsidian"));
			}
		}

		for (int y = -1; y < 2; y++) {
			if (level.getTile(xt, yt + y) instanceof WaterTile) {
				level.setTile(xt, yt + y, Tiles.get("Raw Obsidian"));
			}
		}

		return false;
	}
	
	public int getLightRadius(Level level, int x, int y) {
		return 3;
	}
	
}
