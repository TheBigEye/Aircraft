package minicraft.level.tile.farming;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.ItemEntity;
import minicraft.entity.mob.Player;
import minicraft.entity.mob.VillagerMob;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.item.Item;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;
import minicraft.level.tile.DirtTile;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

public class FarmTile extends Tile {
    private static Sprite sprite = new Sprite(0, 38, 2, 2, 1, true, new int[][] { { 1, 0 }, { 0, 1 } });

    public FarmTile(String name) {
        super(name, sprite);
    }

    protected FarmTile(String name, Sprite sprite) {
        super(name, sprite);
    }
    
    @Override
    public void render(Screen screen, Level level, int x, int y) {
        sprite.render(screen, x << 4, y << 4, 0, DirtTile.dirtColor(level.depth));
    }

    @Override
    public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == ToolType.Shovel) {
                if (player.payStamina(4 - tool.level) && tool.payDurability()) {
                	Sound.genericHurt.playOnDisplay();
                    level.setTile(xt, yt, Tiles.get("Dirt"));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean tick(Level level, int xt, int yt) {
        int age = level.getData(xt, yt);
        if (age < 5) {
            level.setData(xt, yt, age + 1);
        }
        return true;
    }

    @Override
    public void steppedOn(Level level, int xt, int yt, Entity entity) {
        if (entity instanceof ItemEntity || entity instanceof VillagerMob) {
            return;
        }
        
        if (random.nextInt(60) != 0) {
            return;
        }
        
        if (level.getData(xt, yt) < 5) {
            return;
        }
        
        level.setTile(xt, yt, Tiles.get("Dirt"));

        
        switch (random.nextInt(3)) {
            case 0: Sound.Tile_farmland.playOnDisplay(); break;
            case 1: Sound.Tile_farmland_2.playOnDisplay(); break;
            case 2: Sound.Tile_farmland_3.playOnDisplay(); break;
            default:
            	Sound.Tile_farmland.playOnDisplay(); break;
        }
    }
}