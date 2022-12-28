package minicraft.entity.mob;

import java.util.ArrayList;
import java.util.List;

import minicraft.core.Game;
import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.furniture.Spawner;
import minicraft.gfx.MobSprite;
import minicraft.gfx.Point;
import minicraft.gfx.Screen;
import minicraft.item.Items;
import minicraft.level.tile.Tiles;

public class Creeper extends EnemyMob {
	private static MobSprite[][][] sprites;
	static {
		sprites = new MobSprite[4][1][2];
		for (int i = 0; i < 4; i++) {
			MobSprite[] list = MobSprite.compileSpriteList(4, 0 + (i * 2), 2, 2, 0, 2);
			sprites[i][0] = list;
		}
	}

	private int light;

	private static final int MAX_FUSE_TIME = 60;
	private static final int TRIGGER_RADIUS = 64;
	private static final int BLAST_DAMAGE = 50;

	private int fuseTime = 0;
	private boolean fuseLit = false;

	private final String[] explosionBlacklist = new String[] { "hard rock", "obsidian wall", "raw obsidian"};

	public Creeper(int lvl) {
		super(lvl, sprites, 10, 50);
	}

	@Override
	public boolean move(int xa, int ya) {
		boolean result = super.move(xa, ya);
		dir = Direction.DOWN;
		if (xa == 0 && ya == 0) {
			walkDist = 0;
		}
		return result;
	}

	@Override
	public void tick() {
		super.tick();

		// Creepers should not explode if player is in passive mode
		if (Settings.get("diff").equals("Peaceful") || Game.isMode("Creative")) {
			return; 
		}

		if (fuseTime > 0) {
			fuseTime--; // fuse getting shorter...
			xa = ya = 0;
			light = 2;

		} else if (fuseLit) { // fuseLit is set to true when fuseTime is set to max, so this happens after fuseTime hits zero, while fuse is lit.
			xa = ya = 0;
			light = 0;

			boolean playerInRange = false; // tells if any players are within the blast

			// Find if the player is in range and store it in playerInRange.
			for (Entity entity : level.getEntitiesOfClass(Mob.class)) {
				Mob mob = (Mob) entity;
				int pdx = Math.abs(mob.x - x);
				int pdy = Math.abs(mob.y - y);
				if (pdx < TRIGGER_RADIUS && pdy < TRIGGER_RADIUS) {
					if (mob instanceof Player) {
						playerInRange = true;
					}
				}
			}

			// Handles what happens when it blows up.
			// It will only blow up if there are any players nearby.
			if (playerInRange) {
            	// Play explosion sound
				Sound.genericExplode.playOnWorld(x, y);
				
				// figure out which tile the mob died on
				int xt = x >> 4;
				int yt = (y - 2) >> 4;

				// used for calculations
				int radius = lvl;

				// The total amount of damage we want to apply.
				int lvlDamage = BLAST_DAMAGE * lvl;

				// hurt all the entities
				List<Entity> entitiesInRange = level.getEntitiesInTiles(xt, yt, radius);
				List<Entity> spawners = new ArrayList<>();

				for (Entity entity : entitiesInRange) {
					if (entity instanceof Mob) {
						Mob mob = (Mob) entity;
						int distx = Math.abs(mob.x - x);
						int disty = Math.abs(mob.y - y);
						float distDiag = (float) Math.sqrt(distx ^ 2 + disty ^ 2);
						mob.hurt(this, (int) (lvlDamage * (1 / (distDiag + 1)) + Settings.getIdx("diff")));
					} else if (entity instanceof Spawner) {
						spawners.add(entity);
					}
				}

				Point[] tilePositions = level.getAreaTilePositions(xt, yt, radius);
				for (Point tilePosition : tilePositions) {
					boolean hasSpawner = false;
					for (Entity spawner : spawners) {
						if (spawner.x >> 4 == tilePosition.x && spawner.y >> 4 == tilePosition.y) {
							hasSpawner = true;
							break;
						}
					}
					if (!hasSpawner) {
						if (level.depth != 1) {
							level.setAreaTiles(tilePosition.x, tilePosition.y, 0, Tiles.get("Hole"), 0, explosionBlacklist);
						} else {
							level.setAreaTiles(tilePosition.x, tilePosition.y, 0, Tiles.get("Infinite Fall"), 0, explosionBlacklist);
						}

					}
				}

				for (Entity entity : entitiesInRange) { // This is repeated because of tilePositions need to be calculated
					if (entity == this) {
						continue;
					}

					Point ePos = new Point(entity.x >> 4, entity.y >> 4);
					for (Point p : tilePositions) {
						if (!p.equals(ePos)) {
							continue;
						}
						if (!level.getTile(p.x, p.y).mayPass(level, p.x, p.y, entity)) {
							entity.die();
						}
					}
				}

				die(); // dying now kind of kills everything. the super class will take care of it.
			} else {
				fuseTime = 0;
				fuseLit = false;
			}
		}
	}

	@Override
	public void render(Screen screen) {
		super.render(screen);
	}

	@Override
	protected void touchedBy(Entity entity) {
		// not explode if the difficulty is peaceful or game mode is crative
		if (Settings.get("diff").equals("Peaceful") || Game.isMode("Creative")) {
			return;
		}

		if (entity instanceof Player) {
			if (fuseTime == 0 && !fuseLit) {
				Sound.genericFuse.playOnWorld(x, y);
				fuseTime = MAX_FUSE_TIME;
				fuseLit = true;
			}
			((Player) entity).hurt(this, 1);
		}
	}

	public boolean canWool() {
		return false;
	}

	public void die() {
		// Only drop items if the creeper has not exploded
		if (!fuseLit) {
			dropItem(1, 4 - Settings.getIdx("diff"), Items.get("Gunpowder"));
		}
		super.die();
	}

	@Override
	public int getLightRadius() {
		return light;
	}
}