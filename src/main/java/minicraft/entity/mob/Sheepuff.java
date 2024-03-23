package minicraft.entity.mob;

import minicraft.core.io.Settings;
import minicraft.graphic.MobSprite;
import minicraft.item.Items;

public class Sheepuff extends SkyMob {
	private static final MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(20, 42);
	private static final String[] sounds = new String[] {"sheepSay1", "sheepSay2", "sheepSay3"};

	/**
	 * Creates a Sheepuff (Floating sheep).
	 */
	public Sheepuff() {
		super(sprites);
	}

	@Override
	public void tick() {
		super.tick();
		
		followOnHold(Items.get("Sky Wart"), 3);
		
		if ((tickTime % (random.nextInt(100) + 120) == 0)) {
			doPlaySound(sounds, 8);
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