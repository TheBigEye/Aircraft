package minicraft.level.tile;

import minicraft.core.Game;
import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.AirWizard;
import minicraft.entity.mob.Mob;
import minicraft.entity.mob.Player;
import minicraft.entity.particle.SmashParticle;
import minicraft.entity.particle.TextParticle;
import minicraft.graphic.Color;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.item.Item;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;

public class CloudCactusTile extends Tile {
    private static final Sprite sprite = new Sprite(65, 2, 2, 2, 1);

    protected CloudCactusTile(String name) {
        super(name, sprite);
        connectsToFerrosite = true;
    }
    
    @Override
    public boolean mayPass(Level level, int x, int y, Entity entity) {
        return entity instanceof AirWizard;
    }

    @Override
    public boolean hurt(Level level, int x, int y, Mob source, int hurtDamage, Direction attackDir) {
        hurt(level, x, y, hurtDamage);
        return true;
    }

    @Override
    public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
        if (Game.isMode("Creative")) {
        	return false; // go directly to hurt method
        }
            
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == ToolType.Pickaxe) {
                if (player.payStamina(6 - tool.level) && tool.payDurability()) {
                    hurt(level, xt, yt, 1);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void hurt(Level level, int x, int y, int hurtDamage) {
        int damage = level.getData(x, y) + hurtDamage;
        int cactusHealth = 10;
        
        if (Game.isMode("Creative")) {
        	hurtDamage = damage = cactusHealth;
        }
        
        
        Sound.playAt("genericHurt", x << 4, y << 4);
        level.add(new SmashParticle(x << 4, y << 4));
        level.add(new TextParticle("" + hurtDamage, (x << 4) + 8, (y << 4) + 8, Color.RED));
        
        if (damage >= cactusHealth) {
            level.setTile(x, y, Tiles.get("Ferrosite"));
        } else {
            level.setData(x, y, damage);
        }
    }

    @Override
    public void render(Screen screen, Level level, int x, int y) {
        Tiles.get("Ferrosite").render(screen, level, x, y);
        sprite.render(screen, x << 4, y << 4);
    }

    @Override
    public void bumpedInto(Level level, int x, int y, Entity entity) {
        if (entity instanceof AirWizard || Settings.get("diff").equals("Peaceful")) {
            return; // Cannot do damage
        }

        if (entity instanceof Mob) {
            ((Mob) entity).hurt(this, x, y, random.nextInt(2) + Settings.getIndex("diff"));
        }
    }
}
