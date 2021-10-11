package minicraft.entity.particle;

import minicraft.gfx.Sprite;

public class FireParticle extends Particle {
    /// This is used for Spawner, when they spawn an entity.

    private static Sprite Sprites = new Sprite(0, 14, 3);

    private int animFrame = 0;

    /**
     * Creates a new particle at the given position. It has a lifetime of 30 ticks
     * and a fire looking sprite.
     * 
     * @param x X map position
     * @param y Y map position
     */
    public FireParticle(int x, int y) {
        super(x, y, 40, Sprites);
    }

    // Used for the fire animations :)
    public void tick() {
        super.tick();

        animFrame++;

        if (animFrame >= 8) {
            animFrame = 0;
        }

        if (animFrame == 0) {
            Sprites = new Sprite(0, 14, 3);
        }
        if (animFrame == 2) {
            Sprites = new Sprite(1, 14, 3);
        }
        if (animFrame == 4) {
            Sprites = new Sprite(2, 14, 3);
        }
        if (animFrame == 6) {
            Sprites = new Sprite(3, 14, 3);
        }

    }

}
