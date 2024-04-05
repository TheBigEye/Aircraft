package minicraft.saveload;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tinylog.Logger;

import minicraft.core.Game;
import minicraft.core.Updater;
import minicraft.core.World;
import minicraft.core.io.Localization;
import minicraft.core.io.Settings;
import minicraft.entity.Arrow;
import minicraft.entity.Boat;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.Fireball;
import minicraft.entity.ItemEntity;
import minicraft.entity.Spark;
import minicraft.entity.furniture.Bed;
import minicraft.entity.furniture.Chest;
import minicraft.entity.furniture.Crafter;
import minicraft.entity.furniture.DeathChest;
import minicraft.entity.furniture.DungeonChest;
import minicraft.entity.furniture.Lantern;
import minicraft.entity.furniture.Spawner;
import minicraft.entity.furniture.Statue;
import minicraft.entity.furniture.Tnt;
import minicraft.entity.mob.AirWizard;
import minicraft.entity.mob.Cat;
import minicraft.entity.mob.Chicken;
import minicraft.entity.mob.Cleric;
import minicraft.entity.mob.Cow;
import minicraft.entity.mob.Creeper;
import minicraft.entity.mob.EnemyMob;
import minicraft.entity.mob.EyeQueen;
import minicraft.entity.mob.Firefly;
import minicraft.entity.mob.Goat;
import minicraft.entity.mob.Golem;
import minicraft.entity.mob.GuiMan;
import minicraft.entity.mob.Keeper;
import minicraft.entity.mob.Knight;
import minicraft.entity.mob.Librarian;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.MobAi;
import minicraft.entity.mob.OldGolem;
import minicraft.entity.mob.Phyg;
import minicraft.entity.mob.Pig;
import minicraft.entity.mob.Player;
import minicraft.entity.mob.Sheep;
import minicraft.entity.mob.Sheepuff;
import minicraft.entity.mob.Skeleton;
import minicraft.entity.mob.Slime;
import minicraft.entity.mob.Snake;
import minicraft.entity.mob.Zombie;
import minicraft.entity.particle.BrightParticle;
import minicraft.entity.particle.CloudParticle;
import minicraft.entity.particle.FireParticle;
import minicraft.entity.particle.HeartParticle;
import minicraft.entity.particle.SmashParticle;
import minicraft.entity.particle.SplashParticle;
import minicraft.entity.particle.TextParticle;
import minicraft.graphic.Color;
import minicraft.item.ArmorItem;
import minicraft.item.Inventory;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.PotionItem;
import minicraft.item.PotionType;
import minicraft.item.StackableItem;
import minicraft.level.Level;
import minicraft.level.tile.Tiles;
import minicraft.network.Network;
import minicraft.screen.AchievementsDisplay;
import minicraft.screen.LoadingDisplay;
import minicraft.screen.MultiplayerDisplay;

public class Load {

	private String location = Game.gameDir;

	private static final String worldExtension = Save.worldExtension;
	private static final String dataExtension = Save.dataExtension;
	private static final String saveExtension = Save.saveExtension;
	private static final String mapExtension = Save.mapExtension;
	
	private static final String oldExtension = Save.oldExtension;
	
	private float loadPercent;

	private ArrayList<String> data;
	private ArrayList<String> extradata; // These two are changed when loading a new file. (see loadFromFile())

	private Version worldVersion;

	{
		worldVersion = null;

		data = new ArrayList<>();
		extradata = new ArrayList<>();
	}

	public Load(String worldname) {
		this(worldname, true);
	}

	public Load(String worldname, boolean loadGame) {
		loadFromFile(location + "/saves/" + worldname + "/Game" + saveExtension);
		
		if (data.get(0).contains(".")) {
			worldVersion = new Version(data.get(0));
		}

		if (worldVersion == null) {
			worldVersion = new Version("1.8");
		}

		if (!loadGame) return;
		
		if (worldVersion.compareTo(new Version("1.9.2")) < 0) {
			new LegacyLoad(worldname);
		} else {
			location += "/saves/" + worldname + "/";

			// For the methods below, and world.
			loadPercent = 5 + World.levels.length - 1; 
			loadPercent = 100f / loadPercent;

			LoadingDisplay.setPercentage(0);
			
			// More of the version will be determined here
			loadGame("Game"); 
			loadWorld("Level");
			loadMap("Level");
			loadEntities("Entities");
			loadInventory("Inventory", Game.player.getInventory());
			loadPlayer("Player", Game.player);

			if (Game.isMode("Creative")) {
				Items.fillCreativeInventory(Game.player.getInventory(), false);
			}
		}
	}

	public Load() {
		this(Game.VERSION);
	}

	public Load(Version worldSaveVersion) {
		this(false);
		worldVersion = worldSaveVersion;
	}

	public Load(boolean loadConfig) {
		if (!loadConfig) {
			return;
		}

		boolean resave = false;

		location += "/";

		// Check if Preferences.json exists. (new version)
		if (new File(location + "Preferences.json").exists()) {
			loadPrefs("Preferences");

		// Check if Preferences.miniplussave exists. (old version)
		} else if (new File(location + "Preferences" + oldExtension).exists()) {
			loadPrefsOld("Preferences");
			Logger.info("Upgrading preferences to JSON...");
			resave = true;

		// No preferences file found.
		} else {
			Logger.warn("No preferences found, creating new file.");
			resave = true;
		}

		// Load unlocks. (new version)
		File testFileOld = new File(location + "unlocks" + oldExtension);
		File testFile = new File(location + "Unlocks" + oldExtension);

		if (new File(location + "Unlocks.json").exists()) {
			loadUnlocks("Unlocks");
			
		// Load old version
		} else if (testFile.exists() || testFileOld.exists()) { 
			if (testFileOld.exists() && !testFile.exists()) {
				if (testFileOld.renameTo(testFile)) {
					new LegacyLoad(testFile);
				} else {
					Logger.info("Failed to rename unlocks to Unlocks; loading old version.");
					new LegacyLoad(testFileOld);
				}
			}

			loadUnlocksOld("Unlocks");
			resave = true;
			Logger.info("Upgrading unlocks to JSON...");
		} else {
			Logger.warn("No unlocks found, creating new file...");
			resave = true;
		}

		// We need to load everything before we save, so it doesn't overwrite unlocks.
		if (resave) {
			new Save();
		}
	}

	public Version getWorldVersion() {
		return worldVersion;
	}

	public static ArrayList<String> loadFile(String filename) throws IOException {
		ArrayList<String> lines = new ArrayList<>();

		InputStream fileStream = Load.class.getResourceAsStream(filename);

		try (BufferedReader br = new BufferedReader(new InputStreamReader(fileStream))) {
			String line;
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
		}

		return lines;
	}

	private void loadFromFile(String filename) {
		data.clear();
		extradata.clear();

		String total;
		try {
			total = loadFromFile(filename, true);
			if (total.length() > 0) {
				data.addAll(Arrays.asList(total.split(",")));
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}

		if (filename.contains("Level")) {
			try {
				total = Load.loadFromFile(filename.substring(0, filename.lastIndexOf("/") + 7) + dataExtension, true);
				extradata.addAll(Arrays.asList(total.split(",")));
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}

		LoadingDisplay.progress(loadPercent);
	}

	public static String loadFromFile(String filename, boolean isWorldSave) throws IOException {
		StringBuilder total = new StringBuilder();

		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String curLine;

			while ((curLine = br.readLine()) != null) {
				total.append(curLine).append(isWorldSave ? "" : "\n");
			}
		}

		return total.toString();
	}

	private void loadGame(String filename) {
		loadFromFile(location + filename + saveExtension);

		// Gets the world version
		worldVersion = new Version(data.remove(0)); 
        
        if (worldVersion.compareTo(new Version("2.2.0-dev1")) >= 0) {
			World.setWorldSeed(Long.parseLong(data.remove(0)));
        }

		if (worldVersion.compareTo(new Version("2.0.4-dev8")) >= 0) {
			loadMode(data.remove(0));
		}

		Updater.setTime(Integer.parseInt(data.remove(0)));

		Updater.gameTime = Integer.parseInt(data.remove(0));
		if (worldVersion.compareTo(new Version("1.9.3-dev2")) >= 0) {
			Updater.pastFirstDay = Updater.gameTime > 65000;
		} else {
			Updater.gameTime = 65000; // Prevents time cheating.
		}

		int diffIdx = Integer.parseInt(data.remove(0));
		if (worldVersion.compareTo(new Version("1.9.3-dev3")) < 0) {
			diffIdx--; // Account for change in difficulty
		}

		Settings.setIndex("diff", diffIdx);
		AirWizard.beaten = Boolean.parseBoolean(data.remove(0));
		EyeQueen.beaten = Boolean.parseBoolean(data.remove(0));
		Settings.set("Cheats", Boolean.parseBoolean(data.remove(0)));

		// Check if the AirWizard was beaten in versions prior to 2.1.0
		if (worldVersion.compareTo(new Version("2.1.0-dev2")) < 0) {
			if (AirWizard.beaten) {
				Logger.debug("AirWizard was beaten in an old version, giving achievement ...");
				AchievementsDisplay.setAchievement("minicraft.achievement.airwizard", true);
			}
		}
	}

	private void loadMode(String modedata) {
		int mode;
		
		if (modedata.contains(";")) {
			String[] modeinfo = modedata.split(";");
			mode = Integer.parseInt(modeinfo[0]);

			// We changed the min mode idx from 1 to 0.
			if (worldVersion.compareTo(new Version("2.0.3")) <= 0) {
				mode--; 
			}

			if (mode == 3) {
				Updater.scoreTime = Integer.parseInt(modeinfo[1]);
				if (worldVersion.compareTo(new Version("1.9.4")) >= 0) {
					Settings.set("scoretime", modeinfo[2]);
				}
			}

		} else {
			mode = Integer.parseInt(modedata);

			// We changed the min mode idx from 1 to 0.
			if (worldVersion.compareTo(new Version("2.0.3")) <= 0) {
				mode--; 
			}

			if (mode == 3) {
				Updater.scoreTime = 300;
			}
		}

		Settings.setIndex("mode", mode);
	}

	private void loadPrefsOld(String filename) {
		loadFromFile(location + filename + oldExtension);

		// the default, because this doesn't really matter much being specific past this if it's not set below.
		Version prefVer = new Version("2.0.2");

		// signifies that this file was last written to by a version after 2.0.2.
		if (!data.get(2).contains(";")) {
			prefVer = new Version(data.remove(0));
		}

		Settings.set("sound", Boolean.parseBoolean(data.remove(0)));
		Settings.set("autosave", Boolean.parseBoolean(data.remove(0)));

		if (prefVer.compareTo(new Version("2.0.4-dev2")) >= 0) {
			Settings.set("fps", Integer.parseInt(data.remove(0)));
		}

		List<String> subdata;

		if (prefVer.compareTo(new Version("2.0.3-dev1")) < 0) {
			subdata = data;

		} else {

			MultiplayerDisplay.savedIP = data.remove(0);

			if (prefVer.compareTo(new Version("2.0.3-dev3")) > 0) {
				MultiplayerDisplay.savedUUID = data.remove(0);
				MultiplayerDisplay.savedUsername = data.remove(0);
			}

			if (prefVer.compareTo(new Version("2.0.4-dev3")) >= 0) {
				String lang = data.remove(0);
				Settings.set("language", lang);
				Localization.changeLanguage(lang);
			}

			String keyData = data.get(0);
			subdata = Arrays.asList(keyData.split(":"));
		}

		for (String keymap : subdata) {
			String[] map = keymap.split(";");
			Game.input.setKey(map[0], map[1]);
		}
	}

	private void loadPrefs(String filename) {
		JSONObject json;

		try {
			json = new JSONObject(loadFromFile(location + filename + ".json", false));
		} catch (JSONException | IOException exception) {
			exception.printStackTrace();
			return;
		}

		/* Start of the parsing */
		@SuppressWarnings("unused")
		Version prefVer = new Version(json.getString("version"));

		// Settings
		Settings.set("sound", json.getBoolean("sound"));
		Settings.set("autosave", json.getBoolean("autosave"));
		Settings.set("diff", json.has("diff") ? json.getString("diff") : "Normal");
		Settings.set("fps", json.getInt("fps"));
        Settings.set("vsync", json.getBoolean("vsync"));
        Settings.set("bossbar", json.has("diff") ? json.getString("diff") : "On screen");
        Settings.set("particles", json.getBoolean("particles"));
        Settings.set("shadows", json.getBoolean("shadows"));

		if (json.has("lang")) {
			String lang = json.getString("lang");
			Settings.set("language", lang);
			Localization.changeLanguage(lang);
		} else {
			Localization.loadSelectedLanguageFile();
		}

		// Load keymap
		JSONArray keyData = json.getJSONArray("keymap");
		List<Object> subdata = keyData.toList();

		for (Object key : subdata) {
			String str = key.toString();

			// Split key and value
			String[] map = str.split(";");
			Game.input.setKey(map[0], map[1]);
		}
	}

	private void loadUnlocksOld(String filename) {
		loadFromFile(location + filename + oldExtension);

		for (String unlock : data) {
			if (unlock.equals("AirSkin")) {
				Settings.set("unlockedskin", true);
			}

			unlock = unlock.replace("HOURMODE", "H_ScoreTime").replace("MINUTEMODE", "M_ScoreTime").replace("M_ScoreTime", "_ScoreTime").replace("2H_ScoreTime", "120_ScoreTime");

			if (unlock.contains("_ScoreTime")) {
				Settings.getEntry("scoretime").setValueVisibility(Integer.parseInt(unlock.substring(0, unlock.indexOf("_"))), true);
			}
		}
	}

	private void loadUnlocks(String filename) {
		JSONObject json;

		try {
			json = new JSONObject(loadFromFile(location + filename + ".json", false));
		} catch (JSONException | IOException exception) {
			exception.printStackTrace();
			return;
		}

		Settings.set("unlockedskin", json.getBoolean("unlockedAirWizardSuit"));

		for (Object i : json.getJSONArray("visibleScoreTimes")) {
			Settings.getEntry("scoretime").setValueVisibility(i, true); // Minutes
		}

		// Load unlocked achievements.
		if (json.has("unlockedAchievements")) {
			AchievementsDisplay.unlockAchievements(json.getJSONArray("unlockedAchievements"));
		}
	}
	

	private void loadWorld(String filename) {
		for (int levelDepth = World.maxLevelDepth; levelDepth >= World.minLevelDepth; levelDepth--) {
			LoadingDisplay.setProgressType(Level.getDepthString(levelDepth));
			int levelIndex = World.levelIndex(levelDepth);
			loadFromFile(location + filename + levelIndex + worldExtension);

			int worldWidth = Integer.parseInt(data.get(0));
			int worldHeight = Integer.parseInt(data.get(1));
			
			boolean hasSeed = worldVersion.compareTo(new Version("2.0.7-dev2")) >= 0;
			long seed = hasSeed ? Long.parseLong(data.get(2)) : 0;
			Settings.set("size", worldWidth);

			short[] tiles = new short[worldWidth * worldHeight];
			short[] datas = new short[worldWidth * worldHeight];

			for (int x = 0; x < worldWidth; x++) {
				for (int y = 0; y < worldHeight; y++) {

					// The tiles are saved with x outer loop, and y inner loop, meaning that
					// the list reads down, then right one, rather than right, then down one.
					int tilesArrayIndex = y + x * worldWidth;
					int tileIndex = x + y * worldWidth;

					String tilename = data.get(tileIndex + (hasSeed ? 4 : 3));
					if (worldVersion.compareTo(new Version("1.9.4-dev6")) < 0) {

						// they were id numbers, not names, at this point
						int tileID = Integer.parseInt(tilename);

						if (Tiles.oldids.get(tileID) != null) {
							tilename = Tiles.oldids.get(tileID);
						} else {
							Logger.warn("Tile list doesn't contain tile {}", tileID);
							tilename = "grass";
						}
					}

					if (tilename.equalsIgnoreCase("WOOL") && worldVersion.compareTo(new Version("2.0.6-dev4")) < 0) {
						switch (Integer.parseInt(extradata.get(tileIndex))) {
							case 1: tilename = "Red Wool"; break;
							case 2: tilename = "Yellow Wool"; break;
							case 3: tilename = "Green Wool"; break;
							case 4: tilename = "Blue Wool"; break;
							case 5: tilename = "Black Wool"; break;
							default:
								tilename = "Wool";
						}
					}
					
					if (worldVersion.compareTo(new Version("2.2.0-dev1")) >= 0) {
						if (tilename.equalsIgnoreCase("TREE")) {
							tilename = "Oak Tree";
							Logger.info("Detected old TREE tile, converting to new OAK TREE tile...");
						}
						
						if (tilename.equalsIgnoreCase("WOOD PLANKS")) {
							tilename = "Oak Planks";
							Logger.info("Detected old WOOD PLANKS tile, converting to new OAK PLANKS tile...");
						}
						
						if (tilename.equalsIgnoreCase("WOOD WALL")) {
							tilename = "Oak Wall";
							Logger.info("Detected old WOOD WALL tile, converting to new OAK WALL tile...");
						}
						
						if (tilename.equalsIgnoreCase("WOOD DOOR")) {
							tilename = "Oak Door";
							Logger.info("Detected old WOOD DOOR tile, converting to new OAK DOOR tile...");
						}
					}

					if (worldVersion.compareTo(new Version("2.0.3-dev6")) < 0) {
						if (levelDepth == World.minLevelDepth + 1 && tilename.equalsIgnoreCase("LAPIS")) {
							// don't replace *all* the lapis
							if (Math.random() < 0.8) {
								tilename = "Gem Ore";
							}
						}
					}

					tiles[tilesArrayIndex] = Tiles.get(tilename).id;
					datas[tilesArrayIndex] = Short.parseShort(extradata.get(tileIndex));
				}
			}

			Level parent = World.levels[World.levelIndex(levelDepth + 1)];
			World.levels[levelIndex] = new Level(worldWidth, worldHeight, seed, levelDepth, parent, false);

			Level currentLevel = World.levels[levelIndex];
			currentLevel.tiles = tiles;
			currentLevel.data = datas;

			if (Game.debug) {
				currentLevel.printTileLocs(Tiles.get("Stairs Down"));
			}

			if (parent == null) {
				continue;
			}

			/// confirm that there are stairs in all the places that should have stairs.
			for (minicraft.graphic.Point p : parent.getMatchingTiles(Tiles.get("Stairs Down"))) {
				if (currentLevel.getTile(p.x, p.y) != Tiles.get("Stairs Up")) {
					currentLevel.printLevelLoc("INCONSISTENT STAIRS detected; placing stairsUp", p.x, p.y);
					currentLevel.setTile(p.x, p.y, Tiles.get("Stairs Up"));
				}
			}
			for (minicraft.graphic.Point p : currentLevel.getMatchingTiles(Tiles.get("Stairs Up"))) {
				if (parent.getTile(p.x, p.y) != Tiles.get("Stairs Down")) {
					parent.printLevelLoc("INCONSISTENT STAIRS detected; placing stairsDown", p.x, p.y);
					parent.setTile(p.x, p.y, Tiles.get("Stairs Down"));
				}
			}
		}
	}
	
	public void loadMap(String filename) {
		for (int levelDepth = World.maxLevelDepth; levelDepth >= World.minLevelDepth; levelDepth--) {
		    LoadingDisplay.setProgressType("Map");
		    LoadingDisplay.setProgressType(Level.getDepthString(levelDepth));
		    int levelIndex = World.levelIndex(levelDepth);

		    // Clear the data list before loading each level's map data
		    data.clear();

		    loadFromFile(location + filename + levelIndex + mapExtension);

		    if (data.size() >= 1) { // Check if there is at least one element in the data list
		        Level currentLevel = World.levels[levelIndex];

		        // Get the base64-encoded explored data from the data list
		        String exploredData = data.get(0);

		        // Decode the base64 string to a byte array
		        byte[] byteArray = Base64.getDecoder().decode(exploredData);

		        // Deserialize the byte array to the explored array
		        try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(byteArray))) {
		            boolean[][] explored = (boolean[][]) objectInputStream.readObject();
		            currentLevel.explored = explored;
		        } catch (IOException | ClassNotFoundException e) {
		            e.printStackTrace();
		        }
		    }
		}
	}


	public void loadPlayer(String filename, Player player) {
		LoadingDisplay.setProgressType("Player");
		loadFromFile(location + filename + saveExtension);
		loadPlayer(player, data);
	}
	

	public void loadPlayer(Player player, List<String> origData) {
		List<String> data = new ArrayList<>(origData);

		player.x = Integer.parseInt(data.remove(0));
		player.y = Integer.parseInt(data.remove(0));
		player.spawnx = Integer.parseInt(data.remove(0));
		player.spawny = Integer.parseInt(data.remove(0));
		player.health = Integer.parseInt(data.remove(0));

		if (worldVersion.compareTo(new Version("2.0.4-dev7")) >= 0) {
			player.hunger = Integer.parseInt(data.remove(0));
		}
		player.armor = Integer.parseInt(data.remove(0));

		if (worldVersion.compareTo(new Version("2.0.5-dev5")) >= 0 || player.armor > 0 || worldVersion.compareTo(new Version("2.0.5-dev4")) == 0 && data.size() > 5) {
			if (worldVersion.compareTo(new Version("2.0.4-dev7")) < 0) {
				// Reverse order b/c we are taking from the end
				player.currentArmor = (ArmorItem) Items.get(data.remove(data.size() - 1));
				player.armorDamageBuffer = Integer.parseInt(data.remove(data.size() - 1));
			} else {
				player.armorDamageBuffer = Integer.parseInt(data.remove(0));
				player.currentArmor = (ArmorItem) Items.get(data.remove(0), true);
			}
		}
		player.setScore(Integer.parseInt(data.remove(0)));

		if (worldVersion.compareTo(new Version("2.0.4-dev7")) < 0) {
			int arrowCount = Integer.parseInt(data.remove(0));

			if (worldVersion.compareTo(new Version("2.0.1-dev1")) < 0) {
				player.getInventory().add(Items.get("arrow"), arrowCount);
			}
		}
	
		Game.currentLevel = Integer.parseInt(data.remove(0));
		Level level = World.levels[Game.currentLevel];

		// Removes the user player from the level, in case they would be added twice.
		if (!player.isRemoved()) {
			player.remove(); 
		}
	
		if  (level != null) {
			level.add(player);
		} else {
			Logger.trace("Game level to add player {} to is null.", player);
		}

		if (worldVersion.compareTo(new Version("2.0.4-dev8")) < 0) {
			String modedata = data.remove(0);
			if (player == Game.player) {
				loadMode(modedata); // Only load if you're loading the main player
			}
		}

		String potionEffects = data.remove(0);
		if (!potionEffects.equals("PotionEffects[]")) {
			String[] effects = potionEffects.replace("PotionEffects[", "").replace("]", "").split(":");

			for (String s : effects) {
				String[] effect = s.split(";");
				PotionType pName = Enum.valueOf(PotionType.class, effect[0]);
				PotionItem.applyPotion(player, pName, Integer.parseInt(effect[1]));
			}
		}

		if (worldVersion.compareTo(new Version("1.9.4-dev4")) < 0) {
			String colorsList = data.remove(0).replace("[", "").replace("]", "");
			String[] color = colorsList.split(";");
			int[] colors = new int[color.length];

			for (int i = 0; i < colors.length; i++) {
				colors[i] = Integer.parseInt(color[i]) / 50;
			}

			String spriteColor = "" + colors[0] + colors[1] + colors[2];
			Logger.debug("Loaded player shirt color as {}", spriteColor);
			player.shirtColor = Integer.parseInt(spriteColor);

		} else if (worldVersion.compareTo(new Version("2.0.6-dev4")) < 0) {
			String color = data.remove(0);
			int[] colors = new int[3];

			for (int i = 0; i < 3; i++) {
				colors[i] = Integer.parseInt(String.valueOf(color.charAt(i)));
			}
			
			player.shirtColor = Color.get(1, colors[0] * 51, colors[1] * 51, colors[2] * 51);
		} else {
			player.shirtColor = Integer.parseInt(data.remove(0));
		}

		// This works for some reason... lol
		Settings.set("skinon", player.suitOn = Boolean.parseBoolean(data.remove(0)));
        
		player.nightCount  = Integer.parseInt(data.remove(0));
        player.isNiceNight = Boolean.parseBoolean(data.remove(0));
	}

	protected static String subOldName(String name, Version worldVer) {
		if (worldVer.compareTo(new Version("1.9.4-dev4")) < 0) {
			name = name.replace("Hatchet", "Axe").replace("Pick", "Pickaxe").replace("Pickaxeaxe", "Pickaxe").replace("Spade", "Shovel")
					.replace("Pow glove", "Power Glove").replace("II", "").replace("W.Bucket", "Water Bucket").replace("L.Bucket", "Lava Bucket")
					.replace("G.Apple", "Gold Apple").replace("St.", "Stone").replace("Ob.", "Obsidian").replace("I.Lantern", "Iron Lantern")
					.replace("G.Lantern", "Gold Lantern").replace("BrickWall", "Wall").replace("Brick", " Brick").replace("Wall", " Wall").replace("  ", " ");
			if (name.equals("Bucket")) {
				name = "Empty Bucket";
			}
		}

		if (worldVer.compareTo(new Version("1.9.4")) < 0) {
			name = name.replace("I.Armor", "Iron Armor").replace("S.Armor", "Snake Armor").replace("L.Armor", "Leather Armor").replace("G.Armor", "Gold Armor").replace("BrickWall", "Wall");
		}

		if (worldVer.compareTo(new Version("2.0.6-dev3")) < 0) {
			name = name.replace("Fishing Rod", "Wood Fishing Rod");
		}

		// If save is older than 2.0.6.
		if (worldVer.compareTo(new Version("2.0.6")) < 0) {
			if (name.startsWith("Pork Chop")) {
				name = name.replace("Pork Chop", "Cooked Pork");
			}
		}

		// If save is older than 2.0.7-dev1.
		if (worldVer.compareTo(new Version("2.0.7-dev1")) < 0) {
			if (name.startsWith("Seeds")) {
				name = name.replace("Seeds", "Wheat Seeds");
			}
		}

		// If save is older than 2.1.0-dev2.
		if (worldVer.compareTo(new Version("2.1.0-dev2")) < 0) {
			if (name.startsWith("Shear")) {
				name = name.replace("Shear", "Shears");
			}
		}
		
		if (worldVer.compareTo(new Version("2.2.0-dev1")) < 0) {
			if (name.startsWith("Plank")) {
				name = name.replace("Plank", "Oak Plank");
			}
			if (name.startsWith("Plank Wall")) {
				name = name.replace("Plank Wall", "Oak Wall");
			}
			if (name.startsWith("Plank Door")) {
				name = name.replace("Plank Door", "Oak Door");
			}
		}

		return name;
	}

	public void loadInventory(String filename, Inventory inventory) {
		loadFromFile(location + filename + saveExtension);
		loadInventory(inventory, data);
	}

	public void loadInventory(Inventory inventory, List<String> data) {
		inventory.clear();

		for (String item : data) {
			if (item.length() == 0) {
				System.err.println("loadInventory: Item in data list is \"\", skipping item");
				continue;
			}

			if (worldVersion.compareTo(new Version("2.1.0-dev2")) < 0) {
				item = subOldName(item, worldVersion);
			}

			if (item.contains("Power Glove")) {
				continue; // Just pretend it doesn't exist. Because it doesn't. :P
			}

			// System.out.println("Loading item: " + item);

			if (worldVersion.compareTo(new Version("2.0.4")) <= 0 && item.contains(";")) {
				String[] currentData = item.split(";");
				String itemName = currentData[0];
				
				Item newItem = Items.get(itemName);
				int count = Integer.parseInt(currentData[1]);

				if (newItem instanceof StackableItem) {
					((StackableItem) newItem).count = count;
					inventory.add(newItem);
				} else {
					inventory.add(newItem, count);
				}

			} else {
				Item itemToAdd = Items.get(item);
				inventory.add(itemToAdd);
			}
		}
	}

	private void loadEntities(String filename) {
		LoadingDisplay.setProgressType("Entities");
		loadFromFile(location + filename + saveExtension);

		for (Level level : World.levels) {
			level.clearEntities();
		}

		for (String name : data) {
			if (name.startsWith("Player")) {
				continue;
			}
			loadEntity(name, worldVersion, true);
		}
		
		for (Level level : World.levels) {
			level.checkChestCount();
			level.checkAirWizard();
		}
	}

	@Nullable
	public static Entity loadEntity(String entityData, boolean isLocalSave) {
		if (isLocalSave) {
			System.out.println("Warning: Assuming version of save file is current while loading entity: " + entityData);
		}
		return Load.loadEntity(entityData, Game.VERSION, isLocalSave);
	}

	
	@Nullable @SuppressWarnings({ "rawtypes", "unused" })
	public static Entity loadEntity(String entityData, Version worldSaveVersion, boolean isLocalSave) {
		entityData = entityData.trim();

		if (entityData.length() == 0) {
			return null;
		}

		String[] stuff = entityData.substring(entityData.indexOf("[") + 1, entityData.indexOf("]")).split(":");
		List<String> info = new ArrayList<>(Arrays.asList(stuff));

		// This gets the text before "[", which is the entity name.
		String entityName = entityData.substring(0, entityData.indexOf("["));

		int x = Integer.parseInt(info.get(0));
		int y = Integer.parseInt(info.get(1));

		int eid = -1;
		if (!isLocalSave) {
			eid = Integer.parseInt(info.remove(2));

			// If I find an entity that is loaded locally, but on another level in the
			// entity data provided, then I ditch the current entity and make a new one from
			// the info provided.
			Entity existing = Network.getEntity(eid);

			if (existing != null) {
				// Existing one is out of date; replace it.
				existing.remove();
				Game.levels[Game.currentLevel].add(existing);
				return null;
			}
		}

		Entity newEntity = null;
		
		if (entityName.equals("Spark") && !isLocalSave) {
			int airWizardID = Integer.parseInt(info.get(2));
			Entity sparkOwner = Network.getEntity(airWizardID);
			
			if (sparkOwner instanceof AirWizard) {
				newEntity = new Spark((AirWizard) sparkOwner, x, y, 1);
			} else {
				Logger.error("failed to load spark; owner id doesn't point to a correct entity");
				return null;
			}

		} else if (entityName.equals("Fireball") && !isLocalSave) {
			int eyeQueenID = Integer.parseInt(info.get(2));
			Entity fireballOwner = Network.getEntity(eyeQueenID);
			
			if (fireballOwner instanceof EyeQueen) {
				newEntity = new Fireball((EyeQueen) fireballOwner, x, y, 1, 1);
			} else {
				Logger.error("failed to load fireball; owner id doesn't point to a correct entity");
				return null;
			}

		} else {
			int mobLvl = 1;
			Class c = null;
			if (!Crafter.names.contains(entityName)) {
				try {
					c = Class.forName("minicraft.entity.mob." + entityName);
				} catch (ClassNotFoundException ignored) {
				}
			}

			if (c != null && EnemyMob.class.isAssignableFrom(c)) {
				mobLvl = Integer.parseInt(info.get(info.size() - 2));
			}

			if (mobLvl == 0) {
				if (Game.debug) Logger.info("Level 0 mob: {}", entityName);
				mobLvl = 1;
			}

			newEntity = getEntity(entityName.substring(entityName.lastIndexOf(".") + 1), mobLvl);
		}

		if (newEntity == null)
			return null;

		if (newEntity instanceof Mob) { // This is structured the same way as in Save.java.
			Mob mob = (Mob) newEntity;
			mob.health = Integer.parseInt(info.get(2));

			Class c = null;

			if (worldSaveVersion.compareTo(new Version("2.0.7-dev1")) >= 0) { // If the version is more or equal to 2.0.7-dev1
				if (newEntity instanceof Sheep) {
					Sheep sheep = ((Sheep) mob);
					if (info.get(3).equalsIgnoreCase("true")) {
						sheep.sheared = true;

					}
					mob = sheep;
				}
			}

			newEntity = mob;
		} else if (newEntity instanceof Chest) {
			Chest chest = (Chest) newEntity;
			
			boolean isDeathChest = chest instanceof DeathChest;
			boolean isDungeonChest = chest instanceof DungeonChest;
			List<String> chestInfo = info.subList(2, info.size() - 1);

			int endIndex = chestInfo.size() - (isDeathChest || isDungeonChest ? 1 : 0);
			for (int index = 0; index < endIndex; index++) {
				String itemData = chestInfo.get(index);
				if (worldSaveVersion.compareTo(new Version("2.1.0-dev3")) < 0) {
					itemData = subOldName(itemData, worldSaveVersion);
				}

				if (itemData.contains("Power Glove")) {
					continue; // Ignore it.
				}

				Item item = Items.get(itemData);
				chest.getInventory().add(item);
			}

			if (isDeathChest) {
				((DeathChest) chest).time = Integer.parseInt(chestInfo.get(chestInfo.size() - 1));
			} else if (isDungeonChest) {
				((DungeonChest) chest).setLocked(Boolean.parseBoolean(chestInfo.get(chestInfo.size() - 1)));
				if (((DungeonChest) chest).isLocked()) {
					World.levels[Integer.parseInt(info.get(info.size() - 1))].chestCount++;
				}
			}

			newEntity = chest;

		} else if (newEntity instanceof Spawner) {
			MobAi mob = (MobAi) getEntity(info.get(2).substring(info.get(2).lastIndexOf(".") + 1), Integer.parseInt(info.get(3)));

			if (mob != null) {
				newEntity = new Spawner(mob);
			}

		} else if (newEntity instanceof Lantern && worldSaveVersion.compareTo(new Version("1.9.4")) >= 0 && info.size() > 3) {
			newEntity = new Lantern(Lantern.Type.values()[Integer.parseInt(info.get(2))]);
		}

		if (!isLocalSave) {
			if (newEntity instanceof Arrow) {
				int ownerID = Integer.parseInt(info.get(2));
				Mob mob = (Mob) Network.getEntity(ownerID);

				if (mob != null) {
					Direction shootDirection = Direction.values[Integer.parseInt(info.get(3))];
					int arrowDamage = Integer.parseInt(info.get(5));
					newEntity = new Arrow(mob, x, y, shootDirection, arrowDamage);
				}
			}
			if (newEntity instanceof ItemEntity) {
				Item item = Items.get(info.get(2));
				double zz = Double.parseDouble(info.get(3));
				int lifetime = Integer.parseInt(info.get(4));
				int timeleft = Integer.parseInt(info.get(5));
				double xa = Double.parseDouble(info.get(6));
				double ya = Double.parseDouble(info.get(7));
				double za = Double.parseDouble(info.get(8));
				newEntity = new ItemEntity(item, x, y, zz, lifetime, timeleft, xa, ya, za);
			}
			if (newEntity instanceof TextParticle) {
				int textColor = Integer.parseInt(info.get(3));
				newEntity = new TextParticle(info.get(2), x, y, textColor);

				// if (Game.debug) System.out.println("Loaded text particle; color: "+ Color.toString(textColor)+", text: " + info.get(2));
			}
		}

		newEntity.eid = eid; // This will be -1 unless set earlier, so a new one will be generated when adding it to the level.
		if (newEntity instanceof ItemEntity && eid == -1) {
			Logger.warn("Item entity was loaded with no eid");
		}

		int currentLevel = Integer.parseInt(info.get(info.size() - 1));
		if (World.levels[currentLevel] != null) {
			World.levels[currentLevel].add(newEntity, x, y);
		}
		return newEntity;
	}

    @Nullable
    private static Entity getEntity(String string, int moblvl) {

        switch (string) {
	        // Load Mob entities
	        case "Player": return null;
	        case "RemotePlayer": return null;
	        case "Cow": return new Cow();
	        case "Goat": return new Goat();
	        case "Sheep": return new Sheep();
	        case "Sheepuff": return new Sheepuff();
	        case "Chicken": return new Chicken();
	        case "Pig": return new Pig();
	        case "Phyg": return new Phyg();
	        case "Cleric": return new Cleric();
	        case "Librarian": return new Librarian();
	        case "GuiMan": return new GuiMan();
	        case "Cat": return new Cat();
	        case "Golem": return new Golem();
	        case "Zombie": return new Zombie(moblvl);
	        case "Slime": return new Slime(moblvl);
	        case "Creeper": return new Creeper(moblvl);
	        case "Skeleton": return new Skeleton(moblvl);
	        case "Knight": return new Knight(moblvl);
	        case "OldGolem": return new OldGolem(moblvl);
	        case "Snake": return new Snake(moblvl);
	        case "Cthulhu": return new EyeQueen(moblvl); // Check...
	        case "EyeQueen": return new EyeQueen(moblvl);
	        case "DeepGuardian": return new Keeper(moblvl);
	        case "Keeper": return new Keeper(moblvl);
	        case "AirWizard": return new AirWizard(moblvl > 1);
	        case "Firefly": return new Firefly();
	
	        // Load Furniture entities
	        case "Boat": return new Boat();
	        case "Spawner": return new Spawner(new Zombie(1));
	        case "Workbench": return new Crafter(Crafter.Type.Workbench);
	        case "Chest": return new Chest();
	        case "DeathChest": return new DeathChest();
	        case "DungeonChest": return new DungeonChest(false);
	        case "Anvil": return new Crafter(Crafter.Type.Anvil);
	        case "Enchanter": return new Crafter(Crafter.Type.Enchanter);
	        case "Stonecutter": return new Crafter(Crafter.Type.Stonecutter);
	        case "Assembler": return new Crafter(Crafter.Type.Assembler);
	        case "Brewery": return new Crafter(Crafter.Type.Brewery);
	        case "Loom": return new Crafter(Crafter.Type.Loom);
	        case "Furnace": return new Crafter(Crafter.Type.Furnace);
	        case "Oven": return new Crafter(Crafter.Type.Oven);
	        case "Bed": return new Bed();
	        case "SlimeStatue": return new Statue(Statue.Type.Slime);
	        case "ZombieStatue": return new Statue(Statue.Type.Zombie);
	        case "SkeletonStatue": return new Statue(Statue.Type.Skeleton);
	        case "Tnt": return new Tnt();
	        case "Lantern": return new Lantern(Lantern.Type.NORM);
	        case "Arrow": return new Arrow(new Skeleton(0), 0, 0, Direction.NONE, 0);
	        case "ItemEntity": return new ItemEntity(Items.get("unknown"), 0, 0);
	
	        // Load Particles
	        case "FireParticle": return new FireParticle(0, 0);
	        case "SplashParticle": return new SplashParticle(0, 0);
	        case "HeartParticle": return new HeartParticle(0, 0);
	        case "BrightParticle": return new BrightParticle(0, 0);
	        case "SmashParticle": return new SmashParticle(0, 0);
	        case "CloudParticle": return new CloudParticle(0, 0);
	        case "TextParticle": return new TextParticle("", 0, 0, 0);
	
	        default:
	            System.err.println("LOAD ERROR: unknown or outdated entity requested: " + string);
	            return null;
        }
    }
}
