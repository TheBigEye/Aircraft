package minicraft.entity.particle;

import minicraft.graphic.Sprite;

public class HeartParticle extends Particle {
	/// This is used for lovely mobs.

	private static Sprite Sprites = new Sprite(0, 16, 3);
	private int frame = 0;

	/**
	 * Creates a new particle at the given position. It has a lifetime of 16 ticks
	 * and a heart looking sprite.
	 * 
	 * @param x X map position
	 * @param y Y map position
	 */
	public HeartParticle(int x, int y) {
		super(x, y, 16, Sprites);
	}

	// Used for the hearts animations :)
	public void tick() {
		super.tick();

        // Animation
        frame = (frame + 1) % 4;
        Sprites = new Sprite(frame, 16, 3);
	}
}
