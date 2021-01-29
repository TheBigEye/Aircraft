package minicraft.entity.mob.villager;

import java.util.ArrayList;

import minicraft.core.Game;
import minicraft.entity.mob.Player;
import minicraft.gfx.MobSprite;
import minicraft.item.Recipe;
import minicraft.screen.CraftingDisplay;


public class Librarian extends VillagerMob {
	private static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(0, 44);
  
  public static ArrayList<Recipe> LibrarianTrdes = new ArrayList<>();
  
  static {
	  LibrarianTrdes.add(new Recipe("Book_1", new String[] { "Apple_5" }));
	  LibrarianTrdes.add(new Recipe("Paper_3", new String[] { "Leaf_5" }));
	  LibrarianTrdes.add(new Recipe("Protection I_1", new String[] { "iron_20", "Book_1" }));
	  LibrarianTrdes.add(new Recipe("Protection II_1", new String[] { "iron_30", "Book_2" }));
	  LibrarianTrdes.add(new Recipe("Protection III_1", new String[] { "gold_20", "Book_3" }));
	  LibrarianTrdes.add(new Recipe("Sharp I_1", new String[] { "iron_20", "Book_1" }));
	  LibrarianTrdes.add(new Recipe("Sharp II_1", new String[] { "iron_30", "Book_2" }));
	  LibrarianTrdes.add(new Recipe("Sharp III_1", new String[] { "gold_30", "Book_3" }));
  }
  
  public Librarian() {
		super(sprites);
	}
  
  public void tick() {
    super.tick();
  }
  
  public boolean use(Player player) {
    Game.setMenu(new CraftingDisplay(minicraft.entity.mob.villager.Librarian.LibrarianTrdes, "Trade", player));
    return true;
  }
  
  public void die() {
    super.die();
  }
}
