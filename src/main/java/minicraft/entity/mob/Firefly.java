package minicraft.entity.mob;

import minicraft.core.Updater;
import minicraft.gfx.MobSprite;

public class Firefly extends FlyMob {
	private static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(20, 40);

	private int lightRadius = 3;

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
			sprites = MobSprite.compileMobSpriteAnimations(20, 40); 
			lightRadius = 2;
			
		} else {
			// Light OFF	
			if (tickTime / 8 % 2 == 0 && lightRadius > 0) {
				lightRadius--;
			}

			if (lightRadius == 0) {
				sprites = MobSprite.compileMobSpriteAnimations(20, 38); 	
				lightRadius = 0; // check

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
		return lightRadius;
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
