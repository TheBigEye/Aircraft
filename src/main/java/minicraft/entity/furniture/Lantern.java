package minicraft.entity.furniture;

import minicraft.core.io.Settings;
import minicraft.entity.particle.BrightParticle;
import minicraft.graphic.Sprite;

public class Lantern extends Furniture {
	public enum Type {

		NORM("Lantern", 9, 0),
		IRON("Iron Lantern", 12, 2),
		GOLD("Gold Lantern", 15, 4);

		protected String title;
		protected int light;
		protected int offset;
		
		Type(String title, int light, int offset) {
			this.title = title;
			this.offset = offset;
			this.light = light;
		}
	}

	public Lantern.Type type;

	private int tickTime;

	/**
	 * Creates a lantern of a given type.
	 * 
	 * @param type Type of lantern.
	 */
	public Lantern(Lantern.Type type) {
		super(type.title, new Sprite(16 + type.offset, 30, 2, 2, 2), 3, 2);
		this.type = type;
	}

	@Override
	public Furniture clone() {
		return new Lantern(type);
	}

	/**
	 * Gets the size of the radius for light underground (Bigger number, larger light)
	 */
	@Override
	public int getLightRadius() {
		return type.light;
	}

	@Override
	public void tick() {
		super.tick();
		tickTime++;

		// Add bright particles
		if ((tickTime / 1 % 12 == 0) && Settings.get("particles").equals(true)) {
			level.add(new BrightParticle(x - 5 + random.nextInt(4), y - 4 + random.nextInt(4)));
		}
	}
}
