package minicraft.entity.furniture;

import java.util.ArrayList;
import java.util.Random;

import minicraft.core.Game;
import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.mob.EnemyMob;
import minicraft.entity.mob.MobAi;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.FireParticle;
import minicraft.entity.particle.TextParticle;
import minicraft.gfx.Color;
import minicraft.gfx.Point;
import minicraft.gfx.Sprite;
import minicraft.item.FurnitureItem;
import minicraft.item.Item;
import minicraft.item.PotionType;
import minicraft.item.PowerGloveItem;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;

public class Spawner extends Furniture {

	private Random rnd = new Random();
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
		sprite.color = col = mob.col;

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
		super(getClassName(m.getClass()) + " Spawner", new Sprite(8, 26 + frame, 2, 2, 2), 7, 2);
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
		if (tickTime / 2 % 8 == 0 && Settings.get("particles").equals(true)) {
			int randX = rnd.nextInt(14);
			int randY = rnd.nextInt(12);
			level.add(new FireParticle(x - 10 + randX, y - 8 + randY));
		}  else {
			frame = rnd.nextInt(2) * 2;
		}

		if (Settings.get("diff").equals("Peaceful")) {
			resetSpawnInterval();
		}
	}

	/**
	 * Resets the spawner so it can spawn another mob.
	 */
	private void resetSpawnInterval() {
		spawnTick = rnd.nextInt(maxSpawnInterval - minSpawnInterval + 1) + minSpawnInterval;
	}

	/**
	 * Tries to spawn a new mob.
	 */
	private void trySpawn() {
		if (level == null) return;
		if (level.mobCount >= level.maxMobCount) {
			return; // can't spawn more entities
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
		} catch (Exception ex) {
			System.err.println("Spawner ERROR: could not spawn mob; error initializing mob instance:");
			ex.printStackTrace();
			return;
		}

		Point pos = new Point(x >> 4, y >> 4);
		Point[] areaPositions = level.getAreaTilePositions(pos.x, pos.y, 1);
		ArrayList<Point> validPositions = new ArrayList<>();
		for (Point p : areaPositions) {
			if (!(!level.getTile(p.x, p.y).mayPass(level, p.x, p.y, newmob) || mob instanceof EnemyMob && level.getTile(p.x, p.y).getLightRadius(level, p.x, p.y) > 0)) {
				validPositions.add(p);
			}
		}

		if (validPositions.size() == 0) {
			return; // cannot spawn mob.
		}

		Point spawnPos = validPositions.get(random.nextInt(validPositions.size()));

		newmob.x = spawnPos.x << 4;
		newmob.y = spawnPos.y << 4;

		if (Game.debug) level.printLevelLoc("Spawning new " + mob, (newmob.x >> 4), (newmob.y >> 4), "...");

		level.add(newmob);
		Sound.Furniture_spawner_spawn.playOnWorld(x, y, player.x, player.y);

		// Fire particles when spawn a mob
		if (Settings.get("particles").equals(true)) {
			for (int i = 0; i < 3; i++) {
				int randX = rnd.nextInt(16);
				int randY = rnd.nextInt(12);
				level.add(new FireParticle(x - 8 + randX, y - 6 + randY));
			}
		}
	}

	@Override
	public boolean interact(Player player, Item item, Direction attackDir) {
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;

			Sound.Furniture_spawner_hurt.playOnGui();

			int dmg;
			if (Game.isMode("Creative")) {
				dmg = health;
			} else {
				dmg = tool.level + random.nextInt(2);

				if (tool.type == ToolType.Pickaxe) {
					dmg += random.nextInt(5) + 2;
				}

				if (player.potionEffects.containsKey(PotionType.Haste)) {
					dmg *= 2;
				}
			}

			health -= dmg;
			level.add(new TextParticle("" + dmg, x, y, Color.get(-1, 200, 300, 400)));
			if (health <= 0) {
				level.remove(this);
                
                // Random spawner sound 
				switch (random.nextInt(4)) {
					case 0: Sound.Furniture_spawner_hurt.playOnGui(); break;
					case 1: Sound.Furniture_spawner_destroy.playOnGui(); break;
					case 2: Sound.Furniture_spawner_destroy_2.playOnGui(); break;
				    case 3: Sound.Furniture_spawner_destroy_3.playOnGui(); break;
				    case 4: Sound.Furniture_spawner_destroy_3.playOnGui(); break;
				    default: Sound.Furniture_spawner_hurt.playOnGui(); break;
				}

				// Sound.playerDeath.playOnGui();
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
    		} catch (Exception ex) {
    			ex.printStackTrace();
    		}
    		return true;
    	}

    	return false;
    }

    public Furniture clone() {
    	return new Spawner(mob);
    }
}
