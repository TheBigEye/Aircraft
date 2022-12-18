package minicraft.entity.particle;

import minicraft.gfx.Sprite;

public class FireParticle extends Particle {
    /// This is used for Spawner, when they spawn an entity, and for the torches.
    private static Sprite Sprites = new Sprite(0, 14, 3);
    private int frame = 0;

    /**
     * Creates a new particle at the given position. It has a lifetime of 40 ticks
     * and a fire looking sprite.
     * 
     * @param x X map position
     * @param y Y map position
     */
    public FireParticle(int x, int y) {
        super(x, y, 40, Sprites);
    }

    @Override
    public void tick() {
        super.tick();
        
        // Animation
        frame = (frame + 1) % 4;
        Sprites = new Sprite(frame, 14, 3);
    }
}
