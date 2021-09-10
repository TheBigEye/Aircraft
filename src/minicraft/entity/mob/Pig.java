package minicraft.entity.mob;

import org.jetbrains.annotations.Nullable;

import minicraft.core.io.Settings;
import minicraft.entity.Direction;
import minicraft.entity.particle.FireParticle;
import minicraft.gfx.MobSprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;

public class Pig extends PassiveMob {
	private static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(0, 30);
	
	// Burn
	public boolean isBurn = false;
    private int burnTime = 0;
	
	/**
	 * Creates a pig.
	 */
	public Pig() {
		super(sprites);
	}
	
	public void tick() {
		super.tick();
		
		Player player = getClosestPlayer();
		if (player != null && player.activeItem != null && player.activeItem.name.equals("Carrot")){ //This function will make the entity follow the player directly
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
		if (Settings.get("diff").equals("Hard")) {min = 0; max = 2;}
		
		dropItem(min, max, Items.get("raw pork"));
		
		super.die();
	}
}
