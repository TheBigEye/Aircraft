package minicraft.entity.particle;

import minicraft.gfx.Sprite;

public class BrightParticle extends Particle {
    /// This is used for the Lanterns.
    private static Sprite sprite = new Sprite(0, 17, 3);
    private int spriteFrame = 0;

    /**
     * Creates a new particle at the given position. It has a lifetime of 16 ticks
     * and a bright looking sprite.
     * 
     * @param x X map positiona
     * @param y Y map position
     */
    public BrightParticle(int x, int y) {
        super(x, y, 16, sprite);
    }

    @Override
    public void tick() {
        super.tick();
        
        spriteFrame = (spriteFrame + 1) % 4;
        sprite = new Sprite(spriteFrame, 17, 3);
    }
}
