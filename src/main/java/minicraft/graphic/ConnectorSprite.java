package minicraft.graphic;

import minicraft.level.Level;
import minicraft.level.tile.ConnectTile;
import minicraft.level.tile.Tile;

public class ConnectorSprite {
	/**
	 * This class is meant for those tiles that look different when they are
	 * touching other tiles of their type; aka, they "connect" to them.
	 * 
	 * Since I think connecting tile sprites tend to have three color categories,
	 * maybe this should have two extra colors..?
	 * 
	 * This class will need to keep rack of the following sprites: -a sprite for
	 * each kind of intersection; aka a 3x3 grid of sprite pixels, that show the
	 * sprite for each position, totally surrounded, nothing of left, etc.
	 * 
	 */

	public Sprite sparse, sides, full;
	private Class<? extends Tile> owner;
	private boolean checkCorners;

	public ConnectorSprite(Class<? extends Tile> owner, Sprite sparse, Sprite sides, Sprite full) {
		this(owner, sparse, sides, full, true);
	}

	public ConnectorSprite(Class<? extends Tile> owner, Sprite sparse, Sprite sides, Sprite full, boolean cornersMatter) {
		this.owner = owner;
		this.sparse = sparse;
		this.sides = sides;
		this.full = full;
		this.checkCorners = cornersMatter;
	}

	public ConnectorSprite(Class<? extends Tile> owner, Sprite sparse, Sprite full) {
		this(owner, sparse, sparse, full, false);
	}

	public void render(Screen screen, Level level, int x, int y) {
		render(screen, level, x, y, -1);
	}

	public void render(Screen screen, Level level, int x, int y, int whiteTint) {
		// System.out.println("rendering sprite for tile " + owner);

		Tile upTile = level.getTile(x, y - 1);
		Tile downTile = level.getTile(x, y + 1);
		Tile leftTile = level.getTile(x - 1, y);
		Tile rightTile = level.getTile(x + 1, y);

		boolean up = connectsToDoEdgeCheck(upTile, true);
		boolean down = connectsToDoEdgeCheck(downTile, true);
		boolean left = connectsToDoEdgeCheck(leftTile, true);
		boolean right = connectsToDoEdgeCheck(rightTile, true);

		boolean upLeft = connectsToDoEdgeCheck(level.getTile(x - 1, y - 1), false);
		boolean downLeft = connectsToDoEdgeCheck(level.getTile(x - 1, y + 1), false);
		boolean upRight = connectsToDoEdgeCheck(level.getTile(x + 1, y - 1), false);
		boolean downRight = connectsToDoEdgeCheck(level.getTile(x + 1, y + 1), false);

		x <<= 4;
		y <<= 4;

		if (up && left) {
			if (upLeft || !checkCorners) full.renderPixel(0, 0, screen, x, y);
			else sides.renderPixel(1, 1, screen, x, y);
		} else {
			sparse.renderPixel(left ? 1 : 0, up ? 1 : 0, screen, x, y);
		}

		if (up && right) {
			if (upRight || !checkCorners) full.renderPixel(1, 0, screen, x + 8, y);
			else sides.renderPixel(0, 1, screen, x + 8, y);
		} else {
			sparse.renderPixel(right ? 1 : 2, up ? 1 : 0, screen, x + 8, y);
		}

		if (down && left) {
			if (downLeft || !checkCorners) full.renderPixel(0, 1, screen, x, y + 8);
			else sides.renderPixel(1, 0, screen, x, y + 8);
		} else {
			sparse.renderPixel(left ? 1 : 0, down ? 1 : 2, screen, x, y + 8);
		}

		if (down && right) {
			if (downRight || !checkCorners) full.renderPixel(1, 1, screen, x + 8, y + 8);
			else sides.renderPixel(0, 0, screen, x + 8, y + 8);
		} else {
			sparse.renderPixel(right ? 1 : 2, down ? 1 : 2, screen, x + 8, y + 8);
		}

	}

	// it is expected that some tile classes will override this on class instantiation.
	public boolean connectsTo(Tile tile, boolean isSide) {
		// System.out.println("original connection check");
		return tile.getClass() == owner;
	}

	public boolean connectsToDoEdgeCheck(Tile tile, boolean isSide) {
		if (tile.getClass() == ConnectTile.class) {
			return true;
		} else {
			return connectsTo(tile, isSide);
		}
	}

	public static Sprite makeSprite(int w, int h, int mirror, boolean repeat, int... coords) {
		return makeSprite(w, h, mirror, 1, repeat, coords);
	}

	public static Sprite makeSprite(int w, int h, int mirror, int sheet, boolean repeat, int... coords) {
		Sprite.Px[][] pixels = new Sprite.Px[h][w];
		int i = 0;
		for (int row = 0; row < h && i < coords.length; row++) {
			for (int column = 0; column < w && i < coords.length; column++) {
				int pos = coords[i];
				pixels[row][column] = new Sprite.Px(pos % 32, pos / 32, mirror, sheet);
				i++;
				if (i == coords.length && repeat) i = 0;
			}
		}

		return new Sprite(pixels);
	}
}
