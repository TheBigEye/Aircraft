package minicraft.level.tile;

import minicraft.entity.Entity;
import minicraft.graphic.ConnectorSprite;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.level.Level;

public class WaterTile extends Tile {
    private static final ConnectorSprite sprite;

    static {
        sprite = new ConnectorSprite(WaterTile.class, new Sprite(18, 21, 3, 3, 1), Sprite.dots(0)) {
            @Override
            public boolean connectsTo(Tile tile, boolean isSide) {
                return tile.connectsToFluid;
            }
        };
    }
	
	protected WaterTile(String name) {
		super(name, (ConnectorSprite) null);
		connectorSprite = sprite;
		connectsToFluid = true;
	}

	@Override
	public boolean mayPass(Level level, int x, int y, Entity entity) {
		return entity.canSwim();
	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		long seed = ((tickCount + (x / 2 - y) * 4311) / 10);
	    sprite.full = Sprite.randomDots(seed, 21, 21);
	    sprite.sparse.color = DirtTile.dirtColor(level.depth);
	    sprite.render(screen, level, x, y);
	}

	@Override
	public boolean tick(Level level, int xt, int yt) {
	    int xn = xt;
	    int yn = yt;
	    
	    switch (random.nextInt(2)) {
	        case 0: xn += (random.nextInt(2) << 1) - 1; break;
	        case 1: yn += (random.nextInt(2) << 1) - 1; break;
	    }

	    if (level.getTile(xn, yn) instanceof HoleTile) {
	        level.setTile(xn, yn, this);
	    }

	    for (int x = -1; x < 2; x++) {
	        if (level.getTile(xt + x, yt) instanceof LavaTile) {
	            level.setTile(xt + x, yt, Tiles.get("Raw Obsidian"));
	        }
	    }
	    for (int y = -1; y < 2; y++) {
	        if (level.getTile(xt, yt + y) instanceof LavaTile) {
	            level.setTile(xt, yt + y, Tiles.get("Raw Obsidian"));
	        }
	    }
	    return false;
	}
}