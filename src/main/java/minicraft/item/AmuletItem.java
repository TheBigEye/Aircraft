package minicraft.item;

import java.util.ArrayList;

import minicraft.entity.Direction;
import minicraft.entity.Summoner;
import minicraft.entity.mob.EyeQueen;
import minicraft.entity.mob.Keeper;
import minicraft.entity.mob.MobAi;
import minicraft.entity.mob.Player;
import minicraft.graphic.Sprite;
import minicraft.level.Level;
import minicraft.level.tile.Tile;

public class AmuletItem extends Item {
	
	private boolean removed = false;

    protected static ArrayList<Item> getAllInstances() {
        ArrayList<Item> items = new ArrayList<Item>();
        items.add(new AmuletItem("Eye Amulet", new Sprite(11, 38, 0), new EyeQueen(1)));
        items.add(new AmuletItem("Slimy Amulet", new Sprite(11, 38, 0), new Keeper(1)));
        return items;
    }

    private MobAi mob;

    private AmuletItem(String name, Sprite sprite, MobAi mob) {
        super(name, sprite);
        this.mob = mob;
    }

    @Override
    public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, Direction attackDir) {	
		level.add(new Summoner(player, this, attackDir.getX(), attackDir.getY()));
		removed = true;
		return true;
    }

    @Override
    public boolean interactsWithWorld() {
        return false;
    }
    
    @Override
    public boolean isDepleted() {
        return removed;
    }
    
    public MobAi getSummonMob() {
    	return mob;
    }

    public AmuletItem clone() {
        return new AmuletItem(getName(), getSprite(), mob);
    }
}
