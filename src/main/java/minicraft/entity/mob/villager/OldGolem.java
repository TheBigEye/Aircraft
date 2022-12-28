package minicraft.entity.mob.villager;

import minicraft.core.Game;
import minicraft.core.io.Settings;
import minicraft.entity.mob.EnemyMob;
import minicraft.gfx.MobSprite;
import minicraft.item.Items;

public class OldGolem extends EnemyMob {
    private static MobSprite[][][] sprites;
    static {
        sprites = new MobSprite[4][4][2];
        for (int i = 0; i < 4; i++) {
            MobSprite[][] list = MobSprite.compileMobSpriteAnimations(30, 0);
            sprites[i] = list;
        }
    }

    /**
     * Creates a bad golem of the given level.
     * 
     * @param lvl Golem's level.
     */
    public OldGolem(int lvl) {
        super(lvl, sprites, 3, 100);
    }

    @Override
    public void tick() {
        super.tick();
        
        if (Game.isMode("Creative")) {
        	return;
        }
    }

    public void die() {
    	if (Settings.get("diff").equals("Peaceful")) dropItem(2, 2, Items.get("Gear"));
        if (Settings.get("diff").equals("Easy")) dropItem(2, 2, Items.get("Gear"));
        if (Settings.get("diff").equals("Normal")) dropItem(1, 2, Items.get("Gear"));
        if (Settings.get("diff").equals("Hard")) dropItem(1, 1, Items.get("Gear"));

        if (random.nextInt(60) == 2) {
            level.dropItem(x, y, Items.get("Iron"));
        }

        if (random.nextInt(40) == 19) {
            int rand = random.nextInt(3);
            if (rand == 0) {
                level.dropItem(x, y, Items.get("Iron"));
            } else if (rand == 1) {
                level.dropItem(x, y, Items.get("Gold"));
            } else if (rand == 2) {
                level.dropItem(x, y, Items.get("Gem"));
            }
        }

        super.die();
    }
}
