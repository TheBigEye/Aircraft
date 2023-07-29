package minicraft.entity.mob;

import minicraft.core.Game;
import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.graphic.MobSprite;
import minicraft.item.Item;
import minicraft.item.Items;

public class Chicken extends PassiveMob {
    private static final MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(10, 40);

    /**
     * Creates a Chicken.
     */

    public Chicken() {
        super(sprites);
    }

    public void tick() {
        super.tick();

        // Drop eggs each 15 secs
        int min = 0;
        int max = 0;

        if (Settings.get("diff").equals("Peaceful")) {min = 1;max = 2;}
        if (Settings.get("diff").equals("Easy")) {min = 1; max = 2;}
        if (Settings.get("diff").equals("Normal")) {min = 1; max = 1;}
        if (Settings.get("diff").equals("Hard")) {min = 0; max = 1;}

        if (random.nextInt(3000) == 1 && Game.isMode("Survival")) { // drop eggs each 15 secs
            dropItem(min, max, Items.get("egg"));
        }

        // follows to the player if holds seeds
        followOnHold(2, "Seeds", false);
        
		// Chicken sounds
		if (tickTime / 8 % 16 == 0 && random.nextInt(8) == 4) {
			if (random.nextBoolean()) {
				if (!random.nextBoolean()) {
					Sound.chickenSay1.playOnLevel(this.x, this.y);
				} else {
					Sound.chickenSay2.playOnLevel(this.x, this.y);
				}
			} else {
				Sound.chickenSay3.playOnLevel(this.x, this.y);
			}
		}
    }

    @Override
    public void die() {
		int min = 0;
		int max = 0;
		
		String difficulty = (String) Settings.get("diff");

        if (difficulty == "Peaceful" || difficulty == "Easy") { min = 1; max = 2; }
        if (difficulty == "Normal") { min = 1; max = 1; }
        if (difficulty == "Hard") { min = 0; max = 1; }
        
        dropItem(min, max, new Item[] {
        		Items.get("raw chicken"), Items.get("feather") 
        });

        super.die();
    }
}