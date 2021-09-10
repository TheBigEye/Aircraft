package minicraft.entity.mob;

import java.util.Random;

import org.jetbrains.annotations.Nullable;

import minicraft.core.io.Settings;
import minicraft.entity.Direction;
import minicraft.entity.particle.FireParticle;
import minicraft.entity.particle.HeartParticle;
import minicraft.gfx.MobSprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;

public class Cow extends PassiveMob {
	private static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(0, 24);
	
	private Random rnd = new Random();
	
	// Burn
	public boolean isBurn = false;
    private int burnTime = 0;
	
	/**
	 * Creates the cow with the right sprites and color.
	 */
	public Cow() {
		super(sprites, 5);
	}
	
	public void tick() {
		super.tick();

		Player player = getClosestPlayer();
		if (player != null && player.activeItem != null && player.activeItem.name.equals("Wheat")) { // This function will make the entity follow the player directly

			// Render heart particles
			int randX = rnd.nextInt(10);
			int randY = rnd.nextInt(9);

			if (random.nextInt(12) == 1) {
				level.add(new HeartParticle(x - 9 + randX, y - 12 + randY));
			}
			if (random.nextInt(12) == 4) {

			}
			if (random.nextInt(12) == 6) {
				level.add(new HeartParticle(x - 9 + randX, y - 12 + randY));
			}
			if (random.nextInt(12) == 8) {

			}
			if (random.nextInt(12) == 12) {
				level.add(new HeartParticle(x - 9 + randX, y - 12 + randY));
			}

			int xd = player.x - x;
			int yd = player.y - y;

			/// if player is less than 6.25 tiles away, then set move dir towards player
			int sig0 = 1; // this prevents too precise estimates, preventing mobs from bobbing up and down.
			xa = ya = 0;
			if (xd < sig0)
				xa = -1;
			if (xd > sig0)
				xa = +1;
			if (yd < sig0)
				ya = -1;
			if (yd > sig0)
				ya = +1;
		} else {
			// if the Pet was following the player, but has now lost it, it stops moving.
			// *that would be nice, but I'll just make it move randomly instead.
			randomizeWalkDir(false);
		}
		if (isBurn == true) {
	        burnTime++;
	        if (burnTime >= 128) {
	        	burnTime = 0;
	        	isBurn = false;
	        }		
	        
	        if (burnTime >= 1) {
	        	if (random.nextInt(4) == 2) {
	        		int randX = random.nextInt(10);
	        		int randY = random.nextInt(9);
	        		
	        		level.add(new FireParticle(x - 4 + randX, y - 4 + randY));
	        	
	        		this.hurt(this, 1);
	        	}	
	        }
	        
		} else {
			burnTime = 0; // Check
		}
		
		}
	
	public boolean interact(Player player, @Nullable Item item, Direction attackDir) {
		if (isBurn) return false;

		if (item instanceof ToolItem) {			
			if (((ToolItem) item).type == ToolType.Flintnsteel) {
				isBurn = true;
				return true;
			}
		}
		return false;
	}
	
	public void die() {
		int min = 0, max = 0;
		if (Settings.get("diff").equals("Peaceful")) {min = 1; max = 3;}
		if (Settings.get("diff").equals("Easy")) {min = 1; max = 3;}
		if (Settings.get("diff").equals("Normal")) {min = 1; max = 2;}
		if (Settings.get("diff").equals("Hard")) {min = 0; max = 1;}
		
		dropItem(min, max, Items.get("leather"), Items.get("raw beef"));
		
		super.die();
	}
}
