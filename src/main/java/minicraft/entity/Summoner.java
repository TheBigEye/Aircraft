package minicraft.entity;

import minicraft.core.Game;
import minicraft.core.Updater;
import minicraft.core.Updater.Time;
import minicraft.entity.mob.*;
import minicraft.graphic.Rectangle;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.item.AmuletItem;
import org.tinylog.Logger;

import java.util.List;

public class Summoner extends Entity {
    // Indicates if the summoner should return to its owner.
    public boolean comeback = false;
    
    // Reference to the owner of the summoner.
    private Mob owner;
    
    // The amulet item associated with this summoner.
    private AmuletItem amuletItem;
    
    // Sprite for rendering the summoner.
    private Sprite sprite;
    
    // Timer to track how long the summoner has been active.
    private int time = 0;
    
    // Accelerations in x and y directions.
    private double xa, ya;
    
    // Exact positions in x and y (using double for smooth movement).
    private double xx, yy;

    // Flag to ensure that the summoner spawns its mob only once.
    private boolean spawned = false;
    
    // Fixed spawn time
    private int spawnTime;

    /**
     * Constructor for the Summoner class.
     *
     * @param owner the owner of the summoner
     * @param amulet the amulet object used for summoning
     * @param xa the initial acceleration in x direction
     * @param ya the initial acceleration in y direction
     */
    public Summoner(Mob owner, AmuletItem amulet, double xa, double ya) {
        super(0, 0);
        this.owner = owner;
        this.amuletItem = amulet;

        // Start the summoner at the owner's position.
        xx = owner.x;
        yy = owner.y;

        // Increase the initial acceleration by a factor of 1.3 for a noticeable effect.
        this.xa = xa * 1.3;
        this.ya = ya * 1.3;

        // Get the sprite from the amulet to represent this summoner.
        this.sprite = amulet.getSprite();

        // Calculate the spawn time once. This time value determines when the mob will be spawned.
        this.spawnTime = 125 - random.nextInt(5);
    }

    /**
     * Updates the state of the summoner every tick.
     * Handles movement, comeback behavior, and mob spawning.
     */
    public void tick() {
        time++;

        // Update positions using a time-based division for deceleration effect.
        xx += (xa / time) * 16;
        x = (int) xx;

        yy += (ya / time) * 16;
        y = (int) yy;

        // If the owner no longer exists, remove the summoner.
        if (owner == null) {
            this.remove();
            return;
        }

        // If comeback is active, adjust acceleration to return toward the owner.
        if (comeback) {
            Player player = getClosestPlayer(); // Get the nearest player (if needed for additional effects)
            if (player != null) {
                int xd = owner.x - x;
                int yd = owner.y - y;

                // Adjust acceleration in x direction based on owner's relative position.
                if (xd < 1) {
                    xa -= 0.4;
                } else if (xd > 1) {
                    xa += 0.4;
                }

                // Adjust acceleration in y direction based on owner's relative position.
                if (yd < 1) {
                    ya -= 0.4;
                } else if (yd > 1) {
                    ya += 0.4;
                }

                // If the summoner is within range of its owner, return the amulet to the owner.
                if (owner.isWithin(0, this)) {
                    // For players in non-Creative mode, set the active item to the amulet.
                    if (owner instanceof Player && !Game.isMode("Creative")) {
                        ((Player) owner).activeItem = amuletItem;
                    }
                    // End the summoner's existence after returning the amulet.
                    super.die();
                }
            }
        }

        // If comeback is not active yet, start it after a random delay.
        if (!comeback) {
            if (time >= (120 + random.nextInt(16))) {
                comeback = true;
            }
        }

        // Only allow mob spawning at night (or when it's not day/morning).
        if (Updater.getTime() != Time.Day && Updater.getTime() != Time.Morning) {

            // Ensure the summoner is not on a specific tile type (tile id 6).
            if (!(level.getTile(x >> 4, y >> 4).id == 6)) {

                // Use the pre-calculated spawnTime rather than checking every tick.
                if (!spawned && time > spawnTime) {
                    // Get all entities in a large area around the summoner.
                    List<Entity> entitiesInRange = Game.levels[Game.currentLevel].getEntitiesInRect(new Rectangle(x, y, 360 << 1, 360 << 1, Rectangle.CENTER_DIMS));

                    // Do not spawn if certain boss entities are already present.
                    for (Entity entity : entitiesInRange) {
                        if (entity instanceof EyeQueen || entity instanceof Keeper) {
                            return;
                        }
                    }

                    try {
                        // Create a new mob instance to be summoned.
                        MobAi newmob = amuletItem.getSummonMob().getClass().getConstructor(int.class).newInstance(1);
                        level.add(newmob, x, y);
                    } catch (Exception exception) {
                        Logger.error("Could not spawn mob, error initializing mob instance:");
                        exception.printStackTrace();
                        return;
                    }

                    // Mark that the mob has been spawned to avoid duplicate spawns.
                    spawned = true;
                    // End the summoner entity after successful spawn.
                    super.die();
                }
            }
        } else {
            // Notify players if an attempt to spawn is made during the day.
            if (time % 120 == 0 && !comeback) {
                Updater.notifyAll("This doesn't work during the day", 200);
            }
        }
    }

    /**
     * Determines whether the summoner can be blocked by a mob.
     *
     * @param mob the mob attempting to block the summoner
     * @return false, meaning the summoner cannot be blocked.
     */
    public boolean isBlockableBy(Mob mob) {
        return false;
    }

    /**
     * Indicates whether the summoner can swim.
     *
     * @return true, allowing the summoner to move through water.
     */
    @Override
    public boolean canSwim() {
        return true;
    }

    /**
     * Renders the summoner's sprite on the screen.
     *
     * @param screen the screen where the summoner is drawn.
     */
    public void render(Screen screen) {
        // Render the sprite slightly offset to adjust its position on screen.
        this.sprite.render(screen, x - 5, y - 7, 0);
    }

    /**
     * Returns the light radius of the summoner.
     * This defines how far the summoner's light reaches in the game.
     *
     * @return the light radius as an integer.
     */
    @Override
    public int getLightRadius() {
        return 3;
    }
}
