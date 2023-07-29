package minicraft.level.tile;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.level.Level;

public class DoorTile extends Tile {
    private Sprite closedSprite;
    private Sprite openSprite;

    protected Material type;

    protected DoorTile(Material type) {
        super(type.name() + " Door", (Sprite) null);
        this.type = type;
        switch (type) {
	        case Oak:
	            closedSprite = new Sprite(5, 28, 2, 2, 1);
	            openSprite = new Sprite(3, 28, 2, 2, 1);
	            break;
	        case Stone:
	            closedSprite = new Sprite(14, 28, 2, 2, 1);
	            openSprite = new Sprite(12, 28, 2, 2, 1);
	            break;
	        case Obsidian:
	            closedSprite = new Sprite(23, 28, 2, 2, 1);
	            openSprite = new Sprite(21, 28, 2, 2, 1);
	            break;
	        case Spruce:
	            closedSprite = new Sprite(32, 28, 2, 2, 1);
	            openSprite = new Sprite(30, 28, 2, 2, 1);
	            break;
	        case Birch:
	            closedSprite = new Sprite(41, 28, 2, 2, 1);
	            openSprite = new Sprite(39, 28, 2, 2, 1);
	            break;
	        case Holy:
	            closedSprite = new Sprite(50, 28, 2, 2, 1);
	            openSprite = new Sprite(48, 28, 2, 2, 1);
	            break;
        }
        sprite = closedSprite;
    }

    @Override
    public void render(Screen screen, Level level, int x, int y) {
        boolean closed = level.getData(x, y) == 0;
        Sprite currentSprite = closed ? closedSprite : openSprite;
        currentSprite.render(screen, x << 4, y << 4);
    }

    @Override
    public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == type.getRequiredTool()) {
                if (player.payStamina(4 - tool.level) && tool.payDurability()) {
                	Sound.genericHurt.playOnLevel(xt << 4, yt << 4);
                    level.setTile(xt, yt, Tiles.get(id + 6)); // will get the corresponding floor tile.
                    level.dropItem((xt << 4) + 8, (yt << 4) + 8, Items.get(type.name() + " Door"));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hurt(Level level, int x, int y, Mob source, int hurtDamage, Direction attackDir) {
        if (source instanceof Player) {
            boolean closed = level.getData(x, y) == 0;
            level.setData(x, y, closed ? 1 : 0);
        }
        return false;
    }

    @Override
    public boolean mayPass(Level level, int x, int y, Entity entity) {
        boolean closed = level.getData(x, y) == 0;
        return !closed;
    }
}
