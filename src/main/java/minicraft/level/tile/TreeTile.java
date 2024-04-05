package minicraft.level.tile;

import org.tinylog.Logger;

import minicraft.core.Game;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.AirWizard;
import minicraft.entity.mob.Firefly;
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
import minicraft.screen.AchievementsDisplay;

public class TreeTile extends Tile {
	
	//                   00   01
	//X1 X2  //-- Y1  //[AA] [BB] 00 // each tree parts are 8x8 squares
	//-- --  //-- Y2  //[AA] [BB] 01 // together make a 16x32 area
	//-- --  //-- Y3  // --  [AA] 02
	//-- --  //-- Y4  // --  [BB] 03
	
	protected enum Tree {
		OAK(
			new Sprite[] {
		 		new Sprite(18, 0, 1), new Sprite(19, 0, 1),
		 		new Sprite(18, 1, 1), new Sprite(19, 1, 1),
		 		new Sprite(19, 2, 1),
		 		new Sprite(19, 3, 1)
		 		
			}, "Grass", 
			new String[] {
				"Oak Wood", "Acorn", "Apple"
			}, 32
		),

		BIRCH(
			new Sprite[] {
		 		new Sprite(20, 0, 1), new Sprite(21, 0, 1),
		 		new Sprite(20, 1, 1), new Sprite(21, 1, 1),
		 		new Sprite(21, 2, 1),
		 		new Sprite(21, 3, 1)
		 		
			}, "Grass", 
			new String[] {
				"Birch Wood", "Leaf", "Birch cone"
			}, 38
		),
			
		RED_MUSHROOM(
			new Sprite[] {
		 		new Sprite(22, 0, 1), new Sprite(23, 0, 1),
		 		new Sprite(22, 1, 1), new Sprite(23, 1, 1),
		 		new Sprite(23, 2, 1),
		 		new Sprite(23, 3, 1)

			}, "Dirt", 
			new String[] {
				"Oak Wood", "Leaf", "Acorn"
			}, 32
		),
		
		FIR(
			new Sprite[] {
		 		new Sprite(24, 0, 1), new Sprite(25, 0, 1),
		 		new Sprite(24, 1, 1), new Sprite(25, 1, 1),
		 		new Sprite(25, 2, 1),
		 		new Sprite(25, 3, 1)
			
			}, "Snow", 
			new String[] {
				"Spruce wood", "Spruce wood", "Fir cone"
			}, 32
		),
		
		PINE(
			new Sprite[] {
		 		new Sprite(26, 0, 1), new Sprite(27, 0, 1),
		 		new Sprite(26, 1, 1), new Sprite(27, 1, 1),
		 		new Sprite(27, 2, 1),
		 		new Sprite(27, 3, 1)
			
			}, "Snow", 
			new String[] {
				"Spruce wood", "Spruce wood", "Pine cone"
			}, 32
		),
		
		SKYROOT(
			new Sprite[] {
		 		new Sprite(28, 0, 1), new Sprite(29, 0, 1),
		 		new Sprite(28, 1, 1), new Sprite(29, 1, 1),
		 		new Sprite(29, 2, 1),
		 		new Sprite(29, 3, 1)
			
			}, "Sky grass", 
			new String[] {
				"Oak Wood", "Acorn", "Gold Apple"
			}, 34
		),		
		
		BROWN_MUSHROOM(
			new Sprite[] {
		 		new Sprite(30, 0, 1), new Sprite(31, 0, 1),
		 		new Sprite(30, 1, 1), new Sprite(31, 1, 1),
		 		new Sprite(31, 2, 1),
		 		new Sprite(31, 3, 1)
			
			}, "Dirt", 
			new String[] {
				"Oak Wood", "Leaf", "Acorn"
			}, 34
		),		
		
		BLUROOT(
			new Sprite[] {
		 		new Sprite(32, 0, 1), new Sprite(33, 0, 1),
		 		new Sprite(32, 1, 1), new Sprite(33, 1, 1),
		 		new Sprite(33, 2, 1),
		 		new Sprite(33, 3, 1)
			
			}, "Sky grass", 
			new String[] {
				"Oak Wood", "Leaf", "Acorn"
			}, 34
		),		
		
		GOLDROOT(
			new Sprite[] {
		 		new Sprite(34, 0, 1), new Sprite(35, 0, 1),
		 		new Sprite(34, 1, 1), new Sprite(35, 1, 1),
		 		new Sprite(35, 2, 1),
		 		new Sprite(35, 3, 1)
			
			}, "Ferrosite", 
			new String[] {
				"Oak Wood", "Spores", "Spores"
			}, 34
		);


	    private Sprite[] sprites;
	    
	    private final String parentTile;
	    private final String[] loot;
	    private final int health;

		Tree(Sprite[] sprites, String parentTile, String[] loot, int health) {
			this.sprites = sprites;
			
            this.parentTile = parentTile;
            this.loot = loot;
            this.health = health;
		}
	}

	private Tree tree;

    protected TreeTile(Tree type) {
        super((type == Tree.RED_MUSHROOM ? "Red mushroom" : (type == Tree.BROWN_MUSHROOM ? "Red mushroom" : type.name() + " Tree")), (ConnectorSprite) null);
    	this.tree = type;

        switch (type.parentTile.toLowerCase()) {
        	case "grass": connectsToGrass = true; break;
        	case "snow": connectsToSnow = true; break;
        	case "sand": connectsToSand = true; break;
        	case "dirt": connectsToDirt = true; break;
        	case "sky grass": connectsToSkyGrass = true; break;
        	case "ferrosite": connectsToFerrosite = true; break;
        	default: 
        		Logger.error("The connector type {} is invalid", type.parentTile);
        }	
    }
    
    @Override
    public void hurt(Level level, int x, int y, int hurtDamage) {
        if (random.nextInt(50) == 25 && (tree != Tree.BROWN_MUSHROOM|| tree != Tree.RED_MUSHROOM)) {
            level.dropItem((x << 4) + 8, (y << 4) + 8, Items.get("Apple"));
        }

        int damage = level.getData(x, y) + hurtDamage;
        int treeHealth = tree.health;
        
        if (Game.isMode("Creative")) {
        	hurtDamage = damage = treeHealth;
        }
        
        Sound.playAt("genericHurt", x << 4, y << 4);
        level.add(new SmashParticle(x << 4, y << 4));
        
        level.add(new TextParticle("" + hurtDamage, (x << 4) + 8, (y << 4) + 8, Color.RED));

        if (damage >= treeHealth) {
        	
        	level.dropItem((x << 4) + 8, (y << 4) + 8, 2, 2, Items.get(tree.loot[0]));
            level.dropItem((x << 4) + 8, (y << 4) + 8, 1, 2, Items.get(tree.loot[1]));
            level.dropItem((x << 4) + 8, (y << 4) + 8, 0, 1, Items.get(tree.loot[2]));
            
            level.setTile(x, y, Tiles.get(tree.parentTile));
            
			AchievementsDisplay.setAchievement("minicraft.achievement.woodcutter", true);
			
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
    	if (tree == Tree.GOLDROOT) {
    		return 6;
    	}
        return 0;
    }
}