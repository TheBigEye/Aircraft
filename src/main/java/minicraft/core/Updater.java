package minicraft.core;

import minicraft.core.io.Localization;
import minicraft.core.io.Screenshot;
import minicraft.core.io.Settings;
import minicraft.entity.furniture.Bed;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.saveload.Save;
import minicraft.screen.CreditsDisplay;
import minicraft.screen.EndGameDisplay;
import minicraft.screen.LevelTransitionDisplay;
import minicraft.screen.PlayerDeathDisplay;
import minicraft.screen.WorldSelectDisplay;
import org.tinylog.Logger;

import java.awt.*;

/*
* Game updater
*/
public class Updater extends Game {
	private Updater() {}

	/// TIME AND TICKS
	public static final int normalSpeed = 60; // Measured in ticks / second.
	public static float gameSpeed = 1.00f; // Measured in MULTIPLES OF NORMSPEED.
	public static boolean paused = true; // If the game is paused.

	public static int tickCount = 0; // The number of ticks since the beginning of the game day.
	static int time = 0; // Facilities time of day / sunlight.

	public static final int dayLength = 64800; // This value determines how long one game day is.
	public static final int sleepEndTime = dayLength / 8; // This value determines when the player "wakes up" in the morning.
	public static final int sleepStartTime = dayLength / 2 + dayLength / 8; // This value determines when the player allowed to sleep.
	// public static int noon = 32400; // This value determines when the sky switches from getting lighter to getting darker.

	public static int gameTime = 0; // This stores the total time (number of ticks) you've been playing your
	public static boolean pastFirstDay = true; // Used to prevent mob spawn on surface on day 1.
	public static int scoreTime; // Time remaining for score mode

	/**
	 * Indicates if FullScreen Mode has been toggled.
	 */
	static boolean FULLSCREEN;

	/// AUTOSAVE AND NOTIFICATIONS
	public static int notetick = 0; // "note"= notifications.

	private static final int astime = 7200; // Stands for Auto-Save Time (interval)
	public static int asTick = 0; // The time interval between autosaves.
	public static boolean saving = false; // If the game is performing a save.
	public static int savecooldown; // Prevents saving many times too fast, I think.

	public enum Time {
		Morning(0),
		Day(dayLength / 4),
		Evening(dayLength / 2),
		Night(dayLength / 4 * 3);

		public final int tickTime;

		Time(int ticks) {
			tickTime = ticks;
		}
	}

	static void updateFullscreen() {
		// Dispose is needed to set undecorated value
		Initializer.frame.dispose();

		GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];

		if (Updater.FULLSCREEN) {
			Initializer.frame.setUndecorated(true);
			Initializer.frame.setResizable(true);
			Initializer.frame.setVisible(true);
			device.setFullScreenWindow(Initializer.frame);
		} else {
			device.setFullScreenWindow(null);
			Initializer.frame.setUndecorated(false);
			Initializer.frame.setResizable(true);
			Initializer.frame.setVisible(true);
		}

		// Show frame again
		Initializer.frame.setVisible(true);

		// When fullscreen is enabled, focus is lost
		Renderer.canvas.requestFocus();
	}

	// VERY IMPORTANT METHOD!! Makes everything keep happening.
	// In the end, calls menu.tick() if there's a menu, or level.tick() if no menu.
	public static void tick() {

		if (isMode("Creative") && input.getKey("SHIFT-W").clicked ) Game.setDisplay(new LevelTransitionDisplay(1));
		if (isMode("Creative") && input.getKey("SHIFT-S").clicked ) Game.setDisplay(new LevelTransitionDisplay(-1));

		if (input.getKey("FULLSCREEN").clicked) {
			Updater.FULLSCREEN = !Updater.FULLSCREEN;
			Updater.updateFullscreen();
		}

		if (newDisplay != display) {
			if (display != null && (newDisplay == null || newDisplay.getParent() != display)) {
				display.onExit();
			}
			if (newDisplay != null && (display == null || newDisplay != display.getParent())) {
				newDisplay.init(display);
			}

			display = newDisplay;
		}

		Level level = levels[currentLevel];
		if (Bed.sleeping()) {
			// IN BED
			if (gameSpeed != 20) {
				gameSpeed = 20;
			}

			if (tickCount > sleepEndTime) {
				Logger.trace("Passing midnight in bed.");
				pastFirstDay = true;
				tickCount = 0;
			}

			if ((tickCount <= sleepStartTime && tickCount > sleepEndTime) || input.getKey("exit").clicked) { // it has reached morning.
				Logger.trace("Getting out of bed.");
				gameSpeed = 1.00f;
				Bed.restorePlayers();
			}
		}

		// auto-save tick; marks when to do autosave.
		if (!paused) {
			asTick++;
		}

		if (asTick > astime) {
			if (Settings.getBoolean("autosave") && !gameOver && player.health > 0) {
				new Save(WorldSelectDisplay.getWorldName());
			}

			asTick = 0;
		}

		// Increment tickCount if the game is not paused
		if (!paused) {
			setTime(tickCount + 1);
		}

		/// SCORE MODE ONLY
		if (isMode("score") && (!paused && !gameOver)) {
			if (scoreTime <= 0) { // GAME OVER
				gameOver = true;
				setDisplay(new EndGameDisplay());
			}

			scoreTime--;
		}


		// This is the general action statement thing! Regulates menus, mostly.
		if (!Renderer.canvas.hasFocus()) {
			input.releaseAll();
		}

		if (Renderer.canvas.hasFocus() || display instanceof CreditsDisplay) {
			gameTime++;

			input.tick(); // INPUT TICK; no other class should call this, I think...especially the *Menu classes.

			if (display != null) {
				// a menu is active.
				if (player != null) {
					// it is CRUCIAL that the player is ticked HERE, before the menu is ticked. I'm
					// not quite sure why... the menus break otherwise, though.
					player.tick();
				}
				display.tick(input);
				paused = true;

			} else {
				// no menu, currently.
				paused = false;

				// If player is alive, but no level change, nothing happens here.
				if (player.isRemoved() && Renderer.readyToRenderGameplay && !Bed.inBed(player)) {
					// Makes delay between death and death menu.
					World.playerDeadTime++;
					if (World.playerDeadTime > 60) {
						setDisplay(new PlayerDeathDisplay());
						World.playerDeadTime = 0;
					}

				} else if (World.pendingLevelChange != 0) {
					setDisplay(new LevelTransitionDisplay(World.pendingLevelChange));
					World.pendingLevelChange = 0;
				}

				player.tick(); // Ticks the player when there's no menu.

				if (level != null) {
					level.tick(true);
					Tile.tickCount++;
				}

				if (input.getKey("F3").clicked) { // shows debug info in upper-left
					Renderer.showDebugInfo = !Renderer.showDebugInfo;
				}

				if (input.getKey("screenshot").clicked) { // takes an screenshot
					Screenshot.take(Renderer.image);
				}

				// for debugging only
				/*if (debug) {
					if (input.getKey("ctrl-p").clicked) {
						// print all players on all levels, and their coordinates.
						Logger.debug("Printing players on all levels ...");
						for (Level value : levels) {
							if (value == null) continue;
							value.printEntityLocs(Player.class);
						}
					}

					// Host-only cheats.
					if (input.getKey("Shift-r").clicked) World.initWorld(); // For single-player use only.

					if (input.getKey("1").clicked) changeTimeOfDay(Time.Morning);
					if (input.getKey("2").clicked) changeTimeOfDay(Time.Day);
					if (input.getKey("3").clicked) changeTimeOfDay(Time.Evening);
					if (input.getKey("4").clicked) changeTimeOfDay(Time.Night);

					if (input.getKey("SHIFT-T").clicked) Settings.set("mode", "score");

					if (isMode("score") && input.getKey("CTRL-T").clicked) {
						scoreTime = normalSpeed * 5; // 5 seconds
					}

					if (input.getKey("SHIFT-0").clicked) {
						gameSpeed = 1;
                    }

					if (input.getKey("shift-equals").clicked) {
						if (gameSpeed < 1) gameSpeed *= 2;
						else if (normalSpeed * gameSpeed < 2000) gameSpeed++;
					}
					if (input.getKey("shift-minus").clicked) {
						if (gameSpeed > 1) gameSpeed--;
						else if (normalSpeed * gameSpeed > 5) gameSpeed /= 2;
					}

					// Client-only cheats, since they are player-specific.
					if (input.getKey("shift-g").clicked) // This should not be needed, since the inventory should not be altered.
						Items.fillCreativeInventory(player.getInventory());

					if (input.getKey("ctrl-h").clicked) player.health--;
					if (input.getKey("ctrl-b").clicked) player.hunger--;

					if (input.getKey("0").clicked) player.moveSpeed = 1;
					if (input.getKey("equals").clicked) player.moveSpeed++;
					if (input.getKey("minus").clicked && player.moveSpeed > 1) player.moveSpeed--; // -= 0.5D;

					if (input.getKey("shift-u").clicked) {
						levels[currentLevel].setTile(player.x >> 4, player.y >> 4, Tiles.get("Stairs Up"));
					}
					if (input.getKey("shift-d").clicked) {
						levels[currentLevel].setTile(player.x >> 4, player.y >> 4, Tiles.get("Stairs Down"));
					}

				} // end debug only cond.
				*/

			} // end "menu-null" conditional
		} // end hasfocus conditional
	} // end tick()

	/// this is the proper way to change the tickCount.
	public static void setTime(int ticks) {
		if (ticks < Time.Morning.tickTime) ticks = 0; // error correct
		if (ticks < Time.Day.tickTime) time = 0; // morning
		else if (ticks < Time.Evening.tickTime) time = 1; // day
		else if (ticks < Time.Night.tickTime) time = 2; // evening
		else if (ticks < dayLength) time = 3; // night
		else { // back to morning
			time = 0;
			ticks = 0;
			pastFirstDay = true;
		}
		tickCount = ticks;
	}

	/// this is the proper way to change the time of day.
	public static void changeTimeOfDay(Time t) {
		setTime(t.tickTime);
	}

	// this one works too.
	public static void changeTimeOfDay(int timeOfDay) {
		Time[] times = Time.values();
		if (timeOfDay > 0 && timeOfDay < times.length) {
			changeTimeOfDay(times[timeOfDay]); // it just references the other one.
		} else {
			Logger.warn("Time {} does not exist", timeOfDay);
		}
	}

	public static Time getTime() {
		Time[] times = Time.values();
		return times[time];
	}

	/** This adds a notification to all player games. */
	public static void notifyAll(String msg) {
		notifyAll(msg, 0);
	}

	public static void notifyAll(String msg, int notetick) {
		msg = Localization.getLocalized(msg);
		notifications.add(msg);
		Updater.notetick = notetick;
	}
}
