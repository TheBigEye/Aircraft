package minicraft.entity;

import minicraft.gfx.Color;
import minicraft.gfx.MobSprite;
import minicraft.gfx.Screen;
import minicraft.item.Items;
import minicraft.screen.OptionsMenu;

public class Eye extends EnemyMob {
	private static final MobSprite[][] sprites;
	private static final MobSprite[] walking, standing;
	static {
		MobSprite[] list = MobSprite.compileSpriteList(4, 20, 2, 2, 0, 3);
		walking = new MobSprite[] {list[1], list[2]};
		standing = new MobSprite[] {list[0], list[0]};
		sprites = new MobSprite[1][2];
		sprites[0] = standing;
	}
	private static int[] lvlcols = {
		Color.get(-1, 100, 321, 550),
		Color.get(-1, 100, 200, 500),

	};
	
	
	private int fuseTime = 0;
	private boolean fuseLit = false;
	
	public Eye(int lvl) {
		super(lvl, sprites, lvlcols, 10, 100);
	}
	
	public boolean move(int xa, int ya) {
		boolean result = super.move(xa, ya);
		dir = 0;
		if (xa == 0 && ya == 0) walkDist = 0;
		return result;
	}
	
	public void tick() {
		super.tick();
	}
	public void render(Screen screen) {
		if (fuseLit && fuseTime % 6 == 0) {
			super.lvlcols[lvl-1] = Color.get(-1, 252);
		}
		else
			super.lvlcols[lvl-1] = Eye.lvlcols[lvl-1];
		
		this.sprites[0] = walkDist == 0 ? standing : walking;
		
		super.render(screen);
	}
	/*
	public void render(Screen screen) {
		col0 = Color.get(-1, 10, 152, 40);
		col1 = Color.get(-1, 20, 252, 50);
		col2 = Color.get(-1, 10, 152, 40);
		col3 = Color.get(-1, 0, 30, 20);
		col4 = Color.get(-1, 10, 152, 40);
		
		if (isLight()) {
			col0 = col1 = col2 = col3 = col4 = Color.get(-1, 20, 252, 50);
		}
		
		if (lvl == 2) col = Color.get(-1, 100, 522, 050);
		else if (lvl == 3) col = Color.get(-1, 111, 444, 050),
		;
		else if (lvl == 4) col = Color.get(-1, 000, 111, 020);
		
		else if (level.dirtColor == 322) {
			if (Game.time == 0) col = col0;
			if (Game.time == 1) col = col1;
			if (Game.time == 2) col = col2;
			if (Game.time == 3) col = col3;
		} else col = col4;
		
		super.render(screen);
	}
	*/
	
	public boolean canSwim() {
		return true;
	}

	protected void die() {
		if (OptionsMenu.diff == OptionsMenu.easy) dropItem(2, 2, Items.get("Eye Orb"));
		if (OptionsMenu.diff == OptionsMenu.norm) dropItem(1, 1, Items.get("Eye Orb"));
		if (OptionsMenu.diff == OptionsMenu.hard) dropItem(0, 1, Items.get("Eye Orb"));
		
		if(random.nextInt(60) == 2) {
			level.dropItem(x, y, Items.get("iron"));
		}
		
		if(random.nextInt(40) == 19) {
			int rand = random.nextInt(3);
			if(rand == 0) {
				level.dropItem(x, y, Items.get("Eye Orb"));
			} else if(rand == 1) {
				level.dropItem(x, y, Items.get("Eye Orb"));
			} else if(rand == 2) {
				level.dropItem(x, y, Items.get("Eye Orb"));
			}
		}
		
		super.die();
	}
}
