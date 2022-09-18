package minicraft.level.tile;

import minicraft.core.Game;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.SmashParticle;
import minicraft.entity.particle.TextParticle;
import minicraft.gfx.Color;
import minicraft.gfx.ConnectorSprite;
import minicraft.gfx.Screen;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;
import minicraft.screen.AchievementsDisplay;

/// this is all the spikey stuff (except "cloud cactus")
public class OreTile extends Tile {
	public enum OreType {
		Iron(24, 25, 0, 1, 2, 3, Items.get("Iron Ore"), 0),
		Lapis(26, 27, 0, 1, 2, 3, Items.get("Lapis"), 2),
		Gold(28, 29, 0, 1, 2, 3, Items.get("Gold Ore"), 4),
		Gem(30, 31, 0, 1, 2, 3, Items.get("Gem"), 6);

    	private int sprite_x1, sprite_x2;
    	private int sprite_y1, sprite_y2;
    	private int sprite_y3, sprite_y4;
    	
		private Item drop;
		public final int color;

		OreType(int sprite_x1, int sprite_x2, int sprite_y1, int sprite_y2, int sprite_y3, int sprite_y4, Item drop, int color) {
			this.sprite_x1 = sprite_x1; this.sprite_x2 = sprite_x2;
			this.sprite_y1 = sprite_y1; this.sprite_y2 = sprite_y2;
			this.sprite_y3 = sprite_y3; this.sprite_y4 = sprite_y4;
			
			this.drop = drop;
			this.color = color;
		}
		
		protected Item getOre() {
			return drop.clone();
		}
	}

	private OreType type;

	protected OreTile(OreType o) {
		super((o == OreTile.OreType.Lapis ? "Lapis" : o.name() + " Ore"), (ConnectorSprite) null);
		this.type = o;
	}

	public void bumpedInto(Level level, int x, int y, Entity entity) {
		/// this was used at one point to hurt the player if they touched the ore;
		/// that's probably why the sprite is so spikey-looking.
	}

	public Item getOre() {
		return type.getOre();
	}

	public void hurt(Level level, int x, int y, int dmg) {
		int damage = level.getData(x, y) + 1;
		int oreHealth = random.nextInt(10) * 4 + 20;
		if (Game.isMode("Creative")) {
			dmg = damage = oreHealth;
		}

		level.add(new SmashParticle(x * 16, y * 16));
		Sound.Tile_generic_hurt.play();

		level.add(new TextParticle("" + dmg, x * 16 + 8, y * 16 + 8, Color.RED));
		if (dmg > 0) {
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

			level.dropItem(x * 16 + 8, y * 16 + 8, count, type.getOre());
		}
	}

	public boolean hurt(Level level, int x, int y, Mob source, int dmg, Direction attackDir) {
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

	public boolean mayPass(Level level, int x, int y, Entity e) {
		return false;
	}

	public void render(Screen screen, Level level, int x, int y) {
		Tiles.get("Dirt").render(screen, level, x, y);

        boolean u = level.getTile(x, y - 1) == this;
        boolean l = level.getTile(x - 1, y) == this;
        boolean r = level.getTile(x + 1, y) == this;
        boolean d = level.getTile(x, y + 1) == this;
        boolean ul = level.getTile(x - 1, y - 1) == this;
        boolean ur = level.getTile(x + 1, y - 1) == this;
        boolean dl = level.getTile(x - 1, y + 1) == this;
        boolean dr = level.getTile(x + 1, y + 1) == this;

        if (u && ul && l) {
            screen.render(x * 16 + 0, y * 16 + 0, type.sprite_x2 + type.sprite_y2 * 32, 0, 1); // y2
        } else {
            screen.render(x * 16 + 0, y * 16 + 0, type.sprite_x1 + type.sprite_y1 * 32, 0, 1); // y1
        }
        if (u && ur && r) {
            screen.render(x * 16 + 8, y * 16 + 0, type.sprite_x2 + type.sprite_y3 * 32, 0, 1); // y3
        } else {
            screen.render(x * 16 + 8, y * 16 + 0, type.sprite_x2 + type.sprite_y1 * 32, 0, 1); // y1
        }
        if (d && dl && l) {
            screen.render(x * 16 + 0, y * 16 + 8, type.sprite_x2 + type.sprite_y3 * 32, 0, 1); // y3
        } else {
            screen.render(x * 16 + 0, y * 16 + 8, type.sprite_x1 + type.sprite_y2 * 32, 0, 1); // y2
        }
        if (d && dr && r) {
            screen.render(x * 16 + 8, y * 16 + 8, type.sprite_x2 + type.sprite_y2 * 32, 0, 1); // y2
        } else {
            screen.render(x * 16 + 8, y * 16 + 8, type.sprite_x2 + type.sprite_y4 * 32, 0, 1); // y4
        }
	}
}
