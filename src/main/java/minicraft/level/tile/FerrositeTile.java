package minicraft.level.tile;

import minicraft.core.Game;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Player;
import minicraft.gfx.ConnectorSprite;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;

public class FerrositeTile extends Tile {
    private static ConnectorSprite sprite = new ConnectorSprite(FerrositeTile.class, new Sprite(12, 22, 3, 3, 1), new Sprite(15, 24, 2, 2, 1), new Sprite(15, 22, 2, 2, 1)) {
        @Override
        public boolean connectsTo(Tile tile, boolean isSide) {
            return tile != Tiles.get("Infinite fall");
        }
    };

    protected FerrositeTile(String name) {
        super(name, sprite);
        connectsToFerrosite = true;
        connectsToCloud = true;
        maySpawn = true;
    }

    @Override
    public boolean mayPass(Level level, int x, int y, Entity e) {
        return true;
    }

    @Override
    public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
    	
        // creative mode
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == ToolType.Shovel && player.payStamina(5)) {

                /* 
                 If the current level is the sky then when breaking the
                 ferrosite tile Infinite fall appears, if not, hole will appear
                */
                if (Game.currentLevel == 4) {
                    level.setTile(xt, yt, Tiles.get("Infinite fall"));
                } else {
                    level.setTile(xt, yt, Tiles.get("hole"));
                }

                Sound.Tile_generic_hurt.play();
                level.dropItem(xt * 16 + 8, yt * 16 + 8, 1, 3, Items.get("Ferrosite"));
                return true;
            }
        }
        return false;
    }

}
