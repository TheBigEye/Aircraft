package minicraft.entity.mob;

import java.util.ArrayList;

import minicraft.core.Game;
import minicraft.graphic.MobSprite;
import minicraft.item.Recipe;
import minicraft.screen.CraftingDisplay;

public class Librarian extends VillagerMob {
    private static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(0, 48);

    private static final ArrayList<Recipe> librarianTrades = new ArrayList<>();

    static {
        librarianTrades.add(new Recipe("Book_1", new String[] { "Apple_5" }));
        librarianTrades.add(new Recipe("Paper_3", new String[] { "Leaf_5" }));
    }

    public Librarian() {
        super(sprites);
    }

    public void tick() {
        super.tick();
    }

    public boolean use(Player player) {
        Game.setDisplay(new CraftingDisplay(librarianTrades, "Trade", player));
        return true;
    }

    public void die() {
        super.die();
    }
}
