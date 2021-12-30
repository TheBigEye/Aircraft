package minicraft.entity.particle;

import minicraft.gfx.Sprite;

public class BrightParticle extends Particle {
    /// This is used for the Lanterns.

    private static Sprite Sprites = new Sprite(0, 17, 3);

    private int animFrame = 0;

    /**
     * Creates a new particle at the given position. It has a lifetime of 16 ticks
     * and a bright looking sprite.
     * 
     * @param x X map position
     * @param y Y map position
     */
    public BrightParticle(int x, int y) {
        super(x, y, 16, Sprites);
    }

    // Used for the bright particles animations :)
    public void tick() {
        super.tick();

        animFrame++;

        if (animFrame >= 8) {
            animFrame = 0;
        }

        if (animFrame == 0) {
            Sprites = new Sprite(0, 17, 3);
        }
        if (animFrame == 2) {
            Sprites = new Sprite(1, 17, 3);
        }
        if (animFrame == 4) {
            Sprites = new Sprite(2, 17, 3);
        }
        if (animFrame == 6) {
            Sprites = new Sprite(3, 17, 3);
        }

    }
}
