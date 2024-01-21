package minicraft.level.tile;

import minicraft.core.Game;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.SmashParticle;
import minicraft.entity.particle.TextParticle;
import minicraft.graphic.Color;
import minicraft.graphic.ConnectorSprite;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;

/// this is the typical stone you see underground and on the surface, that gives coal.

public class AltarTile extends Tile {
    private ConnectorSprite sprite = new ConnectorSprite(AltarTile.class, new Sprite(27, 6, 3, 3, 1), new Sprite(32, 6, 2, 2, 1), new Sprite(30, 6, 2, 2, 1));
    
    protected AltarTile(String name) {
        super(name, (ConnectorSprite) null);
        connectorSprite = sprite;
    }
    
    private int tickTime = 0;
    
    private Sprite cornerSpriteA = new Sprite(30, 9, 1, 1, 1, 0);
    private Sprite cornerSpriteB = new Sprite(30, 9, 1, 1, 1, 1);
    private Sprite cornerSpriteC = new Sprite(31, 9, 1, 1, 1, 0);
    private Sprite cornerSpriteD = new Sprite(31, 9, 1, 1, 1, 1);
    
    private Sprite gemSprite = new Sprite(30, 8, 1, 1, 1, 0);
    

    @Override
    public boolean tick(Level level, int xt, int yt) {
        tickTime = (tickTime + 1) % 4;
        gemSprite = new Sprite(30 + tickTime % 2, 8, 1, 1, 1, 0);
        
        int damage = level.getData(xt, yt);
        if (damage > 0) {
            level.setData(xt, yt, damage - 1);
            return true;
        }
        return false;
    }

    @Override
    public void render(Screen screen, Level level, int x, int y) {
        boolean isCenterOfAltar = true;
        for (int dy = -1; dy <= 1 && isCenterOfAltar; dy++) {
            for (int dx = -1; dx <= 1 && isCenterOfAltar; dx++) {
                if (dx == 0 && dy == 0) continue;  // Skip the center tile
                if (!(level.getTile(x + dx, y + dy) instanceof AltarTile)) {
                    isCenterOfAltar = false;
                }
            }
        }
        
        sprite.sparse.color = DirtTile.dirtColor(level.depth);
        sprite.render(screen, level, x, y);

        if (isCenterOfAltar) {
            x <<= 4;
            y <<= 4;
            
            // center plate
            cornerSpriteA.render(screen, x, y - 2);
            cornerSpriteB.render(screen, x + 8, y - 2);
            cornerSpriteC.render(screen, x + 8, (y - 2) + 8);
            cornerSpriteD.render(screen, x, (y - 2) + 8);
            
            // center gem
            gemSprite.render(screen, x + 4, y + 1);
        }
    }


    @Override
    public boolean mayPass(Level level, int x, int y, Entity entity) {
        return true;
    }

    @Override
    public boolean hurt(Level level, int x, int y, Mob source, int hurtDamage, Direction attackDir) {
        hurt(level, x, y, hurtDamage);
        return true;
    }

    @Override
    public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
        // creative mode can just act like survival here
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == ToolType.Pickaxe && player.payStamina(4 - tool.level) && tool.payDurability()) {
                hurt(level, xt, yt, random.nextInt(10) + (tool.level) * 5 + 10);
                return true;
            }
        }
        return false;
    }

    @Override
    public void hurt(Level level, int x, int y, int hurtDamage) {
        int damage = level.getData(x, y) + hurtDamage;
        int altarHealth = 50;
        if (Game.isMode("Creative")) {
        	hurtDamage = damage = altarHealth;
        }
        
        Sound.genericHurt.playOnLevel(x << 4, y << 4);
        level.add(new SmashParticle(x << 4, y << 4));
        level.add(new TextParticle("" + hurtDamage, (x << 4) + 8, (y << 4) + 8, Color.RED));
        
        if (damage >= altarHealth) {
            level.dropItem((x << 4) + 8, (y << 4) + 8, 1, 1, Items.get("Holy Stone"));
            level.setTile(x, y, Tiles.get("Dirt"));
        } else {
            level.setData(x, y, damage);
        }
    }
    
    @Override
    public int getLightRadius(Level level, int x, int y) {
        boolean isCenterOfAltar = true;
        for (int dy = -1; dy <= 1 && isCenterOfAltar; dy++) {
            for (int dx = -1; dx <= 1 && isCenterOfAltar; dx++) {
                if (dx == 0 && dy == 0) continue;  // Skip the center tile
                if (!(level.getTile(x + dx, y + dy) instanceof AltarTile)) {
                    isCenterOfAltar = false;
                }
            }
        }
        
        if (isCenterOfAltar) {
        	return 6;
        }
        return 0;
    }
}