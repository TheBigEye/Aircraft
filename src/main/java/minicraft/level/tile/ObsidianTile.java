package minicraft.level.tile;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.graphic.ConnectorSprite;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;

public class ObsidianTile extends Tile {
    private static ConnectorSprite sprite = new ConnectorSprite(ObsidianTile.class, new Sprite(18, 33, 3, 3, 1), new Sprite(21, 33, 2, 2, 1)) {
        public boolean connectsTo(Tile tile, boolean isSide) {
            if (!isSide) {
                return true;
            }
            return tile.connectsToObsidian;
        }
    };

    protected ObsidianTile(String name) {
        super(name, sprite);
        connectorSprite.sides = connectorSprite.sparse;
        connectsToObsidian = true;
        maySpawn = true;
    }

    public boolean tick(Level level, int xt, int yt) {
        // TODO revise this method.
        if (random.nextInt(40) != 0) {
            return false;
        }
        return false;
    }

    @Override
    public void render(Screen screen, Level level, int x, int y) {
        sprite.sparse.color = DirtTile.dirtColor(level.depth);
        sprite.render(screen, level, x, y);
    }

    @Override
    public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
        if (!(item instanceof ToolItem)) {
            return false;
        }

        ToolItem tool = (ToolItem) item;
        ToolType toolType = tool.type;
        
        if (toolType == ToolType.Pickaxe && tool.level != 4) {
        	Sound.genericHurt.playOnLevel(xt << 4, yt << 4);
        } else {
            if (player.payStamina(4 - tool.level) && tool.payDurability()) {
            	Sound.genericHurt.playOnLevel(xt << 4, yt << 4);
                level.setTile(xt, yt, Tiles.get("Hole"));
                level.dropItem((xt << 4) + 8, (yt << 4) + 8, 1, 3, Items.get("Obsidian"));
                return true;
            }
        }
        return false;
    }
}
