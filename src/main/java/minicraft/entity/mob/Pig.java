package minicraft.entity.mob;

import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.gfx.MobSprite;
import minicraft.item.Items;

public class Pig extends PassiveMob {
    private static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(0, 30);
    private int tickTime = 0;

    /**
     * Creates a pig.
     */
    public Pig() {
        super(sprites);
    }

    public void tick() {
        super.tick();
        tickTime++;

        Player player = getClosestPlayer();
        if (player != null && player.activeItem != null && player.activeItem.name.equals("Carrot")) {
            int xd = player.x - x;
            int yd = player.y - y;

            int sig = 1;
            xa = ya = 0;

            if (xd < sig) xa = -1;
            if (xd > sig) xa = +1;
            if (yd < sig) ya = -1;
            if (yd > sig) ya = +1;
        } else {
            randomizeWalkDir(false);
        }
        
		// Pig sounds
		if (tickTime / 8 % 16 == 0 && random.nextInt(8) == 4) {
			if (random.nextBoolean()) {
				if (!random.nextBoolean()) {
					Sound.pigSay1.playOnWorld(x, y, player.x, player.y);
				} else {
					Sound.pigSay2.playOnWorld(x, y, player.x, player.y);
				}
			} else {
				Sound.pigSay3.playOnWorld(x, y, player.x, player.y);
			}
		}
    }

    public void die() {
        int min = 0, max = 0;
        if (Settings.get("diff").equals("Peaceful")) {
            min = 1;
            max = 3;
        }
        if (Settings.get("diff").equals("Easy")) {
            min = 1;
            max = 3;
        }
        if (Settings.get("diff").equals("Normal")) {
            min = 1;
            max = 2;
        }
        if (Settings.get("diff").equals("Hard")) {
            min = 0;
            max = 2;
        }

        // level.add(new XpOrb(random.nextInt(17), x, y), x, y);

        if (isBurn) dropItem(min, max, Items.get("Cooked pork")); // if isBurn.. drop Cooked food
        if (!isBurn) dropItem(min, max, Items.get("raw pork")); // else, drop normal food

        super.die();
    }
}