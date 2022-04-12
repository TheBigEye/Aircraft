package minicraft.core;

import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

import minicraft.core.io.Settings;
import minicraft.entity.furniture.Bed;
import minicraft.entity.mob.Player;
import minicraft.level.Level;
import minicraft.saveload.Load;
import minicraft.screen.AchievementsDisplay;
import minicraft.screen.LoadingDisplay;
import minicraft.screen.PlayerDeathDisplay;
import minicraft.screen.WorldGenDisplay;
import minicraft.screen.WorldSelectDisplay;

public class World extends Game {
	private World() {
	}

	/// This is to map the level depths to each level's
	/// index in Game's levels array. This must ALWAYS
	/// be the same length as the levels array, of
	/// course.
	public static final int[] idxToDepth = { -3, -2, -1, 0, 1, -4 };
											// 0   1   2  3  4  5

	public static final int minLevelDepth, maxLevelDepth;
	static {
		int min, max;
		min = max = idxToDepth[0];

		for (int depth : idxToDepth) {
			if (depth < min) {
				min = depth;
			}
			if (depth > max) {
				max = depth;
			}
		}

		minLevelDepth = min;
		maxLevelDepth = max;
	}

	// Notes:
	// 1 is aether - 0
	// 0 is surface - 1
	// -1 is iron - 2
	// -2 is gold - 3
	// -3 is lava - 4
	// -4 is the dungeon - 5
	// then.. -5 is the hell, yhay 6

	static int worldSize = 128; // The size of the world
	public static int lvlw = worldSize; // The width of the world
	public static int lvlh = worldSize; // The height of the world

	static int playerDeadTime; // the time after you die before the dead menu shows up.
	static int pendingLevelChange; // used to determine if the player should change levels or not.

	@Nullable
	public static Action onChangeAction; // allows action to be stored during a change schedule that should only occur once the screen is blacked out.

	/// SCORE MODE

	/**
	 * This is for a contained way to find the index in the levels array of a level,
	 * based on it's depth. This is also helpful because add a new level in the
	 * future could change this.
	 */
	public static int lvlIdx(int depth) {
		if (depth > maxLevelDepth) return lvlIdx(minLevelDepth);
		if (depth < minLevelDepth) return lvlIdx(maxLevelDepth);

		if (depth == -4) return 5;

		return depth + 3;
	}

	/**
	 * This method is used when respawning, and by initWorld to reset the vars. It
	 * does not generate any new terrain.
	 */
	public static void resetGame() {
		resetGame(true);
	}

	public static void resetGame(boolean keepPlayer) {
		Logger.debug("Resetting...");
		playerDeadTime = 0;
		currentLevel = 3;
		Updater.asTick = 0;
		Updater.notifications.clear();

		if (keepPlayer) {
			player = new Player(player, input);
		} else {
			player = new Player(null, input);
		}

		if (levels[currentLevel] == null)
			return;

		// "shouldRespawn" is false on hardcore, or when making a new world.
		if (PlayerDeathDisplay.shouldRespawn) { // respawn, don't regenerate level.
			Level level = levels[currentLevel];
			player.respawn(level);
			level.add(player); // Adds the player to the current level (always surface here)
		}
	}

	/**
	 * This method is used to create a brand new world, or to load an existing one
	 * from a file. For the loading screen updates to work, it it assumed that
	 * *this* is called by a thread *other* than the one rendering the current
	 * *menu*.
	 **/
	public static void initWorld() { // this is a full reset; everything.
		Logger.debug("Resetting world...");

		PlayerDeathDisplay.shouldRespawn = false;
		resetGame();
		player = new Player(null, input);
		Bed.removePlayers();
		Updater.gameTime = 0;
		Updater.gamespeed = 1;

		// resets tickCount; game starts in the day, so that it's nice and bright.
		Updater.changeTimeOfDay(Updater.Time.Morning);
		gameOver = false;

		levels = new Level[6];

		Updater.scoreTime = (Integer) Settings.get("scoretime") * 60 * Updater.normSpeed;
		LoadingDisplay.setPercentage(0); // this actually isn't necessary, I think; it's just in case.
		Logger.trace("Initializing world non-client...");

		if (WorldSelectDisplay.loadedWorld()) {
			Load loader = new Load(WorldSelectDisplay.getWorldName());
		} else {
			worldSize = (Integer) Settings.get("size");

			float loadingInc = 100f / (maxLevelDepth - minLevelDepth + 1); // The .002 is for floating point errors, in case they occur.
			for (int i = maxLevelDepth; i >= minLevelDepth; i--) {
				// i = level depth; the array starts from the top because the parent level is used as a reference, so it should be constructed first. It is expected that the highest level will have a null parent.

				Logger.trace("Loading level " + i + "...");

				LoadingDisplay.setMessage(Level.getDepthString(i));
				levels[lvlIdx(i)] = new Level(worldSize, worldSize, WorldGenDisplay.getSeed(), i, levels[lvlIdx(i+1)], !WorldSelectDisplay.loadedWorld());

				LoadingDisplay.progress(loadingInc);
			}

			Logger.trace("Level loading complete.");

			Level level = levels[currentLevel]; // Sets level to the current level (3; surface)
			Updater.pastDay1 = false;
			player.findStartPos(level, WorldGenDisplay.getSeed()); // Finds the start level for the player
			level.add(player);
		}

		Renderer.readyToRenderGameplay = true;
		PlayerDeathDisplay.shouldRespawn = true;
		Logger.trace("World initialized.");
	}

	/**
	 * This method is called when you interact with stairs, this will give you the
	 * transition effect. While changeLevel(int) just changes the level.
	 */
	public static void scheduleLevelChange(int dir) {
		scheduleLevelChange(dir, null);
	}

	public static void scheduleLevelChange(int dir, @Nullable Action changeAction) {
		onChangeAction = changeAction;
		pendingLevelChange = dir;
	}

	/**
	 * This method changes the level that the player is currently on. It takes 1
	 * integer variable, which is used to tell the game which direction to go. For
	 * example, 'changeLevel(1)' will make you go up a level, while
	 * 'changeLevel(-1)' will make you go down a level.
	 */
	public static void changeLevel(int dir) {
		if (onChangeAction != null) {
			onChangeAction.act();
			onChangeAction = null;
		}

		levels[currentLevel].remove(player); // Removes the player from the current level.

		int nextLevel = currentLevel + dir;
		if (nextLevel <= -1) {
			// fix accidental level underflow
			nextLevel = levels.length - 1; 
		}
		if (nextLevel >= levels.length) {
			// fix accidental level overflow
			nextLevel = 0; 
		}

		Logger.trace("Setting level from {} to {}", currentLevel, nextLevel);
		currentLevel = nextLevel;

		player.x = (player.x >> 4) * 16 + 8; // sets the player's x coord (to center yourself on the stairs)
		player.y = (player.y >> 4) * 16 + 8; // sets the player's y coord (to center yourself on the stairs)

		levels[currentLevel].add(player); // Adds the player to the level.

		if (currentLevel == 0) {
			AchievementsDisplay.setAchievement("minicraft.achievement.lowest_caves", true);
		}
	}
}
