package minicraft.entity.mob;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

import minicraft.core.Game;
import minicraft.graphic.MobSprite;
import minicraft.item.Recipe;
import minicraft.screen.CraftingDisplay;

public class Cleric extends VillagerMob {
	private static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(0, 50);

	private static final ArrayList<Recipe> clericTrades = new ArrayList<>();

	static {
		LocalDateTime time = LocalDateTime.now();

		// Festive tardes
		if (time.getMonth() == Month.JANUARY) {
			clericTrades.add(new Recipe("Apple_5", new String[] { "Emerald_5" }));
			clericTrades.add(new Recipe("Gold Carrot_5", new String[] { "Gold_5" }));
			clericTrades.add(new Recipe("Gold Apple_5", new String[] { "Gold_5" }));
			clericTrades.add(new Recipe("Frozen Palette_2", new String[] { "Gold_2" }));
			clericTrades.add(new Recipe("Key_3", new String[] { "Gold_3" }));

			// San valentin :)
			if (time.getDayOfMonth() == 14) {
				clericTrades.add(new Recipe("Rose_1", new String[] { "Gold_1" }));
				clericTrades.add(new Recipe("Rose_1", new String[] { "Gold_1" }));
				clericTrades.add(new Recipe("Rose_1", new String[] { "Gold_1" }));
			}
		}

		// hmm ?
		if (time.getMonth() == Month.FEBRUARY) {
			clericTrades.add(new Recipe("Sticky essence_1", new String[] { "Emerald_2" }));
			clericTrades.add(new Recipe("Gaseous essence_5", new String[] { "Gold_1" }));
			clericTrades.add(new Recipe("Master essence_5", new String[] { "Gold_1" }));
			clericTrades.add(new Recipe("GunPowder_4", new String[] { "Gold_1" }));
			clericTrades.add(new Recipe("cloth_3", new String[] { "Gold_1" }));

			// science
			if (time.getDayOfMonth() == 11) {
				clericTrades.add(new Recipe("Brewery_1", new String[] { "Gem_1" }));
			}
		}

		// War trades
		if (time.getMonth() == Month.MARCH) {
			clericTrades.add(new Recipe("ChainMail Armor_1", new String[] { "Emerald_1" }));
			clericTrades.add(new Recipe("Rock Sword_1", new String[] { "Gold_1" }));
			clericTrades.add(new Recipe("Snake Armor_1", new String[] { "Emerald_2" }));
			clericTrades.add(new Recipe("iron Sword_1", new String[] { "Gold_4" }));
			clericTrades.add(new Recipe("Leather Armor_1", new String[] { "Emerald_3" }));
			clericTrades.add(new Recipe("Gem Sword_1", new String[] { "Gold_8" }));

			// San patricio day
			if (time.getDayOfMonth() == 17) {
				clericTrades.add(new Recipe("Green Clothes_1", new String[] { "Iron_3" }));
				clericTrades.add(new Recipe("Green Clothes_1", new String[] { "Iron_3" }));
				clericTrades.add(new Recipe("Green Clothes_1", new String[] { "Iron_3" }));
			}
		}

		// ???
		if (time.getMonth() == Month.APRIL) {
			clericTrades.add(new Recipe("Apple_10", new String[] { "Emerald_2" }));
			clericTrades.add(new Recipe("Emerald_10", new String[] { "Gem_1" }));
			clericTrades.add(new Recipe("Iron_10", new String[] { "Gold_1" }));
			clericTrades.add(new Recipe("stone_16", new String[] { "Gold_1" }));
			clericTrades.add(new Recipe("sand_16", new String[] { "Gold_1" }));
			clericTrades.add(new Recipe("carrot_3", new String[] { "Gold_1" }));

			if (time.getDayOfMonth() == 17) {
				clericTrades.add(new Recipe("Blue Clothes_1", new String[] { "Iron_3" }));
				clericTrades.add(new Recipe("Lapiz_10", new String[] { "Iron_3" }));
				clericTrades.add(new Recipe("Blue Wool_10", new String[] { "Iron_3" }));
			}
			if (time.getDayOfMonth() == 1) {
				clericTrades.add(new Recipe("dirt_1", new String[] { "Gem_5" }));
			}
		}

		// ???
		if (time.getMonth() == Month.MAY) {
			clericTrades.add(new Recipe("Oak Wood_16", new String[] { "Iron_1" }));
			clericTrades.add(new Recipe("Dirt_16", new String[] { "Iron_1" }));
			clericTrades.add(new Recipe("Stone_8", new String[] { "Gold_1" }));

			if (time.getDayOfMonth() == 6) {
				clericTrades.add(new Recipe("dirt_1", new String[] { "Gem_5" }));
			}
		}

		// ???
		if (time.getMonth() == Month.JUNE) {
			clericTrades.add(new Recipe("Iron_1", new String[] { "Torch_8" }));
			clericTrades.add(new Recipe("Iron_1", new String[] { "coal_8" }));
			clericTrades.add(new Recipe("Gold_1", new String[] { "Stick_16" }));

			if (time.getDayOfMonth() == 6) {
				clericTrades.add(new Recipe("Iron_3", new String[] { "Yellow Clothes_1" }));
				clericTrades.add(new Recipe("Iron_3", new String[] { "Gold Apple_2" }));
				clericTrades.add(new Recipe("Iron_3", new String[] { "Gold Carrot_2" }));
			}
		}

		if (time.getMonth() == Month.JULY) {
			clericTrades.add(new Recipe("Apple_10", new String[] { "Emerald_1" }));
			clericTrades.add(new Recipe("Emerald_10", new String[] { "Gem_1" }));
			clericTrades.add(new Recipe("Iron_10", new String[] { "Gold_1" }));

			if (time.getDayOfMonth() == 17) {
				clericTrades.add(new Recipe("Blue Clothes_1", new String[] { "Iron_3" }));
				clericTrades.add(new Recipe("Lapiz_10", new String[] { "Iron_3" }));
				clericTrades.add(new Recipe("Blue Wool_10", new String[] { "Iron_3" }));
			}
			if (time.getDayOfMonth() == 1) {
				clericTrades.add(new Recipe("dirt_1", new String[] { "Gem_5" }));
			}
		}

		// ???
		if (time.getMonth() == Month.AUGUST) {
			clericTrades.add(new Recipe("Birch Wood_16", new String[] { "Iron_1" }));
			clericTrades.add(new Recipe("Dirt_16", new String[] { "Iron_1" }));
			clericTrades.add(new Recipe("Stone_8", new String[] { "Gold_1" }));

			if (time.getDayOfMonth() == 6) {
				clericTrades.add(new Recipe("dirt_1", new String[] { "Gem_5" }));
			}
		}

		if (time.getMonth() == Month.SEPTEMBER) {
			clericTrades.add(new Recipe("Spruce Wood_16", new String[] { "Iron_1" }));
			clericTrades.add(new Recipe("Dirt_16", new String[] { "Iron_1" }));
			clericTrades.add(new Recipe("Stone_8", new String[] { "Gold_1" }));

			if (time.getDayOfMonth() == 6) {
				clericTrades.add(new Recipe("dirt_1", new String[] { "Gem_5" }));
			}
		}

		if (time.getMonth() == Month.OCTOBER) {
			clericTrades.add(new Recipe("Oak Wood_16", new String[] { "Iron_1" }));
			clericTrades.add(new Recipe("Dirt_16", new String[] { "Iron_1" }));
			clericTrades.add(new Recipe("Stone_8", new String[] { "Gold_1" }));

			if (time.getDayOfMonth() == 6) {
				clericTrades.add(new Recipe("dirt_1", new String[] { "Gem_5" }));
			}
		}

		if (time.getMonth() == Month.NOVEMBER) {
			clericTrades.add(new Recipe("Birch Wood_16", new String[] { "Iron_1" }));
			clericTrades.add(new Recipe("Dirt_16", new String[] { "Iron_1" }));
			clericTrades.add(new Recipe("Stone_8", new String[] { "Gold_1" }));

			if (time.getDayOfMonth() == 6) {
				clericTrades.add(new Recipe("dirt_1", new String[] { "Gem_5" }));
			}
		}

		if (time.getMonth() == Month.DECEMBER) {
			clericTrades.add(new Recipe("Spruce Wood_16", new String[] { "Iron_1" }));
			clericTrades.add(new Recipe("Dirt_16", new String[] { "Iron_1" }));
			clericTrades.add(new Recipe("Stone_8", new String[] { "Gold_1" }));

			if (time.getDayOfMonth() == 6) {
				clericTrades.add(new Recipe("dirt_1", new String[] { "Gem_5" }));
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
		Game.setDisplay(new CraftingDisplay(clericTrades, "Trade", player));
		return true;
	}

	@Override
	public void die() {
		super.die();
	}
}
