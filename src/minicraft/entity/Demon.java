package minicraft.entity;

import minicraft.gfx.Color;
import minicraft.gfx.MobSprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.screen.OptionsMenu;

public class Demon extends EnemyMob{
	public static MobSprite[][] sprites =  MobSprite.compileMobSpriteAnimations(0, 14);
	private static int[] lvlcols = {
			Color.get(-1, 000, 300, 000),
			Color.get(-1, 200, 300, 300),

		};
		
		public Demon(int lvl) {
			super(lvl, sprites, lvlcols, 5, 100);
			/*
			col0 = Color.get(-1, 10, 152, 40);
			col1 = Color.get(-1, 20, 252, 50);
			col2 = Color.get(-1, 10, 152, 40);
			col3 = Color.get(-1, 0, 30, 20);
			col4 = Color.get(-1, 10, 42, 30);
			*/
		}
		
		public void tick() {
			super.tick();
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
		
		public boolean canWool() {
			return true;
		}*/

		protected void die() {
			if (OptionsMenu.diff == OptionsMenu.easy) dropItem(2, 3, Items.get("cloth"));
			if (OptionsMenu.diff == OptionsMenu.norm) dropItem(2, 3, Items.get("red clothes"));
			if (OptionsMenu.diff == OptionsMenu.hard) dropItem(0, 1, Items.get("red clothes"), Items.get("Antidious"));
			
			if(random.nextInt(60) == 2) {
				level.dropItem(x, y, Items.get("iron"));
			}
			
			if(random.nextInt(40) == 19) {
				int rand = random.nextInt(3);
				if(rand == 0) {
					level.dropItem(x, y, Items.get("Antidious"));
				} else if(rand == 1) {
					level.dropItem(x, y, Items.get("red clothes"));
				} else if(rand == 2) {
					level.dropItem(x, y, Items.get("Book"));
				}
			}
			
			super.die();
		}
	}
