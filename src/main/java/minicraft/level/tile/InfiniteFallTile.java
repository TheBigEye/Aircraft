package minicraft.level.tile;

import minicraft.core.Game;
import minicraft.core.Updater;
import minicraft.entity.Arrow;
import minicraft.entity.Entity;
import minicraft.entity.mob.AirWizard;
import minicraft.entity.mob.Player;
import minicraft.graphic.Sprite;
import minicraft.level.Level;

public class InfiniteFallTile extends Tile {
	
    protected InfiniteFallTile(String name) {
        super(name, (Sprite) null);
    }

    public boolean tick(Level level, int xt, int yt) {
        int damage = level.getData(xt, yt);
        if (damage > 0) {
            level.setData(xt, yt, damage - 1);
            return true;
        }
        return false;
    }

    public boolean mayPass(Level level, int x, int y, Entity entity) {
        if (entity instanceof AirWizard || entity instanceof Arrow) {
            return true;
        }
        if (entity instanceof Player) {
            Player player = (Player) entity;
            return player.suitOn || Game.isMode("Creative") || !Game.isMode("Creative") && player.fallWarn == true;
        }
        return false;
    }
    
    @Override
    public void bumpedInto(Level level, int x, int y, Entity entity) {
        if (entity instanceof Player) {
	        Player player = (Player) entity;
	        if (!Game.player.fallWarn && !Game.isMode("Creative")) {
	        	Updater.notifyAll("Watch out so you won't slip and fall!");
	        	player.hurt(this, x, y, 1);
	            Game.player.fallWarn = true;
	        }
        }
    }
}
