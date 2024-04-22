package minicraft.level.tile;

import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.graphic.ConnectorSprite;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;

public class SnowTile extends Tile {

    static Sprite steppedOn_sprite;
    static Sprite normal_sprite = new Sprite(12, 16, 2, 2, 1);

    static {
        Sprite.Px[][] pixels = new Sprite.Px[2][2];
        pixels[0][0] = new Sprite.Px(14, 16, 0, 1);
        pixels[0][1] = new Sprite.Px(13, 16, 0, 1);
        pixels[1][0] = new Sprite.Px(12, 17, 0, 1);
        pixels[1][1] = new Sprite.Px(14, 16, 0, 1);
        steppedOn_sprite = new Sprite(pixels);
    }

    private ConnectorSprite sprite = new ConnectorSprite(SnowTile.class, new Sprite(9, 16, 3, 3, 1), normal_sprite) {
        @Override
        public boolean connectsTo(Tile tile, boolean isSide) {
            if (!isSide) {
                return true;
            }
            return tile.connectsToSnow;
        }
    };

    protected SnowTile(String name) {
        super(name, (ConnectorSprite) null);
        connectorSprite = sprite;
        connectsToSnow = true;
        maySpawn = true;
    }

    @Override
    public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
        if (!(item instanceof ToolItem)) {
            return false;
        }

        ToolItem tool = (ToolItem) item;
        ToolType toolType = tool.type;

        if (toolType == ToolType.Shovel) {
            if (player.payStamina(4 - tool.level) && tool.payDurability()) {
                level.setTile(xt, yt, Tiles.get("Dirt"));
                level.dropItem((xt << 4) + 8, (yt << 4) + 8, 1, 2, Items.get("Snow Ball"));
                return true;
            }
        }

        return false;
    }

    @Override
    public void render(Screen screen, Level level, int x, int y) {
        boolean steppedOn = level.getData(x, y) > 0;

        if (steppedOn) {
            connectorSprite.full = SnowTile.steppedOn_sprite;
        } else {
            connectorSprite.full = SnowTile.normal_sprite;
        }

        connectorSprite.sparse.color = DirtTile.dirtColor(level.depth);
        connectorSprite.render(screen, level, x, y);
    }

    @Override
    public void steppedOn(Level level, int x, int y, Entity entity) {
        if (entity instanceof Mob) {
            level.setData(x, y, 10);
        }
        
        if (entity instanceof Player) {
            level.setData(x, y, 10);
        }
    }

    @Override
    public boolean tick(Level level, int x, int y) {
        int damage = level.getData(x, y);
        if (damage > 0) {
            level.setData(x, y, damage - 1);
            return true;
        }
        return false;
    }
}
