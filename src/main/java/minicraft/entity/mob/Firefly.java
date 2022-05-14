package minicraft.entity.mob;

import minicraft.core.Updater;
import minicraft.gfx.MobSprite;

public class Firefly extends FlyMob {
	private static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(68, 68);

	private int lightRadious = 3;

	/**
	 * Creates a firefly.
	 */
	public Firefly() {
		super(sprites);
	}

	public void tick() {
		super.tick();

		// Light ON
		if (Updater.getTime() == Updater.Time.Night) {
			sprites = MobSprite.compileMobSpriteAnimations(68, 68); 
			lightRadious = 2;
			
		} else {
			// Light OFF	
			if (tickTime / 8 % 2 == 0 && lightRadious > 0) {
				lightRadious--;
			}

			if (lightRadious == 0) {
				sprites = MobSprite.compileMobSpriteAnimations(68, 66); 	
				lightRadious = 0; // check

				if (tickTime / 8 % 2 == 0) {
					if (random.nextInt(32) == 4) {
						this.remove();
					}
				}
			}
		}

	}

	@Override
	public int getLightRadius() {
		return lightRadious;
	}
	
    @Override
    public boolean canSwim() {
        return false;
    }

	@Override
	public boolean isSolid() {
		return false;
	}

	public void die() {
		super.die();
	}
}
