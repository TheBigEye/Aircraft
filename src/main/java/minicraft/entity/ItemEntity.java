package minicraft.entity;

import java.util.List;

import minicraft.core.io.Settings;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.FireParticle;
import minicraft.graphic.Color;
import minicraft.graphic.Screen;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.level.tile.Tiles;

public class ItemEntity extends Entity implements ClientTickable {
	private int lifeTime; // the life time of this entity in the level
	private double xa, ya, za; // the x, y, and z accelerations.
	private double xx, yy, zz; // the x, y, and z coordinates; in double precision.
	public final Item item; // the item that this entity is based off of.
	private int time = 0; // time it has lasted in the level

	// solely for multiplayer use.
	private boolean pickedUp = false;

	@SuppressWarnings("unused")
	private long pickupTimestamp;

	/**
	 * Creates an item entity of the item item at position (x,y) with size 2*2.
	 * 
	 * @param item Item to add as item entity
	 * @param x    position on map
	 * @param y    position on map
	 */
	public ItemEntity(Item item, int x, int y) {
		super(2, 2);

		this.item = item;
		this.x = x;
		this.y = y;
		xx = x;
		yy = y;

		zz = 2;
		// random direction for each acceleration
		xa = random.nextGaussian() * 0.3;
		ya = random.nextGaussian() * 0.2;
		za = random.nextFloat() * 0.7 + 1;

		// the idea was to have it last 10-11 seconds, I think.
		lifeTime = 70 * 10 + random.nextInt(70); // sets the lifetime of the item. min = 600 ticks, max = 669 ticks.
		
	}

	/**
	 * Creates an item entity of the item item at position (x,y) with size 2*2.
	 * 
	 * @param item     Item to add as item entity.
	 * @param x        position on map
	 * @param y        position on map
	 * @param zz       z position?
	 * @param lifetime lifetime (in ticks) of the entity.
	 * @param time     starting time (in ticks) of the entity.
	 * @param xa       x velocity
	 * @param ya       y velocity
	 * @param za       z velocity?
	 */
	public ItemEntity(Item item, int x, int y, double zz, int lifetime, int time, double xa, double ya, double za) {
		this(item, x, y);
		this.lifeTime = lifetime;
		this.time = time;
		this.zz = zz;
		this.xa = xa;
		this.ya = ya;
		this.za = za;
	}

	/**
	 * Returns a string representation of the itementity
	 * 
	 * @return string representation of this entity
	 */
	public String getData() {
		return String.join(":", (new String[] { item.getData(), zz + "", lifeTime + "", time + "", xa + "", ya + "", za + "" }));
	}

	@Override
	public void tick() {
		time++;
		if (time >= lifeTime) { // if the time is larger or equal to lifeTime then...
			remove(); // remove from the world
			return; // skip the rest of the code
		}
		// moves each coordinate by the its acceleration
		xx += xa;
		yy += ya;
		zz += za;
		if (zz < 0) { // if z pos is smaller than 0 (which probably marks hitting the ground)
			zz = 0; // set it to zero

			// multiply the accelerations by an amount:
			za *= -0.5;
			xa *= 0.6;
			ya *= 0.6;
		}

		za -= 0.15; // decrease z acceleration by 0.15

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

		// If some item touch lava, is burned
		if (level.getTile(x >> 4,y >> 4) == Tiles.get("Lava")) {
			for (int i = 0; i < 1 + random.nextInt(2); i++) {
				int randX = random.nextInt(16);
				int randY = random.nextInt(12);
				level.add(new FireParticle(x - 8 + randX, y - 6 + randY));
			}

			// Burn the items
			if (time > 60) {
				remove();
			}
		}
	}

	public boolean isSolid() {
		return false; // mobs cannot block this
	}

	@Override
	public void render(Screen screen) {
		/* This first part is for the blinking effect */
		if (time >= lifeTime - 6 * 20) {
			if (time / 6 % 2 == 0) return;
		}

		if (Settings.getBoolean("shadows")) {
			item.sprite.render(screen, x - 5, y - 8, 4, -1, Color.BLACK); // item shadow uses black color
		}
		item.sprite.render(screen, x - 5, y - 8 - (int)(zz));
	}

	@Override
	protected void touchedBy(Entity entity) {
		if (!(entity instanceof Player)) {
			return; // for the time being, we only care when a player touches an item.
		}

		if (time > 30) { // conditional prevents this from being collected immediately.
			if (!pickedUp) {// don't register if we are online and a player touches it; the client will register that.
				pickedUp = true;
				((Player) entity).pickupItem(this);
				pickedUp = isRemoved();
			}
		}
	}

	@Override
	protected List<String> getDataPrints() {
		List<String> prints = super.getDataPrints();
		prints.add(0, item.toString());
		return prints;
	}
	
	@Override
	public int getLightRadius() {
	    return (
	    	this.item.equals(Items.get("Torch")) ||
	    	this.item.equals(Items.get("Lava Bucket")) ||
	    	this.item.equals(Items.get("Lantern")) ||
	    	this.item.equals(Items.get("Iron Lantern")) ||
	    	this.item.equals(Items.get("Gold Lantern")) ||
	    	this.item.equals(Items.get("Summon Altar"))
	    ) ? 1 : 0;
	}

}
