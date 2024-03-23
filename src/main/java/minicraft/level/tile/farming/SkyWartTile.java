package minicraft.level.tile.farming;

import minicraft.core.io.Sound;
import minicraft.entity.Entity;
import minicraft.entity.ItemEntity;
import minicraft.entity.mob.Player;
import minicraft.graphic.Screen;
import minicraft.item.Items;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

public class SkyWartTile extends SkyPlant {

    public SkyWartTile(String name) {
        super(name);
    }

    @Override
    public void render(Screen screen, Level level, int x, int y) {
        int age = level.getData(x, y);
        int icon = age / (maxAge / 5);

        Tiles.get("Sky Farmland").render(screen, level, x, y);

        screen.render((x << 4) + 0, (y << 4) + 0, 10 + 38 * 32 + icon, 0, 1);
        screen.render((x << 4) + 8, (y << 4) + 0, 10 + 38 * 32 + icon, 0, 1);
        screen.render((x << 4) + 0, (y << 4) + 8, 10 + 38 * 32 + icon, 1, 1);
        screen.render((x << 4) + 8, (y << 4) + 8, 10 + 38 * 32 + icon, 1, 1);
    }

    @Override
    protected boolean ifCloud(Level level, int xs, int ys) {
        Tile[] areaTiles = level.getAreaTiles(xs, ys, 3);
        for (Tile tile : areaTiles) {
            if (tile == Tiles.get("Sky grass")) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void harvest(Level level, int x, int y, Entity entity) {
        if (entity instanceof ItemEntity)
            return;
        int age = level.getData(x, y);

        int count = 0;
        if (age >= maxAge) {
            count = random.nextInt(3) + 2;
        } else if (age >= maxAge - maxAge / 5) {
            count = random.nextInt(2);
        }
        
        Sound.playAt("genericHurt", x, y);

        level.dropItem((x << 4) + 8, (y << 4) + 8, count, Items.get("Sky wart"));

        if (age >= maxAge && entity instanceof Player) {
            ((Player) entity).addScore(random.nextInt(4) + 1);
        }

        level.setTile(x, y, Tiles.get("Sky farmland"));
    }

}
