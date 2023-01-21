package minicraft.entity;

import java.util.List;

import org.tinylog.Logger;

import minicraft.core.Game;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.gfx.Rectangle;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.item.AmuletItem;

public class Summoner extends Entity {
	
    public boolean comeback = false;
    private static final int comebackSpeed = 1;
    
    private Mob owner;
    private AmuletItem amuletItem;
    
    private int time = 0;
	private double xa, ya; // the x and y acceleration
	private double xx, yy; // the x and y positions

    public Summoner(Mob owner, AmuletItem amulet, double xa, double ya) {
    	super(0, 0);
    	
		this.owner = owner;
		this.amuletItem = amulet;
		xx = owner.x;
		yy = owner.y;
		this.xa = xa * 1.8;
		this.ya = ya * 1.8;

    }

    public void tick() {
    	time++;

    	xx += xa; x = (int) xx;
    	yy += ya; y = (int) yy;

    	if (owner == null) {
    		remove();
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
    			

    			if (owner.isWithin(1, this)) {
    				if (owner instanceof Player && !Game.isMode("Creative")) {
    					((Player) owner).activeItem = amuletItem;
    				}
    				super.die();
    			}

    		}
    	}

    	if (!comeback) {
    		if (time >= 120) {
    			comeback = true;
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

    		if (!level.getTile(x >> 4, y >> 4).mayPass(level, x >> 4, y >> 4, this)) {
    			comeback = true;
    		}
    	}
    	

		if (random.nextInt(20) == 4 && comeback && time / 2 % 3 == 0) {
			Logger.debug("Summoning {} with {}, at x:{} y:{} ...", amuletItem.getSummonMob(), amuletItem.getName(), x, y);
			level.add(amuletItem.getSummonMob(), x, y);
			super.die();
		}
		
    }

    public boolean isBlockableBy(Mob mob) {
        return false;
    }
    
	@Override
	public boolean canSwim() {
		return true;
	}

    public void render(Screen screen) {	
    	Sprite sprite = amuletItem.getSprite();
		sprite.render(screen, x - 4, y - 4, random.nextInt(3));
    }
    
	@Override
	public int getLightRadius() {
		return 1;
	}
}
