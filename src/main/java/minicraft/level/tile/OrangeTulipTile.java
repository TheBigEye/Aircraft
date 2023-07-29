package minicraft.level.tile;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.graphic.ConnectorSprite;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;

public class OrangeTulipTile extends Tile {
	private static final Sprite sprite = new Sprite(6, 12, 1);

	protected OrangeTulipTile(String name) {
		super(name, (ConnectorSprite) null);
		connectsToGrass = true;
		maySpawn = true;
	}

	@Override
	public boolean hurt(Level level, int x, int y, Mob source, int hurtDamage, Direction attackDir) {
		level.dropItem((x << 4) + 8, (y << 4) + 8, 1, 1, Items.get("Orange Tulip"));
		level.setTile(x, y, Tiles.get("Grass"));
		return true;
	}

	@Override
	public boolean interact(Level level, int x, int y, Player player, Item item, Direction attackDir) {
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Shovel) {
				if (player.payStamina(2 - tool.level) && tool.payDurability()) {
					Sound.genericHurt.playOnLevel(x << 4, y << 4);
					level.setTile(x, y, Tiles.get("Grass"));
					level.dropItem((x << 4) + 8, (y << 4) + 8, Items.get("Orange Tulip"));
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		Tiles.get("Grass").render(screen, level, x, y);

		int data = level.getData(x, y);
		int shape = (data >> 4) % 2;

		x <<= 4;
		y <<= 4;

		sprite.render(screen, x + 8 * shape, y);
		sprite.render(screen, x + 8 * (shape == 0 ? 1 : 0), y + 8);
	}

	@Override
	public boolean tick(Level level, int xt, int yt) {
		if (random.nextInt(30) != 0) {
			return false;
		}

		int xn = xt;
		int yn = yt;

		if (random.nextBoolean()) {
			xn += random.nextInt(2) * 2 - 1;
		} else {
			yn += random.nextInt(2) * 2 - 1;
		}

		if (level.getTile(xn, yn) == Tiles.get("Dirt")) {
			level.setTile(xn, yn, Tiles.get("Grass"));
		}
		return false;
	}
}
