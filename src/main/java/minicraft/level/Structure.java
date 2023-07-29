package minicraft.level;

import java.util.HashMap;
import java.util.HashSet;

import minicraft.entity.furniture.Crafter;
import minicraft.entity.furniture.Furniture;
import minicraft.entity.furniture.Lantern;
import minicraft.entity.furniture.Statue;
import minicraft.graphic.Point;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

// this stores structures that can be drawn at any location.
public class Structure {

    private HashSet<TilePoint> tiles;
    private HashMap<Point, Furniture> furniture;

    public Structure() {
        tiles = new HashSet<>();
        furniture = new HashMap<>();
    }

    public Structure(Structure structure) {
        this.tiles = structure.tiles;
        this.furniture = structure.furniture;
    }

    public void setTile(int x, int y, Tile tile) {
        tiles.add(new TilePoint(x, y, tile));
    }

    public void addFurniture(int x, int y, Furniture furniture) {
        this.furniture.put(new Point(x, y), furniture);
    }

    public void draw(Level level, int xt, int yt) {
        for (TilePoint point : tiles) {
            level.setTile(xt + point.x, yt + point.y, point.t);
        }

        for (Point point : furniture.keySet()) {
            level.add(furniture.get(point).clone(), xt + point.x, yt + point.y, true);
        }
    }

    public void draw(short[] map, int xt, int yt, int mapWidth) {
        for (TilePoint point : tiles) {
            map[(xt + point.x) + (yt + point.y) * mapWidth] = point.t.id;
        }
    }

    public void setData(String keys, String data) {
        // so, the keys are single letters, each letter represents a tile
        HashMap<String, String> keyPairs = new HashMap<>();
        String[] stringKeyPairs = keys.split(",");

        // puts all the keys in the keyPairs HashMap
        for (int i = 0; i < stringKeyPairs.length; i++) {
            String[] thisKey = stringKeyPairs[i].split(":");
            keyPairs.put(thisKey[0], thisKey[1]);
        }

        String[] dataLines = data.split("\n");
        int width = dataLines[0].length();
        int height = dataLines.length;

        for (int i = 0; i < dataLines.length; i++) {
            for (int c = 0; c < dataLines[i].length(); c++) {
                if (dataLines[i].charAt(c) != '*') {
                    Tile tile = Tiles.get(keyPairs.get(String.valueOf(dataLines[i].charAt(c))));
                    this.setTile(-width / 2 + i, -height / 2 + c, tile);
                }
            }
        }
    }

    static class TilePoint {
        int x;
        int y;

        Tile t;

        public TilePoint(int x, int y, Tile tile) {
            this.x = x;
            this.y = y;
            this.t = tile;
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof TilePoint)) {
                return false;
            }
            TilePoint point = (TilePoint) object;
            return x == point.x && y == point.y && t.id == point.t.id;
        }

        @Override
        public int hashCode() {
            return x + y * 51 + t.id * 131;
        }
    }

    static final Structure dungeonGate;
    static final Structure dungeonLock;
    static final Structure lavaPool;
    
    // All the "mobDungeon" structures are for the spawner structures
    static final Structure mobDungeonCenter;
    static final Structure mobDungeonNorth;
    static final Structure mobDungeonSouth;
    static final Structure mobDungeonEast;
    static final Structure mobDungeonWest;

    static final Structure skyDungeon;

    // used for random villages
    static final Structure villageCrops;
    static final Structure villageNormal;
    static final Structure villageHouseTwoDoor;

    static final Structure villageRuinedOverlay1;
    static final Structure villageRuinedOverlay2;

    // ok, because of the way the system works, these structures are rotated 90
    // degrees clockwise when placed, then it's flipped on the vertical
    static {
        dungeonGate = new Structure();
        dungeonGate.setData("O:Obsidian,D:Obsidian Door,W:Obsidian Wall",
             "WWDWW\n" +
             "WOOOW\n" + 
             "DOOOD\n" + 
             "WOOOW\n" + 
             "WWDWW");
        dungeonGate.addFurniture(-1, -1, new Lantern(Lantern.Type.IRON));

        dungeonLock = new Structure();
        dungeonLock.setData("O:Obsidian,W:Obsidian Wall",
            "WWWWW\n" + 
            "WOOOW\n" + 
            "WOOOW\n" + 
            "WOOOW\n" + 
            "WWWWW"
        );

        lavaPool = new Structure();
        lavaPool.setData("L:Lava",
            "LL\n" +
            "LL"
        );

        mobDungeonCenter = new Structure();
        mobDungeonCenter.setData("B:Stone Bricks,W:Stone Wall",
            "WWBWW\n" + 
            "WBBBW\n" + 
            "BBBBB\n" + 
            "WBBBW\n" + 
            "WWBWW"
        );
        mobDungeonNorth = new Structure();
        mobDungeonNorth.setData("B:Stone Bricks,W:Stone Wall", 
            "WWWWW\n" + 
            "WBBBB\n" + 
            "BBBBB\n" + 
            "WBBBB\n" + 
            "WWWWW"
        );
        
        mobDungeonSouth = new Structure();
        mobDungeonSouth.setData("B:Stone Bricks,W:Stone Wall", 
            "WWWWW\n" + 
            "BBBBW\n" + 
            "BBBBB\n" + 
            "BBBBW\n" + 
            "WWWWW"
        );
        
        mobDungeonEast = new Structure();
        mobDungeonEast.setData("B:Stone Bricks,W:Stone Wall", 
            "WBBBW\n" + 
            "WBBBW\n" + 
            "WBBBW\n" + 
            "WBBBW\n" + 
            "WWBWW"
        );
        
        mobDungeonWest = new Structure();
        mobDungeonWest.setData("B:Stone Bricks,W:Stone Wall", 
            "WWBWW\n" + 
            "WBBBW\n" + 
            "WBBBW\n" + 
            "WBBBW\n" + 
            "WBBBW"
        );

        skyDungeon = new Structure();
        skyDungeon.setData("F:Holy Bricks,W:Holy Wall,D:Holy Door,S:Sky Wart,T:Red Wool",
            "***********************\n" + 
            "******WWWWWWWWWWW******\n" + 
            "******WFFFFFFFFFW******\n" + 
            "******WWWWFFFWWWW******\n" + 
            "*********WWDWW*********\n" + 
            "**********WFW**********\n" + 
            "*WWW******WFW******WWW*\n" + 
            "*WFW******WFW******WFW*\n" + 
            "*WFW****WWWTWWW****WFW*\n" + 
            "*WFWW***WSSTSSW***WWFW*\n" + 
            "*WFFWWWWWSTTTSWWWWWFFW*\n" + 
            "*WFFDFFFTTTTTTTFFFDFFW*\n" + 
            "*WFFWWWWWSTTTSWWWWWFFW*\n" + 
            "*WFWW***WSSTSSW***WWFW*\n" + 
            "*WFW****WWWTWWW****WFW*\n" + 
            "*WFW******WFW******WFW*\n" + 
            "*WWW******WFW******WWW*\n" + 
            "**********WFW**********\n" + 
            "*********WWDWW*********\n" + 
            "******WWWWFFFWWWW******\n" + 
            "******WFFFFFFFFFW******\n" + 
            "******WWWWWWWWWWW******\n" + 
            "***********************"
        );
        skyDungeon.addFurniture(0, 0, new Crafter(Crafter.Type.Enchanter));

        villageCrops = new Structure();
        villageCrops.setData("F:Oak Planks,W:Oak Wall,D:Oak Door,G:Grass,O:Path,C:potato,Z:wheat,X:Water",
            "WWWWWO*O**WWWWW\n" + 
            "WFFFW*OO**WFFFW\n" + 
            "WFFFDOO*OODFFFW\n" + 
            "WFFFW*OO**WFFFW\n" + 
            "WWWWW**OO*WWWWW\n" + 
            "**OO**OO*OO*OO*\n" + 
            "*OO*O*OOO**OO**\n" + 
            "OO*OOOOGOOOO*OO\n" +   
            "**OO**OOO**OOO*\n" + 
            "O*O*O*OO*O**O*O\n" + 
            "WWWWW**OO*ZZXCC\n" + 
            "WFFFW*OO**ZZXCC\n" + 
            "WFFFDOO*O*ZZXCC\n" + 
            "WFFFW**OO*ZZXCC\n" + 
            "WWWWW**O*OZZXCC\n"
        );
        villageCrops.addFurniture(0 * 16, 0 * 16, new Statue(Statue.Type.Zombie));

        villageNormal = new Structure();
        villageNormal.setData("F:Oak Planks,W:Oak Wall,D:Oak Door,G:Grass,O:Path",
            "WWWWWO*O**WWWWW\n" + 
            "WFFFW*OO**WFFFW\n" + 
            "WFFFW*O*O*WFFFW\n" + 
            "WFFFW*OO**WFFFW\n" + 
            "WWDWW**OO*WWDWW\n" + 
            "**OO**OO*OO*OO*\n" + 
            "*OO*O*OOO**OO**\n" + 
            "OO*OOOOGOOOO*OO\n" +   
            "**OO**OOO**OOO*\n" + 
            "O*O*O*OO*O**O*O\n" + 
            "WWDWW**OO*WWDWW\n" + 
            "WFFFW*OO**WFFFW\n" + 
            "WFFFW*O*O*WFFFW\n" + 
            "WFFFW**OO*WFFFW\n" + 
            "WWWWW**O*OWWWWW\n"
        );
        villageNormal.addFurniture(0 * 16, 0 * 16, new Statue(Statue.Type.Skeleton));

        villageHouseTwoDoor = new Structure();
        villageHouseTwoDoor.setData("F:Oak Planks,W:Oak Wall,D:Oak Door,G:Grass",
                "WWWWW\n" + "WFFFW\n" + "DFFFW\n" + "WFFFW\n" + "WWDWW");

        villageRuinedOverlay1 = new Structure();
        villageRuinedOverlay1.setData("F:Oak Planks,W:Oak Wall,D:Oak Door,G:Grass",
                "WWWWW\n" + "WFFFW\n" + "WFFFD\n" + "WFFFW\n" + "WWWWW");

        villageRuinedOverlay2 = new Structure();
        villageRuinedOverlay2.setData("F:Oak Planks,W:Oak Wall,D:Oak Door,G:Grass",
                "WWWWW\n" + "WFFFW\n" + "DFFFW\n" + "WFFFW\n" + "WWDWW");
    }
}