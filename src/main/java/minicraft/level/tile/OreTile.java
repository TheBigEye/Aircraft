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
	public enum OreType {
		Iron(Items.get("Iron Ore"), 42, 0),
		Lapis(Items.get("Lapis"), 42, 2),
		Gold(Items.get("Gold Ore"), 44, 0),
		Gem(Items.get("Gem"), 44, 2);
    	
		private Item drop;
		private final int sx;
		private final int sy;

		OreType(Item drop, int sx, int sy) {
			this.drop = drop;
			this.sx = sx;
			this.sy = sy;
		}
		
		protected Item getOre() {
			return drop.clone();
		}
	}

	private OreType type;

	protected OreTile(OreType oreType) {
		super((oreType == OreTile.OreType.Lapis ? "Lapis" : oreType.name() + " Ore"), new Sprite(oreType.sx, oreType.sy, 2, 2, 1));
		this.type = oreType;
	}

	public void bumpedInto(Level level, int x, int y, Entity entity) {
		/// this was used at one point to hurt the player if they touched the ore;
		/// that's probably why the sprite is so spikey-looking.
	}

	public Item getOre() {
		return type.getOre();
	}

	public void hurt(Level level, int x, int y, int hurtDamage) {
		int damage = level.getData(x, y) + 1;
		int oreHealth = (random.nextInt(4) * 4) + 20;
		if (Game.isMode("Creative")) {
			hurtDamage = damage = oreHealth;
		}

		Sound.genericHurt.playOnLevel(x << 4, y << 4);
		level.add(new SmashParticle(x << 4, y << 4));

		level.add(new TextParticle("" + hurtDamage, (x << 4) + 8, (y << 4) + 8, Color.RED));
		if (damage > 0) {
			int count = random.nextInt(2) + 0;
			if (damage >= oreHealth) {
				level.setTile(x, y, Tiles.get("Dirt"));
				count += 2;
			} else {
				level.setData(x, y, damage);
			}			

			if (type.drop.equals(Items.get("Gem")) && !Game.isMode("Creative")){
				AchievementsDisplay.setAchievement("minicraft.achievement.find_gem", true);
			}

			level.dropItem((x << 4) + 8, (y << 4) + 8, count, type.getOre());
		}
	}

	public boolean hurt(Level level, int x, int y, Mob source, int hurtDamage, Direction attackDir) {
		hurt(level, x, y, 0);
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
