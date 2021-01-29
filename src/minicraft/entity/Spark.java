package minicraft.entity;

import java.util.List;

import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.entity.mob.boss.AirWizard;
import minicraft.gfx.Rectangle;
import minicraft.gfx.Screen;

public class Spark extends Entity {
	private int lifeTime; // how much time until the spark disappears
	private double xa, ya; // the x and y acceleration
	private double xx, yy; // the x and y positions
	private int time; // the amount of time that has passed
	private AirWizard owner; // the AirWizard that created this spark
	
	/**
	 * Creates a new spark. Owner is the AirWizard 2 which is spawning this spark.
	 * @param owner The AirWizard spawning the spark.
	 * @param xa X velocity.
	 * @param ya Y velocity.
	 */
	public Spark(AirWizard owner, double xa, double ya) {
		super(0, 0);
		
		this.owner = owner;
		xx = owner.x;
		yy = owner.y;
		this.xa = xa;
		this.ya = ya;
		
		// Max time = 629 ticks. Min time = 600 ticks.
		lifeTime = 9 * 10 + random.nextInt(10);
		

	}
	
	@Override
	public void tick() {
		time++;
		if (time >= lifeTime) {
			remove(); // remove this from the world
			return;
		}
		// move the spark:
		xx += xa;
		yy += ya;
		x = (int) xx;
		y = (int) yy;
		
		xx += xa;
		yy += ya;

		Player player = getClosestPlayer();
		int xd = player.x - x;
		int yd = player.y - y;
		
		int sig0 = 1;
		if (xd < sig0) xa = -0.2;
		if (xd > sig0) xa = +0.6;
		if (yd < sig0) ya = -0.4;
		if (yd > sig0) ya = +0.2;
		
		List<Entity> toHit = level.getEntitiesInRect(new Rectangle(x, y, 0, 0, Rectangle.CENTER_DIMS)); // gets the entities in the current position to hit.
		for (int i = 0; i < toHit.size(); i++) {
			Entity e = toHit.get(i);
			if (e instanceof Mob && !(e instanceof AirWizard)) {
				 // if the entity is a mob, but not a Air Wizard, then hurt the mob with 2 damage.
				((Mob)e).hurt(owner, 2);
			}
		}
	}
	
	/** Can this entity block you? Nope. */
	public boolean isSolid() {
		return false;
	}

	@Override
	public void render(Screen screen) {
		/* this first part is for the blinking effect */
		if (time >= lifeTime - 6 * 20) {
			if (time / 6 % 2 == 0) return; // if time is divisible by 12, then skip the rest of the code.
		}

		int randmirror = random.nextInt(4);

		screen.render(x - 4, y - 4 - 2, 0 + 20 * 32, randmirror, 2); // renders the spark
	}
	
	/**
	 * Returns the owners id as a string.
	 * @return the owners id as a string.
	 */
	public String getData() {
		return owner.eid+"";
	}
}
