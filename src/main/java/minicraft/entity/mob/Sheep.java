package minicraft.entity.mob;

import minicraft.core.io.Settings;
import minicraft.entity.Direction;
import minicraft.graphic.MobSprite;
import minicraft.graphic.Screen;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.tile.GrassTile;
import minicraft.level.tile.SnowTile;
import minicraft.level.tile.Tiles;
import org.jetbrains.annotations.Nullable;

public class Sheep extends PassiveMob {
	private static final MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(0, 42);
	private static final MobSprite[][] cutSprites = MobSprite.compileMobSpriteAnimations(0, 44);

	private static final String[] sounds = new String[] {"sheepSay1", "sheepSay2", "sheepSay3"};

	// Cut
	public boolean sheared = false;
	private int ageWhenCut = 0;

	/**
	 * Creates a sheep entity.
	 */
	public Sheep() {
		super(sprites);
	}

	@Override
	public void render(Screen screen) {
		int xo = x - 8;
		int yo = y - 11;

		MobSprite[][] currenFrame = sheared ? cutSprites : sprites;

		MobSprite currentSprite = currenFrame[dir.getDir()][(walkDist >> 3) % currenFrame[dir.getDir()].length];
		if (hurtTime > 0) {
			currentSprite.render(screen, xo, yo, true);
		} else {
			currentSprite.render(screen, xo, yo);
		}
	}

	@Override
	public void tick() {
		super.tick();

		if (random.nextInt(1000) == 0 && sheared) { // Grazing
			// If tall grasses are present, these are consumed and then turn into grass tiles.
			if (level.getTile(x >> 4, y >> 4) instanceof GrassTile) {
				level.setTile(x >> 4, y >> 4, Tiles.get("dirt"));
				sheared = false;
			}
		}

		// follows to the player if holds wheat
		followOnHold(Items.get("Wheat"), 4);

		if ((tickTime % 200 == 0)) {
			if (level.getTile(x >> 4, y >> 4) instanceof SnowTile) {
				level.add(new Goat(), x, y);
				remove();
			} else if (random.nextInt(4) == 0) {
				playSound(sounds, 7);
			}
		}
	}

	@Override
	public boolean interact(Player player, @Nullable Item item, Direction attackDir) {
		if (sheared) return false;

		if (item instanceof ToolItem) {
			if (((ToolItem) item).type == ToolType.Shears) {
				dropItem(1, 3, Items.get("Wool"));
				((ToolItem) item).payDurability();
				sheared = true;
				ageWhenCut = age;
				return true;
			}
		}
		return false;
	}

	@Override
	public void die() {
		int min = 0, max = 0;
		String difficulty = (String) Settings.get("diff");

        if (difficulty == "Peaceful" || difficulty == "Easy") { min = 1; max = 3; }
        if (difficulty == "Normal") { min = 1; max = 2; }
        if (difficulty == "Hard") { min = 0; max = 2; }

	    if (!sheared) {
	        dropItem(min, max, Items.get("Wool"));
	    }

	    dropItem(min, max, Items.get("Raw beef"));

		super.die();
	}
}
