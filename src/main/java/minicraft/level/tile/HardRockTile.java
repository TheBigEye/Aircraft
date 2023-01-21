package minicraft.level.tile;

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

public class HardRockTile extends Tile {

    private static ConnectorSprite sprite = new ConnectorSprite(HardRockTile.class, new Sprite(18, 9, 3, 3, 1), new Sprite(21, 10, 2, 2, 1), Sprite.missingTexture(2, 2));

    protected HardRockTile(String name) {
        super(name, sprite);
    }

    @Override
    public boolean mayPass(Level level, int x, int y, Entity entity) {
        return entity instanceof Firefly;
    }

    public boolean hurt(Level level, int x, int y, Mob source, int hurtDamage, Direction attackDir) {
        hurt(level, x, y, 0); // Damage is 0 because gem pickaxe
        return true;
    }

    public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
        if (Game.isMode("Creative")) {
            return false; // go directly to hurt method
        }
        
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == ToolType.Pickaxe && tool.level == 4) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					hurt(level, xt, yt, tool.getDamage());
                    return true;
                }
            } else {
                if (random.nextInt(4) == 2) {
                    Game.notifications.add("Gem Pickaxe Required.");
                }
            }
        }
        return false;
    }

    public void hurt(Level level, int x, int y, int hurtDamage) {
        int damage = level.getData(x, y) + hurtDamage;
        int hardrockHealth = 200;
        
        if (Game.isMode("Creative")) {
        	hurtDamage = damage = hardrockHealth;
        }
        
        level.add(new SmashParticle(x << 4, y << 4));
        Sound.genericHurt.playOnWorld(x << 4, y << 4);
        if (damage <= 30) {
        	level.add(new TextParticle("X", (x << 4) + 8, (y << 4) + 8, Color.RED));
        } else {
        	level.add(new TextParticle("" + hurtDamage, (x << 4) + 8, (y << 4) + 8, Color.RED));
        }

        if (damage >= hardrockHealth) {
            level.setTile(x, y, Tiles.get("Dirt"));
            level.dropItem((x << 4) + 8, (y << 4) + 8, 1, 3, Items.get("Stone"));
            level.dropItem((x << 4) + 8, (y << 4) + 8, 0, 1, Items.get("Coal"));
        } else {
            level.setData(x, y, damage);
        }
    }

    @Override
    public void render(Screen screen, Level level, int x, int y) {
        sprite.sparse.color = DirtTile.dCol(level.depth);
        super.render(screen, level, x, y);
    }

    public boolean tick(Level level, int xt, int yt) {
        int damage = level.getData(xt, yt);
        if (damage > 0) {
            level.setData(xt, yt, damage - 1);
            return true;
        }
        return false;
    }
}
