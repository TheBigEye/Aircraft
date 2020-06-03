package minicraft.entity;

import minicraft.gfx.Color;
import minicraft.gfx.MobSprite;
import minicraft.gfx.Screen;
import minicraft.item.Items;
import minicraft.screen.OptionsMenu;

public class Wolf extends FriendlyMob{
	private static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(10, 20);

	public Wolf() {
		super(sprites, Color.get(-1, 000, 444, 555));
	}

	public void render(Screen screen) {
		if (isLight()) {			
		}
		
		super.render(screen);
		
	}
	

	
	protected void die() {
		int min = 0, max = 0;
		if (OptionsMenu.diff == OptionsMenu.easy) {min = 1; max = 3;}
		if (OptionsMenu.diff == OptionsMenu.norm) {min = 1; max = 2;}
		if (OptionsMenu.diff == OptionsMenu.hard) {min = 0; max = 2;}
		
		dropItem(min, max, Items.get("Bone"), Items.get("Raw Beef"));
		
		super.die();
	}
}

