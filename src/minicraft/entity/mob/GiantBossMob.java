package minicraft.entity.mob;

import minicraft.core.Game;
import minicraft.core.Updater;
import minicraft.core.io.Settings;
import minicraft.entity.Entity;
import minicraft.gfx.MobSprite;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

public class GiantBossMob extends MobAi {
	
	public int lvl;
	protected MobSprite[][][] lvlSprites;
	public int detectDist;
	
	/**
	 * Constructor for a hostile (enemy) mob. The level determines what the mob does. sprites contains all the graphics and animations for the mob.
	 * lvlcols is the different color the mob has depending on its level. isFactor determines if the mob's health should be affected by the level and
	 * the difficulty.
	 * @param lvl The mob's level.
	 * @param lvlSprites The mob's sprites (ordered by level, then direction, then animation frame).
	 * @param health How much health the mob has.
	 * @param isFactor false if maxHealth=health, true if maxHealth=health*level*level*difficulty
	 * @param detectDist The distance where the mob will detect the player and start moving towards him/her.
	 * @param lifetime How many ticks this mob will live.
	 * @param rwTime How long the mob will walk in a random direction. (random walk duration)
	 * @param rwChance The chance of this mob will walk in a random direction (random walk chance)
	 */
	public GiantBossMob(int lvl, MobSprite[][][] lvlSprites, int health, boolean isFactor, int detectDist, int lifetime, int rwTime, int rwChance) {
		super(lvlSprites[0], isFactor ? (lvl==0?1:lvl * lvl) * health*((Double)(Math.pow(2, Settings.getIdx("diff")))).intValue() : health, lifetime, rwTime, rwChance);
		this.lvl = lvl == 0 ? 1 : lvl;
		this.lvlSprites = java.util.Arrays.copyOf(lvlSprites, lvlSprites.length);
		this.detectDist = detectDist;
	}
	
	/**
	 * Constructor for a hostile (boss) mob. 
	 * Lifetime will be set to 60 * Game.normSpeed.
	 * @param lvl The mob's level.
	 * @param lvlSprites The mob's sprites (ordered by level, then direction, then animation frame).
	 * @param health How much health the mob has.
	 * @param isFactor false if maxHealth=health, true if maxHealth=health*level*level*difficulty
	 * @param detectDist The distance where the mob will detect the player and start moving towards him/her.
	 * @param rwTime How long the mob will walk in a random direction. (random walk duration)
	 * @param rwChance The chance of this mob will walk in a random direction (random walk chance)
	 */
	public GiantBossMob(int lvl, MobSprite[][][] lvlSprites, int health, boolean isFactor, int detectDist, int rwTime, int rwChance) {
		this(lvl, lvlSprites, health, isFactor, detectDist, 60*Updater.normSpeed, rwTime, rwChance);
	}
	
	/**
	 * Constructor for a hostile (enemy) mob.
	 * isFactor=true,
	 * rwTime=60,
	 * rwChance=200.
	 * 
	 * @param lvl The mob's level.
	 * @param lvlSprites The mob's sprites (ordered by level, then direction, then animation frame).
	 * @param health How much health the mob has.
	 * @param detectDist The distance where the mob will detect the player and start moving towards him/her.
	 */
	public GiantBossMob(int lvl, MobSprite[][][] lvlSprites, int health, int detectDist) {
		this(lvl, lvlSprites, health, true, detectDist, 60, 200);
	}
	
	@Override
	public void tick() {
		super.tick();
		
		Player player = getClosestPlayer();
		if (player != null && player != null) { // checks if player is on zombies level and if there is no time left on randonimity timer
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
				// if the enemy was following the player, but has now lost it, it stops moving.
					//*that would be nice, but I'll just make it move randomly instead.
				randomizeWalkDir(false);
			}
		}
	
	//@Override
	//public void render(Screen screen) {
		//sprites = lvlSprites[lvl - 1];
		//super.render(screen);
	//}
	
	@Override
	protected void touchedBy(Entity entity) { // if an entity (like the player) touches the enemy mob
		super.touchedBy(entity);
		// hurts the player, damage is based on lvl.
		if(entity instanceof Player) {
			((Player)entity).hurt(this, lvl * (Settings.get("diff").equals("Hard") ? 3 : 1));
		}
	}
	
	public void die() {
		super.die(50 * lvl, 1);
	}
	
	/**
	 * Determines if the mob can spawn at the giving position in the given map. 
	 * @param level The level which the mob wants to spawn in.
	 * @param x X map spawn coordinate.
	 * @param y Y map spawn coordinate.
	 * @return true if the mob can spawn here, false if not.
	 */
	public static boolean checkStartPos(Level level, int x, int y) { // Find a place to spawn the mob
		int r = (level.depth == -4 ? (Game.isMode("score") ? 22 : 15) : 13);
		
		if(!MobAi.checkStartPos(level, x, y, 60, r))
			return false;
		
		x = x >> 4;
		y = y >> 4;
		
		Tile tile = level.getTile(x >> 4, y >> 4);
		return tile == Tiles.get("grass") || tile == Tiles.get("flower");
		
	}
	
	@Override
	public int getMaxLevel() {
		return lvlSprites.length;
	}
}