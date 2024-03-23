package minicraft.entity.mob;

import minicraft.core.io.Settings;
import minicraft.graphic.MobSprite;
import minicraft.item.Items;

public class Phyg extends SkyMob {
    private static final MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(20, 44);
    private static final String[] sounds = new String[] {"pigSay1", "pigSay2", "pigSay3"};

    /**
     * Creates a Phyg (Flying pig).
     */
    public Phyg() {
        super(sprites);
    }

	public void tick() {
	    super.tick();

		// follows to the player if holds a carrot
		followOnHold(Items.get("Gold Carrot"), 5);
		
		if ((this.tickTime % (random.nextInt(100) + 120) == 0)) {
			doPlaySound(sounds, 7);
		}
	}

    public void die() {
        int min = 0, max = 0;
        
        if (Settings.get("diff").equals("Peaceful")) min = 1;  max = 3;
        if (Settings.get("diff").equals("Easy")) min = 1; max = 3;
        if (Settings.get("diff").equals("Normal")) min = 1; max = 2;
        if (Settings.get("diff").equals("Hard")) min = 0; max = 2;
        
        dropItem(min, max, Items.get("raw pork"));

        super.die();
    }
}
