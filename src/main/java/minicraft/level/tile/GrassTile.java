package minicraft.level.tile;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.graphic.ConnectorSprite;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;

public class GrassTile extends Tile {
	private static final ConnectorSprite sprite = new ConnectorSprite(GrassTile.class, new Sprite(0, 11, 3, 3, 1), new Sprite(3, 11, 2, 2, 1)) {
		@Override
		public boolean connectsTo(Tile tile, boolean isSide) {
			if (!isSide) {
				return true;
			}
			return tile.connectsToGrass;
		}
	};

	protected GrassTile(String name) {
		super(name, sprite);
		connectorSprite.sides = connectorSprite.sparse;
		connectsToGrass = true;
		maySpawn = true;
	}

	@Override
	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
	    if (!(item instanceof ToolItem)) {
	        return false;
	    }

	    // This avoids repeating tools checks
	    ToolItem tool = (ToolItem) item;
	    ToolType toolType = tool.type;

	    if (toolType == ToolType.Shovel) {
	        if (player.payStamina(4 - tool.level) && tool.payDurability()) {
	        	Sound.genericHurt.playOnLevel(xt << 4, yt << 4);
	            if (random.nextInt(5) == 0) { // 20% chance to drop seeds
	                level.dropItem((xt << 4) + 8, (yt << 4) + 8, 2, Items.get("Seeds"));
	            }
	            return true;
	        }
	    } else if (toolType == ToolType.Hoe) {
	        if (player.payStamina(4 - tool.level) && tool.payDurability()) {
	            Sound.genericHurt.playOnLevel(xt << 4, yt << 4);
	            
	            level.setTile(xt, yt, Tiles.get("Dirt"));
	            
	            if (random.nextInt(15) == 0) { // 80% chance to drop seeds
	                level.dropItem((xt << 4) + 8, (yt << 4) + 8, Items.get("Seeds"));
	            }
	            if (random.nextInt(64) == 0) { // 80% chance to drop seeds
	                level.dropItem((xt << 4) + 8, (yt << 4) + 8, Items.get("Dirt"));
	            }
	            return true;
	        }
	    } else if (toolType == ToolType.Pickaxe) {
	        if (player.payStamina(4 - tool.level) && tool.payDurability()) {
	        	Sound.genericHurt.playOnLevel(xt << 4, yt << 4);
	        	
	            level.setTile(xt, yt, Tiles.get("path"));

	            if (random.nextInt(5) == 0) { // 20% chance to drop seeds
	                level.dropItem((xt << 4) + 8, (yt << 4) + 8, 2, Items.get("Seeds"));
	            }
	            return true;
	        }
	    }

	    return false;
	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		sprite.sparse.color = DirtTile.dirtColor(level.depth);
		sprite.render(screen, level, x, y);
	}

	@Override
	public boolean tick(Level level, int xt, int yt) {
	    if (random.nextInt(40) != 0) return false;

	    int xn = xt;
	    int yn = yt;

	    switch (random.nextInt(2)) {
	        case 0: xn += random.nextInt(2) * 2 - 1; break;
	        case 1: yn += random.nextInt(2) * 2 - 1; break;
	    }

	    if (level.getTile(xn, yn) == Tiles.get("Dirt")) {
	        level.setTile(xn, yn, this);
	    }
	    return false;
	}
}
