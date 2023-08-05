package minicraft.entity.mob;

import minicraft.core.Game;
import minicraft.core.Updater;
import minicraft.core.io.Settings;
import minicraft.entity.particle.HeartParticle;
import minicraft.graphic.MobSprite;
import minicraft.graphic.Screen;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

public class PassiveMob extends MobAi {
    /**
     * Constructor for a non-hostile (passive) mob. healthFactor = 4.
     * 
     * @param sprites The mob's sprites.
     */
    public PassiveMob(MobSprite[][] sprites) {
        this(sprites, 4);
    }

    /**
     * Constructor for a non-hostile (passive) mob.
     * 
     * @param sprites      The mob's sprites.
     * @param healthFactor Determines the mobs health. Will be multiplied by the
     *                     difficulty and then added with 5.
     */
    public PassiveMob(MobSprite[][] sprites, int healthFactor) {
        super(sprites, 8 + healthFactor * Settings.getIndex("diff"), 5 * 60 * Updater.normalSpeed, 45, 40);
    }

    @Override
    public void render(Screen screen) {
        super.render(screen);
    }

    public void tick() {
        super.tick();
    }

    public void followOnHold(int followRadius, String item, boolean heartParticles) {
    	Player player = level.getClosestPlayer(x, y);
    	// This is true if the player isnt null, the active item also isnt null, the player is holding the item and within the followRadius
    	boolean holdingItem = player != null && player.activeItem != null && player.activeItem.name.equals(item) && player.isWithin(followRadius, this);
    	
    	if (holdingItem) {
    		// get player walk direction
	        int xd = player.x - x;
	        int yd = player.y - y;

	        int dir = 1; // this prevents too precise estimates, preventing mobs from bobbing up and down.
	        xa = ya = 0;
	        
	        // follow to the player
	        if (xd < dir) xa = -1;
	        else if (xd > dir) xa = 1;
	        if (yd < dir) ya = -1;
	        else if (yd > dir) ya = 1;
	    } else {
	        randomizeWalkDir(false);
	    }
    	
    	if (heartParticles) {
		    if (Settings.get("particles").equals(true) && tickTime /8 %12 == 0) {
		        level.add(new HeartParticle(x - 2 + random.nextInt(8), y - 16));
		    }
    	}
    }

    @Override
    public void randomizeWalkDir(boolean byChance) {
        if (xa == 0 && ya == 0 && random.nextInt(5) == 0 || byChance || random.nextInt(randomWalkChance) == 0) {
            randomWalkTime = randomWalkDuration;
            // multiple at end ups the chance of not moving by 50%.
            xa = (random.nextInt(3) - 1) * random.nextInt(2);
            ya = (random.nextInt(3) - 1) * random.nextInt(2);
        }
    }

    public void die() {
        super.die(15);
    }

    /**
     * Checks a given position in a given level to see if the mob can spawn there.
     * Passive mobs can only spawn on grass or flower tiles.
     * 
     * @param level The level which the mob wants to spawn in.
     * @param x     X map spawn coordinate.
     * @param y     Y map spawn coordinate.
     * @return true if the mob can spawn here, false if not.
     */
    public static boolean checkStartPos(Level level, int x, int y) {
        int r = (Game.isMode("score") ? 22 : 15) + (Updater.getTime() == Updater.Time.Night ? 0 : 5); 

        if (!MobAi.checkStartPos(level, x, y, 80, r)) {
            return false;
        }

        Tile tile = level.getTile(x >> 4, y >> 4);
        if (tile == Tiles.get("Grass") || tile == Tiles.get("Rose") || tile == Tiles.get("Daisy") || tile == Tiles.get("Poppy") || tile == Tiles.get("Dandelion")) {
        	return true;
        }
        
        return false;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
}
