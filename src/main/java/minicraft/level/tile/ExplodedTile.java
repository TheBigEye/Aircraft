package minicraft.level.tile;

import minicraft.entity.Entity;
import minicraft.graphic.ConnectorSprite;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.level.Level;

/// This class is for tiles WHILE THEY ARE EXPLODING
public class ExplodedTile extends Tile {
	private static ConnectorSprite sprite = new ConnectorSprite(ExplodedTile.class, new Sprite(0, 33, 3, 3, 1), new Sprite(3, 33, 2, 2, 1)) {
		@Override
		public boolean connectsTo(Tile tile, boolean isSide) {
			return !isSide || tile.connectsToLiquid();
		}
	};

	protected ExplodedTile(String name) {
		super(name, sprite);
		connectsToSand = true;
		connectsToFluid = true;
	}

	@Override
	public boolean mayPass(Level level, int x, int y, Entity entity) {
		return true;
	}
	
    public void render(Screen screen, Level level, int x, int y) {
    	switch (level.depth) {
			case 1: Tiles.get("Infinite fall").render(screen, level, x, y); break; // Sky.
	        case 0: Tiles.get("Hole").render(screen, level, x, y); break; // surface.
	        case -4: Tiles.get("Hole").render(screen, level, x, y); break; // dungeons.
	        case 2: Tiles.get("Infinite fall").render(screen, level, x, y); break; // the void.
	        default: Tiles.get("Hole").render(screen, level, x, y); break; // caves.
    	}
        sprite.render(screen, level, x, y);
    }

	@Override
	public int getLightRadius(Level level, int x, int y) {
		return 3;
	}
}
