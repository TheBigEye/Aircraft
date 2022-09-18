package minicraft.entity.furniture;

import java.util.ArrayList;

import minicraft.core.Game;
import minicraft.entity.mob.Player;
import minicraft.gfx.Sprite;
import minicraft.item.Recipe;
import minicraft.item.Recipes;
import minicraft.screen.AchievementsDisplay;
import minicraft.screen.CraftingDisplay;

public class Crafter extends Furniture {

	public enum Type {
		Workbench(new Sprite(16, 24, 2, 2, 2), 3, 2, Recipes.workbenchRecipes, false),
		Oven(new Sprite(12, 24, 2, 2, 2), 3, 2, Recipes.ovenRecipes, false), // NOTE: this true
		Furnace(new Sprite(14, 24, 2, 2, 2), 3, 2, Recipes.furnaceRecipes, true), // NOTE: this true
		Anvil(new Sprite(8, 24, 2, 2, 2), 3, 2, Recipes.anvilRecipes, false),
		Enchanter(new Sprite(24, 24, 2, 2, 2), 7, 2, Recipes.enchantRecipes, false),
		Assembler(new Sprite(10, 26, 2, 2, 2), 7, 2, Recipes.assemblerRecipes, false),
		Stonecutter(new Sprite(12, 26, 2, 2, 2), 7, 2, Recipes.stonecutterRecipes, false),
		Brewery(new Sprite(14, 26, 2, 2, 2), 7, 2, Recipes.breweryRecipes, false),
		Loom(new Sprite(26, 24, 2, 2, 2), 7, 2, Recipes.loomRecipes, false);

		public ArrayList<Recipe> recipes;
		protected Sprite sprite;
		protected int xr;
		protected int yr;
		protected boolean melt;

		Type(Sprite sprite, int xr, int yr, ArrayList<Recipe> list, boolean melt) {
			this.sprite = sprite;
			this.xr = xr;
			this.yr = yr;
			this.melt = melt;
			recipes = list;
			Crafter.names.add(this.name());
		}
	}

	public static ArrayList<String> names = new ArrayList<>();

	public Crafter.Type type;

	/**
	 * Creates a crafter of a given type.
	 * 
	 * @param type What type of crafter this is.
	 */
	public Crafter(Crafter.Type type) {
		super(type.name(), type.sprite, type.xr, type.yr);
		this.type = type;
	}

	@Override
	public boolean use(Player player) {
		if (type.melt) {
			//Game.setDisplay(new MeltingDisplay(type, player));
			AchievementsDisplay.setAchievement("minicraft.achievement.smelt_iron", true);
			Game.setDisplay(new CraftingDisplay(type.recipes, type.name(), player));
		} else {
			Game.setDisplay(new CraftingDisplay(type.recipes, type.name(), player));
		}
		return true;
	}

	@Override
	public Furniture clone() {
		return new Crafter(type);
	}

	@Override
	public String toString() {
		return type.name() + getDataPrints();
	}
}