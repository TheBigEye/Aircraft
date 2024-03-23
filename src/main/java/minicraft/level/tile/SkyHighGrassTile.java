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

public class SkyHighGrassTile extends Tile {
    private static ConnectorSprite sprite = new ConnectorSprite(SkyHighGrassTile.class, new Sprite(51, 6, 3, 3, 1), new Sprite(54, 6, 2, 2, 1)) {

		@Override
		public boolean connectsTo(Tile tile, boolean isSide) {
			if (!isSide)
				return true;
			return tile.connectsToSkyHighGrass;
		}
    };

    protected SkyHighGrassTile(String name) {
		super(name, sprite);
		connectorSprite.sides = connectorSprite.sparse;
        connectsToSkyHighGrass = true;
        maySpawn = true;
    }

    @Override
    public boolean tick(Level level, int xt, int yt) {

        if (random.nextInt(40) != 0) {
            return false;
        }

        int xn = xt;
        int yn = yt;

        if (random.nextBoolean()) {
            xn += random.nextInt(2) * 2 - 1;
        } else {
            yn += random.nextInt(2) * 2 - 1;
        }

        if (level.getTile(xn, yn) == Tiles.get("Sky Dirt")) {
            level.setTile(xn, yn, this);
        }
        return false;
    }

    @Override
    public void render(Screen screen, Level level, int x, int y) {
        Tiles.get("Sky grass").render(screen, level, x, y);

        sprite.render(screen, level, x, y);
    }

    @Override
    public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == ToolType.Shovel) {
                if (player.payStamina(4 - tool.level) && tool.payDurability()) {
                	Sound.playAt("genericHurt", xt << 4, yt << 4);
                    level.setTile(xt, yt, Tiles.get("Sky grass")); // would allow you to shovel cloud, I think.
                    if (random.nextInt(5) == 0) { // 45% chance to drop sky dirt
                        level.dropItem((xt << 4) + 8, (yt << 4) + 8, 2, Items.get("Sky dirt"));
                    }
                    return true;
                }
            }
            if (tool.type == ToolType.Pickaxe) {
                if (player.payStamina(1 - tool.level)) {
                    // level.setTile(xt, yt, Tiles.get("path"));
                	Sound.playAt("genericHurt", xt << 4, yt << 4);
                }
            }
        }
        return false;
    }
}
