package minicraft.modloader;

import java.util.Random;

import minicraft.Game;
import minicraft.entity.Inventory;
import minicraft.entity.Player;
import minicraft.item.Item;
import minicraft.level.Level;
import minicraft.screen.Menu;

public abstract class BaseMod {

	public BaseMod() {
		ModLoader.mods.add(this);
	}

	public abstract void load();

	public void GenerateLevel(Level level, Random random, int i, int j)
    {
		return;
    }

	public void onTickInGame(Game game) {
		return;
	}

	public void onTickInMenu(Menu menu) {
		return;
	}

	public void onTickByPlayer(Player player) {
		return;
	}

	public abstract String getVersion();

	public String getName()
    {
        return getClass().getSimpleName();
    }

	public void KeyboardEvent(int key, boolean pressed)
    {
		return;
    }

	public void TakenFromCrafting(Player player, Item item, Inventory inventory)
    {
		return;
    }

    public void TakenFromFurnace(Player player, Item item)
    {
    	return;
    }

    public String toString()
    {
        return (new StringBuilder(String.valueOf(getName()))).append(' ').append(getVersion()).toString();
    }

    public void onItemPickup(Player player, Item item)
    {
    	return;
    }

}
