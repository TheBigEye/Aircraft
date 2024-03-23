package minicraft.level.tile;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.graphic.Color;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;

public class DirtTile extends Tile {
    private static Sprite[] levelSprite = new Sprite[4];
    static {
        levelSprite[0] = new Sprite(57, 0, 2, 2, 1);
        levelSprite[1] = new Sprite(59, 0, 2, 2, 1);
        levelSprite[2] = new Sprite(61, 2, 2, 2, 1);
    }

    public static int dirtColor(int depth) {
        switch (depth) {
			case 1: return Color.get(1, 112, 153, 152); // Sky.
	        case 0: return Color.get(1, 129, 105, 83); // surface.
	        case -4: return Color.get(1, 44, 21, 67); // dungeons.
	        case 2: return Color.get(1, 73, 55, 44); // the void.
	        default: return Color.get(1, 102); // caves.
        }
    }

    protected static int dirtIndex(int depth) {
        switch (depth) {
	        case 0: return 0; // surface
	        case -4: return 2; // dungeons
	        default: return 1; // caves
        }
    }

    protected DirtTile(String name) {
        super(name, levelSprite[0]);
        maySpawn = true;
    }

    @Override
    public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == ToolType.Shovel) {
                if (player.payStamina(4 - tool.level) && tool.payDurability()) {
                	Sound.playAt("genericHurt", xt << 4, yt << 4);
                    level.setTile(xt, yt, Tiles.get("Hole"));
                    level.dropItem((xt << 4) + 8, (yt << 4) + 8, Items.get("Dirt"));

                    if (random.nextInt(64) == 0) { // 2% chance to drop bones
                        level.dropItem((xt << 4) + 8, (yt << 4) + 8, Items.get("Bone"));
                    }

                    return true;
                }
            }
            if (tool.type == ToolType.Hoe) {
                if (player.payStamina(4 - tool.level) && tool.payDurability()) {
                	Sound.playAt("genericHurt", xt << 4, yt << 4);
                    level.setTile(xt, yt, Tiles.get("Farmland"));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void render(Screen screen, Level level, int x, int y) {
        levelSprite[dirtIndex(level.depth)].render(screen, x << 4, y << 4, 0);
    }
}
