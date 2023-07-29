package minicraft.entity.mob;

import minicraft.core.io.Settings;
import minicraft.graphic.MobSprite;
import minicraft.item.Items;

public class Golem extends DefenderMob {
    private static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(30, 2);

    /**
     * Creates a golem.
     */
    public Golem() {
        super(sprites);
    }

    public void die() {
        int min = 0;
        int max = 0;

        if (Settings.get("diff").equals("Easy")) {
            min = 0;
            max = 0;
        }
        if (Settings.get("diff").equals("Normal")) {
            min = 0;
            max = 1;
        }
        if (Settings.get("diff").equals("Hard")) {
            min = 1;
            max = 1;
        }

        dropItem(min, max, Items.get("Iron Ore"), Items.get("rose"));

        super.die();
    }
}
