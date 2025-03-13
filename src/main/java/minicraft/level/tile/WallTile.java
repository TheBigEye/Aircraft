package minicraft.level.tile;

import minicraft.core.Game;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.AirWizard;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.SmashParticle;
import minicraft.entity.particle.TextParticle;
import minicraft.graphic.Color;
import minicraft.graphic.ConnectorSprite;
import minicraft.graphic.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;

public class WallTile extends Tile {

    private static final String obsidianBricksMsg = "The airwizard must be defeated first.";

    private ConnectorSprite sprite;

    protected Material type;

    protected WallTile(Material type) {
        super(type.name() + " Wall", (ConnectorSprite) null);

        connectsToSkyGrass = true;
        connectsToSkyDirt = true;

        this.type = type;
        switch (type) {
	        case Oak:
	            sprite = new ConnectorSprite(WallTile.class,
	            		 new Sprite(0, 26, 3, 3, 1),
	            		 new Sprite(3, 26, 2, 2, 1),
	                     new Sprite(1, 27, 2, 2, 1, 0, true));
	            break;
	        case Stone:
	            sprite = new ConnectorSprite(WallTile.class,
	            		 new Sprite(9, 26, 3, 3, 1),
	            		 new Sprite(12, 26, 2, 2, 1),
	                     new Sprite(10, 27, 2, 2, 1, 0, true));
	            break;
	        case Obsidian:
	            sprite = new ConnectorSprite(WallTile.class,
	            		 new Sprite(18, 26, 3, 3, 1),
	            		 new Sprite(21, 26, 2, 2, 1),
	                     new Sprite(19, 27, 2, 2, 1, 0, true));
	            break;
	        case Spruce:
	            sprite = new ConnectorSprite(WallTile.class,
	            		 new Sprite(27, 26, 3, 3, 1),
	            		 new Sprite(30, 26, 2, 2, 1),
	                     new Sprite(28, 27, 2, 2, 1, 0, true));
	            break;
	        case Birch:
	            sprite = new ConnectorSprite(WallTile.class,
	            		 new Sprite(36, 26, 3, 3, 1),
	            		 new Sprite(39, 26, 2, 2, 1),
	                     new Sprite(37, 27, 2, 2, 1, 0, true));
	            break;
	        case Holy:
	            sprite = new ConnectorSprite(WallTile.class,
	            		 new Sprite(45, 26, 3, 3, 1),
	            		 new Sprite(48, 26, 2, 2, 1),
	                     new Sprite(46, 27, 2, 2, 1, 0, true));
	            break;
        }
        connectorSprite = sprite;
    }

    @Override
    public boolean mayPass(Level level, int x, int y, Entity entity) {
        return false;
    }

    @Override
    public boolean hurt(Level level, int x, int y, Mob source, int hurtDamage, Direction attackDir) {
        if (Game.isMode("Creative") || level.depth != -3 || type != Material.Obsidian || AirWizard.beaten) {
            hurt(level, x, y, (random.nextInt(6) / 6) * (hurtDamage / 2));
            return true;
        } else {
            Game.notifications.add(obsidianBricksMsg);
            return false;
        }
    }

    @Override
    public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
        if (Game.isMode("Creative")) {
            return false; // go directly to hurt method
        }
        
	    if (!(item instanceof ToolItem)) {
	        return false;
	    }
        
	    ToolItem tool = (ToolItem) item;
	    ToolType toolType = tool.type;

        if (toolType == type.getRequiredTool()) {
            if (level.depth != -3 || type != Material.Obsidian || AirWizard.beaten) {
                if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					hurt(level, xt, yt, tool.getDamage());
                    return true;
                }
            } else {
                Game.notifications.add(obsidianBricksMsg);
            }
        }
        
        return false;
    }

    @Override
    public void hurt(Level level, int x, int y, int hurtDamage) {
    	int damage = level.getData(x, y) + hurtDamage;
        int sbwHealth = 100;

        if (Game.isMode("Creative")) {
        	hurtDamage = damage = sbwHealth;
        }

        Sound.playAt("genericHurt", x << 4, y << 4);
        level.add(new SmashParticle(x << 4, y << 4));
        
        level.add(new TextParticle("" + hurtDamage, (x << 4) + 8, (y << 4) + 8, Color.RED));
        if (damage >= sbwHealth) {

            String itemName = "";
            String tilename = "";

            switch (type) {
	            case Oak: itemName = "Oak Plank"; tilename = "Oak Planks"; break;
	            case Stone: itemName = "Stone Brick"; tilename = "Stone Bricks"; break;
	            case Obsidian: itemName = "Obsidian Brick"; tilename = "Obsidian"; break;
	            case Spruce: itemName = "Spruce Plank"; tilename = "Spruce Planks"; break;
	            case Birch: itemName = "Birch Plank"; tilename = "Birch Planks"; break;
	            case Holy: itemName = "Holy Brick"; tilename = "Holy Bricks"; break;
            }

            level.dropItem((x << 4) + 8, (y << 4) + 8, 1, 6 - type.ordinal(), Items.get(itemName));
            level.setTile(x, y, Tiles.get(tilename));
        } else {
            level.setData(x, y, damage);
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
    public String getName(int data) {
        return Material.values[data].name() + " Wall";
    }
}