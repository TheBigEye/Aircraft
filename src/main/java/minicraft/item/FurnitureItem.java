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
import minicraft.entity.mob.Cat;
import minicraft.entity.mob.Chicken;
import minicraft.entity.mob.Cow;
import minicraft.entity.mob.Creeper;
import minicraft.entity.mob.Firefly;
import minicraft.entity.mob.Goat;
import minicraft.entity.mob.GuiMan;
import minicraft.entity.mob.Keeper;
import minicraft.entity.mob.Knight;
import minicraft.entity.mob.MobAi;
import minicraft.entity.mob.Pig;
import minicraft.entity.mob.Player;
import minicraft.entity.mob.Sheep;
import minicraft.entity.mob.Skeleton;
import minicraft.entity.mob.Slime;
import minicraft.entity.mob.Snake;
import minicraft.entity.mob.Zombie;
import minicraft.entity.mob.boss.AirWizard;
import minicraft.entity.mob.boss.EyeQueen;
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

		// items.add(new FurnitureItem(new Spawner(new KingZombie(1))));
		items.add(new FurnitureItem(new Spawner(new Cow()), 1, 27));
		items.add(new FurnitureItem(new Spawner(new Pig()), 2, 27));
		items.add(new FurnitureItem(new Spawner(new Sheep()), 3, 27));
		items.add(new FurnitureItem(new Spawner(new Goat()), 4, 27));
		items.add(new FurnitureItem(new Spawner(new Chicken()), 5, 27));
		items.add(new FurnitureItem(new Spawner(new GuiMan()), 6, 27));
		items.add(new FurnitureItem(new Spawner(new Cat()), 7, 27));
		items.add(new FurnitureItem(new Spawner(new Cleric()), 8, 27));
		items.add(new FurnitureItem(new Spawner(new Librarian()), 9, 27));
		items.add(new FurnitureItem(new Spawner(new Golem()), 10, 27));
		items.add(new FurnitureItem(new Spawner(new Slime(1)), 11, 27));
		items.add(new FurnitureItem(new Spawner(new Zombie(1)), 12, 27));
		items.add(new FurnitureItem(new Spawner(new Creeper(1)), 13, 27));
		items.add(new FurnitureItem(new Spawner(new Skeleton(1)), 14, 27));
		items.add(new FurnitureItem(new Spawner(new Snake(1)), 15, 27));
		items.add(new FurnitureItem(new Spawner(new Knight(1)), 16, 27));
		items.add(new FurnitureItem(new Spawner(new OldGolem(1)), 17, 27));

		// Air Bosses
		items.add(new FurnitureItem(new Spawner(new AirWizard(false)), 18, 27));

		// Principal Bosses
		items.add(new FurnitureItem(new Spawner(new EyeQueen(1)), 19, 27));
		items.add(new FurnitureItem(new Spawner(new Keeper(1)), 20, 27));
		items.add(new FurnitureItem(new Spawner(new Firefly()), 20, 27));

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
	private int sx, sy; // Sprite position.

	private static int getSpritePos(int fpos) {
		int x = fpos % 32;
		int y = fpos / 32;
		return ((x - 8) / 2) + y * 32;
	}

	private static Sprite getFurnitureSprite(Furniture furniture) {
		Sprite sprite;
		if (furniture instanceof Spawner) {
			MobAi mob = ((Spawner)furniture).mob;
			if (mob instanceof Cow) sprite = new Sprite(1, 27, 1, 1, 0);
			else if (mob instanceof Pig) sprite = new Sprite(2, 27, 1, 1, 0);
			else if (mob instanceof Sheep) sprite = new Sprite(3, 27, 1, 1, 0);
			else if (mob instanceof Goat) sprite = new Sprite(4, 27, 1, 1, 0);
			else if (mob instanceof Chicken) sprite = new Sprite(5, 27, 1, 1, 0);
			else if (mob instanceof GuiMan) sprite = new Sprite(6, 27, 1, 1, 0);
			else if (mob instanceof Cat) sprite = new Sprite(7, 27, 1, 1, 0);
			else if (mob instanceof Cleric) sprite = new Sprite(8, 27, 1, 1, 0);
			else if (mob instanceof Librarian) sprite = new Sprite(9, 27, 1, 1, 0);
			else if (mob instanceof Golem) sprite = new Sprite(10, 27, 1, 1, 0);
			else if (mob instanceof Slime) sprite = new Sprite(11, 27, 1, 1, 0);
			else if (mob instanceof Zombie) sprite = new Sprite(12, 27, 1, 1, 0);
			else if (mob instanceof Creeper) sprite = new Sprite(13, 27, 1, 1, 0);
			else if (mob instanceof Skeleton) sprite = new Sprite(14, 27, 1, 1, 0);
			else if (mob instanceof Snake) sprite = new Sprite(15, 27, 1, 1, 0);
			else if (mob instanceof Knight) sprite = new Sprite(16, 27, 1, 1, 0);
			else if (mob instanceof OldGolem) sprite = new Sprite(17, 27, 1, 1, 0);
			else if (mob instanceof AirWizard) sprite = new Sprite(18, 27, 1, 1, 0);
			else if (mob instanceof EyeQueen) sprite = new Sprite(199, 27, 1, 1, 0);
			else if (mob instanceof Keeper) sprite = new Sprite(20, 27, 1, 1, 0);
			else if (mob instanceof Firefly) sprite = new Sprite(20, 27, 1, 1, 0);
			else sprite = new Sprite(getSpritePos(furniture.sprite.getPos()), 0);
		} else sprite = new Sprite(getSpritePos(furniture.sprite.getPos()), 0);
		return sprite;
	}

	public FurnitureItem(Furniture furniture) {
		super(furniture.name, getFurnitureSprite(furniture));
		this.furniture = furniture; // Assigns the furniture to the item
		placed = false;
	}

	public FurnitureItem(Furniture furniture, int sx , int sy) {
		super(furniture.name, new Sprite(sx, sy, 1, 1, 0)); // get the sprite directly
		this.sx = sx;
		this.sy = sy;
		this.furniture = furniture;
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

			Sound.Mob_player_place.play();

			// Placed furniture's X and Y positions
			furniture.x = xt * 16 + 8;
			furniture.y = yt * 16 + 8;
			level.add(furniture); // adds the furniture to the world
			if (Game.isMode("creative")) {
				furniture = furniture.clone();
			} else {
				placed = true; // the value becomes true, which removes it from the player's active item
			}
			return true;
		}
		return false;
	}

	public boolean isDepleted() {
		return placed;
	}

	public FurnitureItem clone() {
		// in case the item is a spawner, it will use the sprite position (sx, sy)
		// instead if it is not, the constructor will obtain said sprite
		if (furniture.name.contains("Spawner")) {
			return new FurnitureItem(furniture.clone(), sx, sy);
		} else {
			return new FurnitureItem(furniture.clone());
		}
	}
}
