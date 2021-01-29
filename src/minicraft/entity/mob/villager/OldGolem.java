package minicraft.entity.mob.villager;

import minicraft.core.io.Settings;
import minicraft.entity.mob.EnemyMob;
import minicraft.gfx.MobSprite;
import minicraft.item.Items;

public class OldGolem extends EnemyMob {
	private static MobSprite[][][] sprites;
	static {
		sprites = new MobSprite[4][4][2];
		for (int i = 0; i < 4; i++) {
			MobSprite[][] list  = MobSprite.compileMobSpriteAnimations(24, 0 );
			sprites[i] = list;
		}
	}
	
	/**
	 * Creates a bad golem of the given level.
	 * @param lvl Zombie's level.
	 */
	public OldGolem(int lvl) {
		super(lvl, sprites, 1, 100);
	}
	
	@Override
	public void tick() {
		super.tick();
	}
	
	public void die() {
		if (Settings.get("diff").equals("Easy")) dropItem(2, 4, Items.get("gear"));
		if (Settings.get("diff").equals("Normal")) dropItem(2, 3, Items.get("gear"));
		if (Settings.get("diff").equals("Hard")) dropItem(1, 2, Items.get("gear"));
		
		if(random.nextInt(60) == 2) {
			level.dropItem(x, y, Items.get("iron"));
		}
		
		if(random.nextInt(40) == 19) {
			int rand = random.nextInt(3);
			if(rand == 0) {
				level.dropItem(x, y, Items.get("iron"));
			} else if(rand == 1) {
				level.dropItem(x, y, Items.get("gold"));
			} else if(rand == 2) {
				level.dropItem(x, y, Items.get("gem"));
			}
		}
		
		super.die();
	}
}
