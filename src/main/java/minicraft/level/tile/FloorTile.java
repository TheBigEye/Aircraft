package minicraft.level.tile;

import minicraft.core.io.Sound;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Player;
import minicraft.graphic.Sprite;
import minicraft.item.Item;
import minicraft.item.Items;
import minicraft.item.ToolItem;
import minicraft.level.Level;

public class FloorTile extends Tile {
	private Sprite sprite;

	protected Material type;

	protected FloorTile(Material type) {
		super((
			type == Material.Oak ? "Oak Planks" : 
			type == Material.Spruce ? "Spruce Planks" : 
			type == Material.Birch ? "Birch Planks" : 
			type == Material.Holy ? "Holy Bricks" : 
			type == Material.Obsidian ? "Obsidian" : 
			type.name() + " Bricks"),

			(Sprite) null
		);

		connectsToSkyGrass = true;
		connectsToSkyHighGrass = true;
		connectsToSkyDirt = true;

		this.type = type;
		maySpawn = true;
		switch (type) {
			case Oak: sprite = new Sprite(5, 26, 2, 2, 1, 0); break;
			case Stone: sprite = new Sprite(14, 26, 2, 2, 1, 0); break;
			case Obsidian: sprite = new Sprite(23, 26, 2, 2, 1, 0); break;
			case Spruce: sprite = new Sprite(32, 26, 2, 2, 1, 0); break;
			case Birch: sprite = new Sprite(41, 26, 2, 2, 1, 0); break;
			case Holy: sprite = new Sprite(50, 26, 2, 2, 1, 0); break;
		}
		super.sprite = sprite;
	}

	@Override
	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == type.getRequiredTool()) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					
					Sound.genericHurt.playOnLevel(xt << 4, yt << 4);

					if (level.depth == 1) {
						level.setTile(xt, yt, Tiles.get("Cloud hole"));
					} else {
						level.setTile(xt, yt, Tiles.get("Hole"));
					}

					Item drop;
					switch (type) {
						case Oak: drop = Items.get("Oak Plank"); break;
						case Spruce: drop = Items.get("Spruce Plank"); break;
						case Birch: drop = Items.get("Birch Plank"); break;
						default: drop = Items.get(type.name() + " Brick"); break;
					}
					
					level.dropItem((xt << 4) + 8, (yt << 4) + 8, drop);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean mayPass(Level level, int x, int y, Entity entity) {
		return true;
	}
}
