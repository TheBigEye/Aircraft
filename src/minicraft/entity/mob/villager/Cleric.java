package minicraft.entity.mob.villager;

import java.util.ArrayList;

import minicraft.core.Game;
import minicraft.entity.mob.Player;
import minicraft.gfx.MobSprite;
import minicraft.item.Recipe;
import minicraft.screen.CraftingDisplay;

public class Cleric extends VillagerMob {
	private static MobSprite[][] sprites = MobSprite.compileMobSpriteAnimations(0, 46);
  
  public static ArrayList<Recipe> ClericTrdes = new ArrayList<>();
  
  static {
	  ClericTrdes.add(new Recipe("Emerald_1", new String[] { "Apple_5" }));
	  ClericTrdes.add(new Recipe("Gem_1", new String[] { "Emerald_5" }));
	  ClericTrdes.add(new Recipe("Gold_1", new String[] { "Iron_5" }));
  }
  
  public Cleric() {
		super(sprites);
	}
  
  public void tick() {
    super.tick();
  }
  
  public boolean use(Player player) {
    Game.setMenu(new CraftingDisplay(minicraft.entity.mob.villager.Cleric.ClericTrdes, "Trade", player));
    return true;
  }
  
  public void die() {
    super.die();
  }
}
