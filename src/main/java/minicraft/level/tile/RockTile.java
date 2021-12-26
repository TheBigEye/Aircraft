package minicraft.level.tile;

import minicraft.core.Game;
import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
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

// This is the normal stone you see underground and on the surface, that drops coal and stone.

public class RockTile extends Tile {
    private ConnectorSprite sprite = new ConnectorSprite(RockTile.class, new Sprite(18, 6, 3, 3, 1, 3),
            new Sprite(21, 8, 2, 2, 1, 3), new Sprite(21, 6, 2, 2, 1, 3)) {
 	   
        
        public boolean connectsTo(Tile tile, boolean isSide) {
            return tile != Tiles.get("dirt") && tile != Tiles.get("grass") && 
            	   tile != Tiles.get("sand") && tile != Tiles.get("Orange tulip") &&
            	   tile != Tiles.get("tree") && tile != Tiles.get("birch tree") &&
            	   tile != Tiles.get("Stairs Down") && tile != Tiles.get("Stairs up") &&
            	   tile != Tiles.get("lava") && tile != Tiles.get("water") && 
            	   tile != Tiles.get("cactus") && tile != Tiles.get("flower") &&
            	   tile != Tiles.get("Hole") && tile != Tiles.get("Snow") &&
            	   tile != Tiles.get("Lawn") && tile != Tiles.get("path") && 
            	   tile != Tiles.get("Birch tree") && tile != Tiles.get("Fir tree") && 
            	   tile != Tiles.get("Wood wall") && tile != Tiles.get("path") && 
            	   tile != Tiles.get("ice spike") && tile != Tiles.get("Carrot") && 
            	   tile != Tiles.get("pine tree") &&
            	   tile != Tiles.get(13) && tile != Tiles.get(14) && tile != Tiles.get(15) && tile != Tiles.get(16) && tile != Tiles.get(26) &&
            	   tile != Tiles.get(27) && tile != Tiles.get(28) && tile != Tiles.get(29) && tile != Tiles.get(30) && tile != Tiles.get(31) && 
            	   tile != Tiles.get(32) && tile != Tiles.get(33) && tile != Tiles.get(34) && tile != Tiles.get(35) && tile != Tiles.get(36) &&
            	   tile != Tiles.get(37) && tile != Tiles.get(38) && tile != Tiles.get(39) && tile != Tiles.get(40) && tile != Tiles.get(41);
        }
 	   };

    private boolean dropCoal = false;
    private final int maxHealth = 50;

    private int damage;

    protected RockTile(String name) {
        super(name, (ConnectorSprite) null);
        csprite = sprite;
    }

    @Override
    public void render(Screen screen, Level level, int x, int y) {
        sprite.sparse.color = DirtTile.dCol(level.depth);
        sprite.render(screen, level, x, y);
    }

    @Override
    public boolean mayPass(Level level, int x, int y, Entity e) {
        return false;
    }

    @Override
    public boolean hurt(Level level, int x, int y, Mob source, int dmg, Direction attackDir) {
        hurt(level, x, y, dmg);
        return true;
    }

    @Override
    public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {

        // creative mode can just act like survival here
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == ToolType.Pickaxe && player.payStamina(4 - tool.level) && tool.payDurability()) {

                // Drop coal since we use a pickaxe.
                dropCoal = true;
                hurt(level, xt, yt, random.nextInt(10) + (tool.level) * 5 + 10);
                return true;
            }
        }
        return false;
    }

    @Override
    public void hurt(Level level, int x, int y, int dmg) {
        damage = level.getData(x, y) + dmg;

        if (Game.isMode("creative")) {
            dmg = damage = maxHealth;
            dropCoal = true;
        }

        level.add(new SmashParticle(x * 16, y * 16));
        Sound.Tile_generic_hurt.play();

        level.add(new TextParticle("" + dmg, x * 16 + 8, y * 16 + 8, Color.RED));
        if (damage >= maxHealth) {

            if (dropCoal) {
                level.dropItem(x * 16 + 8, y * 16 + 8, 1, 3, Items.get("Stone"));
                int coal = 0;

                if (!Settings.get("diff").equals("Hard")) {
                    coal++;
                }

                level.dropItem(x * 16 + 8, y * 16 + 8, coal, coal + 1, Items.get("Coal"));

            } else {
                level.dropItem(x * 16 + 8, y * 16 + 8, 2, 4, Items.get("Stone"));

            }

            level.setTile(x, y, Tiles.get("Dirt"));

        } else {
            level.setData(x, y, damage);

        }
    }

    @Override
    public boolean tick(Level level, int xt, int yt) {
        damage = level.getData(xt, yt);
        if (damage > 0) {
            level.setData(xt, yt, damage - 1);
            return true;
        }
        return false;
    }
}
