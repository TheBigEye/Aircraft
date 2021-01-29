package minicraft.level.tile;

import minicraft.core.Game;
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
		super(name, (Sprite)null);
	}

	public void render(Screen screen, Level level, int x, int y) {}

	public void tick(Level level, int xt, int yt) {}
		
	
	public boolean mayPass(Level level, int x, int y, Entity e) {
		return e instanceof AirWizard || e instanceof AirWizardPhase2 || e instanceof AirWizardPhase3 || e instanceof Player && ( ((Player) e).skinon || Game.isMode("creative") );
	}
	
}
