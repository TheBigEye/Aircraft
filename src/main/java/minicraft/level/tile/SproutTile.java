package minicraft.level.tile;

import minicraft.entity.Direction;
import minicraft.entity.mob.Mob;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.level.Level;

public class SproutTile extends Tile {
	private static Sprite sprite = new Sprite(4, 13, 1);

	private Tile onType;
	private Tile growsTo;

	protected SproutTile(String name, Tile onType, Tile growsTo) {
		super(name, sprite);
		this.onType = onType;
		this.growsTo = growsTo;
		
		connectsToSand = onType.connectsToSand;
		connectsToGrass = onType.connectsToGrass;
		connectsToSnow = onType.connectsToSnow;
		connectsToFluid = onType.connectsToFluid;

		maySpawn = true;
	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		onType.render(screen, level, x, y);

        x <<= 4;
        y <<= 4;

        sprite.render(screen, x + 8 * 0, y);
        sprite.render(screen, x + 8 * 1, y + 8);
	}

	@Override
	public boolean tick(Level level, int x, int y) {
		int age = level.getData(x, y) + 1;
		// Don't grow if there is an entity on this tile.
		if (age > 100) {
			level.setTile(x, y, growsTo); // TODO: add grow sound
		} else {
			level.setData(x, y, age);
		}
		return true;
	}

	@Override
	public boolean hurt(Level level, int x, int y, Mob source, int hurtDamage, Direction attackDir) {
		level.setTile(x, y, onType);
		return true;
	}
}
