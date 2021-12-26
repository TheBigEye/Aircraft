package minicraft.level.tile;

import java.util.ArrayList;

import minicraft.core.Game;
import minicraft.level.tile.farming.CarrotTile;
import minicraft.level.tile.farming.FarmTile;
import minicraft.level.tile.farming.PotatoTile;
import minicraft.level.tile.farming.SkyFarmTile;
import minicraft.level.tile.farming.SkyWartTile;
import minicraft.level.tile.farming.WheatTile;

public final class Tiles {
    /// idea: to save tile names while saving space, I could encode the names in
    /// base 64 in the save file...^M
    /// then, maybe, I would just replace the id numbers with id names, make them
    /// all private, and then make a get(String) method, parameter is tile name.

    public static ArrayList<String> oldids = new ArrayList<>();

    private static ArrayList<Tile> tiles = new ArrayList<>();

    
    public static void initTileList() {
    	
        if (Game.debug) {
            System.out.println("Initializing tile list...");
        }

        // A total of 256 types of tiles are read
        for (int i = 0; i < 256; i++) {
            tiles.add(null);
        }

        tiles.set(0, new GrassTile("Grass"));
        tiles.set(1, new DirtTile("Dirt"));
        tiles.set(2, new FlowerTile("Flower"));
        tiles.set(3, new HoleTile("Hole"));
        tiles.set(4, new StairsTile("Stairs Up", true));
        tiles.set(5, new StairsTile("Stairs Down", false));
        tiles.set(6, new WaterTile("Water"));
        // this is out of order because of lava buckets
        tiles.set(17, new LavaTile("Lava"));

        tiles.set(7, new RockTile("Rock"));
        tiles.set(8, new TreeTile("Tree"));
        tiles.set(9, new SaplingTile("Tree Sapling", Tiles.get("Grass"), Tiles.get("Tree")));
        tiles.set(10, new SandTile("Sand"));
        tiles.set(11, new CactusTile("Cactus"));
        tiles.set(12, new SaplingTile("Cactus Sapling", Tiles.get("Sand"), Tiles.get("Cactus")));
        tiles.set(13, new OreTile(OreTile.OreType.Iron));
        tiles.set(14, new OreTile(OreTile.OreType.Gold));
        tiles.set(15, new OreTile(OreTile.OreType.Gem));
        tiles.set(16, new OreTile(OreTile.OreType.Lapis));
        tiles.set(18, new LavaBrickTile("Lava Brick"));
        tiles.set(19, new ExplodedTile("Explode"));
        tiles.set(20, new FarmTile("Farmland"));
        tiles.set(21, new WheatTile("Wheat"));
        tiles.set(22, new HardRockTile("Hard Rock"));
        tiles.set(23, new InfiniteFallTile("Infinite Fall"));
        tiles.set(24, new CloudTile("Cloud"));
        tiles.set(25, new CloudCactusTile("Cloud Cactus"));

        // Building tiles
        tiles.set(26, new DoorTile(Tile.Material.Wood));
        tiles.set(27, new DoorTile(Tile.Material.Spruce));
        tiles.set(28, new DoorTile(Tile.Material.Birch));
        tiles.set(29, new DoorTile(Tile.Material.Stone));
        tiles.set(30, new DoorTile(Tile.Material.Obsidian));
        tiles.set(31, new DoorTile(Tile.Material.Holy));
        tiles.set(32, new FloorTile(Tile.Material.Wood));
        tiles.set(33, new FloorTile(Tile.Material.Spruce));
        tiles.set(34, new FloorTile(Tile.Material.Birch));
        tiles.set(35, new FloorTile(Tile.Material.Stone));
        tiles.set(36, new FloorTile(Tile.Material.Obsidian));
        tiles.set(37, new FloorTile(Tile.Material.Holy));
        tiles.set(38, new WallTile(Tile.Material.Wood));
        tiles.set(39, new WallTile(Tile.Material.Spruce));
        tiles.set(40, new WallTile(Tile.Material.Birch));
        tiles.set(41, new WallTile(Tile.Material.Stone));
        tiles.set(42, new WallTile(Tile.Material.Obsidian));
        tiles.set(43, new WallTile(Tile.Material.Holy));

        tiles.set(44, new PathTile("Path"));

        // Wool tiles
        tiles.set(45, new Wool("Wool", Wool.WoolType.NORMAL));
        tiles.set(46, new Wool("Red Wool", Wool.WoolType.RED));
        tiles.set(47, new Wool("Blue Wool", Wool.WoolType.BLUE));
        tiles.set(48, new Wool("Lime Wool", Wool.WoolType.LIME));
        tiles.set(49, new Wool("Yellow Wool", Wool.WoolType.YELLOW));
        tiles.set(50, new Wool("Purple Wool", Wool.WoolType.PURPLE));
        tiles.set(51, new Wool("Black Wool", Wool.WoolType.BLACK));
        tiles.set(52, new Wool("Pink Wool", Wool.WoolType.PINK));
        tiles.set(53, new Wool("Green Wool", Wool.WoolType.GREEN));
        tiles.set(54, new Wool("Light Gray Wool", Wool.WoolType.LIGHT_GRAY));
        tiles.set(55, new Wool("Brown Wool", Wool.WoolType.BROWN));
        tiles.set(56, new Wool("Magenta Wool", Wool.WoolType.MAGENTA));
        tiles.set(57, new Wool("Light Blue Wool", Wool.WoolType.LIGHT_BLUE));
        tiles.set(58, new Wool("Cyan Wool", Wool.WoolType.CYAN));
        tiles.set(59, new Wool("Orange Wool", Wool.WoolType.ORANGE));
        tiles.set(60, new Wool("Gray Wool", Wool.WoolType.GRAY));

        tiles.set(61, new CarrotTile("Carrot"));
        tiles.set(62, new PotatoTile("Potato"));
        tiles.set(63, new SkyWartTile("Sky Wart"));
        tiles.set(64, new LawnTile("Lawn"));
        tiles.set(65, new OrangeTulipTile("Orange Tulip"));

        tiles.set(66, new SnowTile("Snow"));

        tiles.set(67, new BirchTreeTile("Birch Tree"));
        tiles.set(68, new SaplingTile("Birch Sapling", Tiles.get("Grass"), Tiles.get("Birch tree")));

        tiles.set(69, new FirTreeTile("Fir Tree"));
        tiles.set(70, new SaplingTile("Fir Sapling", Tiles.get("Snow"), Tiles.get("Fir tree")));

        tiles.set(71, new PineTreeTile("Pine Tree"));
        tiles.set(72, new SaplingTile("Pine Sapling", Tiles.get("Snow"), Tiles.get("Pine tree")));

        tiles.set(73, new CloudTreeTile("Cloud Tree"));

        tiles.set(74, new IceSpikeTile("Ice Spike"));
        tiles.set(75, new ObsidianTile("Hard Obsidian"));

        // heaven tiles
        tiles.set(76, new SkyGrassTile("Sky Grass"));
        tiles.set(77, new SkyHighGrassTile("Sky High Grass"));
        tiles.set(78, new SkyDirtTile("Sky Dirt"));
        tiles.set(79, new SkyLawnTile("Sky Lawn"));
        tiles.set(80, new FerrositeTile("Ferrosite"));
        tiles.set(81, new SkyFarmTile("Sky Farmland"));
        tiles.set(82, new HolyRockTile("Holy Rock"));
        tiles.set(83, new GoldenCloudTreeTile("Golden Cloud Tree"));
        tiles.set(84, new BlueCloudTreeTile("Blue Cloud Tree"));
        tiles.set(85, new SkyFernTile("Sky Fern"));
        tiles.set(86, new UpRockTile("Up rock"));
        
        tiles.set(87, new JungleGrassTile("Jungle Grass"));

        // tiles.set(?, new SandRockTile("Sand rock"));

        // WARNING: don't use this tile for anything!
        tiles.set(255, new ConnectTile());

        for (int i = 0; i < tiles.size(); i++) {
            if (tiles.get(i) == null)
                continue;
            tiles.get(i).id = (byte) i;
        }
    }

    protected static void add(int id, Tile tile) {
        tiles.set(id, tile);
        System.out.println("Adding " + tile.name + " to tile list with id " + id);
        tile.id = (byte) id;
    }

    static {
        for (int i = 0; i < 256; i++)
            oldids.add(null);

        oldids.set(0, "grass");
        oldids.set(1, "rock");
        oldids.set(2, "water");
        oldids.set(3, "flower");
        oldids.set(4, "tree");
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
        oldids.set(9, "tree Sapling");
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
        oldids.set(130, "purple wool");
        oldids.set(131, "pink wool");
        oldids.set(132, "dark green wool");
        oldids.set(133, "gray wool");
        oldids.set(134, "brown wool");
        oldids.set(135, "magenta wool");
        oldids.set(136, "light blue wool");
        oldids.set(137, "cyan wool");
        oldids.set(138, "orange wool");
        oldids.set(139, "snow");
        oldids.set(140, "cloud tree");
        oldids.set(141, "ice spike");
        oldids.set(142, "spruce Planks");
        oldids.set(143, "spruce wall");
        oldids.set(144, "spruce door");
        oldids.set(155, "spruce door");
        oldids.set(156, "birch Planks");
        oldids.set(157, "birch wall");
        oldids.set(158, "birch door");
        oldids.set(159, "birch door");
        oldids.set(160, "hard obsidian");

        // light/torch versions, for compatibility with before 1.9.4-dev3. (were removed
        // in making dev3)
        oldids.set(100, "grass");
        oldids.set(101, "sand");
        oldids.set(102, "tree");
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
        oldids.set(64, "tree Sapling");
        oldids.set(65, "cactus Sapling");
        oldids.set(160, "purple wool");
        oldids.set(161, "pink wool");
        oldids.set(162, "dark green wool");
        oldids.set(163, "gray wool");
        oldids.set(164, "brown wool");
        oldids.set(165, "magenta wool");
        oldids.set(166, "light blue wool");
        oldids.set(167, "cyan wool");
        oldids.set(168, "orange wool");
        oldids.set(169, "snow");
        oldids.set(171, "fir tree");
        oldids.set(172, "pine tree");
        oldids.set(173, "cloud tree");
        oldids.set(174, "ice spike");
        oldids.set(175, "spruce Planks");
        oldids.set(176, "spruce door");
        oldids.set(177, "spruce door");
        oldids.set(178, "birch Planks");
        oldids.set(179, "birch wall");
        oldids.set(180, "birch door");
        oldids.set(181, "birch door");
        oldids.set(182, "hard obsidian");

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
        oldids.set(190, "torch purple wool");
        oldids.set(191, "torch pink wool");
        oldids.set(192, "torch dark green wool");
        oldids.set(193, "torch gray wool");
        oldids.set(194, "torch brown wool");
        oldids.set(195, "torch magenta wool");
        oldids.set(196, "torch light blue wool");
        oldids.set(197, "torch cyan wool");
        oldids.set(198, "torch orange wool");
        oldids.set(199, "torch snow");
        oldids.set(200, "torch spruce planks");
        oldids.set(201, "torch birch planks");
        oldids.set(202, "torch sky grass");
        oldids.set(203, "torch sky high grass");
        oldids.set(204, "torch sky dirt");
    }

    private static int overflowCheck = 0;

    public static Tile get(String name) {
        // System.out.println("Getting from tile list: " + name);

        name = name.toUpperCase();

        overflowCheck++;

        if (overflowCheck > 50) {
            System.out.println("STACKOVERFLOW prevented in Tiles.get(), on: " + name);
            System.exit(1);
        }

        // System.out.println("Fetching tile " + name);

        Tile getting = null;

        boolean isTorch = false;
        if (name.startsWith("TORCH ")) {
            isTorch = true;
            name = name.substring(6); // cuts off torch prefix.
        }

        if (name.contains("_")) {
            name = name.substring(0, name.indexOf("_"));
        }

        for (Tile t : tiles) {
            if (t == null)
                continue;
            if (t.name.equals(name)) {
                getting = t;
                break;
            }
        }

        if (getting == null) {
            System.out.println("TILES.GET: Invalid tile requested: " + name);
            getting = tiles.get(0);
        }

        if (isTorch) {
            getting = TorchTile.getTorchTile(getting);
        }

        overflowCheck = 0;
        return getting;
    }

    public static Tile get(int id) {
        // System.out.println("Requesting tile by id: " + id);
        if (id < 0)
            id += 256;

        if (tiles.get(id) != null) {
            return tiles.get(id);
        } else if (id >= 128) {
            return TorchTile.getTorchTile(get(id - 128));
        } else {
            System.out.println("TILES.GET: Unknown tile id requested: " + id);
            return tiles.get(0);
        }
    }

    public static boolean containsTile(int id) {
        return tiles.get(id) != null;
    }

    public static String getName(String descriptName) {
        if (!descriptName.contains("_"))
            return descriptName;
        int data;
        String[] parts = descriptName.split("_");
        descriptName = parts[0];
        data = Integer.parseInt(parts[1]);
        return get(descriptName).getName(data);
    }
    
    public static Tile getAll() {
    	
    	Tile t = null;
    	
        for (Tile ti : tiles) {
            t = ti;
        }
    	
		return t;
    	
    }
    
}