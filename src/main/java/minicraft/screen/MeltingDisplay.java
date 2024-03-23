package minicraft.screen;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import minicraft.core.Game;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Sound;
import minicraft.entity.furniture.Crafter;
import minicraft.entity.mob.Player;
import minicraft.graphic.Point;
import minicraft.graphic.Screen;
import minicraft.graphic.SpriteSheet;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.Recipe;
import minicraft.item.StackableItem;
import minicraft.screen.entry.ItemListing;

public class MeltingDisplay extends Display {
    private final Player player;
    private final Recipe[] recipes;

    private final RecipeMenu recipeMenu;
    private final Menu.Builder itemCountMenu, costsMenu, meltDisplay;

    @SuppressWarnings("unused")
    private final StackableItem[] fuelList = new StackableItem[] { (StackableItem) Items.get("Coal") }; // Must be a stackable item
    private final StackableItem currentFuel = (StackableItem) Items.get("Coal");

    public MeltingDisplay(Crafter.Type type, Player player) {
        ArrayList<Recipe> recipes = type.recipes;
        for (Recipe recipe : recipes)
            recipe.checkCanCraft(player);

        String title = type.name();
        this.player = player;
        this.recipes = recipes.toArray(new Recipe[0]);

        recipeMenu = new RecipeMenu(recipes, title, player);

        itemCountMenu = new Menu.Builder(true, 0, RelPos.LEFT).setTitle("Have:").setTitlePos(RelPos.TOP_LEFT)
                .setPositioning(new Point(recipeMenu.getBounds().getRight() + SpriteSheet.boxWidth,
                        recipeMenu.getBounds().getTop()), RelPos.BOTTOM_RIGHT);

        costsMenu = new Menu.Builder(true, 0, RelPos.LEFT).setTitle("Cost:").setTitlePos(RelPos.TOP_LEFT)
                .setPositioning(
                        new Point(itemCountMenu.createMenu().getBounds().getLeft(), recipeMenu.getBounds().getBottom()),
                        RelPos.TOP_RIGHT);

        meltDisplay = new Menu.Builder(true, 0, RelPos.LEFT).setTitle("Fuel").setTitlePos(RelPos.TOP_LEFT)
                .setPositioning(Screen.center, RelPos.BOTTOM_LEFT);

        menus = new Menu[] { recipeMenu, itemCountMenu.createMenu(), costsMenu.createMenu(), meltDisplay.createMenu() };

        refreshData();
    }

    private void refreshData() {
        Menu prev = menus[3];
        if (currentFuel != null) {
            menus[3] = meltDisplay.setEntries(new ItemListing(currentFuel, Integer.toString(currentFuel.count)))
                    .createMenu();
        } else {
            menus[3] = new Menu.Builder(false, 0, RelPos.CENTER).setTitle("No Fuel Found", Color.RED.getRGB())
                    .createMenu();
        }

        menus[2] = costsMenu.setEntries(getCurItemCosts()).createMenu();
        menus[2].setColors(prev);

        menus[1] = itemCountMenu.setEntries(
                new ItemListing(recipes[recipeMenu.getSelection()].getProduct(), String.valueOf(getCurItemCount())))
                .createMenu();
        menus[1].setColors(prev);
    }

    // private StackableItem getFuelFromInventory() {
    // return player.getInventory().
    // }

    private int getCurItemCount() {
        return player.getInventory().count(recipes[recipeMenu.getSelection()].getProduct());
    }

    private ItemListing[] getCurItemCosts() {
        ArrayList<ItemListing> costList = new ArrayList<>();
        HashMap<String, Integer> costMap = recipes[recipeMenu.getSelection()].getCosts();
        for (String itemName : costMap.keySet()) {
            Item cost = Items.get(itemName);
            costList.add(new ItemListing(cost, costMap.get(itemName) + "/" + player.getInventory().count(cost)));
        }

        return costList.toArray(new ItemListing[0]);
    }

    @Override
    public void tick(InputHandler input) {
        if (input.getKey("menu").clicked) {
            Game.exitDisplay();
            return;
        }

        int previousSelection = recipeMenu.getSelection();
        super.tick(input);
        
        if (previousSelection != recipeMenu.getSelection()) {
            refreshData();
        }

        if ((input.getKey("select").clicked || input.getKey("attack").clicked) && recipeMenu.getSelection() >= 0) {
            // check the selected recipe
            Recipe selctedRecipe = recipes[recipeMenu.getSelection()];
            if (selctedRecipe.getCanCraft()) {
            	selctedRecipe.craft(player);

                Sound.play("playerCraft");

                refreshData();
                for (Recipe recipe : recipes) {
                    recipe.checkCanCraft(player);
                }
            }
        }
    }
}
