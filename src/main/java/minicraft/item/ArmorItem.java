package minicraft.item;

import java.util.ArrayList;

import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.graphic.Sprite;
import minicraft.level.Level;
import minicraft.level.tile.Tile;

public class ArmorItem extends StackableItem {

	protected static ArrayList<Item> getAllInstances() {
		ArrayList<Item> items = new ArrayList<>();

		/*
		 * level is considerated the sprite x axis for the amor bar satus, from 1 to 7
		 */
		items.add(new ArmorItem("Leather Armor", new Sprite(0, 9, 0), .3f, 1));
		items.add(new ArmorItem("Snake Armor", new Sprite(1, 9, 0), .4f, 2));
		items.add(new ArmorItem("ChainMail Armor", new Sprite(6, 9, 0), .4f, 7));
		items.add(new ArmorItem("Iron Armor", new Sprite(2, 9, 0), .5f, 3));
		items.add(new ArmorItem("Gold Armor", new Sprite(3, 9, 0), .7f, 4));
		items.add(new ArmorItem("Gem Armor", new Sprite(4, 9, 0), 1f, 5));

		// Useless for now...
		// items.add(new ArmorItem("Prot I Armor", new Sprite(5, 9, 0), .1f, 6));
		// items.add(new ArmorItem("Prot II Armor", new Sprite(5, 9, 0), .1f, 6));
		// items.add(new ArmorItem("Prot III Armor", new Sprite(5, 9, 0), .1f, 6));

		return items;
	}

	private final float armor;
	private final int staminaCost;
	public final int level;

	private ArmorItem(String name, Sprite sprite, float health, int level) {
		this(name, sprite, 1, health, level);
	}

	private ArmorItem(String name, Sprite sprite, int count, float health, int level) {
		super(name, sprite, count);
		this.armor = health;
		this.level = level;
		staminaCost = 9;
	}

	@Override
	public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, Direction attackDir) {
		boolean success = false;
		if (player.currentArmor == null && player.payStamina(staminaCost)) {
			player.currentArmor = this; // set the current armor being worn to this.
			player.armor = (int) (armor * Player.maxArmor); // armor is how many hits are left
			success = true;
		}

		return super.interactOn(success);
	}

	@Override
	public boolean interactsWithWorld() {
		return false;
	}
	
	@Override
	public ArmorItem clone() {
		return new ArmorItem(getName(), sprite, count, armor, level);
	}
}
