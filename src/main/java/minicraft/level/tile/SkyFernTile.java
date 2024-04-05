package minicraft.level.tile;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.graphic.ConnectorSprite;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;

public class SkyFernTile extends Tile {
    private static Sprite sprite = new Sprite(27, 22, 2, 2, 1);
    private int spriteFrame = 0;

    private boolean playerStepped;
    private int fernAttackTick;

    protected SkyFernTile(String name) {
        super(name, (ConnectorSprite) null);
        connectsToSkyHighGrass = true;
        connectsToSkyGrass = true;
        maySpawn = false;
    }

    @Override
    public boolean tick(Level level, int xt, int yt) {
        if (random.nextInt(30) != 0) {
            return false;
        }

        int xn = xt;
        int yn = yt;

        if (random.nextBoolean()) {
            xn += (random.nextInt(2) * 2) - 1;
        } else {
            yn += (random.nextInt(2) * 2) - 1;
        }

        if (level.getTile(xn, yn) instanceof DirtTile) {
            level.setTile(xn, yn, Tiles.get("Sky Grass"));
        }
        return false;
    }

    @Override
    public void render(Screen screen, Level level, int x, int y) {
    	fernAttackTick++;
        Tiles.get("Sky Grass").render(screen, level, x, y);
        if (playerStepped && fernAttackTick / 8 % 2 == 0) {
            spriteFrame = (spriteFrame + 2) % 4 ;
            sprite = new Sprite(spriteFrame + 27, 22, 2, 2, 1);
        } else {
        	sprite = new Sprite(29, 22, 2, 2, 1);
        }
        sprite.render(screen, x << 4, y << 4);
    }

    @Override
    public void steppedOn(Level level, int x, int y, Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            player.hurt(this, x, y, random.nextInt(3));   
            playerStepped = true;
        } else {
        	playerStepped = false;
        }
    }

    @Override
    public boolean interact(Level level, int x, int y, Player player, Item item, Direction attackDir) {
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == ToolType.Shovel) {
                if (player.payStamina(2 - tool.level) && tool.payDurability()) {
                	Sound.playAt("genericHurt", x, y);
                	
                    level.setTile(x, y, Tiles.get("Sky Grass"));

                    if (random.nextInt(20) == 10) { // 20% chance to drop sky seeds
                        level.dropItem((x << 4) + 8, (y << 4) + 8, Items.get("Sky Seeds"));
                    }

                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hurt(Level level, int x, int y, Mob source, int hurtDamage, Direction attackDir) {
        if (random.nextInt(12) == 6) { // 20% chance to drop sky seeds
            level.dropItem((x << 4) + 8, (y << 4) + 8, Items.get("Sky Seeds"));
        }
        level.setTile(x, y, Tiles.get("Sky Grass"));
        return true;
    }
}
