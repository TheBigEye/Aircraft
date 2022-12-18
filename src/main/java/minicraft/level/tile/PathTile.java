package minicraft.level.tile;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;

public class PathTile extends Tile {
	private static Sprite sprite = new Sprite(8, 2, 2, 2, 1);

	public PathTile(String name) {
		super(name, sprite);
		connectsToGrass = false;
		maySpawn = true;
	}

	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
	    if (!(item instanceof ToolItem)) {
	        return false;
	    }

	    // This avoids repeating tools checks
	    ToolItem tool = (ToolItem) item;
	    ToolType toolType = tool.type;
		
		if (toolType == ToolType.Shovel) {
			if (player.payStamina(4 - tool.level) && tool.payDurability()) {
				level.setTile(xt, yt, Tiles.get("Hole"));
				Sound.genericHurt.playOnWorld(xt * 16, yt * 16);
				level.dropItem(xt * 16 + 8, yt * 16 + 8, Items.get("Dirt"));
				return true;
			}
		}
		return false;
	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		sprite.render(screen, x * 16, y * 16, 0, DirtTile.dCol(level.depth));
	}
}
