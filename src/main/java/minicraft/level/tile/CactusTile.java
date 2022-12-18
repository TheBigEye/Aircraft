package minicraft.level.tile;

import minicraft.core.Game;
import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Mob;
import minicraft.entity.particle.SmashParticle;
import minicraft.entity.particle.TextParticle;
import minicraft.gfx.Color;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.item.Items;
import minicraft.level.Level;

public class CactusTile extends Tile {
    private static Sprite sprite = new Sprite(6, 0, 2, 2, 1);

    protected CactusTile(String name) {
        super(name, sprite);
        connectsToSand = true;
    }

    private final String baseTile = "Sand";

    @Override
    public boolean mayPass(Level level, int x, int y, Entity e) {
        return false;
    }

    @Override
    public boolean hurt(Level level, int x, int y, Mob source, int dmg, Direction attackDir) {
        int damage = level.getData(x, y) + dmg;
        int cHealth = 10;
        
        if (Game.isMode("Creative")) {
            dmg = damage = cHealth;
        }
        
        level.add(new SmashParticle(x * 16, y * 16));
        level.add(new TextParticle("" + dmg, x * 16 + 8, y * 16 + 8, Color.RED));

        if (damage >= cHealth) {
            level.setTile(x, y, Tiles.get(baseTile));
            Sound.genericHurt.playOnWorld(x * 16, y * 16);
            level.dropItem(x * 16 + 8, y * 16 + 8, 2, 4, Items.get("Cactus"));
        } else {
            level.setData(x, y, damage);
        }
        return true;
    }

    @Override
    public void render(Screen screen, Level level, int x, int y) {
        Tiles.get(baseTile).render(screen, level, x, y);

        sprite.render(screen, x << 4, y << 4);
    }

    @Override
    public void bumpedInto(Level level, int x, int y, Entity entity) {
        if (!(entity instanceof Mob) || Settings.get("diff").equals("Peaceful")) {
            return; // Cannot do damage
        }
        
        if (entity instanceof Mob) {
            ((Mob) entity).hurt(this, x, y, 1 + Settings.getIdx("diff"));
        }
    }

    @Override
    public boolean tick(Level level, int xt, int yt) {
        int damage = level.getData(xt, yt);
        if (damage > 0) {
            level.setData(xt, yt, damage - 1);
            return true;
        }

        if (Game.IS_April_fools == true) { // April fools texture :)
            sprite = new Sprite(0, 44, 2, 2, 1);
            return true;
        }

        return false;
    }
}
