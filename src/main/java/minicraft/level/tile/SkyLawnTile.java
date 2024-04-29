package minicraft.level.tile;

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

public class SkyLawnTile extends Tile {
	private static final Sprite sprite = new Sprite(40, 23, 1);
	
	private Tile skyGrassTile = Tiles.get("Sky grass");

	protected SkyLawnTile(String name) {
		super(name, (ConnectorSprite) null);
		connectsToSkyGrass = true;
		maySpawn = true;
	}

	@Override
	public boolean hurt(Level level, int x, int y, Mob source, int hurtDamage, Direction attackDir) {
		if (random.nextInt(12) == 1) { // 20% chance to drop sky seeds
			level.dropItem((x << 4) + 8, (y << 4) + 8, Items.get("Sky Seeds"));
		}
		level.setTile(x, y, skyGrassTile);
		return true;
	}

	@Override
	public boolean interact(Level level, int x, int y, Player player, Item item, Direction attackDir) {
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Shovel) {
				if (player.payStamina(2 - tool.level) && tool.payDurability()) {
					level.setTile(x, y, Tiles.get("Sky grass"));

					if (random.nextInt(20) == 1) { // 20% chance to drop sky seeds
						level.dropItem((x << 4) + 8, (y << 4) + 8, Items.get("Sky Seeds"));
					}

					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		skyGrassTile.render(screen, level, x, y);

		int data = level.getData(x, y);
		int shape = (data >> 4) % 2;

		x <<= 4;
		y <<= 4;

		sprite.render(screen, x + 8 * shape, y);
		sprite.render(screen, x + 8 * (shape == 0 ? 1 : 0), y + 8);
	}

	@Override
	public boolean tick(Level level, int xt, int yt) {
		return false;
	}
}
