package minicraft.entity.mob;

import minicraft.core.io.Settings;
import minicraft.gfx.MobSprite;
import minicraft.item.Items;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

public class Goat extends FrostMob {
	private static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(0, 34);
	
	/**
	 * Creates the cow with the right sprites and color.
	 */
	public Goat() {
		super(sprites, 5);
	}
	
	public void tick() {
		super.tick();
		
		Player player = getClosestPlayer();
		if (player != null && player.activeItem != null && player.activeItem.name.equals("Wheat")){ //This function will make the entity follow the player directly
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
		
		Tile tile = level.getTile(x >> 4, y >> 4);
		if ( tile == Tiles.get("grass")|| tile == Tiles.get("sand")|| tile == Tiles.get("lawn")|| tile == Tiles.get("flower")) {
			remove();
			level.add(new Sheep(), x, y);
			
		}
		
		}
	
	public void die() {
		int min = 0, max = 0;
		if (Settings.get("diff").equals("Peaceful")) {min = 1; max = 3;}
		if (Settings.get("diff").equals("Easy")) {min = 1; max = 2;}
		if (Settings.get("diff").equals("Normal")) {min = 1; max = 2;}
		if (Settings.get("diff").equals("Hard")) {min = 0; max = 1;}
		
		dropItem(min, max, Items.get("leather"));
		
		super.die();
	}
}

