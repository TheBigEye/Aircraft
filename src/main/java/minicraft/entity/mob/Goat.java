package minicraft.entity.mob;

import minicraft.core.io.Settings;
import minicraft.graphic.MobSprite;
import minicraft.item.Items;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

public class Goat extends FrostMob {
    private static final MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(10, 42);

    /**
     * Creates the cow with the right sprites and color.
     */
    public Goat() {
        super(sprites, 5);
    }

    public void tick() {
        super.tick();

		// follows to the player if holds wheat
		followOnHold(Items.get("Wheat"), 3);
		
		if (tickTime /16 %24 == 0) {
	        Tile tile = level.getTile(x >> 4, y >> 4);
	        if (tile == Tiles.get("Grass") || tile == Tiles.get("Sand") || tile == Tiles.get("Lawn") || tile == Tiles.get("Rose") || tile == Tiles.get("Daisy") || tile == Tiles.get("Dandelion") || tile == Tiles.get("Poppy")) {
	            this.remove();
	            level.add(new Sheep(), x, y);
	
	        }
		}
    }

    public void die() {
        int min = 0;
        int max = 0;

        if (Settings.get("diff").equals("Peaceful")) {
            min = 1;
            max = 3;
        } else if (Settings.get("diff").equals("Easy") || Settings.get("diff").equals("Normal")) {
            min = 1;
            max = 2;
        } else if (Settings.get("diff").equals("Hard")) {
            min = 0;
            max = 1;
        }

        this.dropItem(min, max, Items.get("Leather"));

        super.die();
    }
}
