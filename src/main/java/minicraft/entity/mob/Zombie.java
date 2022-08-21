package minicraft.entity.mob;

import minicraft.core.io.Settings;
import minicraft.gfx.MobSprite;
import minicraft.item.Items;

public class Zombie extends EnemyMob {
    private static MobSprite[][][] sprites;
    private static int DAMAGE = 1;

    static {
        sprites = new MobSprite[4][4][2];
        for (int i = 0; i < 4; i++) {
            MobSprite[][] list = MobSprite.compileMobSpriteAnimations(8, 0 + (i * 2));
            sprites[i] = list;
        }
    }

    /**
     * Creates a zombie of the given level.
     * 
     * @param lvl Zombie's level.
     */
    public Zombie(int lvl) {
        super(lvl, sprites, DAMAGE, 100);
    }

    @Override
    public void tick() {
        super.tick();

        if (random.nextInt(5) == 1) {

            if (random.nextInt(2) == 0) {
                DAMAGE = 2;
            }
            if (random.nextInt(2) == 1) {
                DAMAGE = 1;
            }
        }

    }

    public void die() {
        if (Settings.get("diff").equals("Peaceful"))
            dropItem(2, 4, Items.get("cloth"));
        if (Settings.get("diff").equals("Easy"))
            dropItem(2, 4, Items.get("cloth"));
        if (Settings.get("diff").equals("Normal"))
            dropItem(1, 3, Items.get("cloth"));
        if (Settings.get("diff").equals("Hard"))
            dropItem(1, 2, Items.get("cloth"));

        if (random.nextInt(60) == 2) {
            level.dropItem(x, y, Items.get("iron"));
        }

        if (random.nextInt(100) < 4) {
            level.dropItem(x, y, Items.get("Potato"));
        }

        if (random.nextInt(255) < 4) {
            level.dropItem(x, y, Items.get("Carrot"));
        }

        if (random.nextInt(40) == 19) {
            int rand = random.nextInt(3);
            switch (rand) {
                case 0: level.dropItem(x, y, Items.get("green clothes")); break;
                case 1: level.dropItem(x, y, Items.get("red clothes")); break;
                case 2: level.dropItem(x, y, Items.get("blue clothes")); break;
                default:
                    break;
            }
        }
        
        super.die();
    }
 
}