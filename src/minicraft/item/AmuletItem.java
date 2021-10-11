package minicraft.item;

import java.util.Random;

import minicraft.core.Updater;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Player;
import minicraft.entity.mob.boss.EyeQueen;
import minicraft.gfx.Sprite;
import minicraft.level.Level;
import minicraft.level.tile.Tile;

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
    public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, Direction attackDir) {

        // Game.setMenu(new LevelTransitionDisplay(-4));

        if (random.nextInt(3) == 0) {
            Sound.Amulet_locked.play();
        }

        if (random.nextInt(3) == 1) {
            Sound.Amulet_locked_2.play();
        }

        if (random.nextInt(3) == 2) {
            Sound.Amulet_sucess.play();
            level.add(new EyeQueen(1), player.x, player.y);
            Updater.notifyAll("The eye queen has awakened!");
            Updater.setTime(600000);
        }

        // new XpOrb(player, player.x, player.y);
        // level.add(new XpOrb(random.nextInt(17) , xt + 8 , yt + 8), player.x,
        // player.y);

        return interact(player, (Entity) null, attackDir);
    }

    @Override
    public boolean interactsWithWorld() {
        return false;
    }

    @Override
    public boolean canAttack() {
        return true;
    }
}
