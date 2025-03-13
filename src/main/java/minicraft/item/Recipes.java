package minicraft.item;

import java.util.ArrayList;

public class Recipes {

	public static final ArrayList<Recipe> anvilRecipes = new ArrayList<>();
	public static final ArrayList<Recipe> ovenRecipes = new ArrayList<>();
	public static final ArrayList<Recipe> furnaceRecipes = new ArrayList<>();
	public static final ArrayList<Recipe> workbenchRecipes = new ArrayList<>();
	public static final ArrayList<Recipe> enchantRecipes = new ArrayList<>();
	public static final ArrayList<Recipe> craftRecipes = new ArrayList<>();
	public static final ArrayList<Recipe> loomRecipes = new ArrayList<>();
	public static final ArrayList<Recipe> stonecutterRecipes = new ArrayList<>();
	public static final ArrayList<Recipe> breweryRecipes = new ArrayList<>();

	static {
		craftRecipes.add(new Recipe("Workbench_1", "Oak Wood_4"));
		craftRecipes.add(new Recipe("Workbench_1", "Spruce Wood_4"));
		craftRecipes.add(new Recipe("Stick_4", "Oak Wood_2"));
		craftRecipes.add(new Recipe("Stick_4", "Spruce Wood_2"));
		craftRecipes.add(new Recipe("Torch_4", "Stick_2", "Coal_2"));
		craftRecipes.add(new Recipe("Grass Seeds_2", "Seeds_1", "Daisy_2"));
		craftRecipes.add(new Recipe("Bone Powder_3", "Bone_1"));
		craftRecipes.add(new Recipe("Oak Plank_2", "Oak Wood_1"));
		craftRecipes.add(new Recipe("Oak Wall_1", "Oak Plank_3"));
		craftRecipes.add(new Recipe("Oak Door_1", "Oak Plank_5"));

		craftRecipes.add(new Recipe("Spruce plank_2", "Spruce Wood_1"));
		craftRecipes.add(new Recipe("Spruce Wall_1", "Spruce plank_3"));
		craftRecipes.add(new Recipe("Spruce Door_1", "Spruce plank_5"));

		craftRecipes.add(new Recipe("Birch plank_2", "Birch Wood_1"));
		craftRecipes.add(new Recipe("Birch Wall_1", "Birch plank_3"));
		craftRecipes.add(new Recipe("Birch Door_1", "Birch plank_5"));

		workbenchRecipes.add(new Recipe("Torch_4", "Stick_2", "Coal_1"));
		workbenchRecipes.add(new Recipe("Lantern_1", "Oak Wood_8", "Slime_4", "Glass_3"));
		workbenchRecipes.add(new Recipe("Paper_3", "Leaf_3", "Oak Wood_3"));
		workbenchRecipes.add(new Recipe("Book_1", "Leather_5", "Paper_5"));
		// workbenchRecipes.add(new Recipe("Boat_1", "Oak Wood_30", "Leather_5", "Wood Shovel_2"));
		workbenchRecipes.add(new Recipe("Bowl_3", "Oak Wood_15"));
		workbenchRecipes.add(new Recipe("Frozen palette_2", "Stick_4", "Apple_4", "Icicle_4"));
		workbenchRecipes.add(new Recipe("Oven_1", "Stone_15"));
		workbenchRecipes.add(new Recipe("Furnace_1", "Stone_20"));
		workbenchRecipes.add(new Recipe("Enchanter_1", "Oak Wood_5", "String_2", "Lapis_10"));
		workbenchRecipes.add(new Recipe("Chest_1", "Oak Wood_20"));
		workbenchRecipes.add(new Recipe("Anvil_1", "Iron_5"));
		workbenchRecipes.add(new Recipe("Tnt_1", "Gunpowder_8", "Sand_4"));
		workbenchRecipes.add(new Recipe("Loom_1", "Oak Wood_10", "Wool_5"));
		workbenchRecipes.add(new Recipe("Stonecutter_1", "Gear_3", "Stone_10", "Iron_1"));
		workbenchRecipes.add(new Recipe("Brewery_1", "Glass_10", "Stone_10", "Iron_3"));
		workbenchRecipes.add(new Recipe("Wood Fishing Rod_1", "Oak Wood_10", "String_3"));
		workbenchRecipes.add(new Recipe("Iron Fishing Rod_1", "Iron_10", "String_3"));
		workbenchRecipes.add(new Recipe("Gold Fishing Rod_1", "Gold_10", "String_3"));
		workbenchRecipes.add(new Recipe("Gem Fishing Rod_1", "Gem_10", "String_3"));

		loomRecipes.add(new Recipe("Light Gray Dye_1", "Gray Dye_1", "Ink Sac_1"));
		loomRecipes.add(new Recipe("Gray Dye_1", "Bone Powder_1", "Ink Sac_1"));
		loomRecipes.add(new Recipe("Green Dye_1", "Lime Dye_1", "Ink Sac_1"));
		loomRecipes.add(new Recipe("Lime Dye_1", "Cactus_1"));
		loomRecipes.add(new Recipe("Yellow Dye_1", "Daisy_1"));
		loomRecipes.add(new Recipe("Orange Dye_1", "Dandelion_1"));
		loomRecipes.add(new Recipe("Brown Dye_1", "Dandelion_1", "Ink Sac_1"));
		loomRecipes.add(new Recipe("Red Dye_1", "Rose_1"));
		loomRecipes.add(new Recipe("Pink Dye_1", "Red Dye_1", "Bone Powder_1"));
		loomRecipes.add(new Recipe("Magenta Dye_1", "Red Dye_1", "Ink Sac_1"));
		loomRecipes.add(new Recipe("Purple Dye_1", "Blue Dye_1", "Ink Sac_1"));
		loomRecipes.add(new Recipe("Blue Dye_1", "Lapis_1", "Ink Sac_1"));
		loomRecipes.add(new Recipe("Cyan Dye_1", "Lime Dye_1", "Blue Dye_1"));
		loomRecipes.add(new Recipe("Light Blue Dye_1", "Blue Dye_1", "Bone Powder_1"));

		loomRecipes.add(new Recipe("String_3", "Wool_1"));
		loomRecipes.add(new Recipe("Wool_1", "String_3"));
		loomRecipes.add(new Recipe("Light Gray Wool_1", "Wool_1", "Light Gray Dye_1"));
		loomRecipes.add(new Recipe("Gray Wool_1", "Wool_1", "Gray Dye_1"));
		loomRecipes.add(new Recipe("Black Wool_1", "Wool_1", "Ink Sac_1"));
		loomRecipes.add(new Recipe("Green Wool_1", "Wool_1", "Green Dye_1"));
		loomRecipes.add(new Recipe("Lime Wool_1", "Wool_1", "Lime Dye_1"));
		loomRecipes.add(new Recipe("Yellow Wool_1", "Wool_1", "Yellow Dye_1"));
		loomRecipes.add(new Recipe("Orange Wool_1", "Wool_1", "Orange Dye_1"));
		loomRecipes.add(new Recipe("Brown Wool_1", "Wool_1", "Brown Dye_1"));
		loomRecipes.add(new Recipe("Red Wool_1", "Wool_1", "Red Dye_1"));
		loomRecipes.add(new Recipe("Pink Wool_1", "Wool_1", "Pink Dye_1"));
		loomRecipes.add(new Recipe("Magenta Wool_1", "Wool_1", "Magenta Dye_1"));
		loomRecipes.add(new Recipe("Purple Wool_1", "Wool_1", "Purple Dye_1"));
		loomRecipes.add(new Recipe("Blue Wool_1", "Wool_1", "Blue Dye_1"));
		loomRecipes.add(new Recipe("Cyan Wool_1", "Wool_1", "Cyan Dye_1"));
		loomRecipes.add(new Recipe("Light Blue Wool_1", "Wool_1", "Light Blue Dye_1"));

		loomRecipes.add(new Recipe("Bed_1", "Oak Wood_5", "Wool_3"));

		loomRecipes.add(new Recipe("Blue Clothes_1", "Cloth_5", "Blue Dye_1"));
		loomRecipes.add(new Recipe("Green Clothes_1", "Cloth_5", "Green Dye_1"));
		loomRecipes.add(new Recipe("Yellow Clothes_1", "Cloth_5", "Yellow Dye_1"));
		loomRecipes.add(new Recipe("Black Clothes_1", "Cloth_5", "Ink Sac_1"));
		loomRecipes.add(new Recipe("Orange Clothes_1", "Cloth_5", "Orange Dye_1"));
		loomRecipes.add(new Recipe("Purple Clothes_1", "Cloth_5", "Purple Dye_1"));
		loomRecipes.add(new Recipe("Cyan Clothes_1", "Cloth_5", "Cyan Dye_1"));
		loomRecipes.add(new Recipe("Reg Clothes_1", "Cloth_5"));

		workbenchRecipes.add(new Recipe("Wood Sword_1", "Stick_1", "Oak Wood_2"));
		workbenchRecipes.add(new Recipe("Wood Spear_1", "Stick_1", "Flint_2", "Oak Wood_3"));
		workbenchRecipes.add(new Recipe("Wood Axe_1", "Stick_2", "Oak Wood_3"));
		workbenchRecipes.add(new Recipe("Wood Hoe_1", "Stick_2", "Oak Wood_2"));
		workbenchRecipes.add(new Recipe("Wood Pickaxe_1", "Stick_2", "Oak Wood_3"));
		workbenchRecipes.add(new Recipe("Wood Shovel_1", "Stick_2", "Oak Wood_1"));
		workbenchRecipes.add(new Recipe("Wood Bow_1", "Stick_3", "String_3"));
		workbenchRecipes.add(new Recipe("Rock Sword_1", "Stick_1", "Stone_2"));
		workbenchRecipes.add(new Recipe("Rock Spear_1", "Stick_1", "Stone_2", "Wood Spear_1"));
		workbenchRecipes.add(new Recipe("Rock Axe_1", "Stick_2", "Stone_3"));
		workbenchRecipes.add(new Recipe("Rock Hoe_1", "Stick_2", "Stone_2"));
		workbenchRecipes.add(new Recipe("Rock Pickaxe_1", "Stick_2", "Stone_3"));
		workbenchRecipes.add(new Recipe("Rock Shovel_1", "Stick_2", "Stone_1"));
		workbenchRecipes.add(new Recipe("Rock Bow_1", "Stick_3", "Stone_3", "String_3"));
		workbenchRecipes.add(new Recipe("Arrow_3", "Stick_1", "Stone_3"));
		workbenchRecipes.add(new Recipe("Map Book_1", "Stick_1", "Book_4", "Coal_4"));

		loomRecipes.add(new Recipe("Leather Armor_1", "Leather_10"));
		loomRecipes.add(new Recipe("Snake Armor_1", "Scale_15"));

		anvilRecipes.add(new Recipe("Shears_1", "Iron_2"));
		anvilRecipes.add(new Recipe("Iron Armor_1", "Iron_10"));
		anvilRecipes.add(new Recipe("Gold Armor_1", "Gold_10"));
		anvilRecipes.add(new Recipe("Gem Armor_1", "Gem_65"));
		anvilRecipes.add(new Recipe("Empty Bucket_1", "Iron_5"));
		anvilRecipes.add(new Recipe("Iron Lantern_1", "Iron_8", "Slime_5", "Glass_4"));
		anvilRecipes.add(new Recipe("Gold Lantern_1", "Gold_10", "Slime_5", "Glass_4"));
		anvilRecipes.add(new Recipe("Iron Sword_1", "Stick_1", "Iron_2"));
		anvilRecipes.add(new Recipe("Iron Spear_1", "Stick_1", "Iron_2", "Rock Spear_1"));
		anvilRecipes.add(new Recipe("Iron Claymore_1", "Iron Sword_1", "Shard_15"));
		anvilRecipes.add(new Recipe("Iron Axe_1", "Stick_2", "Iron_5"));
		anvilRecipes.add(new Recipe("Iron Hoe_1", "Stick_2", "Iron_5"));
		anvilRecipes.add(new Recipe("Iron Pickaxe_1", "Stick_2", "Iron_5"));
		anvilRecipes.add(new Recipe("Iron Shovel_1", "Stick_2", "Iron_5"));
		anvilRecipes.add(new Recipe("Iron Bow_1", "Stick_3", "Iron_5", "String_3"));
		anvilRecipes.add(new Recipe("Gold Sword_1", "Stick_1", "Gold_2"));
		anvilRecipes.add(new Recipe("Gold Spear_1", "Stick_1", "Gold_2", "Iron Spear_1"));
		anvilRecipes.add(new Recipe("Gold Claymore_1", "Gold Sword_1", "Shard_15"));
		anvilRecipes.add(new Recipe("Gold Axe_1", "Stick_2", "Gold_5"));
		anvilRecipes.add(new Recipe("Gold Hoe_1", "Stick_2", "Gold_5"));
		anvilRecipes.add(new Recipe("Gold Pickaxe_1", "Stick_2", "Gold_5"));
		anvilRecipes.add(new Recipe("Gold Shovel_1", "Stick_2", "Gold_5"));
		anvilRecipes.add(new Recipe("Gold Bow_1", "Stick_3", "Gold_5", "String_3"));
		anvilRecipes.add(new Recipe("Gem Sword_1", "Stick_4", "Gem_50"));
		anvilRecipes.add(new Recipe("Gem Spear_1", "Stick_4", "Gem_40", "Gold Spear_1"));
		anvilRecipes.add(new Recipe("Gem Claymore_1", "Gem Sword_1", "Shard_15"));
		anvilRecipes.add(new Recipe("Gem Axe_1", "Stick_4", "Gem_50"));
		anvilRecipes.add(new Recipe("Gem Hoe_1", "Stick_4", "Gem_50"));
		anvilRecipes.add(new Recipe("Gem Pickaxe_1", "Stick_4", "Gem_50"));
		anvilRecipes.add(new Recipe("Gem Shovel_1", "Stick_4", "Gem_50"));
		anvilRecipes.add(new Recipe("Gem Bow_1", "Stick_6", "Gem_50", "String_3"));

		furnaceRecipes.add(new Recipe("Coal_2", "Oak Wood_4", "Coal_1"));
		furnaceRecipes.add(new Recipe("Coal_3", "Spruce Wood_4", "Coal_1"));
		furnaceRecipes.add(new Recipe("Coal_4", "Birch Wood_4", "Coal_1"));
		furnaceRecipes.add(new Recipe("Iron_1", "Iron Ore_3", "Coal_1"));
		furnaceRecipes.add(new Recipe("Gold_1", "Gold Ore_3", "Coal_1"));
		furnaceRecipes.add(new Recipe("Glass_1", "Sand_3", "Coal_1"));
		furnaceRecipes.add(new Recipe("Flint_2", "Stone_10", "Coal_1"));
		furnaceRecipes.add(new Recipe("Natural Rock_5", "Stone_5", "Coal_1"));

		ovenRecipes.add(new Recipe("Cooked Pork_1", "Raw Pork_1", "Coal_1"));
		ovenRecipes.add(new Recipe("Steak_1", "Raw Beef_1", "Coal_1"));
		ovenRecipes.add(new Recipe("Cooked Fish_1", "Raw Fish_1", "Coal_1"));
		ovenRecipes.add(new Recipe("Baked Potato_1", "Potato_1", "Coal_1"));
		ovenRecipes.add(new Recipe("Bread_1", "Wheat_4"));
		ovenRecipes.add(new Recipe("Cooked Chicken_1", "Raw chicken_1", "Coal_1"));
		ovenRecipes.add(new Recipe("Mushroom Soup_3", "Bowl_3", "Red Mushroom_3", "Brown Mushroom_3"));
		ovenRecipes.add(new Recipe("Carrot Soup_3", "Bowl_3", "Carrot_3"));

		enchantRecipes.add(new Recipe("Slimy Amulet_1", "Gold_8", "Green Clothes_2"));
		enchantRecipes.add(new Recipe("Eye Amulet_1", "Gold_8", "Grimoire_1", "Sticky Essence_2"));
		enchantRecipes.add(new Recipe("Gold Apple_1", "Apple_1", "Gold_8"));
		enchantRecipes.add(new Recipe("Gold Carrot_1", "Carrot_1", "Gold_8"));
		enchantRecipes.add(new Recipe("Potion_1", "Glass_1", "Lapis_3"));
		enchantRecipes.add(new Recipe("Speed Potion_1", "Potion_1", "Cactus_5"));
		enchantRecipes.add(new Recipe("Light Potion_1", "Potion_1", "Slime_5"));
		enchantRecipes.add(new Recipe("Swim Potion_1", "Potion_1", "Raw fish_5"));
		enchantRecipes.add(new Recipe("Haste Potion_1", "Potion_1", "Oak Wood_5", "Stone_5"));
		enchantRecipes.add(new Recipe("Lava Potion_1", "Potion_1", "Lava Bucket_1"));
		enchantRecipes.add(new Recipe("Energy Potion_1", "potion_1", "Gem_25"));
		enchantRecipes.add(new Recipe("Regen potion_1", "Potion_1", "Gold Apple_1"));
		enchantRecipes.add(new Recipe("Health Potion_1", "Potion_1", "GunPowder_2", "Leather Armor_1"));
		enchantRecipes.add(new Recipe("Escape Potion_1", "Potion_1", "GunPowder_3", "Lapis_7"));

		stonecutterRecipes.add(new Recipe("Stone Brick_2", "Stone_2"));
		stonecutterRecipes.add(new Recipe("Stone Wall_1", "Stone Brick_3"));
		stonecutterRecipes.add(new Recipe("Stone Door_1", "Stone Brick_5"));
		stonecutterRecipes.add(new Recipe("Obsidian Wall_1", "Obsidian Brick_3"));
		stonecutterRecipes.add(new Recipe("Obsidian Door_1", "Obsidian Brick_5"));
		stonecutterRecipes.add(new Recipe("Slime Statue_1", "Andesite_2", "Basalt_3", "Slime_3"));
		stonecutterRecipes.add(new Recipe("Zombie Statue_1", "Andesite_3", "Basalt_3", "cloth_5"));
		stonecutterRecipes.add(new Recipe("Skeleton Statue_1", "Diorite_3", "Quartzite_3", "bone_3"));
		stonecutterRecipes.add(new Recipe("Sand_3", "Silicon_3", "coal_1"));
		stonecutterRecipes.add(new Recipe("Andesite_3", "Stone_3", "coal_1"));
		stonecutterRecipes.add(new Recipe("Diorite_3", "Stone_3", "coal_1"));
		stonecutterRecipes.add(new Recipe("Granite_3", "Stone_3", "coal_1"));
		stonecutterRecipes.add(new Recipe("Silicon_3", "Stone_3", "coal_1"));
		stonecutterRecipes.add(new Recipe("Basalt_3", "Stone_3", "coal_1"));
		stonecutterRecipes.add(new Recipe("Quartzite_3", "Stone_3", "coal_1"));
		stonecutterRecipes.add(new Recipe("Flint_2", "Stone_5", "coal_1"));

		breweryRecipes.add(new Recipe("xSpeed Potion_1", "Speed potion_2", "Sky wart_5"));
		breweryRecipes.add(new Recipe("xLight Potion_1", "Light potion_2", "Sky wart_4"));
		breweryRecipes.add(new Recipe("xSwim Potion_1", "Swim potion_2", "Sky wart_5"));
		breweryRecipes.add(new Recipe("xLava Potion_1", "Lava potion_2", "Sky wart_6"));
		breweryRecipes.add(new Recipe("xEnergy Potion_1", "Energy potion_2", "Sky wart_6"));
		breweryRecipes.add(new Recipe("xRegen Potion_1", "Regen potion_2", "Sky wart_5"));
		breweryRecipes.add(new Recipe("xHealth Potion_1", "Health potion_2", "Sky wart_5"));
	}
}
