package minicraft.entity.mob;

import minicraft.core.Updater;
import minicraft.core.io.Settings;
import minicraft.gfx.MobSprite;
import minicraft.gfx.Screen;
import minicraft.item.Items;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

public class Sheep extends PassiveMob {
	private static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(0, 26);
	private static final MobSprite[][] cutSprites = MobSprite.compileMobSpriteAnimations(0, 28);

	private static final int WOOL_GROW_TIME = 3 * 60 * Updater.normSpeed; // Three minutes

	public boolean cut = false;
	private int ageWhenCut = 0;
	
	/**
	 * Creates a sheep entity.
	 */
	public Sheep() {
		super(sprites);
	}
	
	@Override
	public void render(Screen screen) {
		int xo = x - 8;
		int yo = y - 11;

		MobSprite[][] curAnim = cut ? cutSprites : sprites;

		MobSprite curSprite = curAnim[dir.getDir()][(walkDist >> 3) % curAnim[dir.getDir()].length];
		if (hurtTime > 0) {
			curSprite.render(screen, xo, yo, true);
		} else {
			curSprite.render(screen, xo, yo);
		}
	}

	
	public void tick() {
		super.tick();
		
		if (age - ageWhenCut > WOOL_GROW_TIME) cut = false;
		
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
		if ( tile == Tiles.get("snow")) {
			remove();
			level.add(new Goat(), x, y);
			
		}
		}
	
	public void shear() {
		cut = true;
		dropItem(1, 3, Items.get("Wool"));
		ageWhenCut = age;
	}
	
	public void die() {
		int min = 0, max = 0;
		if (Settings.get("diff").equals("Passive")) {min = 1; max = 3;}
		if (Settings.get("diff").equals("Easy")) {min = 1; max = 3;}
		if (Settings.get("diff").equals("Normal")) {min = 1; max = 2;}
		if (Settings.get("diff").equals("Hard")) {min = 0; max = 2;}
		
		if (!cut) dropItem(min, max, Items.get("wool"));
		dropItem(min, max, Items.get("Raw Beef"));
		
		super.die();
	}
}
