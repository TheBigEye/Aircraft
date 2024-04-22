package minicraft.entity.furniture;

import java.util.ArrayList;

import org.tinylog.Logger;

import minicraft.core.Game;
import minicraft.core.Updater;
import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.mob.EnemyMob;
import minicraft.entity.mob.MobAi;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.FireParticle;
import minicraft.entity.particle.TextParticle;
import minicraft.graphic.Color;
import minicraft.graphic.Point;
import minicraft.graphic.Sprite;
import minicraft.item.FurnitureItem;
import minicraft.item.Item;
import minicraft.item.PotionType;
import minicraft.item.PowerGloveItem;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.tile.Tile;

public class Spawner extends Furniture {

	private static int frame = 0;

	private static final int ACTIVE_RADIUS = 8 * 16;
	private static final int minSpawnInterval = 200;
	private static final int maxSpawnInterval = 400;
	private static final int minMobSpawnChance = 10; // 1 in minMobSpawnChance chance of calling trySpawn every interval.

	public MobAi mob;
	private int health;
	private int lvl;
	private int maxMobLevel;
	private int spawnTick;
	private int tickTime;

	/**
	 * Initializes the spawners variables to the corresponding values from the mob.
	 * 
	 * @param m The mob which this spawner will spawn.
	 */
	private void initMob(MobAi m) {
		mob = m;
		sprite.color = color = mob.color;

		if (m instanceof EnemyMob) {
			lvl = ((EnemyMob) mob).lvl;
			maxMobLevel = mob.getMaxLevel();
		} else {
			lvl = 1;
			maxMobLevel = 1;
		}

		if (lvl > maxMobLevel) {
			lvl = maxMobLevel;
		}
	}

	/**
	 * Creates a new spawner for the mob m.
	 * 
	 * @param m Mob which will be spawned.
	 */
	public Spawner(MobAi m) {
		super(getClassName(m.getClass()) + " Spawner", new Sprite(0 + frame, 32, 2, 2, 2), 7, 2);
		health = 100;
		initMob(m);
		resetSpawnInterval();
	}

	/**
	 * Returns the classname of a class.
	 * 
	 * @param c The class.
	 * @return String representation of the classname.
	 */
	@SuppressWarnings("rawtypes")
	private static String getClassName(Class c) {
		String fullName = c.getCanonicalName();
		return fullName.substring(fullName.lastIndexOf(".") + 1);
	}

	@Override
	public void tick() {
	    super.tick();

	    tickTime++;

	    spawnTick--;
	    if (spawnTick <= 0) {
	        int chance = (int) (minMobSpawnChance * Math.pow(level.mobCount, 2) / Math.pow(level.maxMobCount, 2)); // this forms a quadratic function that determines the mob spawn chance.
	        if (chance <= 0 || random.nextInt(chance) == 0) trySpawn();
	        resetSpawnInterval();
	    }

	    // Fire particles
	    if (tickTime / 2 % 8 == 0) {
	        if (Settings.get("particles").equals(true)) {
	            level.add(new FireParticle(x - 10 + random.nextInt(14), y - 8 + random.nextInt(12)));
	        }
	    } else {
	        frame = random.nextInt(3) * 2;
	    }

	    if (Settings.get("diff").equals("Peaceful")) {
	        resetSpawnInterval();
	    }
	}


	/**
	 * Resets the spawner so it can spawn another mob.
	 */
	private void resetSpawnInterval() {
		spawnTick = random.nextInt(maxSpawnInterval - minSpawnInterval + 1) + minSpawnInterval;
	}

	/**
	 * Tries to spawn a new mob.
	 */
	private void trySpawn() {
	    if (level == null) return; // if no level, so we cannot do anything
	    if (level.mobCount >= level.maxMobCount) return; // can't spawn more entities

	    if (mob instanceof EnemyMob) {
	        if (level.depth >= 0 && (Updater.tickCount > Updater.sleepEndTime) && (Updater.tickCount < Updater.sleepStartTime)) {
	            return; // Do not spawn if it is on the surface or above and it is under daylight.
	        }
	        if (level.isLight(x >> 4, y >> 4)) {
	            return;
	        }
	    }

	    Player player = getClosestPlayer();
	    if (player == null) {
	        return;
	    }

	    int xd = player.x - x;
	    int yd = player.y - y;

	    if (xd * xd + yd * yd > ACTIVE_RADIUS * ACTIVE_RADIUS) {
	        return;
	    }

	    MobAi newmob;
	    try {
	        if (mob instanceof EnemyMob) {
	            newmob = mob.getClass().getConstructor(int.class).newInstance(lvl);
	        } else {
	            newmob = mob.getClass().getDeclaredConstructor().newInstance();
	        }
	    } catch (Exception exception) {
	        Logger.error("Could not spawn mob, error initializing mob instance:");
	        exception.printStackTrace();
	        return;
	    }

	    Point position = new Point(x >> 4, y >> 4);
	    Point[] areaPositions = level.getAreaTilePositions(position.x, position.y, 1);
	    ArrayList<Point> validPositions = new ArrayList<>();
	    for (Point point : areaPositions) {
	        Tile tile = level.getTile(point.x, point.y);
	        if (!(!tile.mayPass(level, point.x, point.y, newmob) || mob instanceof EnemyMob && tile.getLightRadius(level, point.x, point.y) > 0)) {
	            validPositions.add(point);
	        }
	    }

	    if (validPositions.isEmpty()) {
	        return; // cannot spawn mob.
	    }

	    Point spawnPos = validPositions.get(random.nextInt(validPositions.size()));

	    newmob.x = spawnPos.x << 4;
	    newmob.y = spawnPos.y << 4;

	    if (Game.debug) level.printLevelLoc("Spawning new " + mob, (newmob.x >> 4), (newmob.y >> 4), "...");

	    level.add(newmob);
	    Sound.playAt("genericHurt", this.x, this.y);

	    // Fire particles when spawn a mob
	    if (Settings.get("particles").equals(true)) {
	        for (int i = 0; i < 3; i++) {
	            level.add(new FireParticle(x - 8 + random.nextInt(16), y - 6 + random.nextInt(12)));
	        }
	    }
	}


	@Override
	public boolean interact(Player player, Item item, Direction attackDir) {
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;

			Sound.playAt("genericHurt", this.x, this.y);

			int toolDamage;
			if (Game.isMode("Creative")) {
				toolDamage = health;
			} else {
				toolDamage = tool.level + random.nextInt(2);

				if (tool.type == ToolType.Pickaxe) {
					toolDamage += random.nextInt(5) + 2;
				}

				if (player.potionEffects.containsKey(PotionType.Haste)) {
					toolDamage *= 2;
				}
			}

			health -= toolDamage;
			level.add(new TextParticle("" + toolDamage, x, y, Color.get(-1, 200, 300, 400)));
			if (health <= 0) {
				level.remove(this);
                
                // Random spawner sound 
				switch (random.nextInt(3)) {
					case 0: Sound.playAt("genericHurt", this.x, this.y); break;
					case 1: Sound.playAt("spawnerDestroy1", this.x, this.y); break;
					case 2: Sound.playAt("spawnerDestroy2", this.x, this.y); break;
				    case 3: Sound.playAt("spawnerDestroy3", this.x, this.y); break;
				    default: Sound.playAt("genericHurt", this.x, this.y); break;
				}

				// Sound.playerDeath.playOnDisplay();
				player.addScore(500);
            }

            return true;
        }

        if (item instanceof PowerGloveItem && Game.isMode("Creative")) {
        	level.remove(this);
        	if (!(player.activeItem instanceof PowerGloveItem)) {
        		player.getInventory().add(0, player.activeItem);
        	}
        	player.activeItem = new FurnitureItem(this);
        	return true;
        }

        if (item == null) {
        	return use(player);
        }

        return false;
    }

    @Override
    public boolean use(Player player) {
    	if (Game.isMode("Creative") && mob instanceof EnemyMob) {
    		lvl++;

    		if (lvl > maxMobLevel) {
    			lvl = 1;
    		}
    		try {
    			EnemyMob newmob = (EnemyMob) mob.getClass().getConstructor(int.class).newInstance(lvl);
    			initMob(newmob);
    		} catch (Exception exception) {
    			exception.printStackTrace();
    		}
    		return true;
    	}

    	return false;
    }

    @Override
    public Furniture clone() {
    	return new Spawner(mob);
    }
}
