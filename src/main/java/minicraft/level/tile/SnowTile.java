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

    static Sprite steppedOn_sprite;
    static Sprite normal_sprite = new Sprite(3, 10, 2, 2, 1);

    static {
        Sprite.Px[][] pixels = new Sprite.Px[2][2];
        pixels[0][0] = new Sprite.Px(3, 12, 0, 1); // steps in snow
        pixels[0][1] = new Sprite.Px(4, 10, 0, 1);
        pixels[1][0] = new Sprite.Px(3, 11, 0, 1);
        pixels[1][1] = new Sprite.Px(3, 12, 0, 1);
        steppedOn_sprite = new Sprite(pixels);
    }

    private ConnectorSprite sprite = new ConnectorSprite(SnowTile.class, new Sprite(0, 10, 3, 3, 1), normal_sprite) {
        @Override
        public boolean connectsTo(Tile tile, boolean isSide) {
            if (!isSide) {
                return true;
            }
            return tile.connectsToSnow;
        }
    };

    protected SnowTile(String name) {
        super(name, (ConnectorSprite) null);
        connectorSprite = sprite;
        connectsToSnow = true;
        maySpawn = true;
    }

    @Override
    public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
        if (!(item instanceof ToolItem)) {
            return false;
        }

        ToolItem tool = (ToolItem) item;
        ToolType toolType = tool.type;

        if (toolType == ToolType.Shovel) {
            if (player.payStamina(4 - tool.level) && tool.payDurability()) {
                level.setTile(xt, yt, Tiles.get("Dirt"));
                Sound.Tile_snow_3.playOnGui();
                level.dropItem(xt * 16 + 8, yt * 16 + 8, 1, 2, Items.get("Snow Ball"));
                return true;
            }
        }

        return false;
    }

    @Override
    public void render(Screen screen, Level level, int x, int y) {
        boolean steppedOn = level.getData(x, y) > 0;

        if (steppedOn) {
            connectorSprite.full = SnowTile.steppedOn_sprite;
        } else {
            connectorSprite.full = SnowTile.normal_sprite;
        }

        connectorSprite.sparse.color = DirtTile.dCol(level.depth);
        connectorSprite.render(screen, level, x, y);
    }

    @Override
    public void steppedOn(Level level, int x, int y, Entity entity) {
        if (entity instanceof Mob) {
            level.setData(x, y, 10);
        }
        
        if (entity instanceof Player) {
            level.setData(x, y, 10);
            
            if (random.nextInt(2) == 1) {
            	// Random snow walking sound
            	switch (random.nextInt(4)) {
            		case 0: Sound.Tile_snow.playOnGui(); break;
            		case 1: Sound.Tile_snow.playOnGui(); break;
            		case 2: Sound.Tile_snow_2.playOnGui(); break;
            		case 3: Sound.Tile_snow_3.playOnGui(); break;
            		case 4: Sound.Tile_snow_4.playOnGui(); break;
            		default: Sound.Tile_snow_4.playOnGui(); break;
            	}    
            }
        }
    }

    @Override
    public boolean tick(Level level, int x, int y) {
        int damage = level.getData(x, y);
        if (damage > 0) {
            level.setData(x, y, damage - 1);
            return true;
        }
        return false;
    }
}
