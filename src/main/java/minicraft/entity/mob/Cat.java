package minicraft.entity.mob;

import minicraft.core.io.Settings;
import minicraft.graphic.MobSprite;
import minicraft.item.Items;

public class Cat extends PassiveMob {
	private static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(30, 6);

	/**
	 * Creates a Cat.
	 */
	public Cat() {
		super(sprites);
	}

	public void tick() {
	    super.tick();

	    // follows to the player if holds raw fish
	    followOnHold(Items.get("Raw Fish"), 2);
	}

	@Override
	public void die() {
		int min = 0;
		int max = 0;

		if (Settings.get("diff").equals("Peaceful")) { min = 1; max = 3; }
		if (Settings.get("diff").equals("Easy")) { min = 1; max = 2; }
		if (Settings.get("diff").equals("Normal")) { min = 1; max = 2; }
		if (Settings.get("diff").equals("Hard")) { min = 0; max = 1; }

		dropItem(min, max, Items.get("Raw Fish"));

		super.die();
	}
}