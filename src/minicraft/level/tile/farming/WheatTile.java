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

public class WheatTile extends Plant {

	public WheatTile(String name) {
		super(name);
	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		int age = level.getData(x, y);
		int icon = age / (maxAge / 5);

		Tiles.get("Farmland").render(screen, level, x, y);

		screen.render(x * 16 + 0, y * 16 + 0, 13 + 0 * 32 + icon, 0, 1);
		screen.render(x * 16 + 8, y * 16 + 0, 13 + 0 * 32 + icon, 0, 1);
		screen.render(x * 16 + 0, y * 16 + 8, 13 + 0 * 32 + icon, 1, 1);
		screen.render(x * 16 + 8, y * 16 + 8, 13 + 0 * 32 + icon, 1, 1);
	}

	protected boolean IfWater(Level level, int xs, int ys) {
		Tile[] areaTiles = level.getAreaTiles(xs, ys, 3);
		for (Tile t : areaTiles)
			if (t == Tiles.get("Water"))
				return true;

		return false;
	}
	
	@Override
	protected void harvest(Level level, int x, int y, Entity entity) {
	    if (entity instanceof ItemEntity || entity instanceof VillagerMob) return;
		int age = level.getData(x, y);

		level.dropItem(x*16+8, y*16+8, 1, 2, Items.get("seeds"));
		
		int count = 0;
		if (age >= maxAge) {
			count = random.nextInt(3) + 2;
		} else if (age >= maxAge - maxAge / 5) {
			count = random.nextInt(2);
		}

		level.dropItem(x * 16 + 8, y * 16 + 8, count, Items.get("Wheat"));

		if (age >= maxAge && entity instanceof Player) {
			((Player) entity).addScore(random.nextInt(4) + 1);
		}

		level.setTile(x, y, Tiles.get("Dirt"));
	}

}