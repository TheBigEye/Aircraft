package minicraft.item;

import java.util.HashMap;

import minicraft.core.Game;
import minicraft.entity.mob.Player;

public class Recipe {
	private HashMap<String, Integer> costs = new HashMap<String, Integer>(); // A list of costs for the recipe
	private String product; // the result item of the recipe
	private int amount;
	private boolean canCraft; // checks if the player can craft the recipe

	public Recipe(String createdItem, String... reqItems) {
		canCraft = false;
		String[] sep = createdItem.split("_");
		product = sep[0].toUpperCase(); // assigns the result item
		amount = Integer.parseInt(sep[1]);

		for (String reqItem : reqItems) {
			String[] curSep = reqItem.split("_");
			String curItem = curSep[0].toUpperCase(); // the current cost that's being added to costs.
			int amt = Integer.parseInt(curSep[1]);
			boolean added = false;
			for (String cost : costs.keySet().toArray(new String[0])) { // loop through the costs that have already been added
				if (cost.equals(curItem)) {
					costs.put(cost, costs.get(cost) + amt);
					added = true;
					break;
				}
			}

			if (added) continue;
			costs.put(curItem, amt);
		}
	}

	public boolean checkCanCraft(Player player) {
		canCraft = getCanCraft(player);
		return canCraft;
	}

	// (WAS) abstract method given to the sub-recipe classes.
	public boolean craft(Player player) {
		if (!getCanCraft(player)) {
			return false;
		}

		if (!Game.isMode("Creative")) {
			// remove the cost items from the inventory.
			for (String cost : costs.keySet().toArray(new String[0])) {
				if (!cost.contains("ALAZIF")) player.getInventory().removeItems(Items.get(cost), costs.get(cost));
			}
		}

		// add the crafted items.
		for (int i = 0; i < amount; i++) {
			player.getInventory().add(getProduct());
		}

		return true;
	}

	public int getAmount() {
		return amount;
	}

	public boolean getCanCraft() {
		return canCraft;
	}

	/** Checks if the player can craft the recipe */
	private boolean getCanCraft(Player player) {
		if (Game.isMode("Creative")) {
			return true;
		}

		for (String cost : costs.keySet().toArray(new String[0])) { // cycles through the costs list
			/// this method ONLY WORKS if costs does not contain two elements such that
			/// inventory.count will count an item it contains as matching more than once.
			if (player.getInventory().count(Items.get(cost)) < costs.get(cost)) {
				return false;
			}
		}
		return true;
	}

	public HashMap<String, Integer> getCosts() {
		return costs;
	}

	public Item getProduct() {
		return Items.get(product);
	}
}
