package minicraft.entity.mob;

import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.particle.HeartParticle;
import minicraft.gfx.MobSprite;
import minicraft.item.Items;

public class Pig extends PassiveMob {
    private static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(10, 38);
    private int tickTime = 0;

    /**
     * Creates a pig.
     */
    public Pig() {
        super(sprites);
    }

    public void tick() {
        super.tick();
        tickTime++;

	    Player player = getClosestPlayer();
	    boolean holdingCarrot = player != null && player.activeItem != null && player.activeItem.name.equals("Carrot");

	    // Render heart particles
	    if (Settings.get("particles").equals(true) && holdingCarrot && random.nextInt(6) == 0) {
	        int randX = random.nextInt(10);
	        int randY = random.nextInt(9);
	        level.add(new HeartParticle(x - 9 + randX, y - 12 + randY));
	    }

	    if (holdingCarrot) {
	        int xd = player.x - x;
	        int yd = player.y - y;

	        /// if player is less than 6.25 tiles away, then set move dir towards player
	        int sig0 = 1; // this prevents too precise estimates, preventing mobs from bobbing up and down.
	        xa = ya = 0;

	        if (xd < sig0) xa = -1;
	        else if (xd > sig0) xa = 1;
	        if (yd < sig0) ya = -1;
	        else if (yd > sig0) ya = 1;
	    } else {
	        randomizeWalkDir(false);
	    }
        
		// Pig sounds
		if (tickTime / 8 % 16 == 0 && random.nextInt(8) == 4) {
			if (random.nextBoolean()) {
				if (!random.nextBoolean()) {
					Sound.pigSay1.playOnWorld(x, y);
				} else {
					Sound.pigSay2.playOnWorld(x, y);
				}
			} else {
				Sound.pigSay3.playOnWorld(x, y);
			}
		}
    }

    public void die() {
        int min = 0, max = 0;
        String difficulty = (String) Settings.get("diff");
        
        if (difficulty == "Peaceful" || difficulty == "Easy") { 
        	min = 1; max = 3; 
        } else if (difficulty == "Normal") { 
        	min = 1; max = 2; 
        } else if (difficulty == "Hard") { 
        	min = 0; max = 2; 
        }
        
        if (isBurn) dropItem(min, max, Items.get("Cooked pork")); // if isBurn.. drop Cooked food
        if (!isBurn) dropItem(min, max, Items.get("raw pork")); // else, drop normal food

        super.die();
    }
}