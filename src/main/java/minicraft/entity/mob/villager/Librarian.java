package minicraft.entity.mob.villager;

import java.util.ArrayList;

import minicraft.core.Game;
import minicraft.entity.mob.Player;
import minicraft.gfx.MobSprite;
import minicraft.item.Recipe;
import minicraft.screen.CraftingDisplay;

public class Librarian extends VillagerMob {
    private static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(0, 48);

    protected static final ArrayList<Recipe> librarianTrades = new ArrayList<>();

    static {
        librarianTrades.add(new Recipe("Book_1", new String[] { "Apple_5" }));
        librarianTrades.add(new Recipe("Paper_3", new String[] { "Leaf_5" }));
        librarianTrades.add(new Recipe("Protection I_1", new String[] { "iron_20", "Book_1" }));
        librarianTrades.add(new Recipe("Protection II_1", new String[] { "iron_30", "Book_2" }));
        librarianTrades.add(new Recipe("Protection III_1", new String[] { "gold_20", "Book_3" }));
        librarianTrades.add(new Recipe("Sharp I_1", new String[] { "iron_20", "Book_1" }));
        librarianTrades.add(new Recipe("Sharp II_1", new String[] { "iron_30", "Book_2" }));
        librarianTrades.add(new Recipe("Sharp III_1", new String[] { "gold_30", "Book_3" }));
    }

    public Librarian() {
        super(sprites);
    }

    public void tick() {
        super.tick();
    }

    public boolean use(Player player) {
        Game.setDisplay(new CraftingDisplay(minicraft.entity.mob.villager.Librarian.librarianTrades, "Trade", player));
        return true;
    }

    public void die() {
        super.die();
    }
}
