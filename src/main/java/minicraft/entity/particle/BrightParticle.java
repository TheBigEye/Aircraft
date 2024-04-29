package minicraft.entity.particle;

import minicraft.graphic.Sprite;

public class BrightParticle extends Particle {
    /// This is used for the Lanterns.
    private static Sprite sprite = new Sprite(0, 17, 3);
    private static int spriteFrame = 0;

    /**
     * Creates a new particle at the specified position. The particle has a custom lifetime in ticks
     * and a bright-looking sprite.
     * 
     * @param x X-coordinate of the map position
     * @param y Y-coordinate of the map position
     * @param time Lifetime of the particle in ticks
     */
    public BrightParticle(int x, int y, int time) {
        super(x, y, time, sprite);
    }
    
    /**
     * Creates a new particle at the specified position. The particle has a lifetime of 16 ticks
     * and a bright-looking sprite.
     * 
     * @param x X-coordinate of the map position
     * @param y Y-coordinate of the map position
     */
    public BrightParticle(int x, int y) {
        this(x, y, 16);
    }


    @Override
    public void tick() {
        super.tick();
        
        spriteFrame = (spriteFrame + 1) % 4;
        sprite = new Sprite(spriteFrame, 16, 3);
    }
}
