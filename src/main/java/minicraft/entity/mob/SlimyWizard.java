package minicraft.entity.mob;

import minicraft.core.io.Settings;
import minicraft.graphic.MobSprite;
import minicraft.item.Items;

public class SlimyWizard extends EnemyMob {
    private static MobSprite[][][] sprites;
    static {
        sprites = new MobSprite[4][4][2];
        for (int i = 0; i < 4; i++) {
            MobSprite[][] list = MobSprite.compileMobSpriteAnimations(30, 14 + (i * 2));
            sprites[i] = list;
        }
    }

    /**
     * Creates a Slimy wizard of the given level.
     * 
     * @param lvl Boss level.
     */
    public SlimyWizard(int lvl) {
        super(lvl, sprites, 5, 100);
    }

    @Override
    public void tick() {
        super.tick();

        Player player = getClosestPlayer();
        if (player == null) {
            randomizeWalkDir(false);
        } else {
            int xd = player.x - x;
            int yd = player.y - y;
            
            int sig0 = 1;
            xa = ya = 0;
            if (xd < sig0) xa = -1;
            if (xd > sig0) xa = +1;
            if (yd < sig0) ya = -1;
            if (yd > sig0) ya = +1;
        }
    }

    @Override
    public void die() {
    	int min = 0; int max = 0;
        String difficulty = (String) Settings.get("diff");

        if (difficulty == "Peaceful" || difficulty == "Easy") {
        	min = 2; max = 30;
        } else if (difficulty == "Normal") {
        	min = 2; max = 20;
        } else if (difficulty == "Hard") {
        	min = 1; max = 10;
        }
        
        for (int i= 0; i < 5; i++) {
        	level.dropItem(x, y, Items.get("Sticky essence"));
        }
     
        level.dropItem(x, y, Items.get("green clothes"));
        dropItem(min, max, Items.get("slime"));
        
        super.die();
    }
}
