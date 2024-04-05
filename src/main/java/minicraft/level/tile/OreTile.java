package minicraft.level.tile;

import minicraft.core.Game;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.SmashParticle;
import minicraft.entity.particle.TextParticle;
import minicraft.graphic.Color;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;
import minicraft.screen.AchievementsDisplay;

/// this is all the spikey stuff (except "cloud cactus")
public class OreTile extends Tile {
	
	protected enum Ore {
		Iron(Items.get("Iron Ore"), new Sprite(42, 0, 2, 2, 1)),
		Lapis(Items.get("Lapis"), new Sprite(42, 2, 2, 2, 1)),
		Gold(Items.get("Gold Ore"), new Sprite(44, 0, 2, 2, 1)),
		Gem(Items.get("Gem"), new Sprite(44, 2, 2, 2, 1));
    	
		private Item drop;
		private final Sprite sprite;

		Ore(Item drop, Sprite sprite) {
			this.drop = drop;
			this.sprite = sprite;
		}
		
		protected Item getOre() {
			return drop.clone();
		}
	}

	private Ore ore;

	protected OreTile(Ore ore) {
		super((ore == Ore.Lapis ? "Lapis" : ore.name() + " Ore"), ore.sprite);
		this.ore = ore;
	}

	public void bumpedInto(Level level, int x, int y, Entity entity) {
		/// this was used at one point to hurt the player if they touched the ore;
		/// that's probably why the sprite is so spikey-looking.
	}

	public Item getOre() {
		return ore.getOre();
	}

	public void hurt(Level level, int x, int y, int hurtDamage) {
		int damage = level.getData(x, y) + hurtDamage;
		int health = random.nextInt(10) * 4 + 20;
		
		if (Game.isMode("Creative")) {
			hurtDamage = damage = health;
		}

		Sound.playAt("genericHurt", x << 4, y << 4);
		level.add(new SmashParticle(x << 4, y << 4));

		level.add(new TextParticle("" + hurtDamage, (x << 4) + 8, (y << 4) + 8, Color.RED));
		if (damage > 0) {
			int count = random.nextInt(2);
			if (damage >= health) {
				level.setTile(x, y, Tiles.get("Dirt"));
				count += 2;
			} else {
				level.setData(x, y, damage);
			}			

			if (ore.drop.equals(Items.get("Gem"))){
				AchievementsDisplay.setAchievement("minicraft.achievement.find_gem", true);
			}

			level.dropItem((x << 4) + 8, (y << 4) + 8, count, ore.getOre());
		}
	}

	public boolean hurt(Level level, int x, int y, Mob source, int hurtDamage, Direction attackDir) {
		hurt(level, x, y, hurtDamage);
		return true;
	}

	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		if (Game.isMode("Creative")) {
			return false; // go directly to hurt method
		}
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Pickaxe) {
				if (player.payStamina(6 - tool.level) && tool.payDurability()) {
					hurt(level, xt, yt, tool.getDamage());
					return true;
				}
			}
		}
		return false;
	}

	public boolean mayPass(Level level, int x, int y, Entity entity) {
		return false;
	}

	public void render(Screen screen, Level level, int x, int y) {
		Tiles.get("Dirt").render(screen, level, x, y);
		sprite.render(screen, x << 4, y << 4);
	}
}
