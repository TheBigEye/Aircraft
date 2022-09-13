package minicraft.level.tile;

import org.tinylog.Logger;

import minicraft.core.Game;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Firefly;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.SmashParticle;
import minicraft.entity.particle.TextParticle;
import minicraft.gfx.Color;
import minicraft.gfx.ConnectorSprite;
import minicraft.gfx.Screen;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;
import minicraft.screen.AchievementsDisplay;

public class TreeTile extends Tile {
	
	private TreeType type;

	public enum TreeType {
		Oak(0, 1, 0, 1, 2, 3, "Grass", new String[] {"Oak Wood", "Oak Wood", "Acorn"}, 20),
		Birch(0, 1, 28, 29, 30, 31, "Grass", new String[] {"Birch Wood", "Leaf", "Birch cone"}, 25),
		Red_mushroom(2, 3, 28, 29, 30, 31, "Dirt", new String[] {"Oak Wood", "Leaf", "Acorn"}, 20),
		Fir(4, 5, 28, 29, 30, 31, "Snow", new String[] {"Spruce wood", "Spruce wood", "Fir cone"}, 22),
		Pine(6, 7, 28, 29, 30, 31, "Snow", new String[] {"Spruce wood", "Spruce wood", "Pine cone"}, 22),
		Brown_mushroom(2, 3, 28, 29, 30, 31, "Dirt", new String[] {"Oak Wood", "Leaf", "Acorn"}, 24);

		//                   00   01
		//X1 X2  //-- Y1  //[DD] [DD] 00 // each tree parts are 8x8 squares
		//-- --  //-- Y2  //[DD] [AA] 01 // together make a 16x32 area
		//-- --  //-- Y3  // --  [BA] 02
		//-- --  //-- Y4  // --  [BB] 03
		
    	private int sprite_x1, sprite_x2; 
    	private int sprite_y1, sprite_y2; 
    	private int sprite_y3, sprite_y4; 
    	
    	private String baseTile;
    	private String[] loot;
    	private int health;

		TreeType(int sprite_x1, int sprite_x2, int sprite_y1, int sprite_y2, int sprite_y3, int sprite_y4, String baseTile, String[] loot, int health) {
			this.sprite_x1 = sprite_x1; this.sprite_x2 = sprite_x2;
			this.sprite_y1 = sprite_y1; this.sprite_y2 = sprite_y2;
			this.sprite_y3 = sprite_y3; this.sprite_y4 = sprite_y4;
			
            this.baseTile = baseTile;
            this.loot = loot;
            this.health = health;
		}
	}

    protected TreeTile(TreeType type) {
        super((type == TreeTile.TreeType.Red_mushroom ? "Red mushroom" : type.name() + " Tree"), (ConnectorSprite) null);
    	this.type = type;

        switch (type.baseTile) {
        	case "Grass": connectsToGrass = true; break;
        	case "Snow": connectsToSnow = true; break;
        	case "Sand": connectsToSand = true; break;
        	case "Dirt": connectsToDirt = true; break;
        	case "Sky grass": connectsToSkyGrass = true; break;
        	case "Sky high grass": connectsToSkyHighGrass = true; break;
        	default: 
        		Logger.error("The connector type {} is invalid", type.baseTile);
        }
    }
    
    @Override
    public void render(Screen screen, Level level, int x, int y) {
        Tiles.get(type.baseTile).render(screen, level, x, y);

        boolean u = level.getTile(x, y - 1) == this;
        boolean l = level.getTile(x - 1, y) == this;
        boolean r = level.getTile(x + 1, y) == this;
        boolean d = level.getTile(x, y + 1) == this;
        boolean ul = level.getTile(x - 1, y - 1) == this;
        boolean ur = level.getTile(x + 1, y - 1) == this;
        boolean dl = level.getTile(x - 1, y + 1) == this;
        boolean dr = level.getTile(x + 1, y + 1) == this;

        if (u && ul && l) {
            screen.render(x * 16 + 0, y * 16 + 0, type.sprite_x2 + type.sprite_y2 * 32, 0, 1); // y2
        } else {
            screen.render(x * 16 + 0, y * 16 + 0, type.sprite_x1 + type.sprite_y1 * 32, 0, 1); // y1
        }
        if (u && ur && r) {
            screen.render(x * 16 + 8, y * 16 + 0, type.sprite_x2 + type.sprite_y3 * 32, 0, 1); // y3
        } else {
            screen.render(x * 16 + 8, y * 16 + 0, type.sprite_x2 + type.sprite_y1 * 32, 0, 1); // y1
        }
        if (d && dl && l) {
            screen.render(x * 16 + 0, y * 16 + 8, type.sprite_x2 + type.sprite_y3 * 32, 0, 1); // y3
        } else {
            screen.render(x * 16 + 0, y * 16 + 8, type.sprite_x1 + type.sprite_y2 * 32, 0, 1); // y2
        }
        if (d && dr && r) {
            screen.render(x * 16 + 8, y * 16 + 8, type.sprite_x2 + type.sprite_y2 * 32, 0, 1); // y2
        } else {
            screen.render(x * 16 + 8, y * 16 + 8, type.sprite_x2 + type.sprite_y4 * 32, 0, 1); // y4
        }
    }

    @Override
    public boolean tick(Level level, int xt, int yt) {
        int damage = level.getData(xt, yt);
        if (damage > 0) {
            level.setData(xt, yt, damage - 1);
            return true;
        }
        return false;
    }

    @Override
    public boolean mayPass(Level level, int x, int y, Entity e) {
        return e instanceof Firefly;
    }

    @Override
    public boolean hurt(Level level, int x, int y, Mob source, int dmg, Direction attackDir) {
        hurt(level, x, y, dmg);
        return true;
    }

    @Override
    public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
        if (Game.isMode("Creative")) {
            return false; // Go directly to hurt method
        }
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == ToolType.Axe) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					hurt(level, xt, yt, tool.getDamage());
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void hurt(Level level, int x, int y, int dmg) {
        if (random.nextInt(100) == 50) {
            level.dropItem(x * 16 + 8, y * 16 + 8, Items.get("Apple"));
        }

        int damage = level.getData(x, y) + dmg;
        int treeHealth = type.health;
        if (Game.isMode("Creative")) {
            dmg = damage = treeHealth;
        }

        level.add(new SmashParticle(x * 16, y * 16));
        Sound.Tile_generic_hurt.play();

        level.add(new TextParticle("" + dmg, x * 16 + 8, y * 16 + 8, Color.RED));
        if (damage >= treeHealth) {
        	
        	level.dropItem(x * 16 + 8, y * 16 + 8, 1, 2, Items.get(type.loot[0]));
            level.dropItem(x * 16 + 8, y * 16 + 8, 1, 2, Items.get(type.loot[1]));
            level.dropItem(x * 16 + 8, y * 16 + 8, 0, 1, Items.get(type.loot[2]));
            
            level.setTile(x, y, Tiles.get(type.baseTile));
            
			if (!Game.isMode("creative")) {
				AchievementsDisplay.setAchievement("minicraft.achievement.woodcutter", true);
			}
			
        } else {
            level.setData(x, y, damage);
        }
    } 
}