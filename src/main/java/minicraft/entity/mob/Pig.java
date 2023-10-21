package minicraft.entity.mob;

import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.graphic.MobSprite;
import minicraft.item.Items;

public class Pig extends PassiveMob {
    private static final MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(10, 38);

    /**
     * Creates a pig.
     */
    public Pig() {
        super(sprites);
    }

    @Override
    public void tick() {
        super.tick();

		// follows to the player if holds a carrot
		followOnHold(Items.get("Carrot"), 5);
        
		// Pig sounds
		if (tickTime / 8 % 16 == 0 && random.nextInt(8) == 4) {
			if (random.nextBoolean()) {
				if (!random.nextBoolean()) {
					Sound.pigSay1.playOnLevel(this.x, this.y);
				} else {
					Sound.pigSay2.playOnLevel(this.x, this.y);
				}
			} else {
				Sound.pigSay3.playOnLevel(this.x, this.y);
			}
		}
    }

    @Override
    public void die() {
        int min = 0;
        int max = 0;
        
        if (Settings.get("diff").equals("Peaceful") || Settings.get("diff").equals("Easy")) { min = 1; max = 3; }
        else if (Settings.get("diff").equals("Normal")) { min = 1; max = 2; }
        else if (Settings.get("diff").equals("Hard")) { min = 0; max = 2; }
        
        dropItem(min, max, Items.get("raw pork"));

        super.die();
    }
}