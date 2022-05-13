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
	
	public int health;
	public TreeType type;
	public String[] Loot;
	public String baseTile;
    public int x1, x2;
    public int y1, y2;
    public int y3, y4;
    
    public enum TreeType {
    	TREE(0, 1, 0, 1, 2, 3, "Grass", new String[] {"Wood", "Wood", "Acorn"}, 20),
    	BIRCH(0, 1, 28, 29, 30, 31, "Grass", new String[] {"Birch Wood", "Leaf", "Birch cone"}, 25),
    	RED_MUSHROOM(2, 3, 28, 29, 30, 31, "Dirt", new String[] {"Wood", "Leaf", "Acorn"}, 20),
    	FIR(4, 5, 28, 29, 30, 31, "Snow", new String[] {"Wood", "Wood", "Fir cone"}, 22),
    	PINE(6, 7, 28, 29, 30, 31, "Snow", new String[] {"Spruce wood", "Spruce wood", "Pine cone"}, 22),
    	BROWN_MUSHROOM(2, 3, 28, 29, 30, 31, "Dirt", new String[] {"Wood", "Leaf", "Acorn"}, 24);
    	
        public int x1, x2;
        public int y1, y2;
        public int y3, y4;
        public String baseTile;
        public String[] loot;
        public int health;

        /**
         * Create a type of tree.
         */
        TreeType(int x1, int x2, int y1, int y2, int y3, int y4, String baseTile, String[] loot, int health) {
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.y3 = y3;
            this.y4 = y4;
            this.baseTile = baseTile;
            this.loot = loot;
            this.health = health;
            
        }
    }
	
    public TreeTile(String name, TreeType t) {
        super(name, (ConnectorSprite) null);
    	
    	this.type = t;
        this.baseTile = type.baseTile;
        this.x1 = type.x1; this.x2 = type.x2;
        this.y1 = type.y1; this.y2 = type.y2;
        this.y3 = type.y3; this.y4 = type.y4;
        this.Loot = type.loot;
        this.health = type.health;
        
        switch (this.baseTile) {
        	case "Grass": connectsToGrass = true; break;
        	case "Snow": connectsToSnow = true; break;
        	case "Sand": connectsToSand = true; break;
        	case "Dirt": connectsToDirt = true; break;
        	case "Sky grass": connectsToSkyGrass = true; break;
        	case "Sky high grass": connectsToSkyHighGrass = true; break;
        	default: Logger.error("The connector type {} is invalid", this.baseTile);
        }
    }
    
    @Override
    public void render(Screen screen, Level level, int x, int y) {
        Tiles.get(baseTile).render(screen, level, x, y);

        boolean u = level.getTile(x, y - 1) == this;
        boolean l = level.getTile(x - 1, y) == this;
        boolean r = level.getTile(x + 1, y) == this;
        boolean d = level.getTile(x, y + 1) == this;
        boolean ul = level.getTile(x - 1, y - 1) == this;
        boolean ur = level.getTile(x + 1, y - 1) == this;
        boolean dl = level.getTile(x - 1, y + 1) == this;
        boolean dr = level.getTile(x + 1, y + 1) == this;

        if (u && ul && l) {
            screen.render(x * 16 + 0, y * 16 + 0, x2 + y2 * 32, 0, 1); // y2
        } else {
            screen.render(x * 16 + 0, y * 16 + 0, x1 + y1 * 32, 0, 1); // y1
        }
        if (u && ur && r) {
            screen.render(x * 16 + 8, y * 16 + 0, x2 + y3 * 32, 0, 1); // y3
        } else {
            screen.render(x * 16 + 8, y * 16 + 0, x2 + y1 * 32, 0, 1); // y1
        }
        if (d && dl && l) {
            screen.render(x * 16 + 0, y * 16 + 8, x2 + y3 * 32, 0, 1); // y3
        } else {
            screen.render(x * 16 + 0, y * 16 + 8, x1 + y2 * 32, 0, 1); // y2
        }
        if (d && dr && r) {
            screen.render(x * 16 + 8, y * 16 + 8, x2 + y2 * 32, 0, 1); // y2
        } else {
            screen.render(x * 16 + 8, y * 16 + 8, x2 + y4 * 32, 0, 1); // y4
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
                    hurt(level, xt, yt, random.nextInt(10) + (tool.level) * 5 + 10);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void hurt(Level level, int x, int y, int dmg) {
        if (random.nextInt(100) == 0) {
            level.dropItem(x * 16 + 8, y * 16 + 8, Items.get("Apple"));
        }

        int damage = level.getData(x, y) + dmg;
        int treeHealth = health;
        if (Game.isMode("Creative")) {
            dmg = damage = treeHealth;
        }

        level.add(new SmashParticle(x * 16, y * 16));
        Sound.Tile_generic_hurt.play();

        level.add(new TextParticle("" + dmg, x * 16 + 8, y * 16 + 8, Color.RED));
        if (damage >= treeHealth) {
        	
        	level.dropItem(x * 16 + 8, y * 16 + 8, 1, 2, Items.get(Loot[0]));
            level.dropItem(x * 16 + 8, y * 16 + 8, 1, 2, Items.get(Loot[1]));
            level.dropItem(x * 16 + 8, y * 16 + 8, 0, 1, Items.get(Loot[2]));
            
            level.setTile(x, y, Tiles.get(baseTile));
            
			if (!Game.isMode("creative")) {
				AchievementsDisplay.setAchievement("minicraft.achievement.woodcutter", true);
			}
			
        } else {
            level.setData(x, y, damage);
        }
    } 
}