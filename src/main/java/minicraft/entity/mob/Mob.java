package minicraft.entity.mob;

import minicraft.core.Game;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.furniture.Tnt;
import minicraft.entity.particle.TextParticle;
import minicraft.graphic.Color;
import minicraft.graphic.MobSprite;
import minicraft.item.PotionType;
import minicraft.level.tile.*;

import java.util.Random;

public abstract class Mob extends Entity {

	/* This contains all the mob's sprites, sorted first by direction (index
    corresponding to the dir variable), and then by walk animation state. */
    protected MobSprite[][] sprites;

    /* How far we've walked currently, incremented after each movement. This is used
	to change the sprite; "(walkDist >> 3) & 1" switches between a value of 0 and
    1 every 8 increments of walkDist. */
    public int walkDist = 0;

    /* The direction the mob is facing, used in attacking and rendering. 0 is
    down, 1 is up, 2 is left, 3 is right */
    public Direction dir = Direction.DOWN;

    /* A delay after being hurt, that temporarily prevents further damage for a
    short time */
    public int hurtTime = 0;

    /* The amount of vertical/horizontal knockback that needs to be inflicted, if
    it's not 0, it will be moved one pixel at a time. */
    private int xKnockback, yKnockback;

    public int health; // Mob health
    public final int maxHealth; // The amount of health we currently have, and the maximum.
    protected int walkTime;
    public int speed; // Mob walk speed
    public int tickTime = 0; // Incremented whenever tick() is called, is effectively the age in ticks

    /** Random value for all the mob instances **/
    protected static final Random random = new Random();

    /**
     * Default constructor for a mob. Default x radius is 4, and y radius is 3.
     *
     * @param sprites All of this mob's sprites.
     * @param health  The mob's max health.
     */
    public Mob(MobSprite[][] sprites, int health) {
        super(4, 3);
        this.sprites = sprites;
        this.health = this.maxHealth = health;
        walkTime = 1;
        speed = 1;
    }

    /**
     * Updates the mob.
     */
    @Override
    public void tick() {
        tickTime++; // Increment our tick counter

        if (isRemoved()) {
            return;
        }

        if (level != null && level.getTile(x >> 4, y >> 4) instanceof LavaTile) { // If we are trying to swim in lava
            hurt(Tiles.get("Lava"), x, y, 4); // Inflict 4 damage to ourselves, sourced from the lava Tile, with the direction as the opposite of ours.
        }

        if (health <= 0) {
            die(); // die if no health
        }

        if (hurtTime > 0) {
            hurtTime--; // If a timer preventing damage temporarily is set, decrement it's value
        }

        // The code below checks the direction of the knockback, moves the Mob accordingly, and brings the knockback closer to 0.
        int xd = 0, yd = 0;
        if (xKnockback != 0) {
            xd = (int) Math.ceil(xKnockback / 2);
            xKnockback -= xKnockback / Math.abs(xKnockback);
        }
        if (yKnockback != 0) {
            yd = (int) Math.ceil(yKnockback / 2);
            yKnockback -= yKnockback / Math.abs(yKnockback);
        }

        move(xd, yd, false);

    }

    @Override
    public boolean move(int xa, int ya) { // Move the mob, overrides from Entity
        return move(xa, ya, true);
    }

    private boolean move(int xa, int ya, boolean changeDir) { // knockback shouldn't change mob direction
        if (level == null) {
            return false; // stopped because there's no level to move in!
        }

		// These should return true because the mob is still technically moving; these are just to make it move *slower*.
		if (tickTime % 2 == 0 && (isSwimming() || (!(this instanceof Player) && isWooling()))) {
			return true;
		}
		if (tickTime % walkTime == 0 && walkTime > 1) {
			return true;
		}

        boolean moved = true;

        // If a mobAi has been hurt recently and hasn't yet cooled down, it won't perform the movement (by not calling super)
        if (hurtTime == 0 || this instanceof Player) {
            if (xa != 0 || ya != 0) {
                if (changeDir) {
                	// set the mob's direction; NEVER set it to NONE
                    dir = Direction.getDirection(xa, ya);
                }
                walkDist++;
            }

            // this part makes it so you can't move in a direction that you are currently being knocked back from.
            if (xKnockback != 0) {
                xa = Math.copySign(xa, xKnockback) * -1 != xa ? xa : 0; // if xKnockback and xa have different signs, do nothing, otherwise, set xa to 0.
            }
            if (yKnockback != 0) {
                ya = Math.copySign(ya, yKnockback) * -1 != ya ? ya : 0; // same as above.
            }

            moved = super.move(xa, ya); // Call the move method from Entity
        }
        return moved;
    }

    private boolean isWooling() { // supposed to walk at half speed on wool
        if (level == null)  {
        	return false;
        }
        return (level.getTile(x >> 4, y >> 4) instanceof WoolTile);
    }

    /**
     * Checks if this Mob is currently on a light tile; if so, the mob sprite is
     * brightened.
     *
     * @return true if the mob is on a light tile, false if not.
     */
    public boolean isLight() {
        if (level == null) {
        	return false;
        }
        return level.isLight(x >> 4, y >> 4);
    }

    /**
     * Checks if the mob is swimming (standing on a liquid tile).
     *
     * @return true if the mob is swimming, false if not.
     */
    public boolean isSwimming() {
        if (level == null) return false;
        Tile tile = level.getTile(x >> 4, y >> 4); // Get the tile the mob is standing on (at x/16, y/16)
        return (tile instanceof WaterTile || tile instanceof LavaTile); // Check if the tile is liquid, and return true if so
    }


    public void hurt(Tile tile, int x, int y, int damage) { // Hurt the mob, when the source of damage is a tile
    	// Set attackDir to our own direction, inverted. XORing it with 1 flips the rightmost bit in the variable, this effectively adds one when even, and subtracts one when odd.
        Direction attackDir = Direction.getDirection(dir.getDir() ^ 1);
        if (tile != Tiles.get("Lava")) {
        	if (!(this instanceof Player && (((Player) this).potionEffects.containsKey(PotionType.Lava) || ((Player) this).potionEffects.containsKey(PotionType.xLava)))) {
	        	// Call the method that actually performs damage, and set it to no particular direction
	            doHurt(damage, tile.mayPass(level, x, y, this) ? Direction.NONE : attackDir);
        	}
        }
    }

    public void hurt(Mob mob, int damage) {
        hurt(mob, damage, getAttackDir(mob, this));
    }

    // Hurt the mob, when the source is another mob
    public void hurt(Mob mob, int damage, Direction attackDir) {
        if (mob instanceof Player && Game.isMode("Creative") && mob != this) {
            doHurt(health, attackDir); // kill the mob instantly
        } else {
            doHurt(damage, attackDir); // Call the method that actually performs damage, and use our provided attackDir
        }
    }

	/**
	 * Executed when a TNT bomb explodes near this mob.
	 * @param tnt The TNT exploding.
	 * @param damage The amount of damage the explosion does.
	 */
	public void onExploded(Tnt tnt, int damage) {
		doHurt(damage, getAttackDir(tnt, this));
	}

	// Actually hurt the mob, based on only damage and a direction
    protected void doHurt(int damage, Direction attackDir) {
        // this is overridden in Player.java
        if (isRemoved() || hurtTime > 0) {
            return; // If the mob has been hurt recently and hasn't cooled down, don't continue
        }

        health -= damage; // Actually change the health

        // add the knockback in the correct direction
        xKnockback = attackDir.getX() * 6;
        yKnockback = attackDir.getY() * 6;
        hurtTime = 10; // Set a delay before we can be hurt again
    }

    /**
     * Restores health to this mob.
     *
     * @param heal How much health is restored.
     */
    public void heal(int heal) { // Restore health on the mob
        if (hurtTime > 0) {
            return; // If the mob has been hurt recently and hasn't cooled down, don't continue
        }

        // Add a text particle in our level at our position, that is green and displays the amount healed
        level.add(new TextParticle("" + heal, x, y, Color.GREEN));

        health += heal; // Actually add the amount to heal to our current health
        if (health > maxHealth) {
            health = maxHealth; // If our health has exceeded our maximum, lower it back down to said maximum
        }
    }

    protected static Direction getAttackDir(Entity attacker, Entity hurt) {
        return Direction.getDirection(hurt.x - attacker.x, hurt.y - attacker.y);
    }
}
