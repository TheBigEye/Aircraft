package minicraft.level.tile;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Firefly;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.entity.mob.Slime;
import minicraft.gfx.Color;
import minicraft.gfx.ConnectorSprite;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;

public class SandTile extends Tile {

    static Sprite steppedOn_sprite;
    static Sprite normal_sprite = new Sprite(9, 6, 2, 2, 1);

    static {
        Sprite.Px[][] pixels = new Sprite.Px[2][2];
        pixels[0][0] = new Sprite.Px(9, 8, 0, 1);
        pixels[0][1] = new Sprite.Px(10, 6, 0, 1);
        pixels[1][0] = new Sprite.Px(9, 7, 0, 1);
        pixels[1][1] = new Sprite.Px(9, 8, 0, 1);
        steppedOn_sprite = new Sprite(pixels);
    }

    protected static int sCol(int depth) {
        switch (depth) {
	        case 0: return Color.get(1, 237, 190, 82); // surface.
	        case -4: return Color.get(1, 237, 190, 82); // dungeons.
	        default: return Color.get(1, 237, 190, 82); // caves.
        }
    }

    private ConnectorSprite sprite = new ConnectorSprite(SandTile.class, new Sprite(6, 6, 3, 3, 1), normal_sprite) {
        @Override
        public boolean connectsTo(Tile tile, boolean isSide) {
            if (!isSide) {
                return true;
            }
            return tile.connectsToSand;
        }
    };

    protected SandTile(String name) {
        super(name, (ConnectorSprite) null);
        connectorSprite = sprite;
        connectsToSand = true;
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
                Sound.genericHurt.playOnGui();
                level.dropItem(xt * 16 + 8, yt * 16 + 8, Items.get("Sand"));
                return true;
            }
        }

        return false;
    }

    @Override
    public void render(Screen screen, Level level, int x, int y) {
        boolean steppedOn = level.getData(x, y) > 0;

        if (steppedOn) {
            connectorSprite.full = SandTile.steppedOn_sprite;
        } else {
            connectorSprite.full = SandTile.normal_sprite;
        }

        connectorSprite.sparse.color = DirtTile.dCol(level.depth);
        connectorSprite.render(screen, level, x, y);
    }

    @Override
    public void steppedOn(Level level, int x, int y, Entity entity) {
        if (entity instanceof Mob) {
            level.setData(x, y, 10);
        }
        if (entity instanceof Firefly || entity instanceof Slime) {
        	level.setData(x, y, 0);
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
