package minicraft.entity;

import minicraft.entity.mob.EyeQueen;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.graphic.Rectangle;
import minicraft.graphic.Screen;

import java.util.List;

public class Fireball extends Entity {
	private Mob owner; // The mob that created this spark
	private final int lifeTime; // how much time until the spark disappears
	private double xa, ya; // the x and y acceleration
	private double xx, yy; // the x and y positions
	private int durTime = 15 * 13 + random.nextInt(30);
	private int time; // the amount of time that has passed
	private int type;
	private int damage;

	public Fireball(Mob owner, double xa, double ya, int type, int damage) {
		super(0, 0);

		this.owner = owner;
		xx = owner.x;
		yy = owner.y;
		this.xa = xa;
		this.ya = ya;
		this.type = type;
		this.damage = damage;

		lifeTime = durTime;
	}

	private void SparkCloud(int damage) {
	    durTime = 15 * 13 + random.nextInt(30);

	    // Move the spark:
	    xx += xa; x = (int) xx;
	    yy += ya; y = (int) yy;

	    Player player = getClosestPlayer();

	    if (player != null) {
	        int xd = owner.x - x;
	        int yd = owner.y - y;

	        int sig = 2;
	        xa = 0; ya = 0;

	        if (xd < sig) xa -= random.nextInt(2);
	        if (xd > sig) xa += random.nextInt(2);
	        if (yd < sig) ya -= random.nextInt(2);
	        if (yd > sig) ya += random.nextInt(2);

	        if (random.nextBoolean()) {
	        	xa -= random.nextInt(5);
	        	ya -= random.nextInt(5);
	        } else {
	        	xa += random.nextInt(5);
	        	ya += random.nextInt(5);
	        }

	        if (player.isWithin(0, this)) {
	            player.hurt(owner, damage);
	        }
	    }
	}

	private void SparkRain(int damage) {
		durTime = 25 * 12 + random.nextInt(20);

		// move the spark to the player positon:
		xx += xa; x = (int) xx;
		yy += ya; y = (int) yy;


		//level.add(new FireParticle(x - 4, y - 4, 3));

		Player player = getClosestPlayer();
		if (player != null) { // avoid NullPointer if player dies
			// If the entity is a mob, but not a Air Wizard, then hurt it.
			List<Entity> toHit = level.getEntitiesInRect(entity -> entity instanceof Mob && !(entity instanceof EyeQueen), new Rectangle(x, y, 0, 0, Rectangle.CENTER_DIMS)); // Gets the entities in the current position to hit.
			toHit.forEach(entity -> ((Mob) entity).hurt(owner, damage));
		}
	}

	private void SparkSpiralRain(int damage) {
		durTime = 30 * 10 + random.nextInt(34);

		// move the spark:
		xx += xa; x = (int) xx;
		yy += ya; y = (int) yy;

		//level.add(new FireParticle(x - 4, y, 3));

		Player player = getClosestPlayer();
		if (player != null) { // avoid NullPointer if player dies
			if (player.isWithin(0, this)) {
				player.hurt(owner, damage);
			}
		}
	}

	@Override
	public void tick() {
		time++;

		if (type == 1) SparkCloud(this.damage);
		if (type == 2) SparkRain(this.damage);
		if (type == 3) SparkSpiralRain(this.damage);

		if (time >= lifeTime) {
			remove(); // Remove this from the world
		}
	}


	@Override /** Can this entity block you? Nope. */
	public boolean isSolid() {
		return false;
	}

	@Override
	public void render(Screen screen) {
		screen.render(x - 4, y - 4, ((time % 4) + 4) + (2 << 5), 0, 0);
	}

	/**
	 * Returns the owners id as a string.
	 *
	 * @return the owners id as a string.
	 */
	public String getData() {
		return owner.eid + "";
	}

    public int getLightRadius() {
        return 1;
    }
}
