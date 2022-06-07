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

public class IceSpikeTile extends Tile {
    private static Sprite sprite = new Sprite(4, 12, 1);

    protected IceSpikeTile(String name) {
        super(name, sprite);
        connectsToSnow = true;
    }

    public boolean mayPass(Level level, int x, int y, Entity e) {
        return false;
    }

    @SuppressWarnings("unused")
    public boolean hurt(Level level, int x, int y, Mob source, int dmg, Direction attackDir) {
        int damage = level.getData(x, y) + dmg;
        int cHealth = 10;
        if (Game.isMode("creative"))
            dmg = damage = cHealth;
        level.add(new SmashParticle(x * 16, y * 16));
        level.add(new TextParticle("" + dmg, x * 16 + 8, y * 16 + 8, Color.BLUE));

        if (damage >= cHealth) {
            level.setTile(x, y, Tiles.get("snow"));
            Sound.Tile_generic_hurt.play();
            level.dropItem(x * 16 + 8, y * 16 + 8, 2, 4, Items.get("Icicle"));
        } else {
            level.setData(x, y, damage);
        }
        return true;
    }

    @Override
    public void render(Screen screen, Level level, int x, int y) {
        Tiles.get("Snow").render(screen, level, x, y);

        int data = level.getData(x, y);
        int shape = (data / 16) % 2;

        x = x << 4;
        y = y << 4;

        sprite.render(screen, x + 8 * shape, y);
        sprite.render(screen, x + 8 * (shape == 0 ? 1 : 0), y + 8);
    }

    public void bumpedInto(Level level, int x, int y, Entity entity) {
        if (!(entity instanceof Mob))
            return;
        Mob m = (Mob) entity;
        if (Settings.get("diff").equals("Easy")) {
            m.hurt(this, x, y, 2);
        }
        if (Settings.get("diff").equals("Normal")) {
            m.hurt(this, x, y, 3);
        }
        if (Settings.get("diff").equals("Hard")) {
            m.hurt(this, x, y, 5);
        }
    }

    public boolean tick(Level level, int xt, int yt) {
        int damage = level.getData(xt, yt);
        if (damage > 0) {
            level.setData(xt, yt, damage - 1);
            return true;
        }
        return false;
    }
}
