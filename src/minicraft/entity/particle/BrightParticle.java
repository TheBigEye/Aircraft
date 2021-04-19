package minicraft.entity.particle;

import minicraft.gfx.Sprite;

public class BrightParticle extends Particle {
	/// This is used for the Lanterns.
	
	/**
	 * Creates a new particle at the given position. It has a lifetime of 16 ticks
	 * and a bright looking sprite.
	 * 
	 * @param x X map position
	 * @param y Y map position
	 */
	public BrightParticle(int x, int y) {
		super(x, y, 16, new Sprite(10, 4, 3));
	}
}
