package minicraft.level.tile;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.gfx.ConnectorSprite;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;

public class FerrositeTile extends Tile {
	static Sprite steppedOn, normal = new Sprite(47, 10, 2, 2, 1);
	static {
		Sprite.Px[][] pixels = new Sprite.Px[2][2];
		pixels[0][0] = new Sprite.Px(47, 12, 0, 1); 
		pixels[0][1] = new Sprite.Px(48, 10, 0, 1);
		pixels[1][0] = new Sprite.Px(47, 11, 0, 1);
		pixels[1][1] = new Sprite.Px(47, 12, 0, 1); 
		steppedOn = new Sprite(pixels);
	}

	private ConnectorSprite sprite = new ConnectorSprite(FerrositeTile.class, new Sprite(44, 10, 3, 3, 1, 3), normal) {
		public boolean connectsTo(Tile tile, boolean isSide) {
			if (!isSide)
				return true;
			return tile.connectsToFerrosite;
		}
	};

	protected FerrositeTile(String name) {
		super(name, (ConnectorSprite) null);
		csprite = sprite;
		connectsToFerrosite = true;
		maySpawn = true;
	}

	public void render(Screen screen, Level level, int x, int y) {
		boolean steppedOn = level.getData(x, y) > 0;

		if (steppedOn)
			csprite.full = FerrositeTile.steppedOn;
		else
			csprite.full = FerrositeTile.normal;

		Tiles.get("Cloud").render(screen, level, x, y);

		csprite.render(screen, level, x, y);
	}

	public void tick(Level level, int x, int y) {
		int d = level.getData(x, y);
		if (d > 0)
			level.setData(x, y, d - 1);
	}

	public void steppedOn(Level level, int x, int y, Entity entity) {
		if (entity instanceof Mob) {
			level.setData(x, y, 10);
		}
	}

	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Shovel) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					level.setTile(xt, yt, Tiles.get("cloud"));
					Sound.monsterHurt.play();
					level.dropItem(xt * 16 + 8, yt * 16 + 8, Items.get("sand"));
					return true;
				}
			}
		}
		return false;
	}
}
