package minicraft.entity.mob;

import minicraft.core.Updater;
import minicraft.core.Updater.Time;
import minicraft.entity.Entity;
import minicraft.graphic.MobSprite;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

public class Firefly extends FlyMob {
	private static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(20, 40);

	private int randomWalkTime;
	private int waitTime;

	/**
	 * Creates a firefly.
	 */
	public Firefly() {
		super(sprites);
	}

	public void tick() {
		Tile tile = level.getTile(x >> 4, y >> 4);

		if (Updater.getTime() == Time.Night) {
			if (random.nextInt(1000) == 0 && (tile.id == Tiles.get("Oak tree").id || tile.id == Tiles.get("Birch tree").id)) {
				remove();
				return;
			}
		} else if (Updater.getTime() == Time.Morning) {
			remove();
			return;
		}

		super.tick();

		int speed = ((tickTime & 0x3) == 3) ? 1 : 0;
		if (!move(xa * speed, ya * speed) || randomWalkTime == 0 || random.nextInt(20) == 0) {
			if (randomWalkTime != 0) {
				waitTime = 20 + random.nextInt(60);
			}
			if (waitTime == 0 || tile.id != Tiles.get("Oak tree").id || tile.id != Tiles.get("Birch tree").id) {
				randomWalkTime = 20;
				xa = (random.nextInt(3) - 1) * random.nextInt(2);
				ya = (random.nextInt(3) - 1) * random.nextInt(2);
			} else {
				xa = ya = 0;
			}
		}
		if (randomWalkTime > 0) {
			randomWalkTime--;
			if (randomWalkTime == 0 && (xa != 0 || ya != 0)) {
				waitTime = 180 + random.nextInt(60);
			}
		} else if (waitTime > 0) {
			waitTime--;
		}
	}

	@Override
	public int getLightRadius() {
		if (random.nextInt(10) == 0) {
			return 1; 
		}
		if (random.nextInt(100) == 0) {
			return 3; 
		}
		return 2;
	}


	@Override
	public boolean blocks(Entity entity) {
		return false;
	}
	
    @Override
    public boolean canSwim() {
        return false;
    }
    
    @Override
    public boolean canWool() {
        return true;
    }

	@Override
	public boolean isSolid() {
		return false;
	}

	public void die() {
		super.die();
	}
}
