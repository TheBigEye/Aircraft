package minicraft.entity.mob;

import java.util.Random;

import minicraft.core.Game;
import minicraft.core.io.Settings;
import minicraft.entity.particle.HeartParticle;
import minicraft.gfx.MobSprite;
import minicraft.item.Items;

public class Chicken extends PassiveMob {
    private static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(0, 32);

    private Random rnd = new Random();

    /**
     * Creates a Chicken.
     */

    public Chicken() {
        super(sprites);
    }

    public void tick() {
        super.tick();

        // Drop eggs each 15 secs
        int min = 0;
        int max = 0;

        if (Settings.get("diff").equals("Peaceful")) {min = 1;max = 2;}
        if (Settings.get("diff").equals("Easy")) {min = 1; max = 2;}
        if (Settings.get("diff").equals("Normal")) {min = 1; max = 1;}
        if (Settings.get("diff").equals("Hard")) {min = 0; max = 1;}

        if (random.nextInt(3000) == 1 && Game.isMode("Survival")) { // drop eggs each 15 secs
            dropItem(min, max, Items.get("egg"));
        }

        Player player = getClosestPlayer();
        if (player != null && player.activeItem != null && player.activeItem.name.equals("Seeds")) {

            // Render heart particles
            int randX = rnd.nextInt(10);
            int randY = rnd.nextInt(9);

            if (random.nextInt(12) == 0) {
                level.add(new HeartParticle(x - 9 + randX, y - 12 + randY));
            }
            if (random.nextInt(12) == 12) {
                level.add(new HeartParticle(x - 9 + randX, y - 12 + randY));
            }

            int xd = player.x - x;
            int yd = player.y - y;

            int sig0 = 1;
            xa = ya = 0;
            if (xd < sig0) xa = -1;
            if (xd > sig0) xa = +1;
            if (yd < sig0) ya = -1;
            if (yd > sig0) ya = +1;
            
        } else {
            randomizeWalkDir(false);
        }
    }

    public void die() {
        int min = 0, max = 0;

        if (Settings.get("diff").equals("Passive")) {min = 1; max = 2;}
        if (Settings.get("diff").equals("Easy")) {min = 1; max = 2;}
        if (Settings.get("diff").equals("Normal")) {min = 1; max = 1;}
        if (Settings.get("diff").equals("Hard")) {min = 0; max = 1;}

        if (isBurn) dropItem(min, max, Items.get("Cooked Chicken"));
        if (!isBurn) dropItem(min, max, Items.get("raw chicken"));
        if (!isBurn) dropItem(min, max, Items.get("feather"));

        super.die();
    }
}