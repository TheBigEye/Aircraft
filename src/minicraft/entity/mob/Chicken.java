package minicraft.entity.mob;

import minicraft.core.io.Settings;
import minicraft.gfx.MobSprite;
import minicraft.item.Items;

public class Chicken extends PassiveMob {
	private static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(0, 32);
	
	/**
	 * Creates a Chicken.
	 */
	public Chicken() {
		super(sprites);
	}
	
	public void tick() {
		super.tick();
		int min = 0, max = 0;
		if (Settings.get("diff").equals("Easy")) {min = 1; max = 2;}
		if (Settings.get("diff").equals("Normal")) {min = 1; max = 1;}
		if (Settings.get("diff").equals("Hard")) {min = 0; max = 1;}
		
		if (random.nextInt(1500)==1) { //drop eggs each 15 secs
			dropItem(min, max, Items.get("egg"));
			
		}
		
		Player player = getClosestPlayer();
		if (player != null && player.activeItem != null && player.activeItem.name.equals("Seeds")){ //This function will make the entity follow the player directly
			int xd = player.x - x;
			int yd = player.y - y;
				/// if player is less than 6.25 tiles away, then set move dir towards player
				int sig0 = 1; // this prevents too precise estimates, preventing mobs from bobbing up and down.
				xa = ya = 0;
				if (xd < sig0) xa = -1;
				if (xd > sig0) xa = +1;
				if (yd < sig0) ya = -1;
				if (yd > sig0) ya = +1;
			} else {
				// if the Pet was following the player, but has now lost it, it stops moving.
					//*that would be nice, but I'll just make it move randomly instead.
				randomizeWalkDir(false);
			}
		}
	
	public void die() {
		int min = 0, max = 0;
		if (Settings.get("diff").equals("Easy")) {min = 1; max = 2;}
		if (Settings.get("diff").equals("Normal")) {min = 1; max = 1;}
		if (Settings.get("diff").equals("Hard")) {min = 0; max = 1;}
		
		dropItem(min, max, Items.get("raw chicken"));
		dropItem(min, max, Items.get("feather"));
		
		super.die();
	}
}

