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

public class SkyFernTile extends Tile {
    private static Sprite sprite = new Sprite(27, 22, 2, 2, 1);

    private int stepped;
    private int damage;

    protected SkyFernTile(String name) {
        super(name, (ConnectorSprite) null);
        connectsToSkyHighGrass = true;
        connectsToSkyGrass = true;
        maySpawn = true;
    }

    @Override
    public boolean tick(Level level, int xt, int yt) {

        if (random.nextInt(30) != 0)
            return false;

        int xn = xt;
        int yn = yt;

        if (stepped == 0) {
            sprite = new Sprite(29, 22, 2, 2, 1);
        }

        if (random.nextInt(32) == 16) {
            stepped = 0;
        }

        if (random.nextBoolean())
            xn += random.nextInt(2) * 2 - 1;
        else
            yn += random.nextInt(2) * 2 - 1;

        if (level.getTile(xn, yn) == Tiles.get("dirt")) {
            level.setTile(xn, yn, Tiles.get("sky high grass"));
        }
        return false;
    }

    @Override
    public void render(Screen screen, Level level, int x, int y) {
        Tiles.get("sky high grass").render(screen, level, x, y);

        if (stepped >= 8) {

            if (random.nextInt(12) == 9) {
                sprite = new Sprite(27, 22, 2, 2, 1);
            }
            if (random.nextInt(12) == 1) {
                sprite = new Sprite(29, 22, 2, 2, 1);
            }
            if (random.nextInt(12) == 10) {
                sprite = new Sprite(31, 22, 2, 2, 1);
            }
            if (random.nextInt(12) == 3) {
                sprite = new Sprite(33, 22, 2, 2, 1);
            }
            if (random.nextInt(12) == 11) {
                sprite = new Sprite(31, 22, 2, 2, 1);
            }
            if (random.nextInt(12) == 5) {
                sprite = new Sprite(29, 22, 2, 2, 1);
            }
            if (random.nextInt(12) == 12) {

                sprite = new Sprite(27, 22, 2, 2, 1);
            }
        }

        sprite.render(screen, x * 16, y * 16);
    }

    @Override
    public void steppedOn(Level level, int x, int y, Entity entity) {
        if (entity instanceof Player) {

            Player p = (Player) entity;

            stepped++;
            if (stepped >= 16) {
                stepped = 0;
            }

            if (stepped >= 14) {

                if (random.nextInt(3) == 1) {
                    damage = 5;
                }
                if (random.nextInt(3) == 2) {
                    damage = 4;

                } else {
                    damage = 3;
                }

                p.hurt(this, x, y, damage);
            }
        }
    }

    @Override
    public boolean interact(Level level, int x, int y, Player player, Item item, Direction attackDir) {
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == ToolType.Shovel) {
                if (player.payStamina(2 - tool.level) && tool.payDurability()) {
                    level.setTile(x, y, Tiles.get("sky high grass"));
                    Sound.Tile_generic_hurt.play();

                    if (random.nextInt(20) == 1) { // 20% chance to drop sky seeds
                        level.dropItem(x * 16 + 8, y * 16 + 8, Items.get("Sky Seeds"));
                    }

                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hurt(Level level, int x, int y, Mob source, int dmg, Direction attackDir) {
        if (random.nextInt(12) == 1) { // 20% chance to drop sky seeds
            level.dropItem(x * 16 + 8, y * 16 + 8, Items.get("Sky Seeds"));
        }
        level.setTile(x, y, Tiles.get("sky high grass"));
        return true;
    }
}
