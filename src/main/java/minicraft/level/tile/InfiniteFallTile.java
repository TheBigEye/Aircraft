package minicraft.level.tile;

import minicraft.core.Game;
import minicraft.core.Updater;
import minicraft.entity.Arrow;
import minicraft.entity.Entity;
import minicraft.entity.mob.Player;
import minicraft.entity.mob.boss.AirWizard;
import minicraft.entity.mob.boss.AirWizardPhase2;
import minicraft.entity.mob.boss.AirWizardPhase3;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.level.Level;

public class InfiniteFallTile extends Tile {
	
    protected InfiniteFallTile(String name) {
        super(name, (Sprite) null);
    }

    public void render(Screen screen, Level level, int x, int y) {
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
        return e instanceof AirWizard || e instanceof AirWizardPhase2 || e instanceof AirWizardPhase3
                || e instanceof Arrow || e instanceof Player && (((Player) e).skinon 
                || Game.isMode("creative") ||
                
                // Make un-solid when trigger the fall warning in survival
                !Game.isMode("creative") && Game.player.fallWarn == true); 
    }
    
    @Override
    public void bumpedInto(Level level, int x, int y, Entity entity) {
        if (entity instanceof Player) {
	        Player p = (Player) entity;
	        if (Game.player.fallWarn == false && !Game.isMode("creative")) {
	        	Updater.notifyAll("Watch out so you won't slip and fall!");
	        	p.hurt(this, x, y, 1);
	            Game.player.fallWarn = true;
	        }
        } else {
        	return;
        }
    }

}
