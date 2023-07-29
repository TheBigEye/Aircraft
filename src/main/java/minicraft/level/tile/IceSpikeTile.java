package minicraft.level.tile;

import minicraft.core.Game;
import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Mob;
import minicraft.entity.particle.SmashParticle;
import minicraft.entity.particle.TextParticle;
import minicraft.graphic.Color;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.item.Items;
import minicraft.level.Level;

public class IceSpikeTile extends Tile {
	private static Sprite sprite = new Sprite(15, 16, 1);

	protected IceSpikeTile(String name) {
		super(name, sprite);
		connectsToSnow = true;
	}

	@Override
	public void bumpedInto(Level level, int x, int y, Entity entity) {
		if (!(entity instanceof Mob) || Settings.get("diff").equals("Peaceful")) {
			return; // Cannot do damage
		}

		if (entity instanceof Mob) {
			((Mob) entity).hurt(this, x, y, 1 + Settings.getIndex("diff"));
		}
	}

	public boolean hurt(Level level, int x, int y, Mob source, int hurtDamage, Direction attackDir) {
		int damage = level.getData(x, y) + hurtDamage;
		int iceHealth = 10;

		if (Game.isMode("Creative")) {
			hurtDamage = damage = iceHealth;
		}

		level.add(new SmashParticle(x << 4, y << 4));
		level.add(new TextParticle("" + hurtDamage, (x << 4) + 8, (y << 4) + 8, Color.BLUE));

		if (damage >= iceHealth) {
			Sound.genericHurt.playOnLevel(x << 4, y << 4);
			level.setTile(x, y, Tiles.get("Snow"));
			
			level.dropItem((x << 4) + 8, (y << 4) + 8, 2, 4, Items.get("Icicle"));
		} else {
			level.setData(x, y, damage);
		}
		return true;
	}

	@Override
	public boolean mayPass(Level level, int x, int y, Entity entity) {
		return false;
	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		Tiles.get("Snow").render(screen, level, x, y);

		int data = level.getData(x, y);
		int shape = (data >> 4) % 2;

		x <<= 4;
		y <<= 4;

		sprite.render(screen, x + 8 * shape, y);
		sprite.render(screen, x + 8 * (shape == 0 ? 1 : 0), y + 8);
	}

	@Override
	public boolean tick(Level level, int xt, int yt) {
		int damage = level.getData(xt, yt);
		if (damage > 0) {
			level.setData(xt, yt, damage - 1);
			return true;
		}
		return false;
	}
}
