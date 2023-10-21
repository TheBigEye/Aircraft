package minicraft.saveload;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.tinylog.Logger;

import minicraft.core.Game;
import minicraft.core.Renderer;
import minicraft.core.Updater;
import minicraft.core.World;
import minicraft.core.io.Localization;
import minicraft.core.io.Settings;
import minicraft.entity.Arrow;
import minicraft.entity.Entity;
import minicraft.entity.Fireball;
import minicraft.entity.ItemEntity;
import minicraft.entity.Spark;
import minicraft.entity.furniture.Chest;
import minicraft.entity.furniture.Crafter;
import minicraft.entity.furniture.DeathChest;
import minicraft.entity.furniture.DungeonChest;
import minicraft.entity.furniture.Lantern;
import minicraft.entity.furniture.Spawner;
import minicraft.entity.furniture.Statue;
import minicraft.entity.mob.AirWizard;
import minicraft.entity.mob.EnemyMob;
import minicraft.entity.mob.EyeQueen;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.entity.mob.Sheep;
import minicraft.entity.particle.Particle;
import minicraft.entity.particle.TextParticle;
import minicraft.item.Inventory;
import minicraft.item.Item;
import minicraft.item.PotionType;
import minicraft.level.Level;
import minicraft.screen.AchievementsDisplay;
import minicraft.screen.LoadingDisplay;
import minicraft.screen.MultiplayerDisplay;
import minicraft.screen.WorldSelectDisplay;

public class Save {

	public String location = Game.gameDir;
	File folder;

	// Used to indent the .json files
	private static final int indent = 4;

	// Save files extension
	public static final String worldExtension = ".level";
	public static final String dataExtension = ".data";
	public static final String saveExtension = ".dat";
	public static final String mapExtension = ".map";
	
	public static final String oldExtension = ".miniplussave";

	List<String> data;

	/**
	 * This is the main save method. Called by all Save() methods.
	 *
	 * @param worldFolder The folder of where to save
	 */
	private Save(File worldFolder) {
		data = new ArrayList<>();

		if (worldFolder.getParent().equals("saves")) {
			String worldName = worldFolder.getName();
			if (!worldName.equals(worldName)) {

				Logger.debug("Renaming world in \"{}\" to lowercase ...", worldFolder);
				String path = worldFolder.toString();
				path = path.substring(0, path.lastIndexOf(worldName));
				File newFolder = new File(path + worldName);

				if (worldFolder.renameTo(newFolder)) {
					worldFolder = newFolder;
				} else {
					Logger.error("Failed to rename world folder \"{}\" to \"{}\"", worldFolder, newFolder);
				}
			}
		}

		folder = worldFolder;
		location = worldFolder.getPath() + "/";

		folder.mkdirs();
	}

	/**
	 * This will save world options
	 *
	 * @param worldname The name of the world.
	 */
	public Save(String worldname) {

		this(new File(Game.gameDir + "/saves/" + worldname + "/"));

		writeGame("Game");
		writeWorld("Level");
		writePlayer("Player", Game.player);
		writeInventory("Inventory", Game.player);
		writeEntities("Entities");

		WorldSelectDisplay.updateWorlds();

		Updater.notifyAll("World Saved!");
		Updater.asTick = 0;
		Updater.saving = false;
	}

	/** This will save the settings in the settings menu. */
	public Save() {
		this(new File(Game.gameDir + "/"));

		Logger.debug("Writing preferences and unlocks ...");

		writePrefs();
		writeUnlocks();
	}

	public Save(Player player, boolean writePlayer) {
		// This is simply for access to writeToFile.
		this(new File(Game.gameDir + "/saves/" + WorldSelectDisplay.getWorldName() + "/"));
		if (writePlayer) {
			writePlayer("Player", player);
			writeInventory("Inventory", player);
		}
	}

	public static void writeFile(String filename, String[] lines) throws IOException {
		try (BufferedWriter fileBuffer = new BufferedWriter(new FileWriter(filename))) {
			fileBuffer.write(String.join(System.lineSeparator(), lines));
		}
	}

	public void writeToFile(String filename, List<String> savedata) {
		try {
			writeToFile(filename, savedata.toArray(new String[0]), true);
		} catch (IOException exception) {
			exception.printStackTrace();
		}

		data.clear();

		LoadingDisplay.progress(7);
		if (LoadingDisplay.getPercentage() > 100) {
			LoadingDisplay.setPercentage(100);
		}

		Renderer.render(); // AH HA!!! HERE'S AN IMPORTANT STATEMENT!!!!
	}

	public static void writeToFile(String filename, String[] savedata, boolean isWorldSave) throws IOException {
		try (BufferedWriter fileBuffer = new BufferedWriter(new FileWriter(filename))) {
			for (int i = 0; i < savedata.length; i++) {
				fileBuffer.write(savedata[i]);
				if (isWorldSave) {
					fileBuffer.write(",");
					if (filename.contains("Level5") && i == savedata.length - 1) {
						fileBuffer.write(",");
					}
				} else {
					fileBuffer.write("\n");
				}
			}
		}
	}

	public static void writeJSONToFile(String filename, String json) throws IOException {
		try (BufferedWriter fileBuffer = new BufferedWriter(new FileWriter(filename))) {
			fileBuffer.write(json);
		}
	}

	private void writeGame(String filename) {
		data.add(String.valueOf(Game.VERSION));
        data.add(String.valueOf(World.getWorldSeed()));
		data.add(Settings.getIndex("mode") + (Game.isMode("score") ? ";" + Updater.scoreTime + ";" + Settings.get("scoretime") : ""));
		data.add(String.valueOf(Updater.tickCount));
		data.add(String.valueOf(Updater.gameTime));
		data.add(String.valueOf(Settings.getIndex("diff")));
		data.add(String.valueOf(AirWizard.beaten));
		data.add(String.valueOf(EyeQueen.beaten));
		data.add(String.valueOf(Settings.get("cheats")));

		writeToFile(location + filename + saveExtension, data);
	}

	private void writePrefs() {
		JSONObject json = new JSONObject();

		json.put("version", String.valueOf(Game.VERSION));
		json.put("diff", Settings.get("diff"));
		json.put("sound", String.valueOf(Settings.get("sound")));
		json.put("autosave", String.valueOf(Settings.get("autosave")));
		
		json.put("fps", String.valueOf(Settings.get("fps")));
        json.put("vsync", String.valueOf(Settings.get("vsync")));
        json.put("bossbar", Settings.get("bossbar"));
        json.put("particles", String.valueOf(Settings.get("particles")));
        json.put("shadows", String.valueOf(Settings.get("shadows")));
        
		json.put("lang", Localization.getSelectedLanguage());
		
		json.put("savedIP", MultiplayerDisplay.savedIP);
		json.put("savedUUID", MultiplayerDisplay.savedUUID);
		json.put("savedUsername", MultiplayerDisplay.savedUsername);

        
		json.put("keymap", new JSONArray(Game.input.getKeyPrefs()));

		// Save preferences to json file
		try {
			writeJSONToFile(location + "Preferences.json", json.toString(indent));
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	private void writeUnlocks() {
		JSONObject json = new JSONObject();

		json.put("unlockedAirWizardSuit", Settings.getBoolean("unlockedskin"));

		JSONArray scoretimes = new JSONArray();

		if (Settings.getEntry("scoretime").getValueVisibility(10)) {
			scoretimes.put(10);
		}
		if (Settings.getEntry("scoretime").getValueVisibility(120)) {
			scoretimes.put(120);
		}

		json.put("visibleScoreTimes", scoretimes);
		json.put("unlockedAchievements", new JSONArray(AchievementsDisplay.getUnlockedAchievements()));

		// Save unlocks to json file
		try {
			writeJSONToFile(location + "Unlocks.json", json.toString(indent));
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	private void writeWorld(String filename) {
	    // set the message to display while loading
	    LoadingDisplay.setProgressType("Levels");

	    String worldSize = String.valueOf(Settings.get("size"));

	    // loop through all levels in World.levels array
	    for (int currentLevel = 0; currentLevel < World.levels.length; currentLevel++) {
	        // add world size and level seed to data array
	        data.add(worldSize);
	        data.add(worldSize);
	        data.add(Long.toString(World.levels[currentLevel].getSeed()));
	        data.add(String.valueOf(World.levels[currentLevel].depth));

	        // loop through each tile in the level and add the tile name to data array
	        for (int x = 0; x < World.levels[currentLevel].w; x++) {
	            for (int y = 0; y < World.levels[currentLevel].h; y++) {
	                data.add(String.valueOf(World.levels[currentLevel].getTile(x, y).name));
	            }
	        }
	        
	        // write the data array to file
	        writeToFile(location + filename + currentLevel + worldExtension, data);
	        // clear the data array for next level
	        data.clear();
	    }

	    // loop through all levels in World.levels array
	    for (int currentLevel = 0; currentLevel < World.levels.length; currentLevel++) {
	        // loop through each tile in the level and add the tile data to data array
	        for (int x = 0; x < World.levels[currentLevel].w; x++) {
	            for (int y = 0; y < World.levels[currentLevel].h; y++) {
	                data.add(String.valueOf(World.levels[currentLevel].getData(x, y)));
	            }
	        }
	        // write the data array to file
	        writeToFile(location + filename + currentLevel + dataExtension, data);
	        // clear the data array for next level
	        data.clear();
	    }
	    
	    for (int currentLevel = 0; currentLevel < World.levels.length; currentLevel++) {
	        Level currentLevelObj = World.levels[currentLevel];

	        // Serialize the explored array to a byte array
	        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
	            objectOutputStream.writeObject(currentLevelObj.explored);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        // Convert the byte array to a base64-encoded string
	        String exploredData = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
	        
	        // Add the serialized and encoded explored data to the data array
	        data.add(exploredData);
	        
	        // write the data array to file
	        writeToFile(location + filename + currentLevel + mapExtension, data);
	        // clear the data array for the next level
	        data.clear();
	    }
	}

	private void writePlayer(String filename, Player player) {
		LoadingDisplay.setProgressType("Player");
		writePlayer(player, data);
		writeToFile(location + filename + saveExtension, data);
	}

	public static void writePlayer(Player player, List<String> data) {
		data.clear();
		data.add(String.valueOf(player.x));
		data.add(String.valueOf(player.y));
		data.add(String.valueOf(player.spawnx));
		data.add(String.valueOf(player.spawny));
		data.add(String.valueOf(player.health));
		data.add(String.valueOf(player.hunger));
		data.add(String.valueOf(player.armor));
		data.add(String.valueOf(player.armorDamageBuffer));
		data.add(String.valueOf(player.currentArmor == null ? "NULL" : player.currentArmor.getName()));
		data.add(String.valueOf(player.getScore()));
		data.add(String.valueOf(Game.currentLevel));

		StringBuilder subdata = new StringBuilder("PotionEffects[");

		for (Map.Entry<PotionType, Integer> potion : player.potionEffects.entrySet()) {
			subdata.append(potion.getKey()).append(";").append(potion.getValue()).append(":");
		}

		// Cuts off extra ":" and appends "]"
		if (player.potionEffects.size() > 0) {
			subdata = new StringBuilder(subdata.substring(0, subdata.length() - (1)) + "]");
		} else {
			subdata.append("]");
		}

		data.add(subdata.toString());

		data.add(String.valueOf(player.shirtColor));
		data.add(String.valueOf(player.suitOn));
        
        data.add(String.valueOf(player.nightCount));
        data.add(String.valueOf(player.isNiceNight));
	}

	private void writeInventory(String filename, Player player) {
		writeInventory(player, data);
		writeToFile(location + filename + saveExtension, data);
	}

	public static void writeInventory(Player player, List<String> data) {
		data.clear();

		if (player.activeItem != null) {
			data.add(player.activeItem.getData());
		}

		Inventory playerInventory = player.getInventory();

		for (int itemIndex = 0; itemIndex < playerInventory.size(); itemIndex++) {
			data.add(playerInventory.get(itemIndex).getData());
		}
	}

	private void writeEntities(String filename) {
		LoadingDisplay.setProgressType("Entities");
		for (int currentLevel = 0; currentLevel < World.levels.length; currentLevel++) { // Iterate through each world level
			for (Entity entity : World.levels[currentLevel].getEntitiesToSave()) { // Gets the entities of the current level to be saved
				String savedEntity = writeEntity(entity, true);
				if (savedEntity.length() > 0) {
					data.add(savedEntity);
				}
			}
		}

		writeToFile(location + filename + saveExtension, data);
	}

	public static String writeEntity(Entity entity, boolean isLocalSave) {
		String entityName = entity.getClass().getName();
		entityName = entityName.substring(entityName.lastIndexOf('.') + 1);
		StringBuilder extradata = new StringBuilder();

		// Don't even write ItemEntities or particle effects; Spark... will probably is saved, eventually;
		// it presents an unfair cheat to remove the sparks by reloading the Game.

		// TODO I don't want to, but there are complications.
		// If (e instanceof Particle) return "";

		// wirte these only when sending a world, not writing
		if (isLocalSave && (entity instanceof ItemEntity || entity instanceof Arrow || entity instanceof Spark || entity instanceof Particle)) {
			return "";
		}

		if (!isLocalSave) {
			extradata.append(":").append(entity.eid);
		}

		if (entity instanceof Mob) {
			Mob mob = (Mob) entity;
			extradata.append(":").append(mob.health);

			if (entity instanceof EnemyMob) {
				extradata.append(":").append(((EnemyMob) mob).lvl);
			}

			// Saves if the sheep is cut. If not, we could reload the save and the wool would regenerate.
			if (entity instanceof Sheep) {
				extradata.append(":").append(((Sheep) mob).sheared);
			}
		}

		if (entity instanceof Chest) {
			Chest chest = (Chest) entity;
			
			Inventory chestInventory = chest.getInventory();

			for (int itemIndex = 0; itemIndex < chestInventory.size(); itemIndex++) {
				Item item = chestInventory.get(itemIndex);
				extradata.append(":").append(item.getData());
			}

			if (chest instanceof DeathChest) extradata.append(":").append(((DeathChest) chest).time);
			if (chest instanceof DungeonChest) extradata.append(":").append(((DungeonChest) chest).isLocked());
		}

		if (entity instanceof Spawner) {
			Spawner spawner = (Spawner) entity;
			
			String mobName = spawner.mob.getClass().getName();
			mobName = mobName.substring(mobName.lastIndexOf(".") + 1);
			
			extradata.append(":").append(mobName).append(":").append(spawner.mob instanceof EnemyMob ? ((EnemyMob) spawner.mob).lvl : 1);
		}

		if (entity instanceof Lantern) {
			extradata.append(":").append(((Lantern) entity).type.ordinal());
		}

		if (entity instanceof Crafter) {
			entityName = ((Crafter) entity).type.name();
		}
		
		if (entity instanceof Statue) {
			entityName = ((Statue) entity).type.name() + "Statue";
		}

		if (!isLocalSave) {
			if (entity instanceof ItemEntity) extradata.append(":").append(((ItemEntity) entity).getData());
			if (entity instanceof Arrow) extradata.append(":").append(((Arrow) entity).getData());
			if (entity instanceof Spark) extradata.append(":").append(((Spark) entity).getData());
			if (entity instanceof TextParticle) extradata.append(":").append(((TextParticle) entity).getData());
			if (entity instanceof Fireball) extradata.append(":").append(((Fireball) entity).getData());
		}
		// else // is a local save

		int depth = 0;
		if (entity.getLevel() == null) {
			Logger.warn("Saving entity with no level reference: {}; setting level to surface", entity);
		} else {
			depth = entity.getLevel().depth;
		}

		extradata.append(":").append(World.levelIndex(depth));

		return entityName + "[" + entity.x + ":" + entity.y + extradata + "]";
	}
}
