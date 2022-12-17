package minicraft.level.tile;

import minicraft.core.Game;
import minicraft.core.Updater;
import minicraft.entity.Arrow;
import minicraft.entity.Entity;
import minicraft.entity.mob.Player;
import minicraft.entity.mob.boss.AirWizard;
import minicraft.entity.mob.boss.AirWizardPhase2;
import minicraft.entity.mob.boss.AirWizardPhase3;
import minicraft.gfx.Sprite;
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

    public boolean mayPass(Level level, int x, int y, Entity e) {
        if (e instanceof AirWizard || e instanceof AirWizardPhase2 || e instanceof AirWizardPhase3 || e instanceof Arrow) {
            return true;
        }
        if (e instanceof Player) {
            Player p = (Player) e;
            return p.suitOn || Game.isMode("Creative") || !Game.isMode("Creative") && p.fallWarn == true;
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
