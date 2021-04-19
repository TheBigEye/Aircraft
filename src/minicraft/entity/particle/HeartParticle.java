package minicraft.entity.particle;

import minicraft.gfx.Sprite;

public class HeartParticle extends Particle {
	/// This is used for lovely mobs.
	
	/**
	 * Creates a new particle at the given position. It has a lifetime of 30 ticks
	 * and a heart looking sprite.
	 * 
	 * @param x X map position
	 * @param y Y map position
	 */
	public HeartParticle(int x, int y) {
		super(x, y, 16, new Sprite(10, 2, 3));
	}
}
