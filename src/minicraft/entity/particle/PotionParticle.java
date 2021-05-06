package minicraft.entity.particle;

import minicraft.gfx.Sprite;

public class PotionParticle extends Particle {
	/// This is used for the Lanterns.
	
	/**
	 * Creates a new particle at the given position. It has a lifetime of 16 ticks
	 * and a bright looking sprite.
	 * 
	 * @param x X map position
	 * @param y Y map position
	 */
	public PotionParticle(int x, int y) {
		super(x, y, 16, new Sprite(11, 3, 3));
	}
}
