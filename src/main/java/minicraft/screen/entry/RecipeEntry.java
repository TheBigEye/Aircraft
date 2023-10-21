package minicraft.screen.entry;

import java.util.List;

import minicraft.core.io.InputHandler;
import minicraft.core.io.Localization;
import minicraft.graphic.Font;
import minicraft.graphic.Screen;
import minicraft.item.Recipe;

public class RecipeEntry extends ItemEntry {

    public static RecipeEntry[] useRecipes(List<Recipe> recipes) {
        RecipeEntry[] entries = new RecipeEntry[recipes.size()];
        int recipesListSize = recipes.size();
        
        for (int i = 0; i < recipesListSize; i++) {
            entries[i] = new RecipeEntry(recipes.get(i));
        }
        return entries;
    }

    private Recipe recipe;

    public RecipeEntry(Recipe recipe) {
        super(recipe.getProduct());
        this.recipe = recipe;
    }

    @Override
    public void tick(InputHandler input) {
    }

    @Override
    public void render(Screen screen, int x, int y, boolean isSelected) {
        if (isVisible()) {
            Font.draw(toString(), screen, x, y, recipe.getCanCraft() ? COLOR_SELECTED : COLOR_UNSELECTED);
            getItem().sprite.render(screen, x, y);
        }
    }

    @Override
    public String toString() {
        return "  " + Localization.getLocalized(recipe.getProduct().getName()) + (recipe.getAmount() > 1 ? " x" + recipe.getAmount() : "");
    }
}
