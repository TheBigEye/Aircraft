package minicraft.item;

import minicraft.core.Game;
import minicraft.core.io.Localization;
import minicraft.entity.Entity;
import minicraft.entity.mob.Mob;
import minicraft.graphic.Sprite;
import minicraft.screen.AchievementsDisplay;

import java.util.ArrayList;

public class ToolItem extends Item {

	protected static ArrayList<Item> getAllInstances() {
		ArrayList<Item> items = new ArrayList<>();

		for (ToolType tool : ToolType.values()) {
			if (!tool.noLevel) {
				for (int lvl = 0; lvl <= 4; lvl++) {
					items.add(new ToolItem(tool, lvl));
				}
			} else {
				items.add(new ToolItem(tool));
			}
		}

		return items;
	}

	// The names of the different levels. A later level means a stronger tool.
	protected static final String[] LEVEL_NAMES = { "Wood", "Rock", "Iron", "Gold", "Gem" };

	public ToolType type; // Type of tool (Sword, hoe, axe, pickaxe, shovel)
	public int level; // Level of said tool
	public int durability; // the durability of the tool
	private int damage; // The damage of the tool

	/**
	 * Tool Item, requires a tool type (ToolType.Sword, ToolType.Axe, ToolType.Hoe,
	 * etc) and a level (0 = wood, 2 = iron, 4 = gem, etc)
	 */
	public ToolItem(ToolType type, int level) {
		super(LEVEL_NAMES[level] + " " + type.name(), new Sprite(type.xPos, type.yPos + level, 0));

		this.type = type;
		this.level = level;
		this.damage = level * 5 + 10;

		durability = type.durability * (level + 1); // initial durability fetched from the ToolType
	}

	public ToolItem(ToolType type) {
		super(type.name(), new Sprite(type.xPos, type.yPos, 0));

		this.type = type;
		durability = type.durability;
	}

	/** Gets the name of this tool (and it's type) as a display string. */
	@Override
	public String getDisplayName() {
		if (!type.noLevel) {
			return " " + Localization.getLocalized(LEVEL_NAMES[level]) + " " + Localization.getLocalized(type.toString());
		} else {
			return " " + Localization.getLocalized(type.toString());
		}
	}

	@Override
	public boolean isDepleted() {
		return durability <= 0 && type.durability > 0;
	}

	/** You can attack mobs with tools. */
	@Override
	public boolean canAttack() {
		return type != ToolType.Shears && type != ToolType.Igniter;
	}

	public boolean payDurability() {
		if (durability <= 0) return false;
		if (!Game.isMode("Creative")) durability--;
		return true;
	}

	public int getDamage() {
		return random.nextInt(5) + damage;
	}

	/** Gets the attack damage bonus from an item/tool (sword/axe) */
	public int getAttackDamageBonus(Entity entity) {
		if (!payDurability())
			return 0;

		if (entity instanceof Mob) {
            AchievementsDisplay.setAchievement("minicraft.achievement.monsters", true);
			if (type == ToolType.Axe) {
				return (level + 1) * 2 + random.nextInt(4); // Wood axe damage: 2-5; gem axe damage: 10-13.
			} else if (type == ToolType.Sword || type == ToolType.Spear) {
				return (level + 1) * 3 + random.nextInt(2 + level * level); // Wood: 3-5 damage; gem: 15-32 damage.
			} else if (type == ToolType.Claymore) {
				return (level + 1) * 3 + random.nextInt(4 + level * level * 3); // Wood: 3-6 damage; gem: 15-66 damage.
			} else if (type == ToolType.Pickaxe) {
				return (level + 1) + random.nextInt(2); // Wood: 3-6 damage; gem: 15-66 damage.
			}
			return 1;
		}
		return 0;
	}

	@Override
	public String getData() {
		return super.getData() + "_" + durability;
	}

	/** Sees if this item equals another. */
	@Override
	public boolean equals(Item item) {
		if (item instanceof ToolItem) {
			ToolItem other = (ToolItem) item;
			return other.type == type && other.level == level;
		}
		return false;
	}

	@Override
	public int hashCode() { return type.name().hashCode() + level; }

	public ToolItem clone() {
		ToolItem toolItem;
		if (type.noLevel) {
			toolItem = new ToolItem(type);
		} else {
			toolItem = new ToolItem(type, level);
		}
		toolItem.durability = durability;
		return toolItem;
	}
}
