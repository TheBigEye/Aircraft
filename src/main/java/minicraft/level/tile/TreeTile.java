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
		 		new Sprite(0, 0, 1), new Sprite(1, 0, 1),   // [0] [1]
		 		new Sprite(0, 1, 1), new Sprite(1, 1, 1),   // [2] [3]
		 		new Sprite(1, 2, 1), 						// [-] [4]
		 		new Sprite(1, 3, 1)  						// [-] [5]
		 		
			}, "Grass", 
			new String[] {
				"Oak Wood", "Oak Wood", "Acorn"
			}, 32
		),

		Birch(
			new Sprite[] {
		 		new Sprite(0, 28, 1), new Sprite(1, 28, 1),
		 		new Sprite(0, 29, 1), new Sprite(1, 29, 1),
		 		new Sprite(1, 30, 1),
		 		new Sprite(1, 31, 1)
		 		
			}, "Grass", 
			new String[] {
				"Birch Wood", "Leaf", "Birch cone"
			}, 38
		),
			
		Red_mushroom(
			new Sprite[] {
		 		new Sprite(2, 28, 1), new Sprite(3, 28, 1),
		 		new Sprite(2, 29, 1), new Sprite(3, 29, 1),
		 		new Sprite(3, 30, 1),
		 		new Sprite(3, 31, 1)

			}, "Dirt", 
			new String[] {
				"Oak Wood", "Leaf", "Acorn"
			}, 32
		),
		
		Fir(
			new Sprite[] {
		 		new Sprite(4, 28, 1), new Sprite(5, 28, 1),
		 		new Sprite(4, 29, 1), new Sprite(5, 29, 1),
		 		new Sprite(5, 30, 1),
		 		new Sprite(5, 31, 1)
			
			}, "Snow", 
			new String[] {
				"Spruce wood", "Spruce wood", "Fir cone"
			}, 32
		),
		
		Pine(
			new Sprite[] {
		 		new Sprite(6, 28, 1), new Sprite(7, 28, 1),
		 		new Sprite(6, 29, 1), new Sprite(7, 29, 1),
		 		new Sprite(7, 30, 1),
		 		new Sprite(7, 31, 1)
			
			}, "Snow", 
			new String[] {
				"Spruce wood", "Spruce wood", "Pine cone"
			}, 32
		),
		
		Brown_mushroom(
			new Sprite[] {
		 		new Sprite(8, 28, 1), new Sprite(9, 28, 1),
		 		new Sprite(8, 29, 1), new Sprite(9, 29, 1),
		 		new Sprite(9, 30, 1),
		 		new Sprite(9, 31, 1)
			
			}, "Dirt", 
			new String[] {
				"Oak Wood", "Leaf", "Acorn"
			}, 34
		);


	    private final Sprite[] sprites;
	    
	    private final String baseTile;
	    private final String[] loot;
	    private final int health;

		TreeType(Sprite[] sprites, String baseTile, String[] loot, int health) {
			this.sprites = sprites;
			
            this.baseTile = baseTile;
            this.loot = loot;
            this.health = health;
		}
	}

	private TreeType tree;

    protected TreeTile(TreeType type) {
        super((type == TreeTile.TreeType.Red_mushroom ? "Red mushroom" : type.name() + " Tree"), (ConnectorSprite) null);
    	this.tree = type;

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
    public void hurt(Level level, int x, int y, int dmg) {   	
        if (random.nextInt(50) == 25) {
            level.dropItem(x * 16 + 8, y * 16 + 8, Items.get("Apple"));
        }

        int damage = level.getData(x, y) + dmg;
        int treeHealth = tree.health;
        if (Game.isMode("Creative")) {
            dmg = damage = treeHealth;
        }
        
        level.add(new SmashParticle(x * 16, y * 16));
        Sound.genericHurt.playOnWorld(x * 16, y * 16);

        level.add(new TextParticle("" + dmg, x * 16 + 8, y * 16 + 8, Color.RED));
        if (damage >= treeHealth) {
        	
        	level.dropItem(x * 16 + 8, y * 16 + 8, 1, 2, Items.get(tree.loot[0]));
            level.dropItem(x * 16 + 8, y * 16 + 8, 1, 2, Items.get(tree.loot[1]));
            level.dropItem(x * 16 + 8, y * 16 + 8, 0, 1, Items.get(tree.loot[2]));
            
            level.setTile(x, y, Tiles.get(tree.baseTile));
            
			if (!Game.isMode("Creative")) {
				AchievementsDisplay.setAchievement("minicraft.achievement.woodcutter", true);
			}
			
        } else {
            level.setData(x, y, damage);
        }
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
    public boolean mayPass(Level level, int x, int y, Entity entity) {
        return entity instanceof Firefly;
    }

    @Override
    public void render(Screen screen, Level level, int x, int y) {
        Tiles.get(tree.baseTile).render(screen, level, x, y);

        boolean u = level.getTile(x, y - 1) == this; // up
        boolean l = level.getTile(x - 1, y) == this; // left
        boolean r = level.getTile(x + 1, y) == this; // right
        boolean d = level.getTile(x, y + 1) == this; // down
        boolean ul = level.getTile(x - 1, y - 1) == this; // up-left
        boolean ur = level.getTile(x + 1, y - 1) == this; // up-right
        boolean dl = level.getTile(x - 1, y + 1) == this; // down-left
        boolean dr = level.getTile(x + 1, y + 1) == this; // down-right

        if (u && ul && l) {
            tree.sprites[3].render(screen, x * 16 + 0, y * 16 + 0); // good
        } else {
            tree.sprites[0].render(screen, x * 16 + 0, y * 16 + 0); // good
        }
        if (u && ur && r) {
            tree.sprites[4].render(screen, x * 16 + 8, y * 16 + 0); // good
        } else {
        	tree.sprites[1].render(screen, x * 16 + 8, y * 16 + 0); // good
        }
        if (d && dl && l) {
        	tree.sprites[4].render(screen, x * 16 + 0, y * 16 + 8); // good
        } else {
        	tree.sprites[2].render(screen, x * 16 + 0, y * 16 + 8); // ?
        }
        if (d && dr && r) {
            tree.sprites[3].render(screen, x * 16 + 8, y * 16 + 8); // ?
        } else {
        	tree.sprites[5].render(screen, x * 16 + 8, y * 16 + 8); // good
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
}