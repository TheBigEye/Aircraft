package minicraft.entity.mob;

import java.util.Random;

import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.particle.HeartParticle;
import minicraft.gfx.MobSprite;
import minicraft.item.Items;

public class Cow extends PassiveMob {
    private static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(0, 24);

    private Random rnd = new Random();
    private int tickTime = 0;

    /**
     * Creates the cow with the right sprites and color.
     */
    public Cow() {
        super(sprites, 5);
    }

    public void tick() {
        super.tick();
        tickTime++;

        Player player = getClosestPlayer();
        if (player != null && player.activeItem != null && player.activeItem.name.equals("Wheat")) {

            // Render heart particles
            if (Settings.get("particles").equals(true)) {
                int randX = rnd.nextInt(10);
                int randY = rnd.nextInt(9);

                if (random.nextInt(12) == 0) {
                    level.add(new HeartParticle(x - 9 + randX, y - 12 + randY));
                }
                if (random.nextInt(12) == 12) {
                    level.add(new HeartParticle(x - 9 + randX, y - 12 + randY));
                }
            }

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
            randomizeWalkDir(false);
        }
        
		// Cow sounds
		if (tickTime / 8 % 16 == 0 && random.nextInt(8) == 4) {
			if (random.nextBoolean()) {
				if (!random.nextBoolean()) {
					Sound.cowSay1.playOnWorld(x, y, player.x, player.y);
				} else {
					Sound.cowSay2.playOnWorld(x, y, player.x, player.y);
				}
			} else {
				Sound.cowSay3.playOnWorld(x, y, player.x, player.y);
			}
		}
    }

    public void die() {
        int min = 0, max = 0;

        if (Settings.get("diff").equals("Peaceful")) {min = 1; max = 3;}
        if (Settings.get("diff").equals("Easy")) {min = 1; max = 3;}
        if (Settings.get("diff").equals("Normal")) {min = 1; max = 2;}
        if (Settings.get("diff").equals("Hard")) {min = 0; max = 1;}

        if (isBurn) dropItem(min, max, Items.get("Steak"));
        if (!isBurn) dropItem(min, max, Items.get("leather"), Items.get("raw beef"));

        super.die();
    }
}