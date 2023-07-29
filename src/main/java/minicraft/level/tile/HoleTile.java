package minicraft.level.tile;

import minicraft.entity.Entity;
import minicraft.graphic.ConnectorSprite;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.level.Level;

public class HoleTile extends Tile {
	private static ConnectorSprite sprite = new ConnectorSprite(HoleTile.class, new Sprite(9, 33, 3, 3, 1), new Sprite(12, 33, 2, 2, 1)) {
		
		@Override
		public boolean connectsTo(Tile tile, boolean isSide) {
			return tile.connectsToLiquid();
		}
	};

	protected HoleTile(String name) {
		super(name, sprite);
		connectsToSand = false;
		connectsToFluid = true;
	}

	public boolean mayPass(Level level, int x, int y, Entity entity) {
		return entity.canSwim();
	}

	public void render(Screen screen, Level level, int x, int y) {
		sprite.sparse.color = DirtTile.dirtColor(level.depth);
		sprite.render(screen, level, x, y);
	}
}
