package minicraft.entity.mob;

import minicraft.core.io.Settings;
import minicraft.graphic.MobSprite;
import minicraft.item.Items;

public class Pig extends PassiveMob {
    private static final MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(10, 38);
    private static final String[] sounds = new String[] {"pigSay1", "pigSay2", "pigSay3"};

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
		if ((this.tickTime % (random.nextInt(100) + 120) == 0)) {
			doPlaySound(sounds, 7);
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