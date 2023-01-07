package minicraft.entity.mob;

import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.gfx.MobSprite;
import minicraft.item.Items;

public class Cow extends PassiveMob {
    private static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(0, 40);

    private int tickTime = 0;

    /**
     * Creates the cow with the right sprites and color.
     */
    public Cow() {
        super(sprites, 5);
    }

    public void tick() {
        super.tick();
        tickTime++;

		// follows to the player if holds wheat
		followOnHold(3, "Wheat", false);
        
		// Cow sounds
		if (tickTime /8 %24 == 0 && random.nextInt(8) == 4) {
			if (random.nextBoolean()) {
				if (!random.nextBoolean()) {
					Sound.cowSay1.playOnWorld(x, y);
				} else {
					Sound.cowSay2.playOnWorld(x, y);
				}
			} else {
				Sound.cowSay3.playOnWorld(x, y);
			}
		}
    }

    public void die() {
        int min = 0, max = 0;

        if (Settings.get("diff").equals("Peaceful")) {min = 1; max = 3;}
        if (Settings.get("diff").equals("Easy")) {min = 1; max = 3;}
        if (Settings.get("diff").equals("Normal")) {min = 1; max = 2;}
        if (Settings.get("diff").equals("Hard")) {min = 0; max = 1;}

        if (isBurn) dropItem(min, max, Items.get("Steak"));
        if (!isBurn) dropItem(min, max, Items.get("leather"), Items.get("raw beef"));

        super.die();
    }
}