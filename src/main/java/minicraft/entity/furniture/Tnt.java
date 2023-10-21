package minicraft.entity.furniture;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Timer;

import minicraft.core.Game;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.FireParticle;
import minicraft.graphic.Color;
import minicraft.graphic.Rectangle;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.item.Item;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;
import minicraft.screen.AchievementsDisplay;

public class Tnt extends Furniture implements ActionListener {
	private static int FUSE_TIME = 90;
	private static int BLAST_RADIUS = 32;
	private static int BLAST_DAMAGE = 75;

	private int damage = 0;
	private int light = 0;
	private int fuseTick = 0;
	private boolean fuseLit = false;
	private Timer explodeTimer;
	private Level levelSave;

	private final String[] explosionBlacklist = new String[] {
		"hard rock", "obsidian wall", "raw obsidian", "stairs up", "stairs down", "infinite fall"
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

		// Ignite the TNT when touch lava :)
		if (level.getTile(x >> 4,y >> 4) == Tiles.get("Lava") && fuseLit == false) {
			fuseLit = true;
			for (int i = 0; i < (1 + random.nextInt(3)); i++) {
				level.add(new FireParticle(x - 8 + random.nextInt(16), y - 6 + random.nextInt(12)));
			}
		}

		if (fuseLit) {
			fuseTick++;

			light = 2;
			
			if (fuseTick >= FUSE_TIME) {
				// blow up
				List<Entity> entitiesInRange = level.getEntitiesInRect(new Rectangle(x, y, BLAST_RADIUS * 2, BLAST_RADIUS * 2, Rectangle.CENTER_DIMS));

				for (Entity entity : entitiesInRange) {
					float dist = (float) Math.hypot(entity.x - x, entity.y - y);
					damage = (int) (BLAST_DAMAGE * (1 - (dist / BLAST_RADIUS))) + 1;

					if (entity instanceof Mob && damage > 0) {
						((Mob) entity).onExploded(this, damage);
					}

					// Ignite other bombs in range.
					if (entity instanceof Tnt) {
						Tnt tnt = (Tnt) entity;
						if (!tnt.fuseLit) {
							tnt.fuseLit = true;
							Sound.genericFuse.playOnLevel(this.x, this.y);
							tnt.fuseTick = FUSE_TIME * 2 / 3;
						}

					}
				}

				int xt = x >> 4;
				int yt = (y - 2) >> 4;

				// Get the tiles that have been exploded.
				Tile[] affectedTiles = level.getAreaTiles(xt, yt, 1);

				// Call the onExplode() event.
				for (int i = 0; i < affectedTiles.length; i++) {
					// This assumes that range is 1.
					affectedTiles[i].hurt(level, xt, yt, damage);
					affectedTiles[i].onExplode(level, xt + i % 3 - 1, yt + i / 3 - 1);

				}

				// Play explosion sound
				Sound.genericExplode.playOnLevel(this.x, this.y);

				level.setAreaTiles(xt, yt, 1, Tiles.get("Explode"), 0, explosionBlacklist);

				levelSave = level;
				explodeTimer.start();
				super.remove();

				if (!Game.isMode("Creative")) {
					AchievementsDisplay.setAchievement("minicraft.achievement.demolition", true);
				}

			}
		}
	}

	@Override
	public void render(Screen screen) {
		if (fuseLit) {
			int colorFactor = 100 * ((fuseTick % 15) / 5) + 200;
			color = Color.get(-1, colorFactor, colorFactor + 100, 555);
		}
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
		if (!fuseLit) {
			fuseLit = true;
			Sound.genericFuse.playOnLevel(this.x, this.y);
			return true;
		}
		return false;
	}

	@Override
	public int getLightRadius() {
		return light;
	}

}
