package minicraft.level.tile.farming;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.ItemEntity;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.entity.mob.VillagerMob;
import minicraft.item.Items;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;
import minicraft.level.tile.WaterTile;

public class Plant extends FarmTile {
    protected static int maxAge = 100;

    protected Plant(String name) {
        super(name, null);
    }

    @Override
    public void steppedOn(Level level, int xt, int yt, Entity entity) {
        super.steppedOn(level, xt, yt, entity);
        if (!ifWater(level, xt, yt)) {
        	harvest(level, xt, yt, entity);
        }
    }

    @Override
    public boolean hurt(Level level, int x, int y, Mob source, int hurtDamage, Direction attackDir) {
        harvest(level, x, y, source);
        return true;
    }

    @Override
    public boolean tick(Level level, int xt, int yt) {
        if (random.nextInt(2) == 0) {
            return false;
        }

        int age = level.getData(xt, yt);
        if ((age < maxAge) && random.nextBoolean()) {
            if (!ifWater(level, xt, yt)) {
                level.setData(xt, yt, age + 1);
            } else if (ifWater(level, xt, yt)) {
                level.setData(xt, yt, age + 2);
            }
            return true;
        }
        return false;
    }

    protected boolean ifWater(Level level, int xs, int ys) {
        Tile[] areaTiles = level.getAreaTiles(xs, ys, 3);
        for (Tile tile : areaTiles) {
            if (tile instanceof WaterTile) {
                return true;
            }
        }

        return false;
    }

    /**
     * Default harvest method, used for everything that doesn't really need any
     * special behavior.
     */
    protected void harvest(Level level, int x, int y, Entity entity) {
        if (entity instanceof ItemEntity || entity instanceof VillagerMob) {
            return;
        }
        
        int age = level.getData(x, y);

        int count = 0;
        if (age >= maxAge) {
            count = random.nextInt(3) + 2;
        } else if (age >= maxAge - maxAge / 5) {
            count = random.nextInt(2) + 1;
        }

        level.dropItem((x << 4) + 8, (y << 4) + 8, count, Items.get(name));

        if (age >= maxAge && entity instanceof Player) {
            ((Player) entity).addScore(random.nextInt(5) + 1);
        }
        
        Sound.playAt("genericHurt", x, y);
        
        if (random.nextBoolean()) {
        	level.setTile(x, y, Tiles.get("Dirt"));
        } else {
        	level.setTile(x, y, Tiles.get("Farmland"));
        }
    }
}