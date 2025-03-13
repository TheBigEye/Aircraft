package minicraft.entity.mob;

import minicraft.core.Game;
import minicraft.core.Updater;
import minicraft.core.io.Settings;
import minicraft.entity.Entity;
import minicraft.entity.furniture.Bed;
import minicraft.graphic.MobSprite;
import minicraft.graphic.Screen;
import minicraft.level.Level;
import minicraft.level.tile.*;
import minicraft.level.tile.farming.FarmTile;

public class GiantBossMob extends MobAi {

    public int lvl;
    protected MobSprite[][][] lvlSprites;
    public int detectDist;

    /**
     * Constructor for a hostile (enemy) mob. The level determines what the mob
     * does. sprites contains all the graphics and animations for the mob. lvlcols
     * is the different color the mob has depending on its level. isFactor
     * determines if the mob's health should be affected by the level and the
     * difficulty.
     *
     * @param lvl         The mob's level.
     * @param lvlSprites  The mob's sprites (ordered by level, then direction, then
     *                    animation frame).
     * @param health      How much health the mob has.
     * @param isFactor    false if maxHealth=health, true if
     *                    maxHealth=health*level*level*difficulty
     * @param detectDist  The distance where the mob will detect the player and start
     *                    moving towards him/her.
     * @param lifetime    How many ticks this mob will live.
     * @param rwTime      How long the mob will walk in a random direction. (random
     *                    walk duration)
     * @param rwChance    The chance of this mob will walk in a random direction
     *                    (random walk chance)
     */
    public GiantBossMob(int lvl, MobSprite[][][] lvlSprites, int health, boolean isFactor, int detectDist, int lifetime, int rwTime, int rwChance) {
        super(lvlSprites[0], isFactor ? (lvl == 0 ? 1 : lvl * lvl) * (health + 2) * ((Double) (Math.pow(2, Settings.getIndex("diff")))).intValue() : health, lifetime, rwTime, rwChance);
        this.lvl = lvl == 0 ? 1 : lvl;
        this.lvlSprites = java.util.Arrays.copyOf(lvlSprites, lvlSprites.length);
        this.detectDist = detectDist;
    }

    /**
     * Constructor for a hostile (enemy) mob. Lifetime will be set to 60 *
     * Game.normalSpeed.
     *
     * @param lvl        The mob's level.
     * @param lvlSprites The mob's sprites (ordered by level, then direction, then
     *                   animation frame).
     * @param health     How much health the mob has.
     * @param isFactor   false if maxHealth=health, true if
     *                   maxHealth=health*level*level*difficulty
     * @param detectDist The distance where the mob will detect the player and start
     *                   moving towards him/her.
     * @param rwTime     How long the mob will walk in a random direction. (random
     *                   walk duration)
     * @param rwChance   The chance of this mob will walk in a random direction
     *                   (random walk chance)
     */
    public GiantBossMob(int lvl, MobSprite[][][] lvlSprites, int health, boolean isFactor, int detectDist, int rwTime, int rwChance) {
        this(lvl, lvlSprites, health + 2, isFactor, detectDist, 60 * Updater.normalSpeed, rwTime, rwChance);
    }

    /**
     * Constructor for a hostile (enemy) mob. isFactor=true, rwTime=60,
     * rwChance=200.
     *
     * @param lvl        The mob's level.
     * @param lvlSprites The mob's sprites (ordered by level, then direction, then
     *                   animation frame).
     * @param health     How much health the mob has.
     * @param detectDist The distance where the mob will detect the player and start
     *                   moving towards him/her.
     */
    public GiantBossMob(int lvl, MobSprite[][][] lvlSprites, int health, int detectDist) {
        this(lvl, lvlSprites, health + 2, true, detectDist, 60, 200);
    }

    @Override
    public void tick() {
        super.tick();

        if (!Game.isMode("Creative")) {
            Player player = getClosestPlayer();

            // checks if player is on zombies level and if there is no time left on randonimity timer
            if (player != null && !Bed.sleeping() && randomWalkTime <= 0) {
                int xd = player.x - x;
                int yd = player.y - y;

                if (xd * xd + yd * yd < detectDist * detectDist) { /// if player is less than 6.25 tiles away, then set move dir towards player
                    int sig0 = 1; // this prevents too precise estimates, preventing mobs from bobbing up and down.
                    xa = ya = 0;

                    if (xd < sig0) xa = -1;
                    if (xd > sig0) xa = +1;
                    if (yd < sig0) ya = -1;
                    if (yd > sig0) ya = +1;

                } else { // if the enemy was following the player, but has now lost it, it stops moving.
                    // *that would be nice, but I'll just make it move randomly instead.
                    randomizeWalkDir(false);
                }
            }
        }

    }

    @Override
    public void render(Screen screen) {
        super.render(screen, 15, 18);
    }


    public void render(Screen screen, int color, boolean shake) {
    	if (shake) {
    		super.render(screen, 15 - random.nextInt(4), 18 - random.nextInt(2), color);
    	} else {
    		super.render(screen, 15, 18, color);
    	}
    }

    @Override
    protected void touchedBy(Entity entity) { // if an entity (like the player) touches the enemy mob
        super.touchedBy(entity); // hurts the player, damage is based on lvl.
        if (entity instanceof Player) {
            ((Player) entity).hurt(this, lvl * (Settings.get("diff").equals("Hard") ? 3 : 1));
        }
    }

    public void die() {
        super.die(50 * lvl, 1);
    }

    /**
     * Determines if the mob can spawn at the giving position in the given map.
     *
     * @param level The level which the mob wants to spawn in.
     * @param x     X map spawn coordinate.
     * @param y     Y map spawn coordinate.
     * @return true if the mob can spawn here, false if not.
     */
    public static boolean checkStartPos(Level level, int x, int y) {
        int r = level.depth == -4 ? (Game.isMode("score") ? 22 : 15) : 13;

        // Move the check for start position to the beginning of the method
        if (!MobAi.checkStartPos(level, x, y, 60, r)) {
            return false;
        }

        Tile tile = level.getTile(x >> 4, x >> 4);

        // Check for ObsidianTile first, since it is the most likely condition
        if (tile instanceof ObsidianTile) {
            return true;
        }
        // Check for depth to avoid unnecessary checks
        if (level.depth != -4) {
            if (tile instanceof DoorTile || tile instanceof WallTile || tile instanceof FarmTile || tile instanceof MyceliumTile) {
                return false;
            }
            return !level.isLight(x >> 4, x >> 4);
        }
        return false;
    }


    @Override
    public int getMaxLevel() {
        return lvlSprites.length;
    }
}
