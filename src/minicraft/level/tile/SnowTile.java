package minicraft.level.tile;


import minicraft.entity.Entity;
import minicraft.entity.Player;
import minicraft.gfx.Color;
import minicraft.gfx.ConnectorSprite;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;

public class SnowTile extends Tile {
  static Sprite steppedOn;
  
  static Sprite normal = Sprite.dots(Color.get(555, 555, 444, 444));
  
  static {
    Sprite.Px[][] pixels = new Sprite.Px[2][2];
    pixels[0][0] = new Sprite.Px(3, 1, 0);
    pixels[0][1] = new Sprite.Px(1, 0, 0);
    pixels[1][0] = new Sprite.Px(1, 0, 0);
    pixels[1][1] = new Sprite.Px(3, 1, 0);
    steppedOn = new Sprite(pixels, Color.get(555, 555, 444, 444));
  }
  
  private ConnectorSprite sprite = new ConnectorSprite(SnowTile.class, new Sprite(11, 0, 3, 3, Color.get(444, 555, 444, 321), 3), normal) {
      public boolean connectsTo(Tile tile, boolean isSide) {
        if (!isSide)
          return true; 
        return tile.connectsToSnow;
      }
    };
  
  protected SnowTile(String name) {
    super(name, (ConnectorSprite)null);
    this.csprite = this.sprite;
    this.connectsToSnow = true;
    this.maySpawn = true;
  }
  
  public void render(Screen screen, Level level, int x, int y) {
    boolean steppedOn = (level.getData(x, y) > 0);
    if (steppedOn) {
      this.csprite.full = SnowTile.steppedOn;
    } else {
      this.csprite.full = Sprite.dots(Color.get(555, 555, 444, 444));
    } 
    this.csprite.render(screen, level, x, y);
  }
  
  public void tick(Level level, int x, int y) {
    int d = level.getData(x, y);
    if (d > 0)
      level.setData(x, y, d - 1); 
  }
  
  public void steppedOn(Level level, int x, int y, Entity entity) {
    if (entity instanceof minicraft.entity.Mob)
      level.setData(x, y, 10); 
  }
  
	public boolean interact(Level level, int xt, int yt, Player player, Item item, int attackDir) {
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Shovel) {
				if (player.payStamina(4 - tool.level)) {
					level.setTile(xt, yt, Tiles.get("dirt"));
					level.dropItem(xt*16, yt*16, Items.get("sand"));
					return true;
				}
			}
		}
		return false;
	}
}


