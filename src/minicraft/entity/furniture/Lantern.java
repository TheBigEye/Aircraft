package minicraft.entity.furniture;

import java.util.Random;

import minicraft.entity.particle.BrightParticle;
import minicraft.gfx.Sprite;

public class Lantern extends Furniture {
	public enum Type {
		
		NORM("Lantern", 9, 0),
		IRON("Iron Lantern", 12, 2),
		GOLD("Gold Lantern", 15, 4);

		protected int light, offset;
		protected String title;

		Type(String title, int light, int offset) {
			this.title = title;
			this.offset = offset;
			this.light = light;
		}
	}

	public Lantern.Type type;
	
	private Random rnd = new Random();

	/**
	 * Creates a lantern of a given type.
	 * 
	 * @param type Type of lantern.
	 */
	public Lantern(Lantern.Type type) {
		super(type.title, new Sprite(18 + type.offset, 24, 2, 2, 2), 3, 2);
		this.type = type;
	}

	public void tick() {
		super.tick();

		int randX = rnd.nextInt(10);
		int randY = rnd.nextInt(9);

		if (random.nextInt(12) == 1) {
			level.add(new BrightParticle(x - 9 + randX, y - 12 + randY));
		}
		if (random.nextInt(12) == 4) {

		}
		if (random.nextInt(12) == 6) {
			level.add(new BrightParticle(x - 9 + randX, y - 12 + randY));
		}
		if (random.nextInt(12) == 8) {

		}
		if (random.nextInt(12) == 12) {
			level.add(new BrightParticle(x - 9 + randX, y - 12 + randY));
		}
	}
		
	
	@Override
	public Furniture clone() {
		return new Lantern(type);
	}

	/**
	 * Gets the size of the radius for light underground (Bigger number, larger
	 * light)
	 */
	@Override
	public int getLightRadius() {
		return type.light;
	}
}
