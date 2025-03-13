package minicraft.level.tile.farming;

import minicraft.entity.Entity;
import minicraft.entity.ItemEntity;
import minicraft.entity.mob.Player;
import minicraft.entity.mob.VillagerMob;
import minicraft.graphic.Screen;
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
        
		x <<= 4;
		y <<= 4;

        screen.render(x + 0, y + 0, 1 + 40 * 32 + icon, 0, 1);
        screen.render(x + 8, y + 0, 1 + 40 * 32 + icon, 0, 1);
        screen.render(x + 0, y + 8, 1 + 40 * 32 + icon, 1, 1);
        screen.render(x + 8, y + 8, 1 + 40 * 32 + icon, 1, 1);
    }

    @Override
    protected boolean ifWater(Level level, int xs, int ys) {
        Tile[] areaTiles = level.getAreaTiles(xs, ys, 1);
        for (Tile tile : areaTiles) {
            if (tile == Tiles.get("Water")) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void harvest(Level level, int x, int y, Entity entity) {
        if (entity instanceof ItemEntity || entity instanceof VillagerMob) {
            return;
        }
        
        int age = level.getData(x, y);

        int count = 0;
        if (age >= maxAge) {
            count = random.nextInt(3) + 1;
        } else if (age >= maxAge - maxAge / 5) {
            count = random.nextInt(2);
        }

        level.dropItem((x << 4) + 8, (y << 4) + 8, count, Items.get("Potato"));

        if (age >= maxAge && entity instanceof Player) {
            ((Player) entity).addScore(random.nextInt(4) + 1);
        }

        if (random.nextBoolean()) {
        	level.setTile(x, y, Tiles.get("Dirt"));
        } else {
        	level.setTile(x, y, Tiles.get("Farmland"));
        }
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