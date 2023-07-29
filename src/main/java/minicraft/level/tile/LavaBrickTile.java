package minicraft.level.tile;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.graphic.Sprite;
import minicraft.item.Item;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;


// TODO: replace this with Magma tile
public class LavaBrickTile extends Tile {
	private static Sprite sprite = new Sprite(19, 2, 2, 2, 1);

	protected LavaBrickTile(String name) {
		super(name, sprite);
	}

	public void bumpedInto(Level level, int x, int y, Entity entity) {
		if (entity instanceof Mob) {
			((Mob) entity).hurt(this, x, y, 3);
		}
	}

	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
	    if (!(item instanceof ToolItem)) {
	        return false;
	    }

	    // This avoids repeating tools checks
	    ToolItem tool = (ToolItem) item;
	    ToolType toolType = tool.type;
	    
		if (toolType == ToolType.Pickaxe) {
			if (player.payStamina(4 - tool.level) && tool.payDurability()) {
				Sound.genericHurt.playOnLevel(xt << 4, yt << 4);
				level.setTile(xt, yt, Tiles.get("Lava"));
				return true;
			}
		}
		return false;
	}

	public boolean mayPass(Level level, int x, int y, Entity entity) {
		return entity.canWool();
	}
}
