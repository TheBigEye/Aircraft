package minicraft.entity;

import java.util.List;

import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.gfx.Rectangle;
import minicraft.gfx.Screen;

public class Fireball extends Entity implements ClientTickable {
	private Direction dir;
	private int damage;
	public Mob owner;
	private int speed;

	public Fireball(Mob owner, Direction dir, int damage) {
		this(owner, owner.x, owner.y, dir, damage);
	}

	public Fireball(Mob owner, int x, int y, Direction dir, int damage) {
		super(Math.abs(dir.getX()) + 1, Math.abs(dir.getY()) + 1);
		this.owner = owner;
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.damage = damage;
		this.speed = damage > 3 ? 3 : (damage >= 0 ? 2 : 1);
	}

	public String getData() {
		return owner.eid + ":" + dir.ordinal() + ":"+damage;
	}

	@Override
	public void tick() {
		if (x < 0 || x >> 4 > level.w || y < 0 || y >> 4 > level.h) {
			remove(); // Remove when out of bounds
			return;
		}

		x += dir.getX() * speed;
		y += dir.getY() * speed;

		// TODO I think I can just use the xr yr vars, and the normal system with touchedBy(entity) to detect collisions instead.

		List<Entity> entitylist = level.getEntitiesInRect(new Rectangle(x, y, 0, 0, Rectangle.CENTER_DIMS));
		boolean criticalHit = random.nextInt(11) < 9;
		for (Entity hit : entitylist) {
			if (hit instanceof Mob && hit != owner) {
				Mob mob = (Mob) hit;
				int extradamage = (hit instanceof Player ? 0 : 3) + (criticalHit ? 0 : 1);
				mob.hurt(owner, damage + extradamage, dir);
				//mob.ignite(50);
			}

			if (!level.getTile(x >> 4, y >> 4).mayPass(level, x >> 4, y >> 4, this) && level.getTile(x >> 4, y >> 4).id != 16) {
				this.remove();
			}
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
		int xt = 4;
		int yt = 2;

		if (dir == Direction.LEFT) xt = 5;
		if (dir == Direction.UP) xt = 6;
		if (dir == Direction.DOWN) xt = 7;

		screen.render(x - 4, y - 4, xt + yt * 32, 0);
	}
}
