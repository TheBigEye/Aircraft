package minicraft.level.tile;

import minicraft.entity.Entity;
import minicraft.gfx.ConnectorSprite;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.level.Level;

public class WaterTile extends Tile {
	private ConnectorSprite sprite = new ConnectorSprite(WaterTile.class, new Sprite(12, 6, 3, 3, 1), Sprite.dots(0)) {
		@Override
		public boolean connectsTo(Tile tile, boolean isSide) {
			return tile.connectsToFluid;
		}
	};

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
	    long seed = ((tickCount + (x / 2 - y) * 4311) / 10) * 54687121L + x * 3271612L + y * 3412987161L;
	    sprite.full = Sprite.randomDots(seed, 0);
	    sprite.sparse.color = DirtTile.dCol(level.depth);
	    sprite.render(screen, level, x, y);
	}

	@Override
	public boolean tick(Level level, int xt, int yt) {
	    int xn = xt;
	    int yn = yt;

	    switch (random.nextInt(2)) {
	        case 0: xn += random.nextInt(2) * 2 - 1; break;
	        case 1: yn += random.nextInt(2) * 2 - 1; break;
	    }

	    if (level.getTile(xn, yn) == Tiles.get("Hole")) {
	        level.setTile(xn, yn, this);
	    }

	    Tile lavaTile = Tiles.get("Lava");
	    Tile rawObsidianTile = Tiles.get("raw obsidian");
	    for (int x = -1; x < 2; x++) {
	        if (level.getTile(xt + x, yt) == lavaTile) {
	            level.setTile(xt + x, yt, rawObsidianTile);
	        }
	    }
	    for (int y = -1; y < 2; y++) {
	        if (level.getTile(xt, yt + y) == lavaTile) {
	            level.setTile(xt, yt + y, rawObsidianTile);
	        }
	    }
	    return false;
	}
}