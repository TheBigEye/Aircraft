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

public class SnowTile extends Tile {
	static Sprite steppedOn, normal = new Sprite(3, 10, 2, 2, 1);
	static {
		Sprite.Px[][] pixels = new Sprite.Px[2][2];
		pixels[0][0] = new Sprite.Px(3, 12, 0, 1); //steps in snow
		pixels[0][1] = new Sprite.Px(4, 10, 0, 1);
		pixels[1][0] = new Sprite.Px(3, 11, 0, 1);
		pixels[1][1] = new Sprite.Px(3, 12, 0, 1);
		steppedOn = new Sprite(pixels);
	}
	
	private ConnectorSprite sprite = new ConnectorSprite(SnowTile.class, new Sprite(0, 10, 3, 3, 1, 3), normal)
	{
		public boolean connectsTo(Tile tile, boolean isSide) {
			if(!isSide) return true;
			return tile.connectsToSnow;
		}
	};
	
	protected SnowTile(String name) {
		super(name, (ConnectorSprite)null);
		csprite = sprite;
		connectsToSnow = true;
		maySpawn = true;
	}
	
	public void render(Screen screen, Level level, int x, int y) {
		boolean steppedOn = level.getData(x, y) > 0;
		
		if(steppedOn) csprite.full = SnowTile.steppedOn;			
		else csprite.full = SnowTile.normal;

		csprite.sparse.color = DirtTile.dCol(level.depth);
		
		csprite.render(screen, level, x, y);
		

	}
	

	public void tick(Level level, int x, int y) {
		int d = level.getData(x, y);
		if (d > 0) level.setData(x, y, d - 1);
		
		
	}

	public void steppedOn(Level level, int x, int y, Entity entity) {
		if (entity instanceof Mob) {
			level.setData(x, y, 10);
		}
		if (entity instanceof Player) {
			level.setData(x, y, 10);
			
			if (random.nextInt(50) == 0) {
				Sound.Snow.play();
			}
			if (random.nextInt(50) == 10) {
				Sound.Snow.play();
			}
			if (random.nextInt(50) == 50) {
				Sound.Snow.play();
			}

		}				
	}

	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Shovel) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					level.setTile(xt, yt, Tiles.get("dirt"));
					Sound.Snow.play();
					level.dropItem(xt*16+8, yt*16+8, 1, 2, Items.get("Snow Ball"));
					return true;
				}
			}
		}
		return false;
	}
}
