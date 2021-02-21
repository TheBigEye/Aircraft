package minicraft.entity.mob;

import minicraft.core.io.Settings;
import minicraft.gfx.MobSprite;
import minicraft.item.Items;

public class SlimyWizard extends EnemyMob {
	private static MobSprite[][][] sprites;
	static {
		sprites = new MobSprite[4][4][2];
		for (int i = 0; i < 4; i++) {
			MobSprite[][] list  = MobSprite.compileMobSpriteAnimations(16, 20 + (i * 2));
			sprites[i] = list;
		}
	}
	
	/**
	 * Creates a Slimy wizard of the given level.
	 * @param lvl Boss level.
	 */
	public SlimyWizard(int lvl) {
		super(lvl, sprites, 5, 100);
	}
	
	@Override
	public void tick() {
		super.tick();
		
		Player player = getClosestPlayer();
		if (player != null && player != null) {
			int xd = player.x - x;
			int yd = player.y - y;
				int sig0 = 1;
				xa = ya = 0;
				if (xd < sig0) xa = -1;
				if (xd > sig0) xa = +1;
				if (yd < sig0) ya = -1;
				if (yd > sig0) ya = +1;
			} else {
				randomizeWalkDir(false);
			}
		
	}
	
	public void die() {
		if (Settings.get("diff").equals("Passive")) dropItem(2, 30, Items.get("slime"));
		if (Settings.get("diff").equals("Easy")) dropItem(2, 30, Items.get("slime"));
		if (Settings.get("diff").equals("Normal")) dropItem(2, 20, Items.get("slime"));
		if (Settings.get("diff").equals("Hard")) dropItem(1, 10, Items.get("slime"));
		

			level.dropItem(x, y, Items.get("Sticky essence"));
			level.dropItem(x, y, Items.get("Sticky essence"));
			level.dropItem(x, y, Items.get("Sticky essence"));
			level.dropItem(x, y, Items.get("green clothes"));

		
		if(random.nextInt(40) == 19) {
			int rand = random.nextInt(3);
			if(rand == 0) {
				level.dropItem(x, y, Items.get("green clothes"));
			} else if(rand == 1) {
				level.dropItem(x, y, Items.get("green clothes"));
			} else if(rand == 2) {
				level.dropItem(x, y, Items.get("slime"));
			}
		}
		
		super.die();
	}
}

