package minicraft.entity;

import java.util.List;

import org.tinylog.Logger;

import minicraft.core.Game;
import minicraft.core.Updater;
import minicraft.core.Updater.Time;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.MobAi;
import minicraft.entity.mob.Player;
import minicraft.graphic.Rectangle;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.item.AmuletItem;

public class Summoner extends Entity {
    public boolean comeback = false;
    private boolean summonable;
    private static final int comebackSpeed = 1;

    private Mob owner;
    private AmuletItem amuletItem;
    
    private Sprite sprite;

    private int time = 0;
    private double xa, ya; // the x and y acceleration
    private double xx, yy; // the x and y positions

    /**
     * Constructor for Summoner class.
     * 
     * @param owner the owner of the summoner
     * @param amulet the amulet item
     * @param xa the x acceleration of the summoner
     * @param ya the y acceleration of the summoner
     */
    public Summoner(Mob owner, AmuletItem amulet, double xa, double ya) {
        super(0, 0);
        
        this.sprite = amulet.getSprite();
        this.owner = owner;
        this.amuletItem = amulet;
        
        xx = owner.x;
        yy = owner.y;
        this.xa = xa * 1.3;
        this.ya = ya * 1.3;
        
        this.summonable = true;
    }

    /**
     * Updates the state of the summoner.
     */
    public void tick() {
        time++;

        xx += (xa / time) * 16; x = (int) xx;
        yy += (ya / time) * 16; y = (int) yy;
        

        if (owner == null) {
            this.remove();
            return;
        }

        if (comeback) {
            Player player = getClosestPlayer();

            if (player != null) {
                int xd = owner.x - x;
                int yd = owner.y - y;

                if (xd < comebackSpeed) {
                    xa--;
                } else if (xd > comebackSpeed) {
                    xa++;
                }

                if (yd < comebackSpeed) {
                    ya--;
                } else if (yd > comebackSpeed) {
                    ya++;
                }

                if (owner.isWithin(0, this)) {
                    if (owner instanceof Player && !Game.isMode("Creative")) {
                        ((Player) owner).activeItem = amuletItem;
                    }
                    super.die();
                }
            }
        }

        if (!comeback) {
            if (time >= (120 + random.nextInt(16))) {
                comeback = true;
                this.summonable = false;
                time = 0;
            }

            List<Entity> entities = level.getEntitiesInRect(new Rectangle(x, y, 0, 0, Rectangle.CENTER_DIMS));
            if (entities.size() > 1) {
                int entityIndex = 0;
                while (entityIndex < entities.size()) {
                    if (!(entities.get(entityIndex) instanceof Mob) || (entities.get(entityIndex) instanceof Player)) {
                        entityIndex++;
                    } else {
                        if (xa < 0) {
                            xa--;
                        } else if (xa > 0) {
                            xa++;
                        }

                        if (ya < 0) {
                            ya--;
                        } else if (ya > 0) {
                            ya++;
                        }
                        return;
                    }
                }
            }

        }
        

        if (Updater.getTime() != Time.Day && Updater.getTime() != Time.Morning) {
    	    if (!(level.getTile(x >> 4, y >> 4).id == 6)) {
    	        if (this.summonable && time > (125 - random.nextInt(5))) {
    	        	time = 0;
    	        	this.summonable = false;
    	        	
    	        	MobAi newmob;
    	        	
    	        	try {
    	    	         newmob = amuletItem.getSummonMob().getClass().getConstructor(int.class).newInstance(1);
    	    	    } catch (Exception exception) {
    	    	        Logger.error("Could not spawn mob, error initializing mob instance:");
    	    	        exception.printStackTrace();
    	    	        return;
    	    	    }

    	    	    level.add(newmob, x, y);
    	            super.die();
    	        }
    	    }
        } else {
        	if (time % 120 == 0) Updater.notifyAll("This doesn't work during the day", 200);
        }

    }

    /**
     * Returns whether or not the summoner is blockable by a mob.
     */
    public boolean isBlockableBy(Mob mob) {
        return false;
    }

    /**
     * Returns whether or not the summoner can swim.
     */
    @Override
    public boolean canSwim() {
        return true;
    }

    /**
     * Renders the summoner on screen.
     */
    public void render(Screen screen) {
        sprite.render(screen, x - 5, y - 7, 0);
    }

    /**
     * Returns the light radius of the summoner.
     */
    @Override
    public int getLightRadius() {
        return 3;
    }
}

