package minicraft.item;

import minicraft.core.Game;
import minicraft.entity.Boat;
import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.graphic.Sprite;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

public class BoatItem extends Item {
	private static final Sprite boatSprite = new Sprite(0, 32, 0);
	private boolean placed = false;

	public BoatItem(String name) {
		super(name, boatSprite);
	}

	@Override
	public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, Direction attackDir) {
		if (level.getTile(xt, yt) == Tiles.get("Water")) {
			Boat boat = new Boat();

			boat.x = (xt << 4) + 8;
			boat.y = (yt << 4) + 8;

			level.add(boat);
			if (Game.isMode("Creative")) {
				boat = boat.clone();
			} else {
				placed = true;
			}

			return true;
		}
		return false;
	}

	@Override
	public boolean isDepleted() {
		return placed;
	}

	@Override
	public Item clone() {
		return new BoatItem("Boat");
	}
}
