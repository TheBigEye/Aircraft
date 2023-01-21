package minicraft.level.tile;

import org.tinylog.Logger;

import minicraft.core.Game;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Firefly;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.entity.mob.boss.AirWizard;
import minicraft.entity.particle.SmashParticle;
import minicraft.entity.particle.TextParticle;
import minicraft.gfx.Color;
import minicraft.gfx.ConnectorSprite;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;
import minicraft.screen.AchievementsDisplay;

public class TreeTile extends Tile {
	
	//                   00   01
	//X1 X2  //-- Y1  //[AA] [BB] 00 // each tree parts are 8x8 squares
	//-- --  //-- Y2  //[AA] [BB] 01 // together make a 16x32 area
	//-- --  //-- Y3  // --  [AA] 02
	//-- --  //-- Y4  // --  [BB] 03
	
	public enum TreeType {
		Oak(
			new Sprite[] {
		 		new Sprite(0, 34, 1), new Sprite(1, 34, 1),   // [0] [1]
		 		new Sprite(0, 35, 1), new Sprite(1, 35, 1),   // [2] [3]
		 		new Sprite(1, 36, 1), 						  // [-] [4]
		 		new Sprite(1, 37, 1)  						  // [-] [5]
		 		
			}, "Grass", 
			new String[] {
				"Oak Wood", "Oak Wood", "Acorn"
			}, 32
		),

		Birch(
			new Sprite[] {
		 		new Sprite(2, 34, 1), new Sprite(3, 34, 1),
		 		new Sprite(2, 35, 1), new Sprite(3, 35, 1),
		 		new Sprite(3, 36, 1),
		 		new Sprite(3, 37, 1)
		 		
			}, "Grass", 
			new String[] {
				"Birch Wood", "Leaf", "Birch cone"
			}, 38
		),
			
		Red_mushroom(
			new Sprite[] {
		 		new Sprite(4, 34, 1), new Sprite(5, 34, 1),
		 		new Sprite(4, 35, 1), new Sprite(5, 35, 1),
		 		new Sprite(5, 36, 1),
		 		new Sprite(5, 37, 1)

			}, "Dirt", 
			new String[] {
				"Oak Wood", "Leaf", "Acorn"
			}, 32
		),
		
		Fir(
			new Sprite[] {
		 		new Sprite(6, 34, 1), new Sprite(7, 34, 1),
		 		new Sprite(6, 35, 1), new Sprite(7, 35, 1),
		 		new Sprite(7, 36, 1),
		 		new Sprite(7, 37, 1)
			
			}, "Snow", 
			new String[] {
				"Spruce wood", "Spruce wood", "Fir cone"
			}, 32
		),
		
		Pine(
			new Sprite[] {
		 		new Sprite(8, 34, 1), new Sprite(9, 34, 1),
		 		new Sprite(8, 35, 1), new Sprite(9, 35, 1),
		 		new Sprite(9, 36, 1),
		 		new Sprite(9, 37, 1)
			
			}, "Snow", 
			new String[] {
				"Spruce wood", "Spruce wood", "Pine cone"
			}, 32
		),
		
		Skyroot(
			new Sprite[] {
		 		new Sprite(10, 34, 1), new Sprite(11, 34, 1),
		 		new Sprite(10, 35, 1), new Sprite(11, 35, 1),
		 		new Sprite(11, 36, 1),
		 		new Sprite(11, 37, 1)
			
			}, "Sky grass", 
			new String[] {
				"Oak Wood", "Leaf", "Acorn"
			}, 34
		),		
		Brown_mushroom(
			new Sprite[] {
		 		new Sprite(12, 34, 1), new Sprite(13, 34, 1),
		 		new Sprite(12, 35, 1), new Sprite(13, 35, 1),
		 		new Sprite(13, 36, 1),
		 		new Sprite(13, 37, 1)
			
			}, "Dirt", 
			new String[] {
				"Oak Wood", "Leaf", "Acorn"
			}, 34
		),		
		Bluroot(
			new Sprite[] {
		 		new Sprite(14, 34, 1), new Sprite(15, 34, 1),
		 		new Sprite(14, 35, 1), new Sprite(15, 35, 1),
		 		new Sprite(15, 36, 1),
		 		new Sprite(15, 37, 1)
			
			}, "Sky grass", 
			new String[] {
				"Oak Wood", "Leaf", "Acorn"
			}, 34
		),		
		Goldroot(
			new Sprite[] {
		 		new Sprite(16, 34, 1), new Sprite(17, 34, 1),
		 		new Sprite(16, 35, 1), new Sprite(17, 35, 1),
		 		new Sprite(17, 36, 1),
		 		new Sprite(17, 37, 1)
			
			}, "Sky High Grass", 
			new String[] {
				"Oak Wood", "Leaf", "Acorn"
			}, 34
		);


	    private Sprite[] sprites;
	    
	    private final String parentTile;
	    private final String[] loot;
	    private final int health;

		TreeType(Sprite[] sprites, String parentTile, String[] loot, int health) {
			this.sprites = sprites;
			
            this.parentTile = parentTile;
            this.loot = loot;
            this.health = health;
		}
	}

	private TreeType tree;

    protected TreeTile(TreeType type) {
        super((type == TreeType.Red_mushroom ? "Red mushroom" : (type == TreeType.Brown_mushroom ? "Red mushroom" : type.name() + " Tree")), (ConnectorSprite) null);
    	this.tree = type;

        switch (type.parentTile.toLowerCase()) {
        	case "grass": connectsToGrass = true; break;
        	case "snow": connectsToSnow = true; break;
        	case "sand": connectsToSand = true; break;
        	case "dirt": connectsToDirt = true; break;
        	case "sky grass": connectsToSkyGrass = true; break;
        	case "sky high grass": connectsToSkyHighGrass = true; break;
        	default: 
        		Logger.error("The connector type {} is invalid", type.parentTile);
        }	
    }
    
    @Override
    public void hurt(Level level, int x, int y, int hurtDamage) {   	
        if (random.nextInt(50) == 25 && (tree != TreeType.Brown_mushroom || tree != TreeType.Red_mushroom)) {
            level.dropItem((x << 4) + 8, (y << 4) + 8, Items.get("Apple"));
        }

        int damage = level.getData(x, y) + hurtDamage;
        int treeHealth = tree.health;
        if (Game.isMode("Creative")) {
        	hurtDamage = damage = treeHealth;
        }
        
        level.add(new SmashParticle(x << 4, y << 4));
        Sound.genericHurt.playOnWorld(x << 4, y << 4);

        level.add(new TextParticle("" + hurtDamage, (x << 4) + 8, (y << 4) + 8, Color.RED));
        if (damage >= treeHealth) {
        	
        	level.dropItem((x << 4) + 8, (y << 4) + 8, 1, 2, Items.get(tree.loot[0]));
            level.dropItem((x << 4) + 8, (y << 4) + 8, 1, 2, Items.get(tree.loot[1]));
            level.dropItem((x << 4) + 8, (y << 4) + 8, 0, 1, Items.get(tree.loot[2]));
            
            level.setTile(x, y, Tiles.get(tree.parentTile));
            
			if (!Game.isMode("Creative")) {
				AchievementsDisplay.setAchievement("minicraft.achievement.woodcutter", true);
			}
			
        } else {
            level.setData(x, y, damage);
        }
    }

    @Override
    public boolean hurt(Level level, int x, int y, Mob source, int hurtDamage, Direction attackDir) {
        hurt(level, x, y, hurtDamage);
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
    public boolean mayPass(Level level, int x, int y, Entity entity) {
        return entity instanceof Firefly || entity instanceof AirWizard;
    }
    

    @Override
    public void render(Screen screen, Level level, int x, int y) {
        Tiles.get(tree.parentTile).render(screen, level, x, y);

        boolean u = level.getTile(x, y - 1) == this; // up
        boolean l = level.getTile(x - 1, y) == this; // left
        boolean r = level.getTile(x + 1, y) == this; // right
        boolean d = level.getTile(x, y + 1) == this; // down
        boolean ul = level.getTile(x - 1, y - 1) == this; // up-left
        boolean ur = level.getTile(x + 1, y - 1) == this; // up-right
        boolean dl = level.getTile(x - 1, y + 1) == this; // down-left
        boolean dr = level.getTile(x + 1, y + 1) == this; // down-right

        if (u && ul && l) {
            tree.sprites[3].render(screen, (x << 4) + 0, (y << 4) + 0);
        } else {
            tree.sprites[0].render(screen, (x << 4) + 0, (y << 4) + 0);
        }
        if (u && ur && r) {
            tree.sprites[4].render(screen, (x << 4) + 8, (y << 4) + 0);
        } else {
        	tree.sprites[1].render(screen, (x << 4) + 8, (y << 4) + 0);
        }
        if (d && dl && l) {
        	tree.sprites[4].render(screen, (x << 4) + 0, (y << 4) + 8);
        } else {
        	tree.sprites[2].render(screen, (x << 4) + 0, (y << 4) + 8);
        }
        if (d && dr && r) {
            tree.sprites[3].render(screen, (x << 4) + 8, (y << 4) + 8);
        } else {
        	tree.sprites[5].render(screen, (x << 4) + 8, (y << 4) + 8);
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
    public int getLightRadius(Level level, int x, int y) {
    	if (tree == TreeType.Goldroot) {
    		return 6;
    	}
        return 0;
    }
}