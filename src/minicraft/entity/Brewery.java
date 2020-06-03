package minicraft.entity;

import minicraft.gfx.Color;
import minicraft.gfx.Sprite;
import minicraft.screen.CraftingMenu;

/**
 * 		Brewery
 * 
 * Enables the player to brew ale (maybe other things in the future).
 * 
 * @author Dejvino
 */
public class Brewery extends Furniture {
	public Brewery() {
		super("Brewery", new Sprite(22, 8, 2, 2, Color.get(-1, 333, 310, 5)), 3, 2);
		
	}

	public boolean use(Player player, int attackDir) {
		player.game.setMenu(new CraftingMenu(minicraft.item.Recipes.breweryRecipes, player));
		return true;
	}
}