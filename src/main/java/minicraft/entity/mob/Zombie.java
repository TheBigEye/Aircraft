package minicraft.entity.mob;

import minicraft.core.io.Settings;
import minicraft.gfx.MobSprite;
import minicraft.item.Items;

public class Zombie extends EnemyMob {
    private static MobSprite[][][] sprites;

    static {
        sprites = new MobSprite[4][4][2];
        for (int i = 0; i < 4; i++) {
            MobSprite[][] list = MobSprite.compileMobSpriteAnimations(10, 0 + (i * 2));
            sprites[i] = list;
        }
    }

    /**
     * Creates a zombie of the given level.
     * 
     * @param lvl Zombie's level.
     */
    public Zombie(int lvl) {
        super(lvl, sprites, 14, 100);
    }

    @Override
    public void tick() {
        super.tick();
    }

    public void die() {
    	int min = 0; int max = 0;
        String difficulty = (String) Settings.get("diff");

        if (difficulty == "Peaceful" || difficulty == "Easy") {
        	min = 2; max = 4;
        } else if (difficulty == "Normal") {
        	min = 1; max = 3;
        } else if (difficulty == "Hard") {
        	min = 1; max = 2;
        }
       
        if (random.nextInt(60) == 2) {
            level.dropItem(x, y, Items.get("iron"));
        }

        if (random.nextInt(100) < 4) {
            level.dropItem(x, y, Items.get("Potato"));
        }

        if (random.nextInt(200) < 4) {
            level.dropItem(x, y, Items.get("Carrot"));
        }
        
        dropItem(min, max, Items.get("cloth"));

        int rand = random.nextInt(3);
        if (random.nextInt(40) == 19) {
            switch (rand) {
                case 0: level.dropItem(x, y, Items.get("green clothes")); break;
                case 1: level.dropItem(x, y, Items.get("red clothes")); break;
                case 2: level.dropItem(x, y, Items.get("blue clothes")); break;
                default: break;
            }
        }
        
        super.die();
    }
 
}