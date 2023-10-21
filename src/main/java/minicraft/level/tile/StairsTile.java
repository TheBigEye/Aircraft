package minicraft.level.tile;

import minicraft.core.Game;
import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.furniture.Furniture;
import minicraft.entity.mob.Player;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.item.Item;
import minicraft.item.PowerGloveItem;
import minicraft.level.Level;

public class StairsTile extends Tile {
	
	// Normal stairs
	private static Sprite up_sprite = new Sprite(48, 0, 2, 2, 1, 0);
	private static Sprite down_sprite = new Sprite(48, 2, 2, 2, 1, 0);
	

	protected StairsTile(String name, boolean leadsUp) {	
		super(name, leadsUp ? up_sprite : down_sprite);

		maySpawn = false;
	}

	@Override
	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		super.interact(level, xt, yt, player, item, attackDir);

		// Makes it so you can remove the stairs if you are in creative and debug mode.
		if (item instanceof PowerGloveItem && Game.isMode("Creative") && Game.debug) {
			Sound.genericHurt.playOnLevel(xt << 4, yt << 4);
			level.setTile(xt, yt, Tiles.get("Grass"));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mayPass(Level level, int x, int y, Entity entity) {
		return !(entity instanceof Furniture);
	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {

		// Get the tiles from a 3x3 area from the tile center
		Tile[] areaTiles = level.getAreaTiles(x, y, 1);

		switch (level.depth) {
			case 1: Tiles.get("Cloud").render(screen, level, x, y); break; // Sky.
			default: Tiles.get("Dirt").render(screen, level, x, y); break; // caves, surface and dungeon
		}

		for (Tile tile : areaTiles) {
			if (tile == Tiles.get("Obsidian")) Tiles.get("Obsidian").render(screen, level, x, y);
			if (tile == Tiles.get("Stone bricks")) Tiles.get("Stone bricks").render(screen, level, x, y);
			if (tile == Tiles.get("Oak planks")) Tiles.get("Oak planks").render(screen, level, x, y);
			if (tile == Tiles.get("Spruce planks")) Tiles.get("Spruce planks").render(screen, level, x, y);
			if (tile == Tiles.get("Birch planks")) Tiles.get("Birch planks").render(screen, level, x, y);
			if (tile == Tiles.get("Holy bricks")) Tiles.get("Holy bricks").render(screen, level, x, y);
		}

		sprite.render(screen, x << 4, y << 4, 0);
	}

}
