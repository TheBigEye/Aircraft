package minicraft.entity.mob;

import minicraft.core.Game;
import minicraft.core.Updater;
import minicraft.core.io.Settings;
import minicraft.entity.Entity;
import minicraft.gfx.MobSprite;
import minicraft.gfx.Screen;
import minicraft.level.Level;
import minicraft.level.tile.DoorTile;
import minicraft.level.tile.MyceliumTile;
import minicraft.level.tile.Tile;
import minicraft.level.tile.WallTile;
import minicraft.level.tile.farming.FarmTile;
import minicraft.level.tile.farming.Plant;

public class FriendlyMob extends MobAi {

    protected int color;
    public int lvl;

    public FriendlyMob(MobSprite[][] sprites, int healthFactor) {
        super(sprites, 5 + healthFactor * Settings.getIndex("diff"), 5 * 60 * Updater.normalSpeed, 45, 40);
    }

    public void tick() {
        super.tick();

        Player player = getClosestPlayer();
        if (player != null) { // checks if player is on zombies level and if there is no time left on
                              // randonimity timer
            int xd = player.x - x;
            int yd = player.y - y;
            /// if player is less than 6.25 tiles away, then set move dir towards player
            int sig0 = 1; // this prevents too precise estimates, preventing mobs from bobbing up and
                          // down.
            xa = ya = 0;
            if (xd < sig0) xa = -1;
            if (xd > sig0) xa = +1;
            if (yd < sig0) ya = -1;
            if (yd > sig0) ya = +1;
        } else {
            // if the enemy was following the player, but has now lost it, it stops moving.
            // *that would be nice, but I'll just make it move randomly instead.
            randomizeWalkDir(false);
        }
    }

    public void render(Screen screen) {
        super.render(screen);
    }

    protected void touchedBy(Entity entity) { // if an entity (like the player) touches the enemy mob
        super.touchedBy(entity);
        // hurts the player, damage is based on lvl.
        if (entity instanceof Player) {
            ((Player) entity).hurt(this, lvl * (Settings.get("diff").equals("Hard") ? 2 : 1));
        }
    }

    public boolean canAttack() {
        return true;
    }

    public void die() {
        super.die(15);
    }

    public static boolean checkStartPos(Level level, int x, int y) { // Find a place to spawn the mob
        int r = (level.depth == -4 ? (Game.isMode("score") ? 22 : 15) : 13);

        if (!MobAi.checkStartPos(level, x, y, 60, r)) {
            return false;
        }

        Tile tile = level.getTile(x >> 4, y >> 4);

        // Check for depth to avoid unnecessary checks
        if (level.depth != -4) {
            if (tile instanceof DoorTile || tile instanceof WallTile|| tile instanceof Plant || tile instanceof FarmTile || tile instanceof MyceliumTile) {
                return false;
            }
            return !level.isLight(x >> 4, x >> 4);
        }
        return false;
    }

    public int getMaxLevel() {
        return 1;
    }
}