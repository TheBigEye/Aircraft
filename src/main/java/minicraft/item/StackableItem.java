package minicraft.item;

import java.util.ArrayList;

import minicraft.core.Game;
import minicraft.core.io.Localization;
import minicraft.graphic.Sprite;

// some items are direct instances of this class; those instances are the true "items",
// like stone, wood, wheat, or coal; you can't do anything with them besides use them to make something else.

public class StackableItem extends Item {

	protected static ArrayList<Item> getAllInstances() {
		ArrayList<Item> items = new ArrayList<>();

		items.add(new StackableItem("Oak Wood", new Sprite(2, 1, 0)));
		items.add(new StackableItem("Spruce Wood", new Sprite(1, 1, 0)));
		items.add(new StackableItem("Birch Wood", new Sprite(0, 1, 0)));

		items.add(new StackableItem("Leaf", new Sprite(23, 0, 0)));
		items.add(new StackableItem("Paper", new Sprite(3, 8, 0)));
		items.add(new StackableItem("Leather", new Sprite(8, 0, 0)));
		items.add(new StackableItem("Wheat", new Sprite(6, 0, 0)));
		items.add(new StackableItem("Key", new Sprite(0, 4, 0)));
		items.add(new StackableItem("Arrow", new Sprite(0, 2, 0)));
		items.add(new StackableItem("Icicle", new Sprite(11, 3, 0)));
		items.add(new StackableItem("Stick", new Sprite(9, 3, 0)));
		items.add(new StackableItem("Bowl", new Sprite(13, 3, 0)));
		items.add(new StackableItem("String", new Sprite(1, 4, 0)));
		items.add(new StackableItem("Feather", new Sprite(15, 3, 0)));
		items.add(new StackableItem("Egg", new Sprite(14, 3, 0)));

		// Elements
		items.add(new StackableItem("Obsidian", new Sprite(17, 3, 0)));
		items.add(new StackableItem("Stone", new Sprite(2, 0, 0)));
		items.add(new StackableItem("Holy Stone", new Sprite(3, 44, 0)));

		items.add(new StackableItem("Andesite", new Sprite(0, 38, 0)));
		items.add(new StackableItem("Diorite", new Sprite(1, 38, 0)));
		items.add(new StackableItem("Granite", new Sprite(2, 38, 0)));
		items.add(new StackableItem("Silicon", new Sprite(3, 38, 0)));
		items.add(new StackableItem("Basalt", new Sprite(4, 38, 0)));
		items.add(new StackableItem("Quartzite", new Sprite(5, 38, 0)));

		// Dyes
		items.add(new StackableItem("Light gray dye", new Sprite(1, 22, 0)));
		items.add(new StackableItem("Gray dye", new Sprite(2, 22, 0)));
		items.add(new StackableItem("Ink sac", new Sprite(3, 22, 0)));
		items.add(new StackableItem("Green dye", new Sprite(4, 22, 0)));
		items.add(new StackableItem("Lime dye", new Sprite(5, 22, 0)));
		items.add(new StackableItem("Yellow dye", new Sprite(6, 22, 0)));
		items.add(new StackableItem("Orange dye", new Sprite(7, 22, 0)));
		items.add(new StackableItem("Brown dye", new Sprite(8, 22, 0)));
		items.add(new StackableItem("Red dye", new Sprite(9, 22, 0)));
		items.add(new StackableItem("Pink dye", new Sprite(10, 22, 0)));
		items.add(new StackableItem("Magenta dye", new Sprite(11, 22, 0)));
		items.add(new StackableItem("Purple dye", new Sprite(12, 22, 0)));
		items.add(new StackableItem("Blue dye", new Sprite(13, 22, 0)));
		items.add(new StackableItem("Cyan dye", new Sprite(14, 22, 0)));
		items.add(new StackableItem("Light blue dye", new Sprite(15, 22, 0)));

		// Ores
		items.add(new StackableItem("Coal", new Sprite(2, 4, 0)));
		items.add(new StackableItem("Iron Ore", new Sprite(3, 4, 0)));
		items.add(new StackableItem("Lapis", new Sprite(4, 4, 0)));
		items.add(new StackableItem("Gold Ore", new Sprite(5, 4, 0)));
		items.add(new StackableItem("Iron", new Sprite(6, 4, 0)));
		items.add(new StackableItem("Gold", new Sprite(7, 4, 0)));
		items.add(new StackableItem("GunPowder", new Sprite(8, 4, 0)));

		items.add(new StackableItem("Slime", new Sprite(9, 4, 0)));
		items.add(new StackableItem("Glass", new Sprite(10, 4, 0)));
		items.add(new StackableItem("Cloth", new Sprite(11, 4, 0)));
		items.add(new StackableItem("Gem", new Sprite(12, 4, 0)));
		items.add(new StackableItem("Emerald", new Sprite(16, 4, 0)));
		items.add(new StackableItem("Scale", new Sprite(13, 4, 0)));
		items.add(new StackableItem("Shard", new Sprite(14, 4, 0)));
		items.add(new StackableItem("Gear", new Sprite(15, 4, 0)));
		items.add(new StackableItem("Spring", new Sprite(16, 3, 0)));
		items.add(new StackableItem("Flint", new Sprite(8, 3, 0)));
		items.add(new StackableItem("Protection I", new Sprite(2, 8, 0)));
		items.add(new StackableItem("Protection II", new Sprite(2, 8, 0)));
		items.add(new StackableItem("Protection III", new Sprite(2, 8, 0)));
		items.add(new StackableItem("Sharp I", new Sprite(2, 8, 0)));
		items.add(new StackableItem("Sharp II", new Sprite(2, 8, 0)));
		items.add(new StackableItem("Sharp III", new Sprite(2, 8, 0)));

		// Essences
		// items.add(new StackableItem("Cordyceps essence", new Sprite(1, 7, 0)));
		items.add(new StackableItem("Sticky essence", new Sprite(2, 7, 0)));
		items.add(new StackableItem("Gaseous essence", new Sprite(3, 7, 0)));
		items.add(new StackableItem("Master essence", new Sprite(4, 7, 0)));

		return items;
	}

	public int count;
	// public int maxCount = 64; // TODO I want to implement this later.

	protected StackableItem(String name, Sprite sprite) {
		super(name, sprite);
		count = 1;
	}

	protected StackableItem(String name, Sprite sprite, int count) {
		this(name, sprite);
		this.count = count;
	}

	public boolean stacksWith(Item other) {
		return other instanceof StackableItem && other.getName().equals(getName());
	}

	/// this is used by (most) subclasses, to standardize the count decrement
	/// behavior. This is not the normal interactOn method.
	protected boolean interactOn(boolean subClassSuccess) {
		if (subClassSuccess && !Game.isMode("Creative")) {
			count--;
		}
		return subClassSuccess;
	}

	/** Called to determine if this item should be removed from an inventory. */
	@Override
	public boolean isDepleted() {
		return count <= 0;
	}

	@Override
	public StackableItem clone() {
		return new StackableItem(getName(), sprite, count);
	}

	@Override
	public String toString() {
		return super.toString() + "-Stack_Size:" + count;
	}

	@Override
	public String getData() {
		return getName() + "_" + count;
	}

	@Override
	public String getDisplayName() {
		String amount = (count > 999 ? 999 : count) + " ";
		return " " + amount + Localization.getLocalized(getName());
	}
}
