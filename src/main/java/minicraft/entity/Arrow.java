package minicraft.entity;

import java.util.List;

import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.gfx.Color;
import minicraft.gfx.Rectangle;
import minicraft.gfx.Screen;
import minicraft.item.Items;

public class Arrow extends Entity implements ClientTickable {
	private Direction dir;
	private int damage;
	private Mob owner;
	private int speed;

	public Arrow(Mob owner, Direction dir, int damage) {
		this(owner, owner.x, owner.y, dir, damage);
	}

	public Arrow(Mob owner, int x, int y, Direction dir, int damage) {
		super(Math.abs(dir.getX()) + 1, Math.abs(dir.getY()) + 1);
		this.owner = owner;
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.damage = damage;
		this.speed = damage > 3 ? 4 : (damage >= 0 ? 3 : 2);
	}

	/**
	 * Generates information about the arrow.
	 * 
	 * @return string representation of owner, xdir, ydir and damage.
	 */
	public String getData() {
		return owner.eid + ":" + dir.ordinal() + ":" + damage;
	}

	@Override
	public void tick() {
		// Check if the projectile is out of bounds
		if ( x < 0 || x >> 4 > level.w || y < 0 || y >> 4 > level.h) {
			remove();
			return;
		}		

		// Update position based on direction and speed
		x += dir.getX() * speed;
		y += dir.getY() * speed;

		// Check if the projectile collides with any entities
		List<Entity> entitylist = level.getEntitiesInRect(new Rectangle(x, y, 0, 0, Rectangle.CENTER_DIMS));
		boolean criticalHit = random.nextInt(11) < 9;

		for (Entity hit : entitylist) {
			if (hit instanceof Mob && hit != owner) {
				Mob mob = (Mob) hit;
				int extradamage = (hit instanceof Player ? 0 : 3) + (criticalHit ? 0 : 1);
				mob.hurt(owner, damage + extradamage, dir);
				if (random.nextBoolean()) {
					remove();
				}
				break; // No need to check other entities
			}
		}

		// Check if the projectile collides with a solid block (pos / 16 -> pos >> 4)
		if (!level.getTile(x >> 4, y >> 4).mayPass(level, x >> 4, y >> 4, this) && level.getTile(x >> 4, y >> 4).id != 16) {
			if (random.nextBoolean() && owner instanceof Player) {
				level.dropItem(x - (dir.getX() * 4), y - (dir.getY() * 4), Items.get("Arrow"));
			}
			Sound.genericHurt.playOnWorld(x, y);
			remove();
		}
	}

	@Override
	public boolean isSolid() {
		return false;
	}
	
	@Override
	public boolean canSwim() {
		return true;
	}

	@Override
	public void render(Screen screen) {
		int xt = 0;
		int yt = 2;

		if (dir == Direction.LEFT) xt = 1;
		if (dir == Direction.UP) xt = 2;
		if (dir == Direction.DOWN) xt = 3;

		if (Settings.get("shadows").equals(true)) {
			screen.render(x - 4, y + 2, xt + yt * 32, 0, 0, -1, false, Color.BLACK); // Shadow
		}
		screen.render(x - 4, y - 4, xt + yt * 32, 0, 0); // Sprite
	}
}
