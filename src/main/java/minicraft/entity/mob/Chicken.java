package minicraft.entity.mob;

import minicraft.core.Game;
import minicraft.core.io.Settings;
import minicraft.graphic.MobSprite;
import minicraft.item.Item;
import minicraft.item.Items;

public class Chicken extends PassiveMob {
    private static final MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(10, 40);
    
    private static final String[] sounds = new String[] {"chickenSay1", "chickenSay2", "chickenSay3"};
 
    /**
     * Creates a Chicken.
     */

    public Chicken() {
        super(sprites);
    }

    public void tick() {
        super.tick();
        
        if (tickTime % 2000 == 0) {
            if (Game.isMode("Survival")) { // drop eggs each 15 secs
                level.dropItem(x, y, Items.get("egg"));
            }
        }
        
        // follows to the player if holds seeds
        followOnHold(Items.get("Seeds"), 2);
        
		// Chicken sounds
		if ((tickTime % 100 == 0) && random.nextInt(4) == 0) {
			playSound(sounds, 4);
		}
    }

    @Override
    public void die() {
		int min = 0;
		int max = 0;
		
		String difficulty = (String) Settings.get("diff");

        if (difficulty.equals("Peaceful") || difficulty.equals("Easy")) { 
        	min = 1; max = 2; 
        } else if (difficulty.equals("Normal")) {
        	min = 1; max = 1; 
        } else if (difficulty.equals("Hard")) {
        	min = 0; max = 1; 
        }
        
        dropItem(min, max, new Item[] {
        	Items.get("raw chicken"), Items.get("feather") 
        });

        super.die();
    }
}