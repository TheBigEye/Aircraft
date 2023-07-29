package minicraft.level.tile;

import minicraft.core.Game;
import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Firefly;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.TextParticle;
import minicraft.graphic.Color;
import minicraft.graphic.ConnectorSprite;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;
import minicraft.screen.AchievementsDisplay;

public class MossyRockTile extends Tile {
	private ConnectorSprite sprite = new ConnectorSprite(MossyRockTile.class, new Sprite(58, 1, 3, 3, 1), new Sprite(61, 3, 2, 2, 1), new Sprite(61, 1, 2, 2, 1)) {

		@Override
		public boolean connectsTo(Tile tile, boolean isSide) {
			return tile == Tiles.get("Mossy Rock");
		}
	};

	private boolean dropCoal = false;
	private final int maxHealth = 75;

	private int damage;

	public MossyRockTile(String name) {
		super(name, (ConnectorSprite) null);
		connectorSprite = sprite;
	}

	@Override
	public void hurt(Level level, int x, int y, int hurtDamage) {
		damage = level.getData(x, y) + (hurtDamage / 2);
		if (Game.isMode("Creative")) {
			hurtDamage = damage = maxHealth;
			dropCoal = true;
		}

		Sound.genericHurt.playOnLevel(x << 4, y << 4);

		level.add(new TextParticle("" + hurtDamage, (x << 4) + 8, (y << 4) + 8, Color.RED));
		if (damage >= maxHealth) {

			if (dropCoal) {
				level.dropItem((x << 4) + 8, (y << 4) + 8, 1, 3, Items.get("Stone"));
				int coal = 0;

				if (!Settings.get("diff").equals("Hard")) {
					coal++;
				}
				level.dropItem((x << 4) + 8, (y << 4) + 8, coal, coal + 1, Items.get("Coal"));
			} else {
				level.dropItem((x << 4) + 8, (y << 4) + 8, 1, 2, Items.get("Stone"));
			}
			level.setTile(x, y, Tiles.get("Dirt"));
		} else {
			level.setData(x, y, damage);
		}
	}

	@Override
	public boolean hurt(Level level, int x, int y, Mob source, int hurtDamage, Direction attackDir) {
		dropCoal = false; // Can only be reached when player hits w/o pickaxe, so remove ability to get coal
		hurt(level, x, y, hurtDamage);
		return true;
	}

	@Override
	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		// Creative mode can just act like survival here
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Pickaxe && player.payStamina(4 - tool.level) && tool.payDurability()) {
				AchievementsDisplay.setAchievement("minicraft.achievement.mine_stone", true);

				// Drop coal since we use a pickaxe.
				dropCoal = true;
				hurt(level, xt, yt, tool.getDamage());
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mayPass(Level level, int x, int y, Entity entity) {
		return entity instanceof Firefly;
	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		sprite.sparse.color = DirtTile.dirtColor(level.depth);
		sprite.render(screen, level, x, y);
	}

	@Override
	public boolean tick(Level level, int xt, int yt) {
		damage = level.getData(xt, yt);
		if (damage > 0) {
			level.setData(xt, yt, damage - 1);
			return true;
		}
		return false;
	}
}
