package minicraft.level.tile;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
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

public class MushroomTile extends Tile {
	
	protected enum Mushroom {
		BROWN("Brown Mushroom", new Sprite(33, 16, 1)),
		RED("Red Mushroom", new Sprite(32, 16, 1));
		
		private final String name;
    	private final Sprite sprite;

    	Mushroom(String name, Sprite sprite) {
			this.name = name;
			this.sprite = sprite;
		}
	}

	private Mushroom mushroom;

    protected MushroomTile(Mushroom mushroom) {
        super(mushroom.name, (ConnectorSprite) null);
    	this.mushroom = mushroom;
    	
    	connectsToMycelium = true;
    	maySpawn = true;
    }
  
	@Override
	public boolean interact(Level level, int x, int y, Player player, Item item, Direction attackDir) {
        if (!(item instanceof ToolItem)) {
            return false;
        }

        ToolItem tool = (ToolItem) item;
        ToolType toolType = tool.type;

		if (toolType == ToolType.Shovel) {
			if (player.payStamina(2 - tool.level) && tool.payDurability()) {
				Sound.playAt("genericHurt", x << 4, y << 4);
				level.setTile(x, y, Tiles.get("Mycelium"));
				level.dropItem((x << 4) + 8, (y << 4) + 8, Items.get(mushroom.name));
				return true;
			}
		}
		return false;
	}
    
    @Override
    public boolean hurt(Level level, int x, int y, Mob source, int hurtDamage, Direction attackDir) {
    	level.dropItem((x << 4) + 8, (y << 4) + 8, 0, 1, Items.get(mushroom.name));
        level.setTile(x, y, Tiles.get("Mycelium"));
        return true;
    }

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		Tiles.get("Mycelium").render(screen, level, x, y);

		int data = level.getData(x, y);
		int shape = (data >> 4) % 2;

		x <<= 4;
		y <<= 4;

		mushroom.sprite.render(screen, x + 8 * shape, y);
		mushroom.sprite.render(screen, x + 8 * (shape == 0 ? 1 : 0), y + 8);
	}

    @Override
    public boolean tick(Level level, int xt, int yt) {
        int damage = level.getData(xt, yt);
        if (damage > 0) {
            level.setData(xt, yt, damage - 1);
            return true;
        }
        return false;
    } 
}