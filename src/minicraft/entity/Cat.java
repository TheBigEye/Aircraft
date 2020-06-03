package minicraft.entity;

import minicraft.gfx.Color;
import minicraft.gfx.MobSprite;
import minicraft.gfx.Screen;
import minicraft.item.Items;
import minicraft.screen.OptionsMenu;

public class Cat extends PassiveMob{
	private static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(16, 10);
	
	public Cat() {
		super(sprites, Color.get(-1, 000, 530, 555));
	}

	public void render(Screen screen) {
		if (isLight()) {
			
		}
		
		super.render(screen);
	}
	
	public void tick() {
		super.tick();
		
		Player player = getClosestPlayer();
		if (player != null && player.activeItem != null && player.activeItem.name.equals("Cooked Fish")){ //This function will make the entity follow the player directly
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
		if (OptionsMenu.diff == OptionsMenu.norm) {min = 1; max = 3;}
		if (OptionsMenu.diff == OptionsMenu.hard) {min = 0; max = 2;}
		
		dropItem(min, max, Items.get("leather"));
		
		super.die();
	}
}
