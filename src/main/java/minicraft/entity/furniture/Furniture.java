package minicraft.entity.furniture;

import minicraft.core.Game;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.FireParticle;
import minicraft.graphic.Color;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.item.FurnitureItem;
import minicraft.item.Item;
import minicraft.item.PowerGloveItem;
import minicraft.level.tile.LavaTile;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * Many furniture classes are very similar; they might not even need to be there at all...
 */

public class Furniture extends Entity {

	/** Random value for all the furniture instances **/
	protected static final Random random = new Random();

    // time for each push; multi is for multiplayer, to make it so not so many updates are sent.
    protected int pushTime = 0;

    private Direction pushDir = Direction.NONE; // the direction to push the furniture
    public Sprite sprite;
    public String name;

    /**
     * Constructor for the furniture entity. Size will be set to 3.
     *
     * @param name   Name of the furniture.
     * @param sprite Furniture sprite.
     */
    public Furniture(String name, Sprite sprite) {
        this(name, sprite, 3, 3);
    }

    /**
     * Constructor for the furniture entity. Radius is only used for collision
     * detection.
     *
     * @param name   Name of the furniture.
     * @param sprite Furniture sprite.
     * @param xr     Horizontal radius.
     * @param yr     Vertical radius.
     */
    public Furniture(String name, Sprite sprite, int xr, int yr) {
        // all of these are 2x2 on the spritesheet; radius is for collisions only.
        super(xr, yr);
        this.name = name;
        this.sprite = sprite;
        color = sprite.color;
    }

    @Override
    public Furniture clone() {
        try {
        	return getClass().getDeclaredConstructor().newInstance();
        } catch (Exception exception) {
        	exception.printStackTrace();
        }
        return new Furniture(name, sprite);
    }

    @Override
    public void tick() {
        // moves the furniture in the correct direction.
        move(pushDir.getX(), pushDir.getY());
        pushDir = Direction.NONE;

        if (pushTime > 0) {
            pushTime--; // update pushTime by subtracting 1.
        }

        if (level.getTile(x >> 4, y >> 4) instanceof LavaTile && !(this instanceof Spawner) && !(this instanceof DeathChest) && !(this instanceof Tnt)) {
			for (int i = 0; i < 1 + random.nextInt(3); i++) {
				level.add(new FireParticle(x - 8 + random.nextInt(16), y - 6 + random.nextInt(12)));
			}
			this.die();
		}
    }

    /** Draws the furniture on the screen. */
    public void render(Screen screen) {
        sprite.render(screen, x - 8, y - 8);
    }

    /** Draws the furniture on the screen. */
    public void renderFullbright(Screen screen) {
        sprite.render(screen, x - 8, y - 8, 0, -1, Color.WHITE);
    }

    /** Called when the player presses the MENU key in front of this. */
    public boolean use(Player player) {
        return false;
    }

    @Override
    public boolean blocks(Entity entity) {
        return true; // furniture blocks all entities, even non-solid ones like arrows.
    }

    @Override
    protected void touchedBy(Entity entity) {
        if (entity instanceof Player) {
            tryPush((Player) entity);
        }
    }

    /**
     * Used in PowerGloveItem.java to let the user pick up furniture.
     *
     * @param player The player picking up the furniture.
     */
    @Override
    public boolean interact(Player player, @Nullable Item item, Direction attackDir) {
        if (item instanceof PowerGloveItem) {
            Sound.playAt("genericHurt", x, y);
            remove();

            if (!Game.isMode("Creative") && player.activeItem != null && !(player.activeItem instanceof PowerGloveItem)) {
            	// put whatever item the player is holding into their inventory
            	player.getInventory().add(0, player.activeItem);
            }

            // make this the player's current item.
            player.activeItem = new FurnitureItem(this);
            return true;
        }
        return false;
    }

	/**
	 * Tries to let the player push this furniture.
	 * @param player The player doing the pushing.
	 */
	public void tryPush(Player player) {
		if (pushTime == 0) {
			pushDir = player.dir; // Set pushDir to the player's dir.
			pushTime = 10; // Set pushTime to 10.
		}
	}

    @Override
    public boolean canWool() {
        return true;
    }
}
