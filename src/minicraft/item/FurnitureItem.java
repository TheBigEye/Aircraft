package minicraft.item;

import java.util.ArrayList;

import minicraft.core.Game;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.furniture.Bed;
import minicraft.entity.furniture.Chest;
import minicraft.entity.furniture.Crafter;
import minicraft.entity.furniture.DungeonChest;
import minicraft.entity.furniture.Furniture;
import minicraft.entity.furniture.Lantern;
import minicraft.entity.furniture.Spawner;
import minicraft.entity.furniture.Tnt;
import minicraft.entity.furniture.statue.SkeletonStatue;
import minicraft.entity.furniture.statue.SlimeStatue;
import minicraft.entity.furniture.statue.ZombieStatue;
import minicraft.entity.mob.Chicken;
import minicraft.entity.mob.Cow;
import minicraft.entity.mob.Creeper;
import minicraft.entity.mob.Goat;
import minicraft.entity.mob.GuiMan;
import minicraft.entity.mob.Keeper;
import minicraft.entity.mob.Knight;
import minicraft.entity.mob.Pig;
import minicraft.entity.mob.Player;
import minicraft.entity.mob.Sheep;
import minicraft.entity.mob.Skeleton;
import minicraft.entity.mob.Slime;
import minicraft.entity.mob.Snake;
import minicraft.entity.mob.Zombie;
import minicraft.entity.mob.boss.AirWizard;
import minicraft.entity.mob.boss.EyeQueen;
import minicraft.entity.mob.boss.KingZombie;
import minicraft.entity.mob.villager.Cleric;
import minicraft.entity.mob.villager.Golem;
import minicraft.entity.mob.villager.Librarian;
import minicraft.entity.mob.villager.OldGolem;
import minicraft.gfx.Sprite;
import minicraft.level.Level;
import minicraft.level.tile.Tile;

public class FurnitureItem extends Item {

	protected static ArrayList<Item> getAllInstances() {
		ArrayList<Item> items = new ArrayList<>();

		/// there should be a spawner for each level of mob, or at least make the level
		/// able to be changed.
		// items.add(new FurnitureItem(new CommandBlock()));

		//items.add(new FurnitureItem(new Spawner(new KingZombie(1))));
		items.add(new FurnitureItem(new Spawner(new Cow())));
		items.add(new FurnitureItem(new Spawner(new Pig())));
		items.add(new FurnitureItem(new Spawner(new Sheep())));
		items.add(new FurnitureItem(new Spawner(new Goat())));
		items.add(new FurnitureItem(new Spawner(new Chicken())));
		items.add(new FurnitureItem(new Spawner(new GuiMan())));
		items.add(new FurnitureItem(new Spawner(new Cleric())));
		items.add(new FurnitureItem(new Spawner(new Librarian())));
		items.add(new FurnitureItem(new Spawner(new Golem())));
		items.add(new FurnitureItem(new Spawner(new Slime(1))));
		items.add(new FurnitureItem(new Spawner(new Zombie(1))));
		items.add(new FurnitureItem(new Spawner(new Creeper(1))));
		items.add(new FurnitureItem(new Spawner(new Skeleton(1))));
		items.add(new FurnitureItem(new Spawner(new Snake(1))));
		items.add(new FurnitureItem(new Spawner(new Knight(1))));
		items.add(new FurnitureItem(new Spawner(new OldGolem(1))));
		
		// Air Bosses
		items.add(new FurnitureItem(new Spawner(new AirWizard(false))));
		
		// Principal Bosses
		items.add(new FurnitureItem(new Spawner(new EyeQueen(1))));
		items.add(new FurnitureItem(new Spawner(new Keeper(1))));

		items.add(new FurnitureItem(new Chest()));
		items.add(new FurnitureItem(new DungeonChest(false, true)));
		
		// add the various types of crafting furniture
		for (Crafter.Type type : Crafter.Type.values()) {
			items.add(new FurnitureItem(new Crafter(type)));
		}

		// add the various lanterns
		for (Lantern.Type type : Lantern.Type.values()) {
			items.add(new FurnitureItem(new Lantern(type)));
		}

		items.add(new FurnitureItem(new Tnt()));
		items.add(new FurnitureItem(new Bed()));

		items.add(new FurnitureItem(new SlimeStatue()));
		items.add(new FurnitureItem(new ZombieStatue()));
		items.add(new FurnitureItem(new SkeletonStatue()));

		return items;
	}

	public Furniture furniture; // the furniture of this item
	public boolean placed; // value if the furniture has been placed or not.

	private static int getSpritePos(int fpos) {
		int x = fpos % 32;
		int y = fpos / 32;
		return ((x - 8) / 2) + y * 32;
	}

	public FurnitureItem(Furniture furniture) {
		super(furniture.name, new Sprite(getSpritePos(furniture.sprite.getPos()), 0));
		this.furniture = furniture; // Assigns the furniture to the item
		placed = false;
	}

	/** Determines if you can attack enemies with furniture (you can't) */
	public boolean canAttack() {
		return false;
	}

	/**
	 * What happens when you press the "Attack" key with the furniture in your hands
	 */
	public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, Direction attackDir) {
		if (tile.mayPass(level, xt, yt, furniture)) { // If the furniture can go on the tile

			Sound.place.play();

			// Placed furniture's X and Y positions
			furniture.x = xt * 16 + 8;
			furniture.y = yt * 16 + 8;
			level.add(furniture); // adds the furniture to the world
			if (Game.isMode("creative"))
				furniture = furniture.clone();
			else
				placed = true; // the value becomes true, which removes it from the player's active item

			return true;
		}
		return false;
	}

	public boolean isDepleted() {
		return placed;
	}

	public FurnitureItem clone() {
		return new FurnitureItem(furniture.clone());
	}
}
