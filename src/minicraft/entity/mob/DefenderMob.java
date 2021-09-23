package minicraft.entity.mob;

import minicraft.core.Game;
import minicraft.core.Updater;
import minicraft.core.io.Settings;
import minicraft.entity.Entity;
import minicraft.gfx.MobSprite;
import minicraft.gfx.Screen;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

public class DefenderMob extends MobAi {
    protected int color;

    /**
     * Constructor for a non-hostile (passive) mob.
     * healthFactor = 3.
     * @param sprites The mob's sprites.
     */
    public DefenderMob(MobSprite[][] sprites) {
        this(sprites, 3);
    }

    /**
     * Constructor for a non-hostile (passive) mob.
     * @param sprites The mob's sprites.
     * @param healthFactor Determines the mobs health. Will be multiplied by the difficulty
     * and then added with 5.
     */
    public DefenderMob(MobSprite[][] sprites, int healthFactor) {
        super(sprites, 5 + healthFactor * Settings.getIdx("diff"), 5 * 60 * Updater.normSpeed, 45, 40);
    }

    @Override
    public void render(Screen screen) {
        super.render(screen);
    }

    @Override
    protected void touchedBy(Entity entity) {

        if (Settings.get("diff").equals("Peaceful")) return;

        super.touchedBy(entity);
        if (entity instanceof Zombie) {
            ((Zombie) entity).hurt(this, 2 * (Settings.get("diff").equals("Hard") ? 3 : 1));
        }
        if (entity instanceof Skeleton) {
            ((Skeleton) entity).hurt(this, 2 * (Settings.get("diff").equals("Hard") ? 3 : 1));
        }
        if (entity instanceof Creeper) {
            ((Creeper) entity).hurt(this, 2 * (Settings.get("diff").equals("Hard") ? 3 : 1));
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
     * @param level The level which the mob wants to spawn in.
     * @param x X map spawn coordinate.
     * @param y Y map spawn coordinate.
     * @return true if the mob can spawn here, false if not.
     */
    public static boolean checkStartPos(Level level, int x, int y) {

        int r = (Game.isMode("score") ? 22 : 15) + (Updater.getTime() == Updater.Time.Night ? 0 : 10); // get no-mob radius by

        if (!MobAi.checkStartPos(level, x, y, 16, r))
            return false;

        Tile tile = level.getTile(x >> 4, y >> 4);
        if (tile != Tiles.get("Wood Planks") && tile != Tiles.get("wood planks") && tile != Tiles.get("Path") &&
            tile != Tiles.get("path")) {
            return false;
        } else if (tile != Tiles.get("Grass") && tile != Tiles.get("Sand") && tile != Tiles.get("Snow") &&
            tile != Tiles.get("birch tree") && tile != Tiles.get("tree") && tile != Tiles.get("flower") &&
            tile != Tiles.get("water") && tile != Tiles.get("wheat") && tile != Tiles.get("farmland")) {
            return true;
        } else
            return false;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
}