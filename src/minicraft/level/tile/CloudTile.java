package minicraft.level.tile;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Player;
import minicraft.gfx.Color;
import minicraft.gfx.ConnectorSprite;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;

public class CloudTile extends Tile {
	private static ConnectorSprite sprite = new ConnectorSprite(CloudTile.class, new Sprite(0, 22, 3, 3, 1, 3),
			new Sprite(3, 24, 2, 2, 1, 3), new Sprite(3, 22, 2, 2, 1)) {
		public boolean connectsTo(Tile tile, boolean isSide) {
			return tile != Tiles.get("Infinite fall") && tile != Tiles.get("Ferrosite");
		}
	};

	protected CloudTile(String name) {
		super(name, sprite);
	}

	public boolean mayPass(Level level, int x, int y, Entity e) {
		return true;
	}

	

    //REMINDER: Do not touch this until version 0.6
	@Override
	public void render(Screen screen, Level level, int x, int y) {
		Tiles.get("Ferrosite").render(screen, level, x, y);
		sprite.render(screen, level, x, y);
	}
	
	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		// we don't want the tile to break when attacked with just anything, even in
		// creative mode
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Shovel && player.payStamina(5)) {
				level.setTile(xt, yt, Tiles.get("Ferrosite")); // would allow you to shovel cloud, I think.
				Sound.monsterHurt.play();
				level.dropItem(xt * 16 + 8, yt * 16 + 8, 1, 3, Items.get("cloud"));
				return true;
			}
		}
		return false;
		
	}

	public static int CloudCol(int depth) {
		
		return Color.get(1, 201, 201, 201);
	}
}
