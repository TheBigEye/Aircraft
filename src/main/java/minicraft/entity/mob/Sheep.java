package minicraft.entity.mob;

import org.jetbrains.annotations.Nullable;

import minicraft.core.Updater;
import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.gfx.MobSprite;
import minicraft.gfx.Screen;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

public class Sheep extends PassiveMob {
	private static final MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(0, 42);
	private static final MobSprite[][] cutSprites = MobSprite.compileMobSpriteAnimations(0, 44);

	private static final int WOOL_GROW_TIME = (3 * 60) * Updater.normalSpeed; // Three minutes

	// Cut
	public boolean sheared = false;
	private int ageWhenCut = 0;
	
	private int tickTime = 0;

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

		MobSprite[][] curAnim = sheared ? cutSprites : sprites;

		MobSprite currentSprite = curAnim[dir.getDir()][(walkDist >> 3) % curAnim[dir.getDir()].length];
		if (hurtTime > 0) {
			currentSprite.render(screen, xo, yo, true);
		} else {
			currentSprite.render(screen, xo, yo);
		}
	}

	public void tick() {
		super.tick();
		tickTime++;

		if (age - ageWhenCut > WOOL_GROW_TIME) {
			sheared = false;
		}

		// follows to the player if holds wheat
		followOnHold(3, "Wheat", false);

		Tile tile = level.getTile(x >> 4, y >> 4);
		if (tile == Tiles.get("Snow")) {
			remove();
			level.add(new Goat(), x, y);
		}
		
		// Sheep sounds
		if (tickTime / 8 % 24 == 0 && random.nextInt(8) == 4) {
			if (random.nextBoolean()) {
				if (!random.nextBoolean()) {
					Sound.sheepSay1.playOnWorld(x, y);
				} else {
					Sound.sheepSay2.playOnWorld(x, y);
				}
			} else {
				Sound.sheepSay3.playOnWorld(x, y);
			}
		}
		
	}

	public boolean interact(Player player, @Nullable Item item, Direction attackDir) {
		if (sheared) return false;

		if (item instanceof ToolItem) {
			if (((ToolItem) item).type == ToolType.Shears) {
				sheared = true;
				dropItem(1, 3, Items.get("Wool"));
				((ToolItem) item).payDurability();
				ageWhenCut = age;
				return true;
			}
		}
		return false;
	}

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