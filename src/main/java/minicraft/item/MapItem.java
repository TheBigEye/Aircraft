package minicraft.item;

import minicraft.core.Game;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Player;
import minicraft.graphic.Sprite;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.screen.MapDisplay;

public class MapItem extends Item {

	public MapItem() {
		super("Map book", new Sprite(4, 8, 0));
	}

    @Override
	public Item clone() {
        return new MapItem();
	}

	@Override
	public boolean interact(Player player, Entity entity, Direction attackDir) {
		Game.setDisplay(new MapDisplay());
		return false;
	}

	@Override
	public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, Direction attackDir) {
		return this.interact(player, (Entity) null, attackDir);
	}

	@Override
	public boolean interactsWithWorld() {
        return false;
	}
}
