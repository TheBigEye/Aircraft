package minicraft.entity.mob;

import minicraft.core.io.Settings;
import minicraft.entity.particle.HeartParticle;
import minicraft.gfx.MobSprite;
import minicraft.item.Items;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

public class Goat extends FrostMob {
    private static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(10, 42);

    /**
     * Creates the cow with the right sprites and color.
     */
    public Goat() {
        super(sprites, 5);
    }

    public void tick() {
        super.tick();

        Player player = getClosestPlayer();
        boolean holdingWheat = player != null && player.activeItem != null && player.activeItem.name.equals("Wheat");

	    // Render heart particles
	    if (Settings.get("particles").equals(true) && holdingWheat && random.nextInt(6) == 0) {
	        int randX = random.nextInt(10);
	        int randY = random.nextInt(9);
	        level.add(new HeartParticle(x - 9 + randX, y - 12 + randY));
	    }
 
        if (holdingWheat) {
            int xd = player.x - x;
            int yd = player.y - y;

            /// if player is less than 6.25 tiles away, then set move dir towards player
            int sig0 = 1; // this prevents too precise estimates, preventing mobs from bobbing up and down.
            xa = ya = 0;

            if (xd < sig0) xa = -1;
            else if (xd > sig0) xa = 1;
            if (yd < sig0) ya = -1;
            else if (yd > sig0) ya = 1;
        } else {
            randomizeWalkDir(false);
        }

        Tile tile = level.getTile(x >> 4, y >> 4);
        if (tile == Tiles.get("Grass") || tile == Tiles.get("Sand") || tile == Tiles.get("Lawn") || tile == Tiles.get("Flower")) {
            remove();
            level.add(new Sheep(), x, y);

        }
    }

    public void die() {
        int min = 0;
        int max = 0;

        if (Settings.get("diff").equals("Peaceful")) {
            min = 1;
            max = 3;
        }
        if (Settings.get("diff").equals("Easy")) {
            min = 1;
            max = 2;
        }
        if (Settings.get("diff").equals("Normal")) {
            min = 1;
            max = 2;
        }
        if (Settings.get("diff").equals("Hard")) {
            min = 0;
            max = 1;
        }

        dropItem(min, max, Items.get("Leather"));

        super.die();
    }
}
