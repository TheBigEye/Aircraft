package minicraft.level.tile;

import minicraft.entity.Entity;
import minicraft.gfx.ConnectorSprite;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.level.Level;

public class CloudHoleTile extends Tile {
    private static ConnectorSprite sprite = new ConnectorSprite(CloudHoleTile.class, new Sprite(24, 9, 3, 3, 1), new Sprite(27, 9, 2, 2, 1)) {
        public boolean connectsTo(Tile tile, boolean isSide) {
            return tile.connectsToLiquid();
        }
    };

    protected CloudHoleTile(String name) {
        super(name, sprite);
        connectsToSand = false;
        connectsToFluid = true;
    }

    public void render(Screen screen, Level level, int x, int y) {
    	Tiles.get("Cloud").render(screen, level, x, y);
        sprite.render(screen, level, x, y);
    }

    public boolean mayPass(Level level, int x, int y, Entity e) {
        return e.canSwim();
    }
}