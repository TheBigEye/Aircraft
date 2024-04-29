package minicraft.level.tile.farming;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.ItemEntity;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.entity.mob.VillagerMob;
import minicraft.graphic.Screen;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

public class CarrotTile extends Plant {

	public CarrotTile(String name) {
		super(name);
	}

	@Override
	protected void harvest(Level level, int x, int y, Entity entity) {
		int age = level.getData(x, y);

		int count = 0;
		if (age >= 50) {
			count = random.nextInt(3) + 1;
		} else if (age >= 40) {
			count = random.nextInt(2) + 0;
		}

		level.dropItem((x << 4) + 8, (y << 4) + 8, count, Items.get("Carrot"));

		if (age >= 50 && entity instanceof Player) {
			((Player) entity).addScore(random.nextInt(5) + 1);
		}
		
		Sound.playAt("genericHurt", x << 4, y << 4);

        if (random.nextBoolean()) {
        	level.setTile(x, y, Tiles.get("Dirt"));
        } else {
        	level.setTile(x, y, Tiles.get("Farmland"));
        }
	}

	@Override
	public boolean hurt(Level level, int x, int y, Mob source, int hurtDamage, Direction attackDir) {
		harvest(level, x, y, source);
		return true;
	}

	@Override
	public boolean ifWater(Level level, int xs, int ys) {
		Tile[] areaTiles = level.getAreaTiles(xs, ys, 2);
		for (Tile tile : areaTiles) {
			if (tile == Tiles.get("Water")) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Shovel) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					Sound.playAt("genericHurt", xt << 4, yt << 4);
					level.setTile(xt, yt, Tiles.get("Dirt"));
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		int age = level.getData(x, y);
		int icon = age / (maxAge / 5);

		Tiles.get("Farmland").render(screen, level, x, y);

		screen.render((x << 4) + 0, (y << 4) + 0, 0 + 39 * 32 + icon, 0, 1);
		screen.render((x << 4) + 8, (y << 4) + 0, 0 + 39 * 32 + icon, 0, 1);
		screen.render((x << 4) + 0, (y << 4) + 8, 0 + 39 * 32 + icon, 1, 1);
		screen.render((x << 4) + 8, (y << 4) + 8, 0 + 39 * 32 + icon, 1, 1);
	}

	@Override
	public void steppedOn(Level level, int xt, int yt, Entity entity) {
		if (random.nextInt(60) != 0 || entity instanceof VillagerMob || entity instanceof ItemEntity) {
			return;
		}
		
		if (level.getData(xt, yt) < 2) {
			return;
		}
		
        if (!ifWater(level, xt, yt)) {
        	harvest(level, xt, yt, entity);
        }
	}
}