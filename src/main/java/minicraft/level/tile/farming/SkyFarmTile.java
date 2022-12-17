package minicraft.level.tile.farming;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.ItemEntity;
import minicraft.entity.mob.Player;
import minicraft.entity.mob.boss.AirWizard;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

public class SkyFarmTile extends Tile {
    private static Sprite sprite = new Sprite(12, 3, 2, 2, 1, true, new int[][] { { 1, 0 }, { 0, 1 } });

    public SkyFarmTile(String name) {
        super(name, sprite);
        connectsToSkyGrass = true;
        connectsToSkyDirt = true;

    }

    protected SkyFarmTile(String name, Sprite sprite) {
        super(name, sprite);
        connectsToSkyGrass = true;
        connectsToSkyDirt = true;

    }

    @Override
    public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == ToolType.Shovel) {
                if (player.payStamina(4 - tool.level) && tool.payDurability()) {
                    level.setTile(xt, yt, Tiles.get("Sky dirt"));
                    Sound.genericHurt.playOnGui();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean tick(Level level, int xt, int yt) {
        int age = level.getData(xt, yt);
        if (age < 5)
            level.setData(xt, yt, age + 1);
        return true;
    }

    @Override
    public void steppedOn(Level level, int xt, int yt, Entity entity) {
        if (entity instanceof ItemEntity || entity instanceof AirWizard) return;
        if (random.nextInt(60) != 0) return;
        if (level.getData(xt, yt) < 5) return;

        level.setTile(xt, yt, Tiles.get("Sky dirt"));
    }
}
