package minicraft.entity.particle;

import minicraft.gfx.Sprite;

public class MagicParticle extends Particle {
	/// This is used for the Enchater.

	/**
	 * Creates a new particle at the given position. It has a lifetime of 16 ticks
	 * and a magic looking sprite.
	 * 
	 * @param x X map position
	 * @param y Y map position
	 */
	public MagicParticle(int x, int y) {
		super(x, y, 16, new Sprite(10, 3, 3));
	}
}
