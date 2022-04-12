package minicraft.entity.particle;

import minicraft.gfx.Sprite;

public class CloudParticle extends Particle {
	/// This is used for the player when steppen on in Cloud tile

	/**
	 * Creates a new particle at the given position. It has a lifetime of 30 ticks
	 * and a Cloud looking sprite.
	 * 
	 * @param x X map position
	 * @param y Y map position
	 */
	public CloudParticle(int x, int y) {
		super(x, y, 30, new Sprite(12, 2, 3));
	}
}
