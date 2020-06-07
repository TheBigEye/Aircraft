package minicraft.entity;

import minicraft.Game;
import minicraft.gfx.MobSprite;
import minicraft.gfx.Screen;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.StackableItem;
import minicraft.item.TileItem;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;
import minicraft.screen.ModeMenu;
import minicraft.screen.OptionsMenu;
import minicraft.entity.Player;
import minicraft.item.TileItem;

public class FriendlyMob extends MobAi {
	
	protected int color;
	public int lvl;
	
	public FriendlyMob(MobSprite[][] sprites, int color) {this(sprites, color, 3);}
	public FriendlyMob(MobSprite[][] sprites, int color, int healthFactor) {
		super(sprites, 5 + healthFactor * OptionsMenu.diff, 45, 40);
		this.color = color;
		col = color;
	}
	

	
	public void tick() {
		super.tick();
		
		Player player = getClosestPlayer();
		if (player != null && player.activeItem != null && player.activeItem.name.equals("Bone")){ //This function will make the entity follow the player directly
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
	
	
	public void render(Screen screen) {
		col = color;
		super.render(screen);
	}
	
	protected void touchedBy(Entity entity) { // if the entity touches the mob
		super.touchedBy(entity);
		// hurts the player, damage is based on lvl.
		if(entity instanceof EnemyMob) {
			if (OptionsMenu.diff != OptionsMenu.easy)
				entity.hurt(this, lvl, dir);
			else entity.hurt(this, lvl * 2, dir);
		}
	}

	public boolean canAttack() {
		return true;
	}
	
	protected void die() {
		super.die(15);
	}
	
	public static boolean checkStartPos(Level level, int x, int y) { // Find a place to spawn the mob
		int r = (level.depth == -4 ? (ModeMenu.score ? 22 : 15) : 13);
		
		if(!MobAi.checkStartPos(level, x, y, 60, r))
			return false;
		
		x = x >> 4;
		y = y >> 4;
		
		Tile t = level.getTile(x, y);
		if(level.depth == -4) {
			if (t != Tiles.get("Obsidian")) return false;
		} else if (t != Tiles.get("Stone Door") && t != Tiles.get("Wood Door") && t != Tiles.get("Obsidian Door") && t != Tiles.get("wheat") && t != Tiles.get("farmland")) {
			// prevents mobs from spawning on lit tiles, farms, or doors (unless in the dungeons)
			return !level.isLight(x, y);
		} else return false;

		return true;
	}
	
	public int getMaxLevel() {
		return 1;
	}
}