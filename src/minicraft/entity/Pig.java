package minicraft.entity;

import minicraft.gfx.Color;
import minicraft.gfx.MobSprite;
import minicraft.item.Items;
import minicraft.screen.OptionsMenu;

public class Pig extends PassiveMob {
	private static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(16, 14);
	
	public Pig() {
		super(sprites, Color.get(-1, 000, 522, 555));
		/*
		col0 = Color.get(-1, 000, 444, 411);
		col1 = Color.get(-1, 000, 555, 522);
		col2 = Color.get(-1, 000, 333, 311);
		col3 = Color.get(-1, 000, 222, 211);
		col4 = Color.get(-1, 000, 444, 522);
		*/
	}
	/*
	public void render(Screen screen) {
		col0 = Color.get(-1, 000, 444, 411);
		col1 = Color.get(-1, 000, 555, 522);
		col2 = Color.get(-1, 000, 333, 311);
		col3 = Color.get(-1, 000, 222, 211);
		col4 = Color.get(-1, 000, 444, 522);
		
		if (isLight()) {
			col0 = col1 = col2 = col3 = col4 = Color.get(-1, 000, 555, 522);
		}
		
		if (level.dirtColor == 322) {
			if (Game.time == 0) col = col0;
			if (Game.time == 1) col = col1;
			if (Game.time == 2) col = col2;
			if (Game.time == 3) col = col3;
		} else col = col4;
		
		super.render(screen);
	}
	*//*
	public boolean canWool() {
		return true;
	}
	*/
	
	public void tick() {
		super.tick();
		
		Player player = getClosestPlayer();
		if (player != null && player.activeItem != null && player.activeItem.name.equals("Apple")){ //This function will make the entity follow the player directly
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
	
	protected void die() {
		int min = 0, max = 0;
		if (OptionsMenu.diff == OptionsMenu.easy) {min = 1; max = 3;}
		if (OptionsMenu.diff == OptionsMenu.norm) {min = 1; max = 2;}
		if (OptionsMenu.diff == OptionsMenu.hard) {min = 0; max = 2;}
		
		dropItem(min, max, Items.get("raw pork"));
		
		super.die();
	}
}
