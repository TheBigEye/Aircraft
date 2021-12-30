package minicraft.entity.particle;

import minicraft.gfx.Sprite;

public class SplashParticle extends Particle {
    /// This is used for boats and when the player swim in the water

    private static Sprite Sprites = new Sprite(0, 15, 3);

    private int animFrame = 0;

    /**
     * Creates a new particle at the given position. It has a lifetime of 30 ticks
     * and a splash looking sprite.
     * 
     * @param x X map position
     * @param y Y map position
     */
    public SplashParticle(int x, int y) {
        super(x, y, 40, Sprites);
    }

    // Used for the water animations :)
    public void tick() {
        super.tick();

        animFrame++;

        if (animFrame >= 8) {
            animFrame = 0;
        }

        if (animFrame == 0) {
            Sprites = new Sprite(0, 15, 3);
        }
        if (animFrame == 2) {
            Sprites = new Sprite(1, 15, 3);
        }
        if (animFrame == 4) {
            Sprites = new Sprite(2, 15, 3);
        }
        if (animFrame == 6) {
            Sprites = new Sprite(3, 15, 3);
        }

    }
}
