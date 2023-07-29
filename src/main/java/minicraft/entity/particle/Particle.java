package minicraft.entity.particle;

import java.util.Random;

import minicraft.entity.ClientTickable;
import minicraft.entity.Entity;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;

public class Particle extends Entity implements ClientTickable {
	private int time; // lifetime elapsed.
	private int lifetime;

	protected Sprite sprite;
	
	/** Random values used for particles instances **/
	protected static final Random random = new Random();

	/**
	 * Creates an particle entity at the given position. The particle has a x and y
	 * radius = 1.
	 * 
	 * @param x        X map coordinate
	 * @param y        Y map coordinate
	 * @param xr       x radius of the particle
	 * @param lifetime How many game ticks the particle lives before its removed
	 * @param sprite   The particle's sprite
	 */
	public Particle(int x, int y, int xr, int lifetime, Sprite sprite) {
		// make a particle at the given coordinates
		super(xr, 1);
		this.x = x;
		this.y = y;
		this.lifetime = lifetime;
		this.sprite = sprite;
		time = 0;
	}

	public Particle(int x, int y, int lifetime, Sprite sprite) {
		this(x, y, 1, lifetime, sprite);
	}

	@Override
	public void tick() {
		time++;
		if (time > lifetime) {
			remove();
		}
	}

	@Override
	public void render(Screen screen) {
		sprite.render(screen, x, y);
	}

	@Override
	public boolean isSolid() {
		return false;
	}
}
