package minicraft.entity.furniture;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Timer;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.FireParticle;
import minicraft.graphic.Rectangle;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.item.Item;
import minicraft.item.PowerGloveItem;
import minicraft.level.Level;
import minicraft.level.tile.LavaTile;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;
import minicraft.screen.AchievementsDisplay;

public class Tnt extends Furniture implements ActionListener {
	private static final int FUSE_TIME = 90;
	private static final int BLAST_RADIUS = 32;
	private static final int BLAST_DAMAGE = 75;

	private int damage = 0;
	private int light = 0;
	private int fuseTick = 0;
	private boolean fuseLit = false;
	private Timer explodeTimer;
	private Level levelSave;

	private static final String[] explosionBlacklist = new String[] {
		"hard rock", "obsidian wall", "raw obsidian", "stairs up", "stairs down", "infinite fall", "summon altar"
	};

	/**
	 * Creates a new tnt furniture.
	 */
	public Tnt() {
		super("Tnt", new Sprite(26, 30, 2, 2, 2), 3, 2);
		fuseLit = false;
		fuseTick = 0;

		explodeTimer = new Timer(300, this);
	}

	@Override
	public void tick() {
	    super.tick();

	    if (!fuseLit) {
	        if (level.getTile(x >> 4, y >> 4) instanceof LavaTile) {
	            fuseLit = true;
	            Sound.playAt("genericFuse", this.x, this.y);
	            for (int i = 0; i < (1 + random.nextInt(3)); i++) {
	                level.add(new FireParticle(x - 8 + random.nextInt(16), y - 6 + random.nextInt(12)));
	            }
	        }
	    } else {
	        fuseTick++;
	        light = 2;
	        
	        if (fuseTick >= FUSE_TIME) {
	            int xt = x >> 4;
	            int yt = (y - 2) >> 4;
	            Rectangle explosionArea = new Rectangle(x, y, BLAST_RADIUS << 1, BLAST_RADIUS << 1, Rectangle.CENTER_DIMS);
	            List<Entity> entitiesInRange = level.getEntitiesInRect(explosionArea);

	            for (Entity entity : entitiesInRange) {
	                float dist = (float) Math.hypot(entity.x - x, entity.y - y);
	                damage = (int) (BLAST_DAMAGE * (1 - (dist / BLAST_RADIUS))) + 1;

	                if (entity instanceof Mob && damage > 0) {
	                    ((Mob) entity).onExploded(this, damage);
	                }

	                if (entity instanceof Tnt) {
	                    Tnt tnt = (Tnt) entity;
	                    if (!tnt.fuseLit) {
	                        tnt.fuseLit = true;
	                        Sound.playAt("genericFuse", x, y);
	                        tnt.fuseTick = FUSE_TIME * 2 / 3;
	                    }
	                }
	            }

	            Tile[] affectedTiles = level.getAreaTiles(xt, yt, 1);

	            for (int i = 0; i < affectedTiles.length; i++) {
	                affectedTiles[i].hurt(level, xt, yt, damage);
	                affectedTiles[i].onExplode(level, xt + i % 3 - 1, yt + i / 3 - 1);
	            }

	            Sound.playAt("genericExplode", this.x, this.y);
	            level.setAreaTiles(xt, yt, 1, Tiles.get("Explode"), 0, explosionBlacklist);

	            levelSave = level;
	            explodeTimer.start();
	            super.remove();

	            AchievementsDisplay.setAchievement("minicraft.achievement.demolition", true);
	        }
	    }
	}


	@Override
	public void render(Screen screen) {
		/*if (fuseLit) {
			int colorFactor = 100 * ((fuseTick % 15) / 5) + 200;
			color = Color.get(-1, colorFactor, colorFactor + 100, 555);
		}*/
		super.render(screen);
	}

	/**
	 * Does the explosion.
	 */
	public void actionPerformed(ActionEvent e) {
		explodeTimer.stop();
		int xt = x >> 4;
		int yt = (y - 2) >> 4;

		if (levelSave.depth != 1 && levelSave.depth != 2) {
			levelSave.setAreaTiles(xt, yt, 1, Tiles.get("Hole"), 0, explosionBlacklist);
		} else {
			levelSave.setAreaTiles(xt, yt, 1, Tiles.get("Ferrosite"), 0, explosionBlacklist);
		}

		levelSave = null;
	}

	@Override
	public boolean interact(Player player, Item heldItem, Direction attackDir) {
    	if (heldItem instanceof PowerGloveItem) {
			if (!fuseLit) {
				return super.interact(player, heldItem, attackDir);
			}
    		return true;
    	} else {
			if (!fuseLit) {
				fuseLit = true;
				Sound.playAt("genericFuse", this.x, this.y);
				return true;
			}
    	}
		return false;
	}

	@Override
	public int getLightRadius() {
		return light;
	}

}
