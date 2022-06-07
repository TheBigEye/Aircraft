package minicraft.entity;

import java.util.List;
import minicraft.core.Game;
import minicraft.core.io.Settings;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.entity.mob.boss.AirWizard;
import minicraft.entity.mob.boss.AirWizardPhase2;
import minicraft.entity.mob.boss.AirWizardPhase3;
import minicraft.gfx.Color;
import minicraft.gfx.Rectangle;
import minicraft.gfx.Screen;

public class Spark extends Entity {

	private Mob owner; // The mob that created this spark
	private double xa, ya; // the x and y acceleration
	private double xx, yy; // the x and y positions
	private int type;

	private int lifeTime; // how much time until the spark disappears
	private int LTFactor = 15 * 13 + random.nextInt(30);
	private int time; // the amount of time that has passed

	/**
	 * Creates a new spark. Owner is the AirWizard which is spawning this spark.
	 * 
	 * @param owner The AirWizard spawning the spark.
	 * @param xa	X velocity.
	 * @param ya	Y velocity.
	 */
	public Spark(Mob owner, double xa, double ya, int type) {
		super(0, 0);

		this.owner = owner;
		xx = owner.x;
		yy = owner.y;
		this.xa = xa;
		this.ya = ya;
		this.type = type;

		lifeTime = LTFactor;
	}

	@Override
	public void tick() {
		time++;

		if (type == 1) {
			LTFactor = 15 * 13 + random.nextInt(30);

			// Move the spark:
			xx += xa; x = (int) xx;
			yy += ya; y = (int) yy;

			Player player = getClosestPlayer();
			if (player != null) {

				int xd = player.x - x;
				int yd = player.y - y;

				int sig0 = 1; 
				xa = ya = 0;

				if (xd < sig0) xa = -0.5;
				if (xd > sig0) xa = +0.6;
				if (yd < sig0) ya = -0.5;
				if (yd > sig0) ya = +0.6;

				// Random position
				switch (random.nextInt(3)) {
				case 0: xa += 1; ya += 1; break;
				case 1: xa -= 1; ya -= 1; break;
				case 2: xa -= 1; ya += 1; break;
				case 3: xa += 1; ya -= 1; break;
				default: xa += 1; ya += 1; break;
				}
			}

			// if the entity is a mob, but not a Air Wizard, then hurt the mob with 2 damage.
			List<Entity> toHit = level.getEntitiesInRect(entity -> entity instanceof Mob && !(entity instanceof AirWizard), new Rectangle(x, y, 0, 0, Rectangle.CENTER_DIMS)); // gets the entities in the current position to hit.
			toHit.forEach(entity -> ((Mob) entity).hurt(owner, 2));

		} else if (type == 2) {

			LTFactor = 25 * 10 + random.nextInt(20);

			// move the spark to the player positon:
			xx += xa; x = (int) xx;
			yy += ya; y = (int) yy;

			List<Entity> toHit = level.getEntitiesInRect(entity -> entity instanceof Mob && !(entity instanceof AirWizardPhase2), new Rectangle(x, y, 0, 0, Rectangle.CENTER_DIMS));
			toHit.forEach(entity -> ((Mob) entity).hurt(owner, 4));

		} else if (type == 3) {

			LTFactor = 30 * 10 + random.nextInt(30);

			// move the spark:
			xx += xa; x = (int) xx;
			yy += ya; y = (int) yy;

			List<Entity> toHit = level.getEntitiesInRect(entity -> entity instanceof Mob && !(entity instanceof AirWizardPhase3), new Rectangle(x, y, 0, 0, Rectangle.CENTER_DIMS));
			toHit.forEach(entity -> ((Mob) entity).hurt(owner, 5));

		}

		if (time >= lifeTime) {
			remove(); // Remove this from the world
			return;
		}
	}

	/** Can this entity block you? Nope. */
	@Override
	public boolean isSolid() {
		return false;
	}

	@Override
	public void render(Screen screen) {
		int randmirror = 0;

		// If we are in a menu, or we are on a server.
		if (Game.getDisplay() == null) {

			// The blinking effect.
			if (time >= lifeTime - 6 * 20) {
				if (time / 6 % 2 == 0) {
					return; // If time is divisible by 12, then skip the rest of the code.
				}
			}
			randmirror = random.nextInt(4);
		}

		if ((boolean) Settings.get("shadows")  == true) {
            screen.render(x - 4, y - 4 + 2, 0 + 20 * 32, randmirror, 2, -1, false, Color.BLACK); // renders the shadow on the ground
        }        
		screen.render(x - 4, y - 4 - 2, 0 + 20 * 32, randmirror, 2); // renders the spark
	}

	/**
	 * Returns the owners id as a string.
	 * 
	 * @return the owners id as a string.
	 */
	public String getData() {
		return owner.eid + "";
	}
}