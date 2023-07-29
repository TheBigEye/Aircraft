package minicraft.util;

import java.util.HashMap;
import java.util.Map;

import minicraft.graphic.Color;
import minicraft.level.tile.Tiles;

public enum MapData {

    GRASS(Tiles.get("Grass").id, Color.get(1, 84, 168, 84)),
    LAWN(Tiles.get("Lawn").id, Color.get(1, 96, 165, 96)),
    ORANGE_TULIP(Tiles.get("Orange Tulip").id, Color.get(1, 96, 165, 96)),

    DIRT(Tiles.get("Dirt").id, Color.get(1, 131, 108, 108)),
    MYCELIUM(Tiles.get("Mycelium").id, Color.get(1, 120, 105, 105)),
    FLOWER(Tiles.get("Flower").id, Color.get(1, 96, 165, 96)),
    RED_MUSHROOM(Tiles.get("Red Mushroom").id, Color.get(1, 117, 102, 102)),
    BROWN_MUSHROOM(Tiles.get("Brown Mushroom").id, Color.get(1, 117, 102, 102)),
    HOLE(Tiles.get("Hole").id, Color.get(1, 61, 47, 8)),

    WATER(Tiles.get("Water").id, Color.get(1, 26, 44, 137)),
    LAVA(Tiles.get("Lava").id, Color.get(1, 200, 32, 32)),

    ROCK(Tiles.get("Rock").id, Color.get(1, 122, 122, 122)),
    UP_ROCK(Tiles.get("Up Rock").id, Color.get(1, 147, 147, 147)),
    HARD_ROCK(Tiles.get("Hard Rock").id, Color.get(1, 127, 126, 107)),

    CACTUS(Tiles.get("Cactus").id, Color.get(1, 183, 183, 91)),

    OAK_TREE(Tiles.get("Oak Tree").id, Color.get(1, 37, 83, 37)),
    BIRCH_TREE(Tiles.get("Birch Tree").id, Color.get(1, 12, 117, 12)),
    FIR_TREE(Tiles.get("Fir Tree").id, Color.get(1, 19, 139, 98)),
    PINE_TREE(Tiles.get("Pine Tree").id, Color.get(1, 17, 127, 89)),

    SAND(Tiles.get("Sand").id, Color.get(1, 226, 226, 111)),
    SNOW(Tiles.get("Snow").id, Color.get(1, 240, 240, 240)),
    ICE(Tiles.get("Ice").id, Color.get(1, 104, 110, 236)),

    ICE_SPIKE(Tiles.get("Ice Spike").id, Color.get(1, 240, 240, 240)),

    STAIRS_UP(Tiles.get("Stairs Up").id, 0xffffff),
    STAIRS_DOWN(Tiles.get("Stairs Down").id, 0xffffff),
    OAK_FLOOR(Tiles.get("Oak Planks").id, Color.get(1, 145, 79, 14)),
    OAK_WALL(Tiles.get("Oak Wall").id, Color.get(1, 122, 67, 12)),
    OAK_DOOR(Tiles.get("Oak Door").id, Color.get(1, 122, 72, 23)),
    STONE_FLOOR(Tiles.get("Stone Bricks").id, Color.get(1, 163, 163, 163)),
    STONE_WALL(Tiles.get("Stone Wall").id, Color.get(1, 99, 99, 99)),
    STONE_DOOR(Tiles.get("Stone Door").id, Color.get(1, 119, 119, 119)),
    OBSIDIAN_FLOOR(Tiles.get("Obsidian").id, Color.get(1, 76, 30, 100)),
    OBSIDIAN_WALL(Tiles.get("Obsidian Wall").id, Color.get(1, 46, 24, 118)),
    OBSIDIAN_DOOR(Tiles.get("Obsidian Door").id, Color.get(1, 44, 21, 67)),
    RAW_OBSIDIAN(Tiles.get("Raw Obsidian").id, Color.get(1, 73, 28, 98)),

    // Wools in the map
    WOOL(Tiles.get("Wool").id, Color.get(1, 220, 220, 220)),
    BLACK_WOOL(Tiles.get("Black Wool").id, Color.get(1, 21, 21, 21)),
    YELLOW_WOOL(Tiles.get("Yellow Wool").id, Color.get(1, 197, 197, 44)),
    LIME_WOOL(Tiles.get("Lime Wool").id, Color.get(1, 71, 178, 59)),
    BLUE_WOOL(Tiles.get("Blue Wool").id, Color.get(1, 51, 75, 160)),
    RED_WOOL(Tiles.get("Red Wool").id, Color.get(1, 132, 44, 44)),
    PURPLE_WOOL(Tiles.get("Purple Wool").id, Color.get(1, 127, 63, 180)),
    PINK_WOOL(Tiles.get("Pink Wool").id, Color.get(1, 224, 175, 198)),
    GREEN_WOOL(Tiles.get("Green Wool").id, Color.get(1, 53, 70, 29)),
    BROWN_WOOL(Tiles.get("Brown Wool").id, Color.get(1, 91, 56, 36)),
    MAGENTA_WOOL(Tiles.get("Magenta Wool").id, Color.get(1, 184, 87, 194)),
    LIGHT_BLUE_WOOL(Tiles.get("Light Blue Wool").id, Color.get(1, 111, 155, 220)),
    CYAN_WOOL(Tiles.get("Cyan Wool").id, Color.get(1, 65, 109, 132)),
    ORANGE_WOOL(Tiles.get("Orange Wool").id, Color.get(1, 229, 110, 71)),

    // Crops in the map
    FARMLAND(Tiles.get("Farmland").id, Color.get(1, 145, 75, 75)),
    WHEAT(Tiles.get("Wheat").id, Color.get(1, 226, 226, 111)),
    CARROT(Tiles.get("Carrot").id, Color.get(1, 242, 131, 33)),

    INFINITE_FALL(Tiles.get("Infinite Fall").id, Color.get(1, 0, 87, 122)),
    CLOUD(Tiles.get("Cloud").id, Color.WHITE),
    CLOUD_CACTUS(Tiles.get("Cloud Cactus").id, Color.WHITE),

    FERROSITE(Tiles.get("Ferrosite").id, Color.get(1, 203, 197, 121)),
    SKYROOT_TREE(Tiles.get("Skyroot Tree").id, Color.get(1, 71, 112, 68)),
    GOLDROOT_TREE(Tiles.get("Goldroot Tree").id, Color.get(1, 187, 161, 79)),
    BLUROOT_TREE(Tiles.get("Bluroot Tree").id, Color.get(1, 0, 118, 158)),
    HOLY_ROCK(Tiles.get("Holy Rock").id, Color.get(1, 97, 147, 126)),
    SKY_LAWN(Tiles.get("Sky Lawn").id, Color.get(1, 86, 163, 131)),
    SKY_HIGH_GRASS(Tiles.get("Sky High Grass").id, Color.get(1, 79, 150, 120)),
    SKY_DIRT(Tiles.get("Sky Dirt").id, Color.get(1, 73, 108, 114)),
    SKY_GRASS(Tiles.get("Sky Grass").id, Color.get(1, 90, 171, 138)),
    SKY_FERN(Tiles.get("Sky Fern").id, Color.get(1, 92, 137, 109));

    private static final Map<Integer, MapData> BY_ID = new HashMap<Integer, MapData>();

    static {
        for (MapData mapData : MapData.values()) {
            MapData.BY_ID.put(mapData.tileID, mapData);
        }
    }

    public int tileID;
    public int color;

    MapData(int id, int color) {
        tileID = id;
        this.color = color;
    }   

    public static MapData getById(int id) {
        return MapData.BY_ID.get(id);
    }
}
