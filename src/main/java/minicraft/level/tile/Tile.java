package minicraft.level.tile;

import java.util.Random;

import minicraft.core.World;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.gfx.ConnectorSprite;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.item.ToolType;
import minicraft.level.Level;

public abstract class Tile {
    public static int tickCount = 0; // A global tickCount used in the Lava & water tiles.
    protected Random random = new Random();

    protected enum Material {
        Wood(ToolType.Axe), Spruce(ToolType.Axe), Birch(ToolType.Axe), Stone(ToolType.Pickaxe),
        Obsidian(ToolType.Pickaxe), Holy(ToolType.Pickaxe);

        public static final Material[] values = Material.values();

        private final ToolType requiredTool;

        Material(ToolType requiredTool) {
            this.requiredTool = requiredTool;
        }

        public ToolType getRequiredTool() {
            return requiredTool;
        }
    }

    public final String name;

    public short id;

    public boolean connectsToGrass = false;
    public boolean connectsToCloud = false;
    public boolean connectsToUpRock = false;
    public boolean connectsToSand = false;
    public boolean connectsToFluid = false;
    protected boolean connectsToLava = false;
    protected boolean connectsToRock = false;
    public int light;
    protected boolean maySpawn;

    protected Sprite sprite;
    protected ConnectorSprite csprite;
    protected boolean connectsToSnow = false;
    protected boolean connectsToObsidian = false;

    public boolean connectsToSkyGrass = false;
    protected boolean connectsToSkyDirt = false;
    public boolean connectsToSkyHighGrass = false;
    public boolean connectsToFerrosite = false;
    public boolean connectsToDirt = false;
    
    public boolean connectsToJungleGrass = false;

    {
        light = 1;
        maySpawn = false;
        sprite = null;
        csprite = null;
    }

    protected Tile(String name, Sprite sprite) {
        this.name = name.toUpperCase();
        this.sprite = sprite;
    }

    protected Tile(String name, ConnectorSprite sprite) {
        this.name = name.toUpperCase();
        csprite = sprite;
    }

    /**
     * This method is used by tiles to specify the default "data" they have in a
     * level's data array. Used for starting health, color/type of tile, etc.
     */
    /// at least, that was the idea at first...
    public int getDefaultData() {
        return 0;
    }

    /** Render method, used in sub-classes */
    public void render(Screen screen, Level level, int x, int y) {
        if (sprite != null)
            sprite.render(screen, x << 4, y << 4);
        if (csprite != null)
            csprite.render(screen, level, x, y);
    }

    public boolean maySpawn() {
        return maySpawn;
    }

    /** Returns if the player can walk on it, overrides in sub-classes */
    public boolean mayPass(Level level, int x, int y, Entity e) {
        return true;
    }

    /** Gets the light radius of a tile, Bigger number = bigger circle */
    public int getLightRadius(Level level, int x, int y) {
        return 0;
    }
    
    public int getLightRadius(Level level, int x, int y, int light) {
        return light;
    }

    /**
     * Hurt the tile with a specified amount of damage.
     * 
     * @param level     The level this happened on.
     * @param x         X pos of the tile.
     * @param y         Y pos of the tile.
     * @param source    The mob that damaged the tile.
     * @param dmg       Damage to taken.
     * @param attackDir The direction of the player hitting.
     * @return If the damage was applied.
     */
    public boolean hurt(Level level, int x, int y, Mob source, int dmg, Direction attackDir) {
        return false;
    }

    /**
     * Hurt the tile with a specified amount of damage.
     * 
     * @param level The level this happened on.
     * @param x     X position of the tile.
     * @param y     Y position of the tile.
     * @param dmg   The damage taken.
     */
    public void hurt(Level level, int x, int y, int dmg) {
    }

    /** What happens when you run into the tile (ex: run into a cactus) */
    public void bumpedInto(Level level, int xt, int yt, Entity entity) {
    }

    /** Update method */
    public boolean tick(Level level, int xt, int yt) {
        return false;
    }

    /** What happens when you are inside the tile (ex: lava) */
    public void steppedOn(Level level, int xt, int yt, Entity entity) {
    }

    /** What happens when you hit an item on a tile (ex: Pickaxe on rock) */
    public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
        return false;
    }
    
	/**
	 * Executed when the tile is exploded.
	 * The call for this method is done just before the tiles are changed to exploded tiles.
	 * @param level The level we are on.
	 * @param xt X position of the tile.
	 * @param yt Y position of the tile.
	 * @return true if successful.
	 */
	public boolean onExplode(Level level, int xt, int yt) {
		return false;
	}

    /** Sees if the tile connects to a fluid. */
    public boolean connectsToLiquid() {
        return connectsToFluid;
    }

    public int getData(String data) {
        try {
            return Integer.parseInt(data);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public boolean matches(int thisData, String tileInfo) {
        return name.equals(tileInfo.split("_")[0]);
    }

    public String getName(int data) {
        return name;
    }

    public static String getData(int depth, int x, int y) {
        try {
            byte lvlidx = (byte) World.lvlIdx(depth);
            Level curLevel = World.levels[lvlidx];
            int pos = x + curLevel.w * y;

            int tileid = curLevel.tiles[pos];
            int tiledata = curLevel.data[pos];

            return lvlidx + ";" + pos + ";" + tileid + ";" + tiledata;
        } catch (NullPointerException | IndexOutOfBoundsException ignored) {
        }

        return "";
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Tile))
            return false;
        Tile o = (Tile) other;
        return name.equals(o.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
