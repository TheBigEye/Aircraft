package minicraft.item;

import java.util.Random;

import minicraft.core.Game;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.EyeQueen;
import minicraft.entity.mob.EyeQueenPhase2;
import minicraft.entity.mob.Player;
import minicraft.gfx.Sprite;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.screen.MapDisplay;

public class AmuletItem extends Item {
	
	private static Random random = new Random();

    public AmuletItem() {
        super("Eye Amulet", new Sprite(4, 33, 0));
    }

    @Override
    public Item clone() {
        return new AmuletItem();
    }

    @Override
    public boolean interact(Player player, Entity entity, Direction attackDir) {
        if (!Game.isValidServer()) {
        }
        return false;
    }

    @Override
    public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, Direction attackDir) {    	

		if (random.nextInt(3) == 1) {
			Sound.Call.play();
		}
		if (random.nextInt(3) == 2) {
			level.add(new EyeQueen(1), player.x, player.y);
		}
		if (random.nextInt(3) == 3) {
			level.add(new EyeQueen(1), player.x, player.y);
		}
		
        return this.interact(player, (Entity)null, attackDir);
    }

    @Override
    public boolean interactsWithWorld() {
        return false;
    }

    @Override
    public boolean canAttack() {
        return false;
    }
}
