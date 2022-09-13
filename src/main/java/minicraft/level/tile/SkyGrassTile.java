package minicraft.level.tile;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.gfx.ConnectorSprite;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;

public class SkyGrassTile extends Tile {
    private static ConnectorSprite sprite = new ConnectorSprite(SkyGrassTile.class, new Sprite(44, 6, 3, 3, 1), new Sprite(47, 8, 2, 2, 1), new Sprite(47, 6, 2, 2, 1)) {

        @Override
        public boolean connectsTo(Tile tile, boolean isSide) { // Sky grass cannot connect with these tiles
        	 return tile != Tiles.get("Infinite fall") && tile != Tiles.get("Ferrosite") && tile != Tiles.get("Cloud cactus") && tile != Tiles.get("Cloud Hole");
        }

    };

    protected SkyGrassTile(String name) {
    	super(name, (ConnectorSprite) null);
        //csprite.sides = csprite.sparse;
        connectsToSkyGrass = true;
        //connectsToFerrosite = true;
        maySpawn = true;
    }

    @Override
    public boolean tick(Level level, int xt, int yt) {

        if (random.nextInt(40) != 0) {
            return false;
        }

        int xn = xt;
        int yn = yt;

        if (random.nextBoolean()) {
            xn += random.nextInt(2) * 2 - 1;
        } else {
            yn += random.nextInt(2) * 2 - 1;
        }

        if (level.getTile(xn, yn) == Tiles.get("Sky dirt")) {
            level.setTile(xn, yn, this);
        }

        return false;
    }

    @Override
    public void render(Screen screen, Level level, int x, int y) {
        if (Tiles.get("cloud") != null) {
            Tiles.get("cloud").render(screen, level, x, y);
        } else {
            Tiles.get("Ferrosite").render(screen, level, x, y);
        }
        sprite.render(screen, level, x, y);
    }

    @Override
    public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;

            if (tool.type == ToolType.Shovel) {
                if (player.payStamina(4 - tool.level) && tool.payDurability()) {
                    level.setTile(xt, yt, Tiles.get("Sky dirt")); // would allow you to shovel cloud, I think.
                    Sound.Tile_generic_hurt.play();
                    if (random.nextInt(20) == 0) { // 20% chance to drop sky seeds
                        level.dropItem(xt * 16 + 8, yt * 16 + 8, 2, Items.get("Sky seeds"));
                    }
                    return true;
                }
            }

            if (tool.type == ToolType.Pickaxe) {
                if (player.payStamina(4 - tool.level) && tool.payDurability()) {
                    // level.setTile(xt, yt, Tiles.get("path"));
                    Sound.Tile_generic_hurt.play();
                }
            }
            
            if (tool.type == ToolType.Hoe) {
                if (player.payStamina(4 - tool.level) && tool.payDurability()) {
                    level.setTile(xt, yt, Tiles.get("sky farmland"));
                    Sound.Tile_generic_hurt.play();
                    return true;
                }
            }
            
        }
        return false;
    }
}
