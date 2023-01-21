package minicraft.level.tile;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Player;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;

public class WoolTile extends Tile {

	public enum WoolType {
		NORMAL(new Sprite(10, 30, 2, 2, 1)),
		LIGHT_GRAY(new Sprite(0, 28, 2, 2, 1)),
		GRAY(new Sprite(8, 28, 2, 2, 1)),
		BLACK(new Sprite(14, 30, 2, 2, 1)),
		GREEN(new Sprite(6, 30, 2, 2, 1)),
		LIME(new Sprite(14, 28, 2, 2, 1)),
		YELLOW(new Sprite(12, 30, 2, 2, 1)),
		ORANGE(new Sprite(0, 30, 2, 2, 1)),
		BROWN(new Sprite(4, 30, 2, 2, 1)),
		RED(new Sprite(10, 28, 2, 2, 1)),
		PINK(new Sprite(8, 28, 2, 2, 1)),
		MAGENTA(new Sprite(4, 28, 2, 2, 1)),
		PURPLE(new Sprite(8, 30, 2, 2, 1)),
		BLUE(new Sprite(12, 28, 2, 2, 1)),
		CYAN(new Sprite(2, 28, 2, 2, 1)),
		LIGHT_BLUE(new Sprite(2, 30, 2, 2, 1));

		public final Sprite sprite;

		/**
		 * Create a type of wool.
		 *
		 * @param sprite The sprite for the type of wool.
		 */
		WoolType(Sprite sprite) {
			this.sprite = sprite;
		}
	}

	public WoolTile(String name, WoolType woolType) {
		super(name, woolType.sprite);
	}

	@Override
	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
	    if (!(item instanceof ToolItem)) {
	        return false;
	    }

	    ToolItem tool = (ToolItem) item;
	    ToolType toolType = tool.type;
	    
		if (toolType == ToolType.Shears) {
			if (player.payStamina(3 - tool.level) && tool.payDurability()) {
				level.setTile(xt, yt, Tiles.get("Hole"));
				Sound.genericHurt.playOnGui();
				level.dropItem((xt << 4) + 8, (yt << 4) + 8, Items.get(name));
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean mayPass(Level level, int x, int y, Entity entity) {
		return entity.canWool();
	}
}