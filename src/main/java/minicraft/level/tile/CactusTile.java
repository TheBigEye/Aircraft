package minicraft.level.tile;

import minicraft.core.Game;
import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Mob;
import minicraft.entity.particle.SmashParticle;
import minicraft.entity.particle.TextParticle;
import minicraft.graphic.Color;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.item.Items;
import minicraft.level.Level;

public class CactusTile extends Tile {
    private static final Sprite sprite = new Sprite(65, 0, 2, 2, 1);

    protected CactusTile(String name) {
        super(name, sprite);
        connectsToSand = true;
    }

    @Override
    public boolean mayPass(Level level, int x, int y, Entity entity) {
        return false;
    }

    @Override
    public boolean hurt(Level level, int x, int y, Mob source, int hurtDamage, Direction attackDir) {
        int damage = level.getData(x, y) + hurtDamage;
        int cactusHealth = 10;

        if (Game.isMode("Creative")) {
        	hurtDamage = damage = cactusHealth;
        }

        level.add(new SmashParticle(x << 4, y << 4));
        level.add(new TextParticle("" + hurtDamage, (x << 4) + 8, (y << 4) + 8, Color.RED));

        if (damage >= cactusHealth) {
        	Sound.playAt("genericHurt", x << 4, y << 4);
            level.setTile(x, y, Tiles.get("Sand"));
            level.dropItem((x << 4) + 8, (y << 4) + 8, 2, 4, Items.get("Cactus"));
        } else {
            level.setData(x, y, damage);
        }
        return true;
    }

    @Override
    public void render(Screen screen, Level level, int x, int y) {
    	Tiles.get("Sand").render(screen, level, x, y);
        sprite.render(screen, x << 4, y << 4);
    }

    @Override
    public void bumpedInto(Level level, int x, int y, Entity entity) {
        if (!(entity instanceof Mob) || Settings.get("diff").equals("Peaceful")) {
            return; // Cannot do damage
        }

        ((Mob) entity).hurt(this, x, y, 1 + Settings.getIndex("diff"));
    }

    @Override
    public boolean tick(Level level, int xt, int yt) {
        int damage = level.getData(xt, yt);
        if (damage > 0) {
            level.setData(xt, yt, damage - 1);
            return true;
        }

        return false;
    }
}
