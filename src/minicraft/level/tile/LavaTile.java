package minicraft.level.tile;

import minicraft.entity.Entity;
import minicraft.gfx.ConnectorSprite;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.level.Level;

public class LavaTile extends Tile {
	private ConnectorSprite sprite = new ConnectorSprite(LavaTile.class, new Sprite(12, 9, 3, 3, 1, 3), Sprite.dots(0))
	{
		public boolean connectsTo(Tile tile, boolean isSide) {
			return tile.connectsToFluid;
			//return tile.connectsToLava;
		}
	};
	
	protected LavaTile(String name) {
		super(name, (ConnectorSprite)null);
		super.csprite = sprite;
		connectsToSand = false;
		connectsToFluid = true;
		connectsToLava = false;
	}
	
	public void render(Screen screen, Level level, int x, int y) {
		long seed = (tickCount + (x / 2 - y) * 4311) / 10 * 54687121l + x * 3271612l + y * 3412987161l;
		sprite.full = Sprite.randomDots(seed, 1);
		sprite.sparse.color = DirtTile.dCol(level.depth);
		sprite.render(screen, level, x, y);
	}
	
	public boolean mayPass(Level level, int x, int y, Entity e) {
		return e.canSwim();
	}

	public void tick(Level level, int xt, int yt) {
		int xn = xt;
		int yn = yt;

		if (random.nextBoolean()) xn += random.nextInt(2) * 2 - 1;
		else yn += random.nextInt(2) * 2 - 1;

		if (level.getTile(xn, yn) == Tiles.get("hole")) {
			level.setTile(xn, yn, this);
		}
		if (level.getTile(xn, yn) == Tiles.get("Wood Planks")) {
			level.setTile(xn, yn, this);
	    }
		if (level.getTile(xn, yn) == Tiles.get("Black Wool")) {
			level.setTile(xn, yn, this);
	    }
		if (level.getTile(xn, yn) == Tiles.get("Yellow Wool")) {
			level.setTile(xn, yn, this);
	    }
		if (level.getTile(xn, yn) == Tiles.get("Green Wool")) {
			level.setTile(xn, yn, this);
	    }
		if (level.getTile(xn, yn) == Tiles.get("Blue Wool")) {
			level.setTile(xn, yn, this);
	    }
		if (level.getTile(xn, yn) == Tiles.get("Red Wool")) {
			level.setTile(xn, yn, this);
	    }
		if (level.getTile(xn, yn) == Tiles.get("Wool")) {
			level.setTile(xn, yn, this);
	    }
		if (level.getTile(xn, yn) == Tiles.get("Purple Wool")) {
			level.setTile(xn, yn, this);
	    }
		if (level.getTile(xn, yn) == Tiles.get("Pink Wool")) {
			level.setTile(xn, yn, this);
	    }
		if (level.getTile(xn, yn) == Tiles.get("Dark Green Wool")) {
			level.setTile(xn, yn, this);
	    }
		if (level.getTile(xn, yn) == Tiles.get("Gray Wool")) {
			level.setTile(xn, yn, this);
	    }
		if (level.getTile(xn, yn) == Tiles.get("Brown Wool")) {
			level.setTile(xn, yn, this);
	    }
		if (level.getTile(xn, yn) == Tiles.get("Magenta Wool")) {
			level.setTile(xn, yn, this);
	    }
		if (level.getTile(xn, yn) == Tiles.get("Light Blue Wool")) {
			level.setTile(xn, yn, this);
	    }
		if (level.getTile(xn, yn) == Tiles.get("Cyan Wool")) {
			level.setTile(xn, yn, this);
	    }
		if (level.getTile(xn, yn) == Tiles.get("Orange Wool")) {
			level.setTile(xn, yn, this);
	    }
	}

	public int getLightRadius(Level level, int x, int y) {
		return 6;
	}
}
