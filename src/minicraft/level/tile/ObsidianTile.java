package minicraft.level.tile;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.gfx.ConnectorSprite;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;

public class ObsidianTile extends Tile {
	private static ConnectorSprite sprite = new ConnectorSprite(ObsidianTile.class, new Sprite(30, 6, 3, 3, 1, 3), new Sprite(33, 6, 2, 2, 1))
	{
		public boolean connectsTo(Tile tile, boolean isSide) {
			if(!isSide) return true;
			return tile.connectsToObsidian;
		}
	};
	
	protected ObsidianTile(String name) {
		super(name, sprite);
		csprite.sides = csprite.sparse;
		connectsToObsidian = true;
		maySpawn = true;
	}

	public void tick(Level level, int xt, int yt) {
		// TODO revise this method.
		if (random.nextInt(40) != 0) return;

	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		sprite.sparse.color = DirtTile.dCol(level.depth);
		sprite.render(screen, level, x, y);
	}

	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Shovel) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					Sound.monsterHurt.play();
					return true;
				}
			}
			if (tool.type == ToolType.Hoe) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					Sound.monsterHurt.play();
					return true;
				}
			}
			if (tool.type == ToolType.Pickaxe && tool.level == 0) {
				Sound.monsterHurt.play();
			}
			if (tool.type == ToolType.Pickaxe && tool.level == 1) {
				Sound.monsterHurt.play();
			}
			if (tool.type == ToolType.Pickaxe && tool.level == 2) {
				Sound.monsterHurt.play();
			}
			if (tool.type == ToolType.Pickaxe && tool.level == 3) {
				Sound.monsterHurt.play();
			}
			if (tool.type == ToolType.Pickaxe && tool.level == 4) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					level.setTile(xt, yt, Tiles.get("hole"));
					level.dropItem(xt*16+8, yt*16+8, 1, 3, Items.get("Obsidian"));
					Sound.monsterHurt.play();
				}
			}
		}
		return false;
	}
}

