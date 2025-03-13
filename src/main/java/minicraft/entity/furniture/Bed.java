package minicraft.entity.furniture;

import minicraft.core.Game;
import minicraft.core.Updater;
import minicraft.entity.mob.Player;
import minicraft.graphic.Sprite;
import minicraft.level.Level;

import java.util.HashMap;

public class Bed extends Furniture {

	private static int playersAwake = 1;
	private static final HashMap<Player, Bed> sleepingPlayers = new HashMap<>();

	/**
	 * Creates a new furniture with the name Bed and the bed sprite and color.
	 */
	public Bed() {
		super("Bed", new Sprite(28, 30, 2, 2, 2), 3, 2);
	}

	/** Called when the player attempts to get in bed. */
	@Override
	public boolean use(Player player) {
		if (checkCanSleep(player)) { // if it is late enough in the day to sleep...

			// set the player spawn coord. to their current position, in tile coords (hence " >> 4")
			player.spawnx = player.x >> 4;
			player.spawny = player.y >> 4;

			sleepingPlayers.put(player, this);
			player.remove();

			playersAwake = 0;
		}
		return true;
	}


	public static boolean checkCanSleep(Player player) {
		if (inBed(player)) return false;

		if (!(Updater.tickCount >= Updater.sleepStartTime || Updater.tickCount < Updater.sleepEndTime && Updater.pastFirstDay)) {
			Game.notifications.add("You can only sleep at night!");
			return false;
		}
		return true;
	}

	public static boolean sleeping() {
		return playersAwake == 0;
	}

	public static boolean inBed(Player player) {
		return sleepingPlayers.containsKey(player);
	}

	public static Level getBedLevel(Player player) {
		Bed bed = sleepingPlayers.get(player);
		if (bed == null) return null;
		return bed.getLevel();
	}

	// get the player "out of bed"; used on the client only.
	public static void removePlayer(Player player) {
		sleepingPlayers.remove(player);
	}

	public static void removePlayers() {
		sleepingPlayers.clear();
	}

	// client should not call this.
	public static void restorePlayer(Player player) {
		Bed bed = sleepingPlayers.remove(player);
		if (bed != null) {
			if (bed.getLevel() == null) {
				Game.levels[Game.currentLevel].add(player);
			} else {
				bed.getLevel().add(player);
			}

			playersAwake = 1;
		}
	}

	// client should not call this.
	public static void restorePlayers() {
		for (Player player : sleepingPlayers.keySet()) {
			Bed bed = sleepingPlayers.get(player);
			bed.getLevel().add(player);
		}

		sleepingPlayers.clear();
		playersAwake = 1;
	}
}
