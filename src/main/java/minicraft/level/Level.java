package minicraft.level;

import minicraft.core.Game;
import minicraft.core.Updater;
import minicraft.core.World;
import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.Entity;
import minicraft.entity.Fireball;
import minicraft.entity.ItemEntity;
import minicraft.entity.Spark;
import minicraft.entity.furniture.*;
import minicraft.entity.mob.*;
import minicraft.graphic.Point;
import minicraft.graphic.Rectangle;
import minicraft.graphic.Screen;
import minicraft.item.Item;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;
import minicraft.level.tile.TorchTile;
import org.tinylog.Logger;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

//--------------------------------------------------------------------------------------------------------------------------------------------------

/*
 * This class is in charge of managing events or functions related to the world (Level)
 *
 *  - Level variables
 *  - Sky dungeon generation
 *  - Checks for entities (AirWizard, mobs and dungeon chests)
 *  - Random music system
 *  - Mob spawn system
 *  - Mob removal system
 *  - Dungeons generation
 *  - Village generation
 */

public class Level {
	private final Random random;

	private static final String[] levelNames = {
		"The Void", "Heaven", "Surface", "Iron", "Gold", "Lava", "Dungeon"
	};

	// the chance of a mob actually trying to spawn when trySpawn is
	// called equals: mobCount / maxMobCount * MOB_SPAWN_FACTOR. so, it
	// basically equals the chance, 1/number, of a mob spawning when
	// the mob cap is reached. I hope that makes sense...
	private static final int MOB_SPAWN_FACTOR = 100;

	public int w, h;
	public int size;
	private final long seed; // The used seed that was used to generate the world

	public short[] tiles; // An array of all the tiles in the world.
	public short[] data; // An array of the data of the tiles in the world

	// Depth level of the level
	public final int depth;

	// Used for Maps Book
	public boolean[][] explored;

	private boolean skyAmbience = false;

	public static String getLevelName(int depth) {
		return levelNames[-1 * depth + 2];
	}

	public static String getDepthString(int depth) {
		return "Level " + (depth < 0 ? "B" + (-depth) : depth);
	}

	// Affects the number of monsters that are on the level, bigger the number the less monsters spawn.
	public int monsterDensity = 8;
	public int mobCount = 0;
	public int maxMobCount;
	public int chestCount;


	/**
	 * I will be using this lock to avoid concurrency exceptions in entities and sparks set
	 */
	private final Object entityLock = new Object(); // I will be using this lock to avoid concurrency exceptions in entities and sparks set
	private final Set<Entity> entities = java.util.Collections.synchronizedSet(new HashSet<>()); // A list of all the entities in the world
	private final Set<Spark> sparks = java.util.Collections.synchronizedSet(new HashSet<>()); // A list of all the sparks in the world
	private final Set<Fireball> fireballs = java.util.Collections.synchronizedSet(new HashSet<>()); // A list of all the sparks in the world
	private final Set<Player> players = java.util.Collections.synchronizedSet(new HashSet<>()); // A list of all the players in the world
	private final List<Entity> entitiesToAdd = new ArrayList<>(); /// entities that will be added to the level on next tick are stored here. This is for the sake of multithreading optimization. (hopefully)
	private final List<Entity> entitiesToRemove = new ArrayList<>(); /// entities that will be removed from the level on next tick are stored here. This is for the sake of multithreading optimization. (hopefully)

	// Creates a sorter for all the entities to be rendered.
	//private static Comparator<Entity> spriteSorter = Comparator.comparingInt(e -> e.y); // Broken

	private static Comparator<Entity> spriteSorter = Comparator.comparingInt(new ToIntFunction<Entity>() {
		@Override
		public int applyAsInt(Entity entity) {
			return entity.y;
		}
	});

	public Entity[] getEntitiesToSave() {
		Entity[] allEntities = new Entity[entities.size() + sparks.size() + fireballs.size() + entitiesToAdd.size()];
		Entity[] toAdd = entitiesToAdd.toArray(new Entity[entitiesToAdd.size()]);
		Entity[] current = getEntityArray();
		System.arraycopy(current, 0, allEntities, 0, current.length);
		System.arraycopy(toAdd, 0, allEntities, current.length, toAdd.length);

		return allEntities;
	}

	// This is a solely debug method I made, to make printing repetitive stuff easier,
	// Should be changed to accept prepend and entity, or a tile (as an Object),
	// It will get the coordinates and class name from the object, and will divide coords
	// by 16 if passed an entity.
	public void printLevelLoc(String prefix, int x, int y) {
		printLevelLoc(prefix, x, y, "");
	}

	public void printLevelLoc(String prefix, int x, int y, String suffix) {
		String levelName = getLevelName(depth);
		Logger.debug("{} on {} level ({}, {}){}", prefix, levelName, x, y, suffix);
	}

	public void printTileLocs(Tile tile) {
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				if (getTile(x, y).id == tile.id) {
					printLevelLoc(tile.name, x, y);
				}
			}
		}
	}

	public void printEntityLocs(Class <? extends Entity> entityClass) {
		int numfound = 0;
		for (Entity entity: getEntityArray()) {
			if (entityClass.isAssignableFrom(entity.getClass())) {
				printLevelLoc(entity.toString(), entity.x >> 4, entity.y >> 4);
				numfound++;
			}
		}

		Logger.debug("Found {} entities in level of depth {}", numfound, depth);
	}

	private void updateMobCap() {
		maxMobCount = 140 + 140 * Settings.getIndex("diff");
		if (depth == 1) maxMobCount /= 2;
		if (depth == 0 || depth == -4) maxMobCount = maxMobCount * 2 / 3;
	}

	/** Level which the world is contained in */
	public Level(int w, int h, long seed, int level, Level parentLevel, boolean makeWorld) {
		depth = level; // assigns the depth variable
		size = w * h;
		this.w = w; // Assigns the width
		this.h = h; // Assigns the height
		this.seed = seed;

        random = new Random(seed);

		short[][] maps; // Multidimensional array (an array within a array), used for the map

		if (level != -4 && level != 0) {
			monsterDensity = 9;
		}

		updateMobCap();

		if (!makeWorld) {
			tiles = new short[size];
			data = new short[size];
			return;
		}

		if (Game.debug) Logger.debug("Generating level {} ...", level);

		maps = LevelGen.createAndValidateMap(w, h, level, seed);
		if (maps == null) {
			Logger.error("Level generation: returned maps array is null");
			return;
		}

		tiles = maps[0]; // assigns the tiles in the map
		data = maps[1]; // assigns the data of the tiles

		if (level < 0) {
			generateSpawnerStructures();
		}

		if (level < 0) {
			generateSummonStructures();
		}

		if (level == 0) {
			generateVillages();
		}

		if (parentLevel != null) { // If the level above this one is not null (aka, if this isn't the sky level)
			for (int y = 0; y < h; y++) { // Loop through height
				for (int x = 0; x < w; x++) { // Loop through width
					if (parentLevel.getTile(x, y) == Tiles.get("Stairs Down")) { // If the tile in the level above the current one is a stairs down then...
						if (level == -4) { /// Make the obsidian wall formation around the stair in the dungeon level
							Structure.dungeonGate.draw(this, w / 2, h / 2);

						} else if (level == 0) { // Surface
							// Surround the sky stairs with hard rock
							Logger.trace("Setting tiles around {}, {} to hard rock ...", x, y);
							setAreaTiles(x, y, 1, Tiles.get("Hard Rock"), 0);

						} else {
							// Any other level, the up-stairs should have dirt on all sides.
							setAreaTiles(x, y, 1, Tiles.get("Dirt"), 0);
						}

						setTile(x, y, Tiles.get("Stairs Up")); // Set a stairs up tile in the same position on the current level
					}
				}
			}

		} else if (depth == 1) { // This is the sky level
			boolean placedSkyDungeon = false;
			while (!placedSkyDungeon) {
				int x = (this.w / 2) - 2;
				int y = (this.h / 2) - 2;

				Structure.skyDungeon.draw(this, x, y);
				placedSkyDungeon = true;
			}
		}

		checkChestCount(false);
		checkAirWizard();

		if (Game.debug) {
			printTileLocs(Tiles.get("Stairs Down"));
		}

        // Initialize the explored array for all map pixels to false
        explored = new boolean[this.w][this.h];
        for (int y = 0; y < this.h; y++) {
            for (int x = 0; x < this.w; x++) {
                explored[y][x] = false;
            }
        }
	}

	public Level(int w, int h, int level, Level parentLevel, boolean makeWorld) {
		this(w, h, 0, level, parentLevel, makeWorld);
	}

	/** Level which the world is contained in */
	public Level(int w, int h, int level, Level parentLevel) {
		this(w, h, level, parentLevel, true);
	}

	public long getSeed() {
		return seed;
	}

	public void checkAirWizard() {
		checkAirWizard(true);
	}

	private void checkAirWizard(boolean check) {
		if (depth == 1 && !AirWizard.beaten) { // Add the airwizard to the surface

			boolean found = false;
			if (check) {
				for (Entity entity: entitiesToAdd) {
                    if (entity instanceof AirWizard) {
                        found = true;
                        break;
                    }
				}
				for (Entity entity: entities) {
                    if (entity instanceof AirWizard) {
                        found = true;
                        break;
                    }
				}
			}

			// if not found the Air Wizard, add then again
			if (!found) {
				AirWizard airWizard = new AirWizard(false);
				this.add(airWizard, w / 2, h / 2, true);
			}
		}

	}

	public void checkChestCount() {
		checkChestCount(true);
	}

	private void checkChestCount(boolean check) {
		// If the level is the dungeon, and we're not just loading the world...
		if (depth != -4) return;

		int numChests = 0;

		if (check) {
			for (Entity entity: entitiesToAdd) {
				if (entity instanceof DungeonChest) {
					numChests++;
				}
			}
			for (Entity entity: entities) {
				if (entity instanceof DungeonChest) {
					numChests++;
				}
			}

			if (Game.debug) Logger.info("Found {} dungeon chests", numChests);
		}

		/// Make DungeonChests!
		for (int i = numChests; i < 10 * (w / 128); i++) {
			DungeonChest dungeonChest = new DungeonChest(true);
			boolean addedchest = false;
			while (!addedchest) { // Keep running until we successfully add a DungeonChest

				// Pick a random tile:
				int x2 = random.nextInt(16 * w) >> 4;
				int y2 = random.nextInt(16 * h) >> 4;

				if (getTile(x2, y2) == Tiles.get("Obsidian")) {
					boolean xaxis = random.nextBoolean();
					if (xaxis) {
						for (int s = x2; s < w - s; s++) {
							if (getTile(s, y2) == Tiles.get("Obsidian Wall")) {
								dungeonChest.x = s * 16 - 24;
								dungeonChest.y = y2 * 16 - 24;
							}
						}
					} else { // y axis
						for (int s = y2; s < y2 - s; s++) {
							if (getTile(x2, s) == Tiles.get("Obsidian Wall")) {
								dungeonChest.x = x2 * 16 - 24;
								dungeonChest.y = s * 16 - 24;
							}
						}
					}
					if (dungeonChest.x == 0 && dungeonChest.y == 0) {
						dungeonChest.x = x2 * 16 - 8;
						dungeonChest.y = y2 * 16 - 8;
					}
					add(dungeonChest);
					chestCount++;
					addedchest = true;
				}
			}
		}
	}

	private void tickEntity(Entity entity) {
		if (entity == null) return;

		if (entity.isRemoved()) {
			remove(entity);
			return;
		}

		if (entity != Game.player) { // player is ticked separately, others are ticked on server
			entity.tick(); /// the main entity tick call.
		}

		if (entity.isRemoved() || entity.getLevel() != this) {
			remove(entity);
		}
	}

	public void tick(boolean fullTick) {
		int count = 0;

		updateMobCap();

		while (!entitiesToAdd.isEmpty()) {
			Entity entity = entitiesToAdd.get(0);
			boolean inLevel = entities.contains(entity);

			if (!inLevel) {
				if (Game.debug) printEntityStatus("Adding ", entity, "furniture.DungeonChest", "mob.AirWizard", "mob.Player");

				synchronized (entityLock) {
					if (entity instanceof Spark) {
						sparks.add((Spark) entity);
					} else if (entity instanceof Fireball) {
						fireballs.add((Fireball) entity);
					} else {
						entities.add(entity);
						if (entity instanceof Player) {
							players.add((Player) entity);
						}
					}
				}
			}
			entitiesToAdd.remove(entity);
		}


		// LEVEL AMBIENT LOOPS!

		// in the sky
		if (depth == 1) {
			if (!skyAmbience) {
				Sound.loop("heavenWind", true);
				skyAmbience = true;
			}
		} else {
			Sound.stop("heavenWind");
			skyAmbience = false;
		}

		if (Updater.tickCount % 32401 == 0) {
			playRandomMusic(depth);
		}

		if (fullTick) {
			// this prevents any entity (or tile) tick action from happening on a server level with no players.

			for (int i = 0; i < size / 50; i++) {
				int xt = random.nextInt(w);
				int yt = random.nextInt(h);
				getTile(xt, yt).tick(this, xt, yt);
			}

			// entity loop

			for (Entity entity: entities) {
				tickEntity(entity);
				if (entity instanceof Mob) {
					count++;
				}
			}

			sparks.forEach(this::tickEntity);
			fireballs.forEach(this::tickEntity);
		}

		while (count > maxMobCount) {
			Entity removeThis = (Entity) entities.toArray()[(random.nextInt(entities.size()))];
			if ((removeThis instanceof MobAi) && !(removeThis instanceof VillagerMob)) {
				// make sure there aren't any close players
				boolean playerClose = entityNearPlayer(removeThis);

				if (!playerClose) {
					remove(removeThis);
					count--;
				}
			}
		}

		while (!entitiesToRemove.isEmpty()) {
			Entity entity = entitiesToRemove.get(0);

			if (Game.debug) printEntityStatus("Removing ", entity, "mob.Player");

			entity.remove(this); // this will safely fail if the entity's level doesn't match this one.

			if (entity instanceof Spark) {
				sparks.remove(entity);
			} else if (entity instanceof Fireball) {
				fireballs.remove(entity);
			} else {
				entities.remove(entity);
			}

			if (entity instanceof Player) {
				players.remove(entity);
			}
			entitiesToRemove.remove(entity);
		}

		mobCount = count;

		if (fullTick && count < maxMobCount) {
			trySpawn();
		}
	}

	/**
	 * Determine if an entity is near any of the players.
	 *
	 * @param entity The entity to check.
	 * @return True if the entity is within 128 units of the x coordinate and 76 units of the y coordinate of any player, false otherwise.
	 */
	public boolean entityNearPlayer(Entity entity) {
		for (Player player : players) {
			if (Math.abs(player.x - entity.x) < 128 && Math.abs(player.y - entity.y) < 76) {
				return true;
			}
		}
		return false;
	}

	public void printEntityStatus(String entityMessage, Entity entity, String... searching) {
		// "searching" can contain any number of class names I want to print when found.
		String entityClass = entity.getClass().getCanonicalName();
		entityClass = entityClass.substring(entityClass.lastIndexOf(".") + 1);
		for (String search: searching) {
			try {
				if (Class.forName("minicraft.entity." + search).isAssignableFrom(entity.getClass())) {
					printLevelLoc(entityMessage + entityClass, entity.x >> 4, entity.y >> 4, ": " + entity);
					break;
				}
			} catch (ClassNotFoundException exception) {
				exception.printStackTrace();
			}
		}
	}

	public void dropItem(int x, int y, int mincount, int maxcount, Item...items) {
		dropItem(x, y, mincount + random.nextInt(maxcount - mincount + 1), items);
	}

	public void dropItem(int x, int y, int count, Item...items) {
		for (int i = 0; i < count; i++) {
			dropItem(x, y, items);
		}
	}

	public void dropItem(int x, int y, Item...items) {
		for (Item item: items) {
			dropItem(x, y, item);
		}
	}

	public ItemEntity dropItem(int x, int y, Item item) {
		int ranx, rany;

		do {
			ranx = (x + random.nextInt(11)) - 5;
			rany = (y + random.nextInt(11)) - 5;
		} while (ranx >> 4 != x >> 4 || rany >> 4 != y >> 4);

		ItemEntity itemEntity = new ItemEntity(item, ranx, rany);
		add(itemEntity);
		return itemEntity;
	}

	/** This method renders all the tiles in the game */
	public void renderBackground(Screen screen, int xScroll, int yScroll) {
		int xo = xScroll >> 4; // The game's horizontal scroll offset
		int yo = yScroll >> 4; // The game's vertical scroll offset

		int w = (Screen.w) >> 4; // Width of the screen being rendered
		int h = (Screen.h) >> 4; // Height of the screen being rendered

		// Sets the scroll offsets
		screen.setOffset(xScroll, yScroll);

		for (int y = yo; y <= h + yo; y++) { // Loops through the vertical positions
			for (int x = xo; x <= w + xo; x++) { // Loops through the horizontal positions
				getTile(x, y).render(screen, this, x, y); // Renders the tile on the screen
			}
		}

		// Resets the offset
		screen.setOffset(0, 0);
	}

	/** Renders all the entity sprites on the screen */
	public void renderSprites(Screen screen, int xScroll, int yScroll) {
		int xo = xScroll >> 4; // The game's horizontal scroll offset
		int yo = yScroll >> 4; // The game's vertical scroll offset

		int w = (Screen.w + 15) >> 4; // Width of the screen being rendered
		int h = (Screen.h + 15) >> 4; // Height of the screen being rendered

		screen.setOffset(xScroll, yScroll); // Sets the scroll offsets.

		// Sorts and renders the sprites on the screen
		sortAndRender(screen, getEntitiesInTiles(xo - 1, yo - 1, (xo + w) + 1, (yo + h) + 1));

		// Resets the offset
		screen.setOffset(0, 0);
	}

	/** Renders the light off tiles and entities in the underground */
	public void renderLight(Screen screen, int xScroll, int yScroll, int brightness) {
		int xo = xScroll >> 4; // The game's horizontal scroll offset
		int yo = yScroll >> 4; // The game's vertical scroll offset

		int w = (Screen.w + 15) >> 4; // Width of the screen being rendered
		int h = (Screen.h + 15) >> 4; // Height of the screen being rendered

		screen.setOffset(xScroll, yScroll); // Sets the scroll offsets

		int radius = 8; // Radius that plays a part of how far away you can be before light stops rendering

		int xBound = w + xo + radius;
		int yBound = h + yo + radius;

		// gets all the entities in the level
		List <Entity> entities = getEntitiesInTiles(xo - radius, yo - radius, xBound, yBound);

		for (Entity entity: entities) { // Loops through the list of entities
			int lightRadius = entity.getLightRadius(); // Gets the light radius from the entity
			// If the light radius is above 0, then render the light
			if (lightRadius > 0) {
				screen.renderLight(entity.x - 1, entity.y - 4, lightRadius * brightness);
			}
		}

		for (int y = yo - radius; y <= yBound; y++) { // Loops through the vertical positions + rBound
			for (int x = xo - radius; x <= xBound; x++) { // Loops through the horizontal positions + rBound
				// If the x & y positions of the sprites are within the map's boundaries
				if (x < 0 || y < 0 || x >= this.w || y >= this.h) continue;

				// Gets the light radius from local tiles (like lava)
				int lightRadius = getTile(x, y).getLightRadius(this, x, y);

				// If the light radius is above 0, then render the light
				if (lightRadius > 0) {
					screen.renderLight((x << 4) + 8, (y << 4) + 8, lightRadius * brightness);
				}
			}
		}

		// Resets the offset
		screen.setOffset(0, 0);
	}

	/** Sorts and renders sprites from an entity list */
	private void sortAndRender(Screen screen, List <Entity> list) {
		list.sort(spriteSorter); // Sorts the list by the spriteSorter

	    for (Entity entity : list) { // Loops through the entity list
	        if (entity.getLevel() == this && !entity.isRemoved()) {
	            entity.render(screen); // Renders the sprite on the screen
	        } else {
	            remove(entity);
	        }
	    }
	}

	/** Gets a tile from the world. */
	public Tile getTile(int x, int y) {
		// If the tile request is outside the world's boundaries, then returns the connector tile
		if (x < 0 || y < 0 || x >= w || y >= h) {
			return Tiles.get("Connector Tile");
		}

		// Returns the tile that is at the position
		int id = tiles[x + y * w];
		if (id < 0) id += 256;

		return Tiles.get(id);
	}

	public void setTile(int x, int y, String tileWithData) {
		if (!tileWithData.contains("_")) {
			setTile(x, y, Tiles.get(tileWithData));
			return;
		}
		String name = tileWithData.substring(0, tileWithData.indexOf("_"));
		int data = Tiles.get(name).getData(tileWithData.substring(name.length() + 1));
		setTile(x, y, Tiles.get(name), data);
	}

	/** Sets a tile to the world */
	public void setTile(int x, int y, Tile tile) {
		setTile(x, y, tile, tile.getDefaultData());
	}

	/** Sets a tile to the world with a specified data value */
	public void setTile(int x, int y, Tile tile, int dataValue) {
		// If the tile request position is outside the world boundaries, then stop the method
		if (x < 0 || y < 0 || x >= w || y >= h) return;

		tiles[x + y * w] = tile.id; // Places the tile at the x & y location
		data[x + y * w] = (short) dataValue; // Sets the data value of the tile
	}

	/** Gets the data from the x & y position */
	public int getData(int x, int y) {
		// If the data request position is outside the world boundaries, then stop the method
		if (x < 0 || y < 0 || x >= w || y >= h) return 0;

		// Returns the last 16 bits (& 0xffff) of the data from that position
		return data[x + y * w] & 0xff;
	}

	/** Sets a data to the x & y positioned tile */
	public void setData(int x, int y, int value) {
		// If the data request position is outside the world boundaries, then stop the method
		if (x < 0 || y < 0 || x >= w || y >= h) return;

		// sets the data as a short (16-bits) for the data
		data[x + y * w] = (short) value;
	}

	/** Adds a entity to the level */
	public void add(Entity entity) {
		if (entity == null) {
			return;
		}
		add(entity, entity.x, entity.y);
	}

	public void add(Entity entity, int x, int y) {
		add(entity, x, y, false);
	}

	public void add(Entity entity, int x, int y, boolean tileCoords) {
		if (entity == null) {
			return;
		}

		if (tileCoords) {
			x = (x << 4) + 8;
			y = (y << 4) + 8;
		}

		entity.setLevel(this, x, y);

		entitiesToRemove.remove(entity); // to make sure the most recent request is satisfied
		if (!entitiesToAdd.contains(entity)) {
			entitiesToAdd.add(entity);
		}
	}

	/** Removes a entity */
	public void remove(Entity entity) {
		entitiesToAdd.remove(entity);
		if (!entitiesToRemove.contains(entity)) {
			entitiesToRemove.add(entity);
		}
	}

	private void trySpawn() {
		int spawnSkipChance = (int)(MOB_SPAWN_FACTOR * Math.pow(mobCount, 2) / Math.pow(maxMobCount, 2));
		if (spawnSkipChance > 0 && random.nextInt(spawnSkipChance) != 0) {
			return; // hopefully will make mobs spawn a lot slower.
		}

		boolean peaceful = Settings.get("diff").equals("Peaceful");

		boolean spawned = false;
		for (int i = 0; i < 15 && !spawned; i++) {

			int minLevel = 1;
			int maxLevel = 1;
			int lvl = 0;

			if (depth < 0) maxLevel = (-depth) + ((Math.random() > 0.75 && -depth != 4) ? 1 : 0);
			if (depth > 0) minLevel = maxLevel = 4;

			if (!peaceful) {
				lvl = random.nextInt(maxLevel - minLevel + 1) + minLevel;
			}
			int spawnChance = random.nextInt(100);
			int nx = (random.nextInt(w) << 4) + 8;
			int ny = (random.nextInt(h) << 4) + 8;

			// System.out.println("trySpawn on level " + depth + " of lvl " + lvl + " mob w/rand " + random + " at tile " + nx + "," + ny);

			// spawns the enemy mobs; first part prevents enemy mob spawn on surface and the sky on first day, more or less.
			if (!peaceful) {
			    if ((depth != 1 && depth != 2) && EnemyMob.checkStartPos(this, nx, ny)) {
			        if (depth == 0 && Updater.getTime() == Updater.Time.Night && !Game.player.isNiceNight) {
			            EnemyMob[] mobs = {new Zombie(lvl), new Skeleton(lvl), new Creeper(lvl)};
			            int mobIndex = spawnChance / 25;
			            if (mobIndex >= 0 && mobIndex < mobs.length) {
			                add(mobs[mobIndex], nx, ny);
			            }
			        } else if (depth == -4) {
			            EnemyMob[] mobs = {new Snake(lvl), new Knight(lvl)};
			            int mobIndex = spawnChance / 50;
			            if (mobIndex >= 0 && mobIndex < mobs.length) {
			                add(mobs[mobIndex], nx, ny);
			            }
			        } else if (depth == -3 || depth != 0) {
			            EnemyMob[] mobs = { new Slime(lvl), new Zombie(lvl), new OldGolem(lvl), new Skeleton(lvl), new Creeper(lvl) };
			            int mobIndex = spawnChance / 20;
			            if (mobIndex >= 0 && mobIndex < mobs.length) {
			                add(mobs[mobIndex], nx, ny);
			            }
			        }
			        spawned = true;
			    }
			}


			if (depth == 2 && EnemyMob.checkStartPos(this, nx, ny)) { // if nether
				if (spawnChance <= 40) add((new Skeleton(1)), nx, ny);
				spawned = true;
			}

			// Spawn mobs on day light
			if ((depth == 0) && (Updater.getTime() != Updater.Time.Night) && (Updater.getTime() != Updater.Time.Evening)) {
				//int spawnChance = random.nextInt(100); // This is global only for peaceful mobs

				// Spawns passive mobs
				if (PassiveMob.checkStartPos(this, nx, ny)) {
					PassiveMob[] mobs = { new Cow(), new Chicken(), new Pig(), new Sheep() }; // Store all the passive mobs
					if (spawnChance >= 64) {
						add(mobs[0], nx, ny);
					} else if (spawnChance >= 56) {
						add(mobs[1], nx, ny);
					} else if (spawnChance >= 50) {
						add(mobs[2], nx, ny);
					} else {
						add(mobs[3], nx, ny);
					}
					spawned = true;
				}

				// Spawn frost mobs
				if (FrostMob.checkStartPos(this, nx, ny)) {
					FrostMob[] mobs = { new GuiMan(), new Goat() };
					if (spawnChance <= 50) {
						add(mobs[0], nx, ny);
					} else {
						add(mobs[1], nx, ny);
					}
					spawned = true;
				}

			// Spawn mobs on nigth moon light
			} else if (depth == 0 && Updater.getTime() == Updater.Time.Night) {
				// Spawns a firefly
				if (Game.player.isNiceNight && FlyMob.checkStartPos(this, nx, ny)) {
					if (spawnChance >= 50 && spawnChance <= 75) {
						add(new Firefly(), nx, ny);
					}
					spawned = true;
				}
			}

			// This generates mobs from the sky, if the Air Wizard is not defeated
			// they will spawn hostile mobs, if instead, it is defeated they will
			// spawn peaceful mobs
			if (depth == 1 && SkyMob.checkStartPos(this, nx, ny)) {
				if (spawnChance <= (Updater.getTime() == Updater.Time.Night ? 22 : 33) && AirWizard.beaten) { // Spawns passive sky mobs.
					add((new Phyg()), nx, ny);
					add((new Sheepuff()), nx, ny);
				} else { // Spawns hostile sky mobs.
					if (!peaceful) {
						if (spawnChance <= 40) {
							add((new Slime(lvl)), nx, ny);
						} else if (spawnChance <= 75) {
							add((new Zombie(lvl)), nx, ny);
						} else if (spawnChance >= 85) {
							add((new OldGolem(lvl)), nx, ny);
						} else if (spawnChance >= 82) {
							add((new Skeleton(lvl)), nx, ny);
						} else {
							add((new Creeper(lvl)), nx, ny);
						}
					}
				}
				spawned = true;
			}

		}
	}

	public void removeAllEnemies() {
		for (Entity entity: getEntityArray()) {
			if (entity instanceof EnemyMob) {
				// don't remove the airwizard bosses! Unless in creative, since you can spawn more.
				if (!(entity instanceof AirWizard) || Game.isMode("Creative")) {
					entity.remove();
				}
			}
		}
	}

	public void clearEntities() {
		entities.clear();
	}

	public Entity[] getEntityArray() {
	    ArrayList<Entity> entitiesList = new ArrayList<Entity>();
	    entitiesList.addAll(entities);
	    entitiesList.addAll(sparks);
	    entitiesList.addAll(fireballs);
	    return entitiesList.toArray(new Entity[entitiesList.size()]);
	}

	public List <Entity> getEntitiesInTiles(int xt, int yt, int radius) {
		return getEntitiesInTiles(xt, yt, radius, false);
	}

	@SafeVarargs
	public final List <Entity> getEntitiesInTiles(int xt, int yt, int radius, boolean includeGiven, Class <? extends Entity> ...entityClasses) {
		return getEntitiesInTiles(xt - radius, yt - radius, xt + radius, yt + radius, includeGiven, entityClasses);
	}

	/**
	 * Get entities in a certain area on the level.
	 *
	 * @param xt0 Left
	 * @param yt0 Top
	 * @param xt1 Right
	 * @param yt1 Bottom
	 */
	public List<Entity> getEntitiesInTiles(int xt0, int yt0, int xt1, int yt1) {
		return getEntitiesInTiles(xt0, yt0, xt1, yt1, false);
	}

	/**
	 * Get entities in a certain area on the level, and filter them by class.
	 *
	 * @param xt0           Left
	 * @param yt0           Top
	 * @param xt1           Right
	 * @param yt1           Bottom
	 * @param includeGiven  If we should accept entities that match the provided
	 *                      entityClasses. If false, we ignore the provided
	 *                      entityClasses.
	 * @param entityClasses Entities to accept.
	 * @return A list of entities in the area.
	 */
	@SafeVarargs
	public final List<Entity> getEntitiesInTiles(int xt0, int yt0, int xt1, int yt1, boolean includeGiven, Class<? extends Entity>... entityClasses) {
	    List<Entity> contained = new ArrayList<>();

	    // Iterate through all entities in the game world
	    for (Entity entity : getEntityArray()) {
	        // Calculate the tile coordinates of the current entity
	        int xt = entity.x >> 4;
	        int yt = entity.y >> 4;

	        // Check if the entity is within the specified rectangular area
	        if (xt >= xt0 && xt <= xt1 && yt >= yt0 && yt <= yt1) {
	            // Check if the entity matches any of the given entity classes
	            boolean matches = false;
	            for (Class<? extends Entity> entityClass : entityClasses) {
	                if (entityClass.isAssignableFrom(entity.getClass())) {
	                    matches = true;
	                    break;
	                }
	            }

	            // Add the entity to the list if it matches the criteria
	            if (matches == includeGiven) {
	                contained.add(entity);
	            }
	        }
	    }

	    return contained;
	}


	/**
	 * Check if there is an entity on the specified tile.
	 * @param x The x position of the tile.
	 * @param y The y position of the tile
	 * @return True if there is an entity on the tile.
	 */
	public final boolean isEntityOnTile(int x, int y) {
	    boolean found = false;
	    HashMap<String, Entity> entitiesMap = new HashMap<String, Entity>();
	    for (Entity entity: getEntityArray()) {
	        entitiesMap.put((entity.x >> 4) + "," + (entity.y >> 4), entity);
	    }
	    if (entitiesMap.containsKey(x + "," + y)){
	        found = true;
	    }
	    return found;
	}

	public List<Entity> getEntitiesInRect(Rectangle area) {
		List<Entity> result = new ArrayList<>();
		for (Entity entity: getEntityArray()) {
			if (entity.isTouching(area)) {
				result.add(entity);
			}
		}
		return result;
	}

	public List<Entity> getEntitiesInRect(Predicate <Entity> filter, Rectangle area) {
		List<Entity> result = new LinkedList<>();
		for (Entity entity: entities) {
			if (filter.test(entity) && entity.isTouching(area)) {
				result.add(entity);
			}
		}
		return result;
	}

	/// finds all entities that are an instance of the given entity.
	public Entity[] getEntitiesOfClass(Class <? extends Entity> targetClass) {
		ArrayList <Entity> matches = new ArrayList<>();
		for (Entity entity: getEntityArray()) {
			if (targetClass.isAssignableFrom(entity.getClass())) {
				matches.add(entity);
			}
		}

		return matches.toArray(new Entity[0]);
	}

	public Player[] getPlayers() {
		return players.toArray(new Player[players.size()]);
	}

	public Entity[] getEntities() {
		return entities.toArray(new Entity[entities.size()]);
	}

	public Player getClosestPlayer(int x, int y) {
		Player[] players = getPlayers();
		if (players.length == 0) {
			return null;
		}

		Player closest = players[0];
		int xd = closest.x - x;
		int yd = closest.y - y;

		int xx = xd * xd;
		int yy = yd * yd;

		for (int i = 1; i < players.length; i++) {
			int curxd = players[i].x - x;
			int curyd = players[i].y - y;
			if ((xx + yy) > ((curxd * curxd) + (curyd * curyd))) {
				closest = players[i];
				xd = curxd;
				yd = curyd;
			}
		}

		return closest;
	}

	public Point[] getAreaTilePositions(int x, int y, int r) {
		return getAreaTilePositions(x, y, r, r);
	}

	public Point[] getAreaTilePositions(int x, int y, int rx, int ry) {
	    Point[] positions = new Point[(rx * 2 + 1) * (ry * 2 + 1)];
	    int index = 0;
	    Point point = new Point(0,0);
	    for (int yp = y - ry; yp <= y + ry; yp++) {
	        for (int xp = x - rx; xp <= x + rx; xp++) {
	            if (xp >= 0 && xp < w && yp >= 0 && yp < h) {
	                point.x = xp;
	                point.y = yp;
	                positions[index++] = point;
	            }
	        }
	    }
	    return positions;
	}

	public Tile[] getAreaTiles(int x, int y, int r) {
		return getAreaTiles(x, y, r, r);
	}

	public Tile[] getAreaTiles(int x, int y, int rx, int ry) {
		ArrayList<Tile> local = new ArrayList<>();

		for (Point point: getAreaTilePositions(x, y, rx, ry)) {
			local.add(getTile(point.x, point.y));
		}

		return local.toArray(new Tile[local.size()]);
	}

	public void setAreaTiles(int xt, int yt, int r, Tile tile, int data) {
		setAreaTiles(xt, yt, r, tile, data, false);
	}

	public void setAreaTiles(int xt, int yt, int r, Tile tile, int data, boolean overwriteStairs) {
		for (int y = yt - r; y <= yt + r; y++) {
			for (int x = xt - r; x <= xt + r; x++) {
				if (overwriteStairs || (!getTile(x, y).name.toLowerCase().contains("stairs"))) {
					setTile(x, y, tile, data);
				}
			}
		}
	}

	public void setAreaTiles(int xt, int yt, int r, Tile tile, int data, String[] blacklist) {
		for (int y = yt - r; y <= yt + r; y++) {
			for (int x = xt - r; x <= xt + r; x++) {
				if (!Arrays.asList(blacklist).contains(getTile(x, y).name.toLowerCase())) {
					setTile(x, y, tile, data);
				}
			}
		}
	}

	@FunctionalInterface
	public interface TileCheck {
		boolean check(Tile tile, int x, int y);
	}

	public List <Point> getMatchingTiles(Tile search) {
		return getMatchingTiles((t, x, y) -> t.equals(search));
	}

	public List <Point> getMatchingTiles(Tile...search) {
		return getMatchingTiles((t, x, y) -> {
			for (Tile poss: search) {
				if (t.equals(poss)) {
					return true;
				}
			}
			return false;
		});
	}

	public List <Point> getMatchingTiles(TileCheck condition) {
		List <Point> matches = new ArrayList<>();
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (condition.check(getTile(x, y), x, y)) {
					matches.add(new Point(x, y));
				}
			}
		}

		return matches;
	}

	public boolean isLight(int x, int y) {
		for (Tile tile: getAreaTiles(x, y, 3)) {
			if (tile instanceof TorchTile) {
				return true;
			}
		}

		for (Entity entity : getEntitiesInRect(entity -> entity instanceof Lantern, new Rectangle(x, y, 8, 8, Rectangle.CENTER_DIMS))) {
			int xx = (entity.x >> 4) - x;
			int yy = (entity.y >> 4) - y;
			int rr = entity.getLightRadius() - 1;
			if (((xx * xx) + (yy * yy)) < (rr * rr)) {
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("unused")
	private boolean noStairs(int x, int y) {
		return getTile(x, y) != Tiles.get("Stairs Down");
	}

	private void generateSummonStructures() {
		if (Game.debug) Logger.info("Generating summon dungeons ...");

		Statue statue = new Statue();
		int x3 = random.nextInt(16 * w) >> 4;
		int y3 = random.nextInt(16 * h) >> 4;
		if (getTile(x3, y3) == Tiles.get("Dirt")) {
			boolean xaxis2 = random.nextBoolean();

			if (xaxis2) {
				for (int s2 = x3; s2 < w - s2; s2++) {
					if (getTile(s2, y3) == Tiles.get("Rock")) {
						statue.x = (s2 << 4) - 24;
						statue.y = (y3 << 4) - 24;
					}
				}
			} else {
				for (int s2 = y3; s2 < y3 - s2; s2++) {
					if (getTile(x3, s2) == Tiles.get("Rock")) {
						statue.x = (x3 << 4) - 24;
						statue.y = (s2 << 4) - 24;
					}
				}
			}

			if (statue.x == 0 && statue.y == 0) {
				statue.x = (x3 << 4) - 8;
				statue.y = (y3 << 4) - 8;
			}

			if (getTile(statue.x >> 4, statue.y >> 4) == Tiles.get("Rock")) {
				setTile(statue.x >> 4, statue.y >> 4, Tiles.get("Dirt"));
			}

			Structure.summonAltar.draw(this, statue.x >> 4, statue.y >> 4);
			add(statue);
		}

	}

	private void generateSpawnerStructures() {
		if (Game.debug) Logger.info("Generating a spawner dungeons ...");

		for (int i = 0; i < 18 / -depth * (w / 128); i++) {

			/// for generating spawner dungeons
			MobAi mob;
			int r = random.nextInt(5);
			if (r == 1) {
				mob = new Skeleton(-depth);
			} else if (r == 2 || r == 0) {
				mob = new Slime(-depth);
			} else {
				mob = new Zombie(-depth);
			}

			Spawner spawner = new Spawner(mob);
			int x3 = random.nextInt(16 * w) >> 4;
			int y3 = random.nextInt(16 * h) >> 4;
			if (getTile(x3, y3) == Tiles.get("Dirt")) {
				boolean xaxis2 = random.nextBoolean();

				if (xaxis2) {
					for (int s2 = x3; s2 < w - s2; s2++) {
						if (getTile(s2, y3) == Tiles.get("Rock")) {
							spawner.x = (s2 << 4) - 24;
							spawner.y = (y3 << 4) - 24;
						}
					}
				} else {
					for (int s2 = y3; s2 < y3 - s2; s2++) {
						if (getTile(x3, s2) == Tiles.get("Rock")) {
							spawner.x = (x3 << 4) - 24;
							spawner.y = (s2 << 4) - 24;
						}
					}
				}

				if (spawner.x == 0 && spawner.y == 0) {
					spawner.x = (x3 << 4) - 8;
					spawner.y = (y3 << 4) - 8;
				}

				if (getTile(spawner.x >> 4, spawner.y >> 4) == Tiles.get("Rock")) {
					setTile(spawner.x >> 4, spawner.y >> 4, Tiles.get("Dirt"));
				}

				Structure.mobDungeonCenter.draw(this, spawner.x >> 4, spawner.y >> 4);

				if (getTile(spawner.x >> 4, (spawner.y >> 4) - 4) == Tiles.get("Dirt")) {
					Structure.mobDungeonNorth.draw(this, spawner.x >> 4, (spawner.y >> 4) - 5);
				}
				if (getTile(spawner.x >> 4, (spawner.y >> 4) + 4) == Tiles.get("Dirt")) {
					Structure.mobDungeonSouth.draw(this, spawner.x >> 4, (spawner.y >> 4) + 5);
				}
				if (getTile((spawner.x >> 4) + 4, spawner.y >> 4) == Tiles.get("Dirt")) {
					Structure.mobDungeonEast.draw(this, (spawner.x >> 4) + 5, spawner.y >> 4);
				}
				if (getTile((spawner.x >> 4) - 4, spawner.y >> 4) == Tiles.get("Dirt")) {
					Structure.mobDungeonWest.draw(this, (spawner.x >> 4) - 5, spawner.y >> 4);
				}

				add(spawner);
				for (int rpt = 0; rpt < 2; rpt++) {
					if (random.nextInt(2) != 0) {
						continue;
					}
					Chest chest = new Chest();
					int chance = -depth;

					chest.fillInventoryRandom("minidungeon", chance);

					add(chest, spawner.x - 16 + rpt * 32, spawner.y - 16);
				}
			}

		}
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------

	/*
	 * Generates the villages on the level
	 *
	 * Villages are somewhat limited in their generation...
	 *
	 * In this list shows the probabilities of generating villages according to the
	 * size of the world
	 *
	 * 128 x 128 = 0 - 1 can be generated 256 x 256 = 2 - 3 can be generated 512 x
	 * 512 = 3 - 4 can be generated 1024 x 1024 = 4 - 4 can be generated
	 *
	 * Villages can only be generated on grass, they may be able to generate in the
	 * middle of deserts .. but that is because the generation of the world is
	 * somewhat uneven, so there may be grass in a desert, and the village can be
	 * generated there
	 *
	 * Previously the villages had no paths .. the first attempt was to make them in
	 * the code .. but it was quite buggy :(, so I got bored and put them directly
	 * in the structures file (structure.java)
	 *
	 */

	private void generateVillages() {
	    int lastVillageX = 8;
	    int lastVillageY = 8;

	    if (Game.debug) Logger.info("Generating villages on surface");

	    // makes 2-3 villages based on world size
	    int numberOfVillages = 2 + random.nextInt(2);

	    for (int i = 0; i < numberOfVillages; i++) {
	        int x, y;
	        do {
	            x = random.nextInt(w);
	            y = random.nextInt(h);
	        } while (getTile(x, y) != Tiles.get("Grass") || (Math.abs(x - lastVillageX) <= 48 && Math.abs(y - lastVillageY) <= 48));

	        lastVillageX = x;
	        lastVillageY = y;

	        boolean hasCrops = random.nextBoolean();

	        // xOffset and yOffset
	        int xo = random.nextInt(8) - 8;
	        int yo = random.nextInt(8) - 8;

	        if (getTile(x, y) != Tiles.get("Rock") || getTile(x, y) != Tiles.get("Up Rock")) {
		        if (hasCrops) {
		            Structure.villageCrops.draw(this, x + xo, y + yo);
		            add(new Cleric(), (x + xo) << 4, (y + yo) << 4);
		            add(new Librarian(), (x + xo) << 4, (y + yo) << 4);
                } else {
		            Structure.villageNormal.draw(this, x + xo, y + yo);
		            add(new Librarian(), (x + xo) << 4, (y + yo) << 4);
		            add(new Cleric(), (x + xo) << 4, (y + yo) << 4);
                }
                add(new Librarian(), (x + xo + 5) << 4, (y + yo - 5) << 4);
                add(new Cleric(), (x + xo - 5) << 4, (y + yo + 4) << 4);

                Chest firstChest = new Chest();
		    	Chest secondChest = new Chest();
		    	firstChest.fillInventoryRandom("villagehouse", random.nextInt(10));
		    	secondChest.fillInventoryRandom("villagehouse", random.nextInt(10));
		    	add(firstChest, (x + xo + 5) << 4, (y + yo - 6) << 4); // up
		    	add(secondChest, (x + xo - 5) << 4, (y + yo + 4) << 4); // down
		    }
	    }
	}


	private void playRandomMusic(int depth) {
	    Map<Integer, String[]> soundMap = new HashMap<>();

	    soundMap.put(1, new String[] {"musicTheme1", "musicTheme2", "musicTheme3"}); // Sky {fall, surface, paradise}
	    soundMap.put(0, new String[] {"musicTheme2", "musicTheme4"}); // Surface {surface, peaceful}
	    soundMap.put(-1, new String[] {"musicTheme5", "musicTheme8", "caveMood", "caveBreath", "caveScream"}); // Caves {cave, deeper, ...}
	    soundMap.put(-2, new String[] {"musicTheme5", "musicTheme6", "musicTheme8"}); // Caverns {cave, cavern, dripping, deeper}


	    if (soundMap.containsKey(depth)) {
	        String[] sounds = soundMap.get(depth);

	        int randomNum = random.nextInt(sounds.length);

	        if (World.currentMusicTheme != null) {
	        	Sound.stop(World.currentMusicTheme );

	        	// if the played theme is equals to the new theme, we try again
		        while (Objects.equals(sounds[randomNum], World.currentMusicTheme)) {
		        	randomNum = random.nextInt(sounds.length);
		        }
	        }

	        Sound.play(sounds[randomNum]);
	        World.currentMusicTheme  = sounds[randomNum];
	    }
	}


	// --------------------------------------------------------------------------------------------------------------------------------------------------

	public String toString() {
		return "Level(depth=" + depth + ")";
	}
}
