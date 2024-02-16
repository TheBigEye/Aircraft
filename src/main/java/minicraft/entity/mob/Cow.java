package minicraft.entity.mob;

import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.graphic.MobSprite;
import minicraft.item.Item;
import minicraft.item.Items;

public class Cow extends PassiveMob {
    private static final MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(0, 40);

    /**
     * Creates the cow with the right sprites and color.
     */
    public Cow() {
        super(sprites, 5);
    }

    public void tick() {
        super.tick();

		// follows to the player if holds wheat
		followOnHold(Items.get("Wheat"), 3);
        
		// Cow sounds
		if ((this.tickTime % (random.nextInt(100) + 120) == 0) && random.nextInt(8) == 0) {
			if (random.nextBoolean()) {
				if (!random.nextBoolean()) {
					Sound.cowSay1.playOnLevel(this.x, this.y);
				} else {
					Sound.cowSay2.playOnLevel(this.x, this.y);
				}
			} else {
				Sound.cowSay3.playOnLevel(this.x, this.y);
			}
			
		}
    }

    @Override
    public void die() {
        int min = 0, max = 0;      
		String difficulty = (String) Settings.get("diff");

        if (difficulty == "Peaceful" || difficulty == "Easy") { min = 1; max = 3; }
        if (difficulty == "Normal") { min = 1; max = 2; }
        if (difficulty == "Hard") { min = 0; max = 1; }

        dropItem(min, max, new Item[] {
        	Items.get("leather"), Items.get("raw beef") 
        });

        super.die();
    }
}