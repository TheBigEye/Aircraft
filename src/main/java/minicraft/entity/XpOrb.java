package minicraft.entity;

import minicraft.core.Game;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.TextParticle;
import minicraft.gfx.Color;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

public class XpOrb extends Entity {

	private static Sprite Sprites = new Sprite(5, 14, 2);

	private int animFrame = 0;

	private int xp;

	private int lifeTime; // how much time until the orb disappears
	private double xa, ya; // the x and y acceleration
	private double xx, yy; // the x and y positions
	private int time; // the amount of time that has passed

	public XpOrb(int xp, double xa, double ya) {
		super(0, 0);

		this.xp = xp;

		this.xa = xa;
		this.ya = ya;

		lifeTime = 12000 + random.nextInt(30);
	}

	private void goToOwner() {
		// Move the spark:

		x = (int) xx;
		y = (int) yy;

		xx += xa;
		yy += ya;

		Player player = getClosestPlayer();

		int xd = (int) (player.x - x + Math.toRadians(xp));
		int yd = (int) (player.y - y + Math.toRadians(xp));

		int sig0 = 1;
		if (xd < sig0)
			xa = -0.2;
		if (xd > sig0)
			xa = +0.2;
		if (yd < sig0)
			ya = -0.2;
		if (yd > sig0)
			ya = +0.2;

		if (random.nextInt(4) == 4) {
			xa += 0.1;
			ya += 0.1;
		}
		if (random.nextInt(4) == 2) {
			xa -= 0.1;
			ya -= 0.1;
		}
		if (random.nextInt(4) == 1) {
			xa -= 0.1;
			ya += 0.1;
		}
		if (random.nextInt(4) == 2) {
			xa += 0.1;
			ya -= 0.1;
		}
	}

	@Override
	public void tick() {
		time++;
		if (time >= lifeTime) {
			remove(); // Remove this from the world
			return;
		}
		if (level != null && level.getTile(x >> 4, y >> 4) == Tiles.get("lava")) {
			remove();
		}

		// Got to the owner
		goToOwner();

		// Animation
		animFrame++;

		if (animFrame >= 14) {
			animFrame = 0;
		}
	}

	public boolean isBlockable(Tile e) {
		return true;
	}

	public boolean canSwim() {
		return false;
	}

	protected void touchedBy(Entity player) {

		level.add(new TextParticle("" + xp, player.x, player.y, Color.GREEN));
		remove();
	}

	@Override
	public int getLightRadius() {
		return 1;
	}

	public void render(Screen screen) {
		int randmirror = 0;

		if (xp < 8) {

			if (animFrame == 0) {
				Sprites = new Sprite(5, 14, 2);
			}
			if (animFrame == 2) {
				Sprites = new Sprite(6, 14, 2);
			}
			if (animFrame == 4) {
				Sprites = new Sprite(7, 14, 2);
			}
			if (animFrame == 6) {
				Sprites = new Sprite(8, 14, 2);
			}
			if (animFrame == 8) {
				Sprites = new Sprite(7, 14, 2);
			}
			if (animFrame == 10) {
				Sprites = new Sprite(6, 14, 2);
			}
			if (animFrame == 12) {
				Sprites = new Sprite(5, 14, 2);
			}

		} else if (xp < 12) {

			if (animFrame == 0) {
				Sprites = new Sprite(5, 15, 2);
			}
			if (animFrame == 2) {
				Sprites = new Sprite(6, 15, 2);
			}
			if (animFrame == 4) {
				Sprites = new Sprite(7, 15, 2);
			}
			if (animFrame == 6) {
				Sprites = new Sprite(8, 15, 2);
			}
			if (animFrame == 8) {
				Sprites = new Sprite(7, 15, 2);
			}
			if (animFrame == 10) {
				Sprites = new Sprite(6, 15, 2);
			}
			if (animFrame == 12) {
				Sprites = new Sprite(5, 15, 2);
			}

		} else if (xp == 16) {

			if (animFrame == 0) {
				Sprites = new Sprite(5, 16, 2);
			}
			if (animFrame == 2) {
				Sprites = new Sprite(6, 16, 2);
			}
			if (animFrame == 4) {
				Sprites = new Sprite(7, 16, 2);
			}
			if (animFrame == 6) {
				Sprites = new Sprite(8, 16, 2);
			}
			if (animFrame == 8) {
				Sprites = new Sprite(7, 16, 2);
			}
			if (animFrame == 10) {
				Sprites = new Sprite(6, 16, 2);
			}
			if (animFrame == 12) {
				Sprites = new Sprite(5, 16, 2);
			}
		}

		// If we are in a menu, or we are on a server.
		if (Game.getDisplay() == null) {
			// The blinking effect.
			if (time >= lifeTime - 6 * 20) {
				if (time / 6 % 2 == 0)
					return; // If time is divisible by 12, then skip the rest of the code.
			}
			randmirror = random.nextInt(4);
		}
		Sprites.render(screen, x, y, randmirror, 2);
	}
}