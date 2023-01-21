package minicraft.entity.particle;

import minicraft.entity.ClientTickable;
import minicraft.gfx.Sprite;

public class SplashParticle extends Particle implements ClientTickable {
	/// This is used for boats and when the player swim in the water
	private static Sprite sprite = new Sprite(0, 15, 3);
	private int spriteFrame = 0;
	
    public double xa, ya, za;
    public double xx, yy, zz;
    
	/**
	 * Creates a new particle at the given position. It has a lifetime of 40 ticks
	 * and a splash looking sprite.
	 * 
	 * @param x X map position
	 * @param y Y map position
	 */
	public SplashParticle(int x, int y) {
		super(x, y, 32, sprite);
		
		xx = x;
		yy = y;
		zz = 2;
		
		// random direction for each acceleration
		xa = random.nextGaussian() * 0.2;
		ya = random.nextGaussian() * 0.1;
		za = random.nextFloat() * 0.6 + 1;
	}

	// Animation
    @Override
	public void tick() {
		super.tick();
            
        spriteFrame = (spriteFrame + 1) % 4;
        sprite = new Sprite(spriteFrame, 15, 3);
        
		// moves each coordinate by the its acceleration
		xx += xa;
		yy += ya;
		zz += za;
		if (zz < 0) { // if z pos is smaller than 0 (which probably marks hitting the ground)
			zz = 0; // set it to zero

			// multiply the accelerations by an amount:
			za *= -0.4;
			xa *= 0.3;
			ya *= 0.3;
		}

		za -= 0.12; // decrease z acceleration by 0.15

		// storage of x and y positions before move
		int ox = x;
		int oy = y;

		// integer conversion of the double x and y postions (which have already been updated):
		int nx = (int) xx;
		int ny = (int) yy;

		// the difference between the double->int new positions, and the inherited x and y positions:
		int expectedx = nx - x; // expected movement distance
		int expectedy = ny - y;

		/// THIS is where x and y are changed.
		move(expectedx, expectedy); // move the ItemEntity.

		// finds the difference between the inherited before and after positions
		int gotx = x - ox;
		int goty = y - oy;

		// Basically, this accounts for any error in the whole double-to-int position conversion thing:
		xx += gotx - expectedx;
		yy += goty - expectedy;
	}
}
