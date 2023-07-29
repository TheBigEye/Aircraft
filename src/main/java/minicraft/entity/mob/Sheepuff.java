package minicraft.entity.mob;

import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.graphic.MobSprite;
import minicraft.item.Items;

public class Sheepuff extends SkyMob {
	private static final MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(20, 42);

	/**
	 * Creates a Sheepuff (Floating sheep).
	 */
	public Sheepuff() {
		super(sprites);
	}

	@Override
	public void tick() {
		super.tick();

		followOnHold(3, "Sky Wart", false);
		
		// Sheep sounds
		if (tickTime / 8 % 24 == 0 && random.nextInt(8) == 4) {
			if (random.nextBoolean()) {
				if (!random.nextBoolean()) {
					Sound.sheepSay1.playOnLevel(this.x, this.y);
				} else {
					Sound.sheepSay2.playOnLevel(this.x, this.y);
				}
			} else {
				Sound.sheepSay3.playOnLevel(this.x, this.y);
			}
		}
	}

	@Override
	public void die() {
		int min = 0, max = 0;
		if (Settings.get("diff").equals("Peaceful")) {
			min = 1;
			max = 3;
		}
		if (Settings.get("diff").equals("Easy")) {
			min = 1;
			max = 3;
		}
		if (Settings.get("diff").equals("Normal")) {
			min = 1;
			max = 2;
		}
		if (Settings.get("diff").equals("Hard")) {
			min = 0;
			max = 2;
		}

		dropItem(min, max, Items.get("Raw Beef"));

		super.die();
	}
}