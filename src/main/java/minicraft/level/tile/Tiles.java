package minicraft.level.tile;

import java.util.ArrayList;
import java.util.HashMap;

import org.tinylog.Logger;

import minicraft.core.Game;
import minicraft.level.tile.FlowerTile.Flower;
import minicraft.level.tile.TreeTile.TreeType;
import minicraft.level.tile.WoolTile.WoolType;
import minicraft.level.tile.farming.CarrotTile;
import minicraft.level.tile.farming.FarmTile;
import minicraft.level.tile.farming.ParsnipTile;
import minicraft.level.tile.farming.PotatoTile;
import minicraft.level.tile.farming.SkyFarmTile;
import minicraft.level.tile.farming.SkyWartTile;
import minicraft.level.tile.farming.WheatTile;
import minicraft.util.Utils;

public final class Tiles {
	/// idea: to save tile names while saving space, I could encode the names in
	/// base 64 in the save file...^M
	/// then, maybe, I would just replace the id numbers with id names, make them
	/// all private, and then make a get(String) method, parameter is tile name.

	public static ArrayList<String> oldids = new ArrayList<>();
	private static final HashMap<Short, Tile> tiles = new HashMap<>();

	public static void initialize() {
		Logger.debug("Initializing tile list ...");

		Tiles.add(0, new GrassTile("Grass"));
		Tiles.add(1, new DirtTile("Dirt"));
		Tiles.add(2, new FlowerTile(Flower.DAISY));
		Tiles.add(3, new HoleTile("Hole"));
		Tiles.add(4, new StairsTile("Stairs Up", true));
		Tiles.add(5, new StairsTile("Stairs Down", false));
		Tiles.add(6, new WaterTile("Water"));
		
		// this is out of order because of lava buckets
		Tiles.add(17, new LavaTile("Lava"));

		Tiles.add(7, new RockTile("Rock"));
		Tiles.add(8, new TreeTile(TreeTile.TreeType.Oak));
		Tiles.add(9, new SaplingTile("Oak Sapling", Tiles.get("Grass"), Tiles.get("Oak Tree")));
		Tiles.add(10, new SandTile("Sand"));
		Tiles.add(11, new CactusTile("Cactus"));
		Tiles.add(12, new SaplingTile("Cactus Sapling", Tiles.get("Sand"), Tiles.get("Cactus")));
		Tiles.add(13, new OreTile(OreTile.OreType.Iron));
		Tiles.add(14, new OreTile(OreTile.OreType.Gold));
		Tiles.add(15, new OreTile(OreTile.OreType.Gem));
		Tiles.add(16, new OreTile(OreTile.OreType.Lapis));
		Tiles.add(18, new GrassTile("Lava Brick"));
		Tiles.add(19, new ExplodedTile("Explode"));
		Tiles.add(20, new FarmTile("Farmland"));
		Tiles.add(21, new WheatTile("Wheat"));
		Tiles.add(22, new HardRockTile("Hard Rock"));
		Tiles.add(23, new InfiniteFallTile("Infinite Fall"));
		Tiles.add(24, new CloudTile("Cloud"));
		Tiles.add(25, new CloudCactusTile("Cloud Cactus"));

		// Building tiles
		Tiles.add(26, new DoorTile(Tile.Material.Oak));
		Tiles.add(27, new DoorTile(Tile.Material.Spruce));
		Tiles.add(28, new DoorTile(Tile.Material.Birch));
		Tiles.add(29, new DoorTile(Tile.Material.Stone));
		Tiles.add(30, new DoorTile(Tile.Material.Obsidian));
		Tiles.add(31, new DoorTile(Tile.Material.Holy));
		Tiles.add(32, new FloorTile(Tile.Material.Oak));
		Tiles.add(33, new FloorTile(Tile.Material.Spruce));
		Tiles.add(34, new FloorTile(Tile.Material.Birch));
		Tiles.add(35, new FloorTile(Tile.Material.Stone));
		Tiles.add(36, new FloorTile(Tile.Material.Obsidian));
		Tiles.add(37, new FloorTile(Tile.Material.Holy));
		Tiles.add(38, new WallTile(Tile.Material.Oak));
		Tiles.add(39, new WallTile(Tile.Material.Spruce));
		Tiles.add(40, new WallTile(Tile.Material.Birch));
		Tiles.add(41, new WallTile(Tile.Material.Stone));
		Tiles.add(42, new WallTile(Tile.Material.Obsidian));
		Tiles.add(43, new WallTile(Tile.Material.Holy));

		Tiles.add(44, new PathTile("Path"));

		// Wool tiles
		Tiles.add(45, new WoolTile("Wool", WoolType.NORMAL));
		Tiles.add(46, new WoolTile("Red Wool", WoolType.RED));
		Tiles.add(47, new WoolTile("Blue Wool", WoolType.BLUE));
		Tiles.add(48, new WoolTile("Lime Wool", WoolType.LIME));
		Tiles.add(49, new WoolTile("Yellow Wool", WoolType.YELLOW));
		Tiles.add(50, new WoolTile("Purple Wool", WoolType.PURPLE));
		Tiles.add(51, new WoolTile("Black Wool", WoolType.BLACK));
		Tiles.add(52, new WoolTile("Pink Wool", WoolType.PINK));
		Tiles.add(53, new WoolTile("Green Wool", WoolType.GREEN));
		Tiles.add(54, new WoolTile("Light Gray Wool", WoolType.LIGHT_GRAY));
		Tiles.add(55, new WoolTile("Brown Wool", WoolType.BROWN));
		Tiles.add(56, new WoolTile("Magenta Wool", WoolType.MAGENTA));
		Tiles.add(57, new WoolTile("Light Blue Wool", WoolType.LIGHT_BLUE));
		Tiles.add(58, new WoolTile("Cyan Wool", WoolType.CYAN));
		Tiles.add(59, new WoolTile("Orange Wool", WoolType.ORANGE));
		Tiles.add(60, new WoolTile("Gray Wool", WoolType.GRAY));

		Tiles.add(61, new CarrotTile("Carrot"));
		Tiles.add(62, new PotatoTile("Potato"));
		Tiles.add(63, new SkyWartTile("Sky Wart"));
		Tiles.add(64, new LawnTile("Lawn"));
		Tiles.add(65, new FlowerTile(Flower.DANDELION));

		Tiles.add(66, new SnowTile("Snow"));

		Tiles.add(67, new TreeTile(TreeType.Birch));
		Tiles.add(68, new SaplingTile("Birch Sapling", Tiles.get("Grass"), Tiles.get("Birch tree")));

		Tiles.add(69, new TreeTile(TreeType.Fir));
		Tiles.add(70, new SaplingTile("Fir Sapling", Tiles.get("Snow"), Tiles.get("Fir tree")));

		Tiles.add(71, new TreeTile(TreeType.Pine));
		Tiles.add(72, new SaplingTile("Pine Sapling", Tiles.get("Snow"), Tiles.get("Pine tree")));

		Tiles.add(73, new TreeTile(TreeType.Skyroot));

		Tiles.add(74, new IceSpikeTile("Ice Spike"));
		Tiles.add(75, new ObsidianTile("Raw Obsidian"));

		// heaven tiles
		Tiles.add(76, new SkyGrassTile("Sky Grass"));
		Tiles.add(77, new SkyHighGrassTile("Sky High Grass"));
		Tiles.add(78, new SkyGrassTile("Sky Dirt"));
		Tiles.add(79, new SkyLawnTile("Sky Lawn"));
		Tiles.add(80, new FerrositeTile("Ferrosite"));
		Tiles.add(81, new SkyFarmTile("Sky Farmland"));
		Tiles.add(82, new HolyRockTile("Holy Rock"));
		Tiles.add(83, new TreeTile(TreeTile.TreeType.Goldroot));
		Tiles.add(84, new TreeTile(TreeTile.TreeType.Bluroot));
		Tiles.add(85, new SkyFernTile("Sky Fern"));
		Tiles.add(86, new UpRockTile("Up Rock"));

		Tiles.add(87, new SkyGrassTile("Cloud Hole"));

		Tiles.add(88, new IceTile("Ice"));
		Tiles.add(89, new MyceliumTile("Mycelium"));
		Tiles.add(90, new MushroomTile(MushroomTile.MushroomType.Brown));
		Tiles.add(91, new MushroomTile(MushroomTile.MushroomType.Red));
		Tiles.add(92, new ParsnipTile("Parsnip"));
		
		Tiles.add(93, new FlowerTile(Flower.ROSE));
		Tiles.add(94, new SproutTile("Rose Sprout", Tiles.get("Grass"), Tiles.get("Rose")));
		
		Tiles.add(95, new FlowerTile(Flower.POPPY));
		Tiles.add(96, new SproutTile("Poppy Sprout", Tiles.get("Grass"), Tiles.get("Poppy")));
		
		Tiles.add(97, new SproutTile("Daisy Sprout", Tiles.get("Grass"), Tiles.get("Daisy")));
		Tiles.add(98, new SproutTile("Dandelion Sprout", Tiles.get("Grass"), Tiles.get("Dandelion")));
		Tiles.add(99, new AltarTile("Summon Altar"));

		// tiles.put((short)?, new SandRockTile("Sand rock"));

		// WARNING: don't use this tile for anything!
		Tiles.add(255, new ConnectTile());

		for (short tile = 0; tile < 256; tile++) {
			if (tiles.get(tile) == null) continue;
			tiles.get(tile).id = (short) tile;
		}
	}

	protected static void add(int id, Tile tile) {
		tiles.put((short) id, tile);
		if (Game.debug) Logger.info("Loading tile ID \"{}\", adding \"{}\" for tile list ... ", id, Utils.formatText(tile.name));
		tile.id = (short) id;
	}

	static {
		for (int i = 0; i < 32768; i++) {
			oldids.add(null);
		}

		oldids.set(0, "grass");
		oldids.set(1, "rock");
		oldids.set(2, "water");
		oldids.set(3, "flower");
		oldids.set(4, "oak tree");
		oldids.set(5, "dirt");
		oldids.set(41, "wool");
		oldids.set(42, "red wool");
		oldids.set(43, "blue wool");
		oldids.set(45, "green wool");
		oldids.set(127, "yellow wool");
		oldids.set(56, "black wool");
		oldids.set(6, "sand");
		oldids.set(7, "cactus");
		oldids.set(8, "hole");
		oldids.set(9, "oak Sapling");
		oldids.set(10, "cactus Sapling");
		oldids.set(11, "farmland");
		oldids.set(12, "wheat");
		oldids.set(13, "lava");
		oldids.set(14, "stairs Down");
		oldids.set(15, "stairs Up");
		oldids.set(17, "cloud");
		oldids.set(30, "explode");
		oldids.set(31, "Wood Planks");
		oldids.set(33, "plank wall");
		oldids.set(34, "stone wall");
		oldids.set(35, "wood door");
		oldids.set(36, "wood door");
		oldids.set(37, "stone door");
		oldids.set(38, "stone door");
		oldids.set(39, "lava brick");
		oldids.set(32, "Stone Bricks");
		oldids.set(120, "Obsidian");
		oldids.set(121, "Obsidian wall");
		oldids.set(122, "Obsidian door");
		oldids.set(123, "Obsidian door");
		oldids.set(18, "hard Rock");
		oldids.set(19, "iron Ore");
		oldids.set(24, "Lapis");
		oldids.set(20, "gold Ore");
		oldids.set(21, "gem Ore");
		oldids.set(22, "cloud Cactus");
		oldids.set(16, "infinite Fall");

		// Light/torch versions, for compatibility with before 1.9.4-dev3. (were removed in making dev3)
		oldids.set(100, "grass");
		oldids.set(101, "sand");
		oldids.set(102, "oak tree");
		oldids.set(103, "cactus");
		oldids.set(104, "water");
		oldids.set(105, "dirt");
		oldids.set(107, "flower");
		oldids.set(108, "stairs Up");
		oldids.set(109, "stairs Down");
		oldids.set(110, "Wood Planks");
		oldids.set(111, "Stone Bricks");
		oldids.set(112, "wood door");
		oldids.set(113, "wood door");
		oldids.set(114, "stone door");
		oldids.set(115, "stone door");
		oldids.set(116, "Obsidian door");
		oldids.set(117, "Obsidian door");
		oldids.set(119, "hole");
		oldids.set(57, "wool");
		oldids.set(58, "red wool");
		oldids.set(59, "blue wool");
		oldids.set(60, "green wool");
		oldids.set(61, "yellow wool");
		oldids.set(62, "black wool");
		oldids.set(63, "Obsidian");
		oldids.set(64, "oak Sapling");
		oldids.set(65, "cactus Sapling");

		oldids.set(44, "torch grass");
		oldids.set(40, "torch sand");
		oldids.set(46, "torch dirt");
		oldids.set(47, "torch wood planks");
		oldids.set(48, "torch stone bricks");
		oldids.set(49, "torch Obsidian");
		oldids.set(50, "torch wool");
		oldids.set(51, "torch red wool");
		oldids.set(52, "torch blue wool");
		oldids.set(53, "torch green wool");
		oldids.set(54, "torch yellow wool");
		oldids.set(55, "torch black wool");
	}

	private static int overflowCheck = 0;
	public static Tile get(String name) {
		// if (Game.debug) Logger.info("Getting from tile list: " + name);

		name = name.toUpperCase();
		overflowCheck++;

		if (overflowCheck > 50) {
			Logger.warn("STACKOVERFLOW prevented in Tiles.get(), on: " + name);
			System.exit(1);
		}

		// if (Game.debug) Logger.info("Fetching tile " + name);

		boolean isTorch = false;
		if (name.startsWith("TORCH")) {
			isTorch = true;
			name = name.substring(6); // Cuts off torch prefix.
		}
		if (name.contains("_")) {
			name = name.substring(0, name.indexOf("_"));
		}

		Tile getting = null;
		
		for (Tile tile: tiles.values()) {
			if (tile == null) continue;
			if (tile.name.equals(name)) {
				getting = tile;
				break;
			}
		}

		if (getting == null) {
			Logger.warn("TILES.GET: Invalid tile requested: " + name);
			getting = tiles.get((short) 0);
		}

		if (isTorch) {
			getting = TorchTile.getTorchTile(getting);
		}

		overflowCheck = 0;
		return getting;
	}

	public static Tile get(int id) {
		//System.out.println("Requesting tile by id: " + id);
		if (id < 0) id += 32768;
		
		Tile tile = tiles.get((short)id);

		if (tile != null) {
			return tile;

		} else if (id >= 32767) {
			return TorchTile.getTorchTile(get(id - 32767));

		} else {
			Logger.warn("TILES.GET: Unknown tile id requested: " + id);
			return tiles.get((short) 0);
		}
	}

	public static boolean containsTile(int id) {
		return tiles.get((short)id) != null;
	}

	public static String getName(String descriptName) {
		if (!descriptName.contains("_")) {
			return descriptName;
		}
		
		String[] parts = descriptName.split("_");
		descriptName = parts[0];
		
		int data;
		data = Integer.parseInt(parts[1]);
		
		return get(descriptName).getName(data);
	}
}