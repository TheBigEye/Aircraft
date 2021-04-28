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

public class SkyGrassTile extends Tile {
	private static ConnectorSprite sprite = new ConnectorSprite(SkyGrassTile.class, new Sprite(44, 6, 3, 3, 1, 3),
			new Sprite(47, 6, 2, 2, 1)) {
		public boolean connectsTo(Tile tile, boolean isSide) {
			if (!isSide)
				return true;
			return tile.connectsToSkyGrass;
		}
	};

	protected SkyGrassTile(String name) {
		super(name, sprite);
		csprite.sides = csprite.sparse;
		connectsToSkyGrass = true;
		maySpawn = true;
	}

	public void tick(Level level, int xt, int yt) {

	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		Tiles.get("Cloud").render(screen, level, x, y);
		sprite.render(screen, level, x, y);
	}

	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Shovel) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					level.setTile(xt, yt, Tiles.get("Cloud")); // would allow you to shovel cloud, I think.
					Sound.monsterHurt.play();
					if (random.nextInt(5) == 0) { // 20% chance to drop seeds
						level.dropItem(xt * 16 + 8, yt * 16 + 8, 2, Items.get("dirt"));
					}
					return true;
				}
			}
			if (tool.type == ToolType.Hoe) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					level.setTile(xt, yt, Tiles.get("dirt"));
					Sound.monsterHurt.play();
					if (random.nextInt(5) != 0) { // 80% chance to drop seeds
						level.dropItem(xt * 16 + 8, yt * 16 + 8, Items.get("dirt"));
					}
					return true;
				}
			}
			if (tool.type == ToolType.Pickaxe) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					level.setTile(xt, yt, Tiles.get("path"));
					Sound.monsterHurt.play();
				}
			}
		}
		return false;
	}
}

