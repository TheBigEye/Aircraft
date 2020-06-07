package minicraft.modloader;

import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import minicraft.Game;
import minicraft.entity.Inventory;
import minicraft.entity.Player;
import minicraft.item.Item;
import minicraft.item.Recipe;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.screen.Menu;

public class ModLoader {

	private static Game game;

	public static List<BaseMod> mods = new ArrayList<BaseMod>();
	public static List<Recipe> aRecipes = new ArrayList<Recipe>();
	public static List<Recipe> oRecipes = new ArrayList<Recipe>();
	public static List<Recipe> fRecipes = new ArrayList<Recipe>();
	public static List<Recipe> wRecipes = new ArrayList<Recipe>();

	public ModLoader() {
		
	}

	public static void addGame(Game g) {
		game = g;
	}

	public static void loadAllMods() {
		for(int i = 0; i < mods.size(); i++) {
			mods.get(i).load();
			System.out.println("ModLoader loads " + mods.get(i).getName() + " " + mods.get(i).getVersion());
		}
	}

	public static void toggleKey(KeyEvent ke, boolean pressed) {
		for(int i = 0; i < mods.size(); i++) {
			mods.get(i).KeyboardEvent(ke.getKeyCode(), pressed);
		}
	}

	public static void tickGame(Game game) {
		for(int i = 0; i < mods.size(); i++) {
			mods.get(i).onTickInGame(game);
		}
	}

	public static void tickMenu(Menu menu) {
		for(int i = 0; i < mods.size(); i++) {
			mods.get(i).onTickInMenu(menu);
		}
	}

	public static void tickPlayer(Player player) {
		for(int i = 0; i < mods.size(); i++) {
			mods.get(i).onTickByPlayer(player);
		}
	}

	public static void LevelGenerate(Level level, Random random, int x, int y) {
		for(int i = 0; i < mods.size(); i++) {
			mods.get(i).GenerateLevel(level, random, x, y);
		}
	}

	public static void PickupItem(Player player, Item item) {
		for(int i = 0; i < mods.size(); i++) {
			mods.get(i).onItemPickup(player, item);
		}
	}

	public static void Craft(Player player, Item item, Inventory inventory) {
		for(int i = 0; i < mods.size(); i++) {
			mods.get(i).TakenFromCrafting(player, item, inventory);
		}
	}

	public static void Furnace(Player player, Item item) {
		for(int i = 0; i < mods.size(); i++) {
			mods.get(i).TakenFromFurnace(player, item);
		}
	}

	

	public static Game getGameInstance() {
		return game;
	}

}
