package minicraft.entity.mob;

import minicraft.core.io.Settings;
import minicraft.graphic.MobSprite;
import minicraft.item.Items;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

public class GuiMan extends FrostMob {
    private static final MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(10, 44);

    /**
     * Creates a Penguin Man.
     */
    public GuiMan() {
        super(sprites);
    }

	public void tick() {
	    super.tick();

		// follows to the player if holds raw fish
	    followOnHold(Items.get("Raw Fish"), 2);
	    
        Tile tile = level.getTile(x >> 4, y >> 4);
        if (tile == Tiles.get("Grass") || tile == Tiles.get("Sand")) {
            remove();
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

        dropItem(min, max, Items.get("Feather"));

        super.die();
    }
}
