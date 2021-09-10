package minicraft.entity;

import java.util.List;

import minicraft.core.Game;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.boss.AirWizardPhase2;
import minicraft.gfx.Rectangle;
import minicraft.gfx.Screen;

public class Spark2 extends Entity {
	private int lifeTime; // how much time until the spark disappears
	private double xa, ya; // the x and y acceleration
	private double xx, yy; // the x and y positions
	private int time; // the amount of time that has passed
	private final AirWizardPhase2 owner; // the AirWizard that created this spark
	
	/**
	 * Creates a new spark. Owner is the AirWizard 2 which is spawning this spark.
	 * @param owner The AirWizard spawning the spark.
	 * @param xa X velocity.
	 * @param ya Y velocity.
	 */
	public Spark2(AirWizardPhase2 owner, double xa, double ya) {
		super(0, 0);
		
		this.owner = owner;
		xx = owner.x;
		yy = owner.y;
		this.xa = xa;
		this.ya = ya;
		
		// Max time = 629 ticks. Min time = 600 ticks.
		lifeTime = 25 * 10 + random.nextInt(20);
	}
	
	@Override
	public void tick() {
		time++;
		if (time >= lifeTime) {
			remove(); // remove this from the world
			return;
		}
		
		// move the spark to the player positon:
		xx += xa;
		yy += ya;
		

		
		x = (int) xx;
		y = (int) yy;
		
		List<Entity> toHit = level.getEntitiesInRect(entity -> entity instanceof Mob && !(entity instanceof AirWizardPhase2), new Rectangle(x, y, 0, 0, Rectangle.CENTER_DIMS)); // gets the entities in the current position to hit.
		toHit.forEach(entity -> ((Mob) entity).hurt(owner, 4));
	}
	
	/** Can this entity block you? Nope. */
	public boolean isSolid() {
		return false;
	}

	@Override
	public void render(Screen screen) {
		int randmirror = 0;

		// If we are in a menu, or we are on a server.
		if (Game.getMenu() == null || Game.ISONLINE) {
			// The blinking effect.
			if (time >= lifeTime - 6 * 20) {
				if (time / 6 % 2 == 0) return; // If time is divisible by 12, then skip the rest of the code.
			}

			randmirror = random.nextInt(4);
		
		}

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

