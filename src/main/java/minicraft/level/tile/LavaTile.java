package minicraft.level.tile;

import minicraft.entity.Entity;
import minicraft.gfx.ConnectorSprite;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.level.Level;

public class LavaTile extends Tile {
	private ConnectorSprite sprite = new ConnectorSprite(LavaTile.class, new Sprite(12, 9, 3, 3, 1), Sprite.dots(0)) {
		public boolean connectsTo(Tile tile, boolean isSide) {
			return tile.connectsToFluid;
		}
	};

	protected LavaTile(String name) {
		super(name, (ConnectorSprite) null);
		super.connectorSprite = sprite;
		connectsToSand = false;
		connectsToFluid = true;
		connectsToLava = false;
	}

	public int getLightRadius(Level level, int x, int y) {
		return 5;
	}

	public boolean mayPass(Level level, int x, int y, Entity entity) {
		return entity.canSwim();
	}

	public void render(Screen screen, Level level, int x, int y) {
		long seed = ((tickCount + (x / 2 - y) * 4311) / 10) * 54687121L + x * 3271612L + y * 3412987161L;
		sprite.full = Sprite.randomDots(seed, 1);
		sprite.sparse.color = DirtTile.dCol(level.depth);
		sprite.render(screen, level, x, y);
	}

	public boolean tick(Level level, int xt, int yt) {
		int xn = xt;
		int yn = yt;

		if (random.nextBoolean()) {
			xn += random.nextInt(2) * 2 - 1;
		} else {
			yn += random.nextInt(2) * 2 - 1;
		}

		if (level.getTile(xn, yn) == Tiles.get("Hole")) {
			level.setTile(xn, yn, this);
		}
		return false;
	}
}
