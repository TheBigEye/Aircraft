package minicraft.entity.furniture;

import minicraft.core.Game;
import minicraft.entity.Direction;
import minicraft.entity.ItemHolder;
import minicraft.entity.mob.Player;
import minicraft.graphic.Sprite;
import minicraft.item.Inventory;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.saveload.Load;
import minicraft.screen.ContainerDisplay;
import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.List;

public class Chest extends Furniture implements ItemHolder {
	private Inventory chestInventory; // Inventory of the chest

	public Chest() {
		this("Chest");
	}

	/**
	 * Creates a chest with a custom name.
	 *
	 * @param name Name of chest.
	 */
	public Chest(String name) {
		super(name, new Sprite(6, 30, 2, 2, 2), 3, 3); // Name of the chest
		chestInventory = new Inventory(); // initialize the inventory.
	}

	/** This is what occurs when the player uses the "Menu" command near this */
	@Override
	public boolean use(Player player) {
		Game.setDisplay(new ContainerDisplay(player, this));
		return true;
	}

	public void fillInventoryRandom(String lootTable, int depth) {
		try {
			String[] lines = Load.loadFile("/resources/chestloot/" + lootTable + ".txt").toArray(new String[] {});

			for (String line : lines) {
				// System.out.println(line);
				String[] data = line.split(",");
				if (!line.startsWith(":")) {
					chestInventory.tryAdd(Integer.parseInt(data[0]), Items.get(data[1]), data.length < 3 ? 1 : Integer.parseInt(data[2]));

				} else if (chestInventory.size() == 0) {
					// adds the "fallback" items to ensure there's some stuff
					String[] fallbacks = line.substring(1).split(":");
					for (String item : fallbacks) {
						chestInventory.add(Items.get(item.split(",")[0]), Integer.parseInt(item.split(",")[1]));
					}
				}
			}
		} catch (IOException exception) {
			Logger.error("Couldn't read loot table \"{}.txt\"", lootTable);
			exception.printStackTrace();
		}
	}

	@Override
	public boolean interact(Player player, @Nullable Item item, Direction attackDir) {
		if (Game.isMode("Creative")) { // Can pickup in Creative
			return super.interact(player, item, attackDir);
		} else { // But not in others gamemodes
			if (chestInventory.size() == 0) {
				return super.interact(player, item, attackDir);
			}
		}
		return false;
	}

	@Override
	public Inventory getInventory() {
        return chestInventory;
	}

	@Override
	public void die() {
		if (level != null) {
			List<Item> items = chestInventory.getItems();
			level.dropItem(x, y, items.toArray(new Item[items.size()]));
		}
		super.die();
	}
}
