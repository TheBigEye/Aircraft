package minicraft.entity.particle;

import minicraft.gfx.Sprite;

public class SmokeParticle extends Particle {
    /// This is used for the Lanterns.

    /**
     * Creates a new particle at the given position. It has a lifetime of 16 ticks
     * and a smoke looking sprite.
     * 
     * @param x X map position
     * @param y Y map position
     */
    public SmokeParticle(int x, int y) {
        super(x, y, 30, new Sprite(11, 4, 3));
    }
}
