package minicraft.entity.particle;

import minicraft.gfx.Sprite;

public class BrightParticle extends Particle {
	/// This is used for the Lanterns.
	private static Sprite Sprites = new Sprite(0, 17, 3);

	/**
	 * Creates a new particle at the given position. It has a lifetime of 16 ticks
	 * and a bright looking sprite.
	 * 
	 * @param x X map positiona
	 * @param y Y map position
	 */
	public BrightParticle(int x, int y) {
		super(x, y, 16, Sprites);
	}


    @Override
	public void tick() {
		super.tick();
		
		int frame = 0;
        
        for (int i = 0; i < 8; i++){
            frame += 1;
            if (frame > 3) frame = 0;
            Sprites = new Sprite(frame, 17, 3);
        }
	}
}
