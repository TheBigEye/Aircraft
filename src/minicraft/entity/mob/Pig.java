package minicraft.entity.mob;

import minicraft.core.io.Settings;
import minicraft.entity.XpOrb;
import minicraft.gfx.MobSprite;
import minicraft.item.Items;

public class Pig extends PassiveMob {
    private static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(0, 30);

    /**
     * Creates a pig.
     */
    public Pig() {
        super(sprites);
    }

    public void tick() {
        super.tick();

        Player player = getClosestPlayer();
        if (player != null && player.activeItem != null && player.activeItem.name.equals("Carrot")) { //This function will make the entity follow the player directly
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

        level.add(new XpOrb(random.nextInt(17) , x , y), x, y);
        
        if (isBurn) dropItem(min, max, Items.get("Cooked pork")); // if isBurn.. drop Cooked food
        if (!isBurn) dropItem(min, max, Items.get("raw pork")); // else, drop normal food

        super.die();
    }
}