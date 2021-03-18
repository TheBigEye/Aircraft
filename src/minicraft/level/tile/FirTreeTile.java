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

public class FirTreeTile extends Tile {

	protected FirTreeTile(String name) {
		super(name, (ConnectorSprite) null);
		connectsToSnow = true;
	}

	public void render(Screen screen, Level level, int x, int y) {
		Tiles.get("snow").render(screen, level, x, y);

		boolean u = level.getTile(x, y - 1) == this;
		boolean l = level.getTile(x - 1, y) == this;
		boolean r = level.getTile(x + 1, y) == this;
		boolean d = level.getTile(x, y + 1) == this;
		boolean ul = level.getTile(x - 1, y - 1) == this;
		boolean ur = level.getTile(x + 1, y - 1) == this;
		boolean dl = level.getTile(x - 1, y + 1) == this;
		boolean dr = level.getTile(x + 1, y + 1) == this;

		if (u && ul && l) {
			screen.render(x * 16 + 0, y * 16 + 0, 5 + 29 * 32, 0, 1);
		} else {
			screen.render(x * 16 + 0, y * 16 + 0, 4 + 28 * 32, 0, 1);// v
		}
		if (u && ur && r) {
			screen.render(x * 16 + 8, y * 16 + 0, 5 + 30 * 32, 0, 1);
		} else {
			screen.render(x * 16 + 8, y * 16 + 0, 5 + 28 * 32, 0, 1);
		}
		if (d && dl && l) {
			screen.render(x * 16 + 0, y * 16 + 8, 5 + 30 * 32, 0, 1);
		} else {
			screen.render(x * 16 + 0, y * 16 + 8, 4 + 29 * 32, 0, 1);
		}
		if (d && dr && r) {
			screen.render(x * 16 + 8, y * 16 + 8, 5 + 29 * 32, 0, 1);
		} else {
			screen.render(x * 16 + 8, y * 16 + 8, 5 + 31 * 32, 0, 1);// V
		}
	}

	// 6 grass
	// 1 + 29

	public void tick(Level level, int xt, int yt) {
		int damage = level.getData(xt, yt);
		if (damage > 0)
			level.setData(xt, yt, damage - 1);
	}

	public boolean mayPass(Level level, int x, int y, Entity e) {
		return false;
	}

	@Override
	public boolean hurt(Level level, int x, int y, Mob source, int dmg, Direction attackDir) {
		hurt(level, x, y, dmg);
		return true;
	}

	@Override
	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		if (Game.isMode("creative"))
			return false; // go directly to hurt method
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Axe) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					hurt(level, xt, yt, random.nextInt(10) + (tool.level) * 5 + 10);
					return true;
				}
			}
		}
		return false;
	}

	public void hurt(Level level, int x, int y, int dmg) {
		if (random.nextInt(100) == 0)
			level.dropItem(x * 16 + 8, y * 16 + 8, Items.get("Apple"));

		int damage = level.getData(x, y) + dmg;
		int treeHealth = 20;
		if (Game.isMode("creative"))
			dmg = damage = treeHealth;

		level.add(new SmashParticle(x * 16, y * 16));
		Sound.monsterHurt.play();

		level.add(new TextParticle("" + dmg, x * 16 + 8, y * 16 + 8, Color.RED));
		if (damage >= treeHealth) {
			level.dropItem(x * 16 + 8, y * 16 + 8, 1, 2, Items.get("Wood"));
			level.dropItem(x * 16 + 8, y * 16 + 8, 1, 2, Items.get("Acorn"));
			level.setTile(x, y, Tiles.get("snow"));
		} else {
			level.setData(x, y, damage);
		}
	}
}
