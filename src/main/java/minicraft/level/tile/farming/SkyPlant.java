package minicraft.level.tile.farming;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.ItemEntity;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.graphic.Sprite;
import minicraft.item.Items;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

public class SkyPlant extends SkyFarmTile {
    protected static int maxAge = 100;
    private String name;

    protected SkyPlant(String name) {
        super(name, (Sprite) null);
        this.name = name;
    }

    @Override
    public void steppedOn(Level level, int xt, int yt, Entity entity) {
        if (entity instanceof ItemEntity)
            return;
        if (random.nextInt(60) != 0)
            return;
        if (level.getData(xt, yt) < 5)
            return;
        // super.steppedOn(level, xt, yt, entity);
        harvest(level, xt, yt, entity);
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
        if (age < maxAge) {
            if (!ifCloud(level, xt, yt)) {
                level.setData(xt, yt, age + 1);
            } else if (ifCloud(level, xt, yt)) {
                level.setData(xt, yt, age + 2);
            }
            return true;
        }

        return false;
    }

    protected boolean ifCloud(Level level, int xs, int ys) {
        Tile[] areaTiles = level.getAreaTiles(xs, ys, 1);
        for (Tile tile : areaTiles) {
            if (tile == Tiles.get("Cloud")) {
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
        if (entity instanceof ItemEntity)
            return;
        int age = level.getData(x, y);

        int count = 0;
        if (age >= maxAge) {
            count = random.nextInt(3) + 2;
        } else if (age >= maxAge - maxAge / 5) {
            count = random.nextInt(2) + 1;
        }
        
        Sound.playAt("genericHurt", x << 4, y << 4);

        level.dropItem((x << 4) + 8, (y << 4) + 8, count, Items.get(name));

        if (age >= maxAge && entity instanceof Player) {
            ((Player) entity).addScore(random.nextInt(5) + 1);
        }

        level.setTile(x, y, Tiles.get("Dirt"));
    }
}