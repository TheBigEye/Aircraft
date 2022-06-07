package minicraft.entity.particle;

import minicraft.gfx.Sprite;

public class SplashParticle extends Particle {
	/// This is used for boats and when the player swim in the water
	private static Sprite Sprites = new Sprite(0, 15, 3);
	private int frame = 0;

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

	// Animation
    @Override
	public void tick() {
		super.tick();
        
        for (int i = 0; i < 8; i += 1){
            frame += 1;
            if (frame > 3) frame = 0;
            Sprites = new Sprite(frame, 15, 3);
        }
	}
}
