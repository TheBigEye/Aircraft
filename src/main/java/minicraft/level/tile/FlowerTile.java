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

public class FlowerTile extends Tile {
	
	public enum Flower {
		DAISY("Daisy", new Sprite(5, 11, 1)),
		ROSE("Rose", new Sprite(6, 11, 1)),
		DANDELION("Dandelion", new Sprite(5, 12, 1)),
		POPPY("Poppy", new Sprite(6, 12, 1));

		private final String name;
		private final Sprite sprite;

		Flower(String name, Sprite sprite) {
			this.name = name;
			this.sprite = sprite;
		}
	}
	
	private Flower flower;

	protected FlowerTile(Flower flower) {
		super(flower.name, (ConnectorSprite) null);
		this.flower = flower;

		connectsToGrass = true;
		maySpawn = true;
	}

	@Override
	public boolean tick(Level level, int xt, int yt) {
		if (random.nextInt(30) != 0) {
			return false;
		}

		int xn = xt;
		int yn = yt;

		if (random.nextBoolean()) {
			xn += (random.nextInt(2) * 2) - 1;
		} else {
			yn += (random.nextInt(2) * 2) - 1;
		}

		if (level.getTile(xn, yn) == Tiles.get("Dirt")) {
			level.setTile(xn, yn, Tiles.get("Grass"));
		}
		return false;
	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		Tiles.get("Grass").render(screen, level, x, y);

		int data = level.getData(x, y);
		int shape = (data >> 4) % 2;

		x <<= 4;
		y <<= 4;
		
		flower.sprite.render(screen, x + 8 * shape, y);
		flower.sprite.render(screen, x + 8 * ((shape == 0) ? 1 : 0), y + 8);
	}

	@Override
	public boolean interact(Level level, int x, int y, Player player, Item item, Direction attackDir) {
	    if (!(item instanceof ToolItem)) {
	        return false;
	    }

	    // This avoids repeating tools checks
	    ToolItem tool = (ToolItem) item;
	    ToolType toolType = tool.type;
	    
		if (toolType == ToolType.Shears) {
			if (player.payStamina(2 - tool.level) && tool.payDurability()) {
				Sound.playAt("genericHurt", x << 4, y << 4);

				level.setTile(x, y, Tiles.get("Grass"));
				
				level.dropItem((x << 4) + 8, (y << 4) + 8, 2, 2, Items.get(flower.name));
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hurt(Level level, int x, int y, Mob source, int hurtDamage, Direction attackDir) {
		level.dropItem((x << 4) + 8, (y << 4) + 8, 0, 1, Items.get(flower.name));
		level.setTile(x, y, Tiles.get("Grass"));
		return true;
	}
}
