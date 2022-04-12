package minicraft.entity.mob.villager;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

import minicraft.core.Game;
import minicraft.entity.mob.Player;
import minicraft.gfx.MobSprite;
import minicraft.item.Recipe;
import minicraft.screen.CraftingDisplay;

public class Cleric extends VillagerMob {
	private static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(0, 46);

	public static final ArrayList<Recipe> ClericTrdes = new ArrayList<>();

	static {
		LocalDateTime time = LocalDateTime.now();

		// Festive tardes
		if (time.getMonth() == Month.JANUARY) {
			ClericTrdes.add(new Recipe("Apple_5", new String[] { "Emerald_5" }));
			ClericTrdes.add(new Recipe("Gold Carrot_5", new String[] { "Gold_5" }));
			ClericTrdes.add(new Recipe("Gold Apple_5", new String[] { "Gold_5" }));
			ClericTrdes.add(new Recipe("Frozen Palette_2", new String[] { "Gold_2" }));
			ClericTrdes.add(new Recipe("Key_3", new String[] { "Gold_3" }));

			// San valentin :)
			if (time.getDayOfMonth() == 14) {
				ClericTrdes.add(new Recipe("Rose_1", new String[] { "Gold_1" }));
				ClericTrdes.add(new Recipe("Rose_1", new String[] { "Gold_1" }));
				ClericTrdes.add(new Recipe("Rose_1", new String[] { "Gold_1" }));
			}
		}

		// hmm ?
		if (time.getMonth() == Month.FEBRUARY) {
			ClericTrdes.add(new Recipe("Sticky essence_1", new String[] { "Emerald_2" }));
			ClericTrdes.add(new Recipe("Gaseous essence_5", new String[] { "Gold_1" }));
			ClericTrdes.add(new Recipe("Master essence_5", new String[] { "Gold_1" }));
			ClericTrdes.add(new Recipe("GunPowder_4", new String[] { "Gold_1" }));
			ClericTrdes.add(new Recipe("cloth_3", new String[] { "Gold_1" }));

			// science
			if (time.getDayOfMonth() == 11) {
				ClericTrdes.add(new Recipe("Brewery_1", new String[] { "Gem_1" }));
			}
		}

		// War trades
		if (time.getMonth() == Month.MARCH) {
			ClericTrdes.add(new Recipe("ChainMail Armor_1", new String[] { "Emerald_1" }));
			ClericTrdes.add(new Recipe("Rock Sword_1", new String[] { "Gold_1" }));
			ClericTrdes.add(new Recipe("Snake Armor_1", new String[] { "Emerald_2" }));
			ClericTrdes.add(new Recipe("iron Sword_1", new String[] { "Gold_4" }));
			ClericTrdes.add(new Recipe("Leather Armor_1", new String[] { "Emerald_3" }));
			ClericTrdes.add(new Recipe("Gem Sword_1", new String[] { "Gold_8" }));

			// San patricio day
			if (time.getDayOfMonth() == 17) {
				ClericTrdes.add(new Recipe("Green Clothes_1", new String[] { "Iron_3" }));
				ClericTrdes.add(new Recipe("Green Clothes_1", new String[] { "Iron_3" }));
				ClericTrdes.add(new Recipe("Green Clothes_1", new String[] { "Iron_3" }));
			}
		}

		// ???
		if (time.getMonth() == Month.APRIL) {
			ClericTrdes.add(new Recipe("Apple_10", new String[] { "Emerald_2" }));
			ClericTrdes.add(new Recipe("Emerald_10", new String[] { "Gem_1" }));
			ClericTrdes.add(new Recipe("Iron_10", new String[] { "Gold_1" }));
			ClericTrdes.add(new Recipe("stone_16", new String[] { "Gold_1" }));
			ClericTrdes.add(new Recipe("sand_16", new String[] { "Gold_1" }));
			ClericTrdes.add(new Recipe("carrot_3", new String[] { "Gold_1" }));

			if (time.getDayOfMonth() == 17) {
				ClericTrdes.add(new Recipe("Blue Clothes_1", new String[] { "Iron_3" }));
				ClericTrdes.add(new Recipe("Lapiz_10", new String[] { "Iron_3" }));
				ClericTrdes.add(new Recipe("Blue Wool_10", new String[] { "Iron_3" }));
			}
			if (time.getDayOfMonth() == 1) {
				ClericTrdes.add(new Recipe("dirt_1", new String[] { "Gem_5" }));
			}
		}

		// ???
		if (time.getMonth() == Month.MAY) {
			ClericTrdes.add(new Recipe("Wood_16", new String[] { "Iron_1" }));
			ClericTrdes.add(new Recipe("Dirt_16", new String[] { "Iron_1" }));
			ClericTrdes.add(new Recipe("Stone_8", new String[] { "Gold_1" }));

			if (time.getDayOfMonth() == 6) {
				ClericTrdes.add(new Recipe("dirt_1", new String[] { "Gem_5" }));
			}
		}

		// ???
		if (time.getMonth() == Month.JUNE) {
			ClericTrdes.add(new Recipe("Iron_1", new String[] { "Torch_8" }));
			ClericTrdes.add(new Recipe("Iron_1", new String[] { "coal_8" }));
			ClericTrdes.add(new Recipe("Gold_1", new String[] { "Stick_16" }));

			if (time.getDayOfMonth() == 6) {
				ClericTrdes.add(new Recipe("Iron_3", new String[] { "Yellow Clothes_1" }));
				ClericTrdes.add(new Recipe("Iron_3", new String[] { "Gold Apple_2" }));
				ClericTrdes.add(new Recipe("Iron_3", new String[] { "Gold Carrot_2" }));
			}
		}

		if (time.getMonth() == Month.JULY) {
			ClericTrdes.add(new Recipe("Apple_10", new String[] { "Emerald_1" }));
			ClericTrdes.add(new Recipe("Emerald_10", new String[] { "Gem_1" }));
			ClericTrdes.add(new Recipe("Iron_10", new String[] { "Gold_1" }));

			if (time.getDayOfMonth() == 17) {
				ClericTrdes.add(new Recipe("Blue Clothes_1", new String[] { "Iron_3" }));
				ClericTrdes.add(new Recipe("Lapiz_10", new String[] { "Iron_3" }));
				ClericTrdes.add(new Recipe("Blue Wool_10", new String[] { "Iron_3" }));
			}
			if (time.getDayOfMonth() == 1) {
				ClericTrdes.add(new Recipe("dirt_1", new String[] { "Gem_5" }));
			}
		}

		// ???
		if (time.getMonth() == Month.AUGUST) {
			ClericTrdes.add(new Recipe("Wood_16", new String[] { "Iron_1" }));
			ClericTrdes.add(new Recipe("Dirt_16", new String[] { "Iron_1" }));
			ClericTrdes.add(new Recipe("Stone_8", new String[] { "Gold_1" }));

			if (time.getDayOfMonth() == 6) {
				ClericTrdes.add(new Recipe("dirt_1", new String[] { "Gem_5" }));
			}
		}

		if (time.getMonth() == Month.SEPTEMBER) {
			ClericTrdes.add(new Recipe("Wood_16", new String[] { "Iron_1" }));
			ClericTrdes.add(new Recipe("Dirt_16", new String[] { "Iron_1" }));
			ClericTrdes.add(new Recipe("Stone_8", new String[] { "Gold_1" }));

			if (time.getDayOfMonth() == 6) {
				ClericTrdes.add(new Recipe("dirt_1", new String[] { "Gem_5" }));
			}
		}

		if (time.getMonth() == Month.OCTOBER) {
			ClericTrdes.add(new Recipe("Wood_16", new String[] { "Iron_1" }));
			ClericTrdes.add(new Recipe("Dirt_16", new String[] { "Iron_1" }));
			ClericTrdes.add(new Recipe("Stone_8", new String[] { "Gold_1" }));

			if (time.getDayOfMonth() == 6) {
				ClericTrdes.add(new Recipe("dirt_1", new String[] { "Gem_5" }));
			}
		}

		if (time.getMonth() == Month.NOVEMBER) {
			ClericTrdes.add(new Recipe("Wood_16", new String[] { "Iron_1" }));
			ClericTrdes.add(new Recipe("Dirt_16", new String[] { "Iron_1" }));
			ClericTrdes.add(new Recipe("Stone_8", new String[] { "Gold_1" }));

			if (time.getDayOfMonth() == 6) {
				ClericTrdes.add(new Recipe("dirt_1", new String[] { "Gem_5" }));
			}
		}

		if (time.getMonth() == Month.DECEMBER) {
			ClericTrdes.add(new Recipe("Wood_16", new String[] { "Iron_1" }));
			ClericTrdes.add(new Recipe("Dirt_16", new String[] { "Iron_1" }));
			ClericTrdes.add(new Recipe("Stone_8", new String[] { "Gold_1" }));

			if (time.getDayOfMonth() == 6) {
				ClericTrdes.add(new Recipe("dirt_1", new String[] { "Gem_5" }));
			}
		}
	}

	public Cleric() {
		super(sprites);
	}

	public void tick() {
		super.tick();
	}

	public boolean use(Player player) {
		Game.setDisplay(new CraftingDisplay(Cleric.ClericTrdes, "Trade", player));
		return true;
	}

	public void die() {
		super.die();
	}
}
