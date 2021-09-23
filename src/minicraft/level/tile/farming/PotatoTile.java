package minicraft.level.tile.farming;

import minicraft.entity.Entity;
import minicraft.entity.ItemEntity;
import minicraft.entity.mob.Player;
import minicraft.entity.mob.villager.VillagerMob;
import minicraft.gfx.Screen;
import minicraft.item.Items;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

public class PotatoTile extends Plant {
	public PotatoTile(String name) {
		super(name);
	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		int age = level.getData(x, y);
		int icon = age / (maxAge / 5);

		Tiles.get("Farmland").render(screen, level, x, y);

		screen.render(x * 16 + 0, y * 16 + 0, 13 + 2 * 32 + icon, 0, 1);
		screen.render(x * 16 + 8, y * 16 + 0, 13 + 2 * 32 + icon, 0, 1);
		screen.render(x * 16 + 0, y * 16 + 8, 13 + 2 * 32 + icon, 1, 1);
		screen.render(x * 16 + 8, y * 16 + 8, 13 + 2 * 32 + icon, 1, 1);
	}

	@Override
	protected boolean IfWater(Level level, int xs, int ys) {
		Tile[] areaTiles = level.getAreaTiles(xs, ys, 1);
		for (Tile t : areaTiles)
			if (t == Tiles.get("Water"))
				return true;

		return false;
	}

	@Override
	protected void harvest(Level level, int x, int y, Entity entity) {
	    if (entity instanceof ItemEntity || entity instanceof VillagerMob) return;
		int age = level.getData(x, y);

		int count = 0;
		if (age >= maxAge) {
			count = random.nextInt(3) + 1;
		} else if (age >= maxAge - maxAge / 5) {
			count = random.nextInt(2);
		}

		level.dropItem(x * 16 + 8, y * 16 + 8, count, Items.get("Potato"));

		if (age >= maxAge && entity instanceof Player) {
			((Player) entity).addScore(random.nextInt(4) + 1);
		}

		level.setTile(x, y, Tiles.get("Dirt"));
	}
}