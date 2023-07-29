package minicraft.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tinylog.Logger;

import minicraft.core.Game;
import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.graphic.Sprite;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.level.tile.Tiles;

public class TileItem extends StackableItem {

	protected static ArrayList<Item> getAllInstances() {
		ArrayList<Item> items = new ArrayList<>();

		/// TileItem sprites all have 1x1 sprites.
		items.add(new TileItem("Flower", (new Sprite(4, 0, 0)), "Flower", "Grass"));
		items.add(new TileItem("Orange Tulip", (new Sprite(22, 0, 0)), "Orange Tulip", "Grass"));
		items.add(new TileItem("Acorn", (new Sprite(1, 3, 0)), "Oak Sapling", "Grass"));
		items.add(new TileItem("Birch Cone", (new Sprite(18, 3, 0)), "Birch Sapling", "Grass"));
		items.add(new TileItem("Fir Cone", (new Sprite(18, 3, 0)), "Fir Sapling", "Snow"));
		items.add(new TileItem("Pine Cone", (new Sprite(19, 3, 0)), "Pine Sapling", "Snow"));
		items.add(new TileItem("Dirt", (new Sprite(0, 0, 0)), "Dirt", "Hole", "Water", "Lava"));
		items.add(new TileItem("Sky Grass", (new Sprite(0, 44, 0)), "Sky Grass", "Cloud"));
		
		items.add(new TileItem("Ice", (new Sprite(15, 21, 0)), "Ice", "Hole", "Water"));
		items.add(new TileItem("Brown Mushroom", (new Sprite(25, 0, 0)), "Brown Mushroom", "Mycelium"));
		items.add(new TileItem("Red Mushroom", (new Sprite(24, 0, 0)), "Red Mushroom", "Mycelium"));

		// Creative mode available tiles:
		items.add(new TileItem("Natural Rock", (new Sprite(2, 0, 0)), "Rock", "Hole", "Dirt", "Sand", "Path", "Grass", "Snow"));
		// items.add(new TileItem("Natural Hard Rock", (new Sprite(2, 0, 0)), "Hard rock", "hole", "dirt", "sand", "path", "grass"));

		items.add(new TileItem("Oak Plank", (new Sprite(0, 5, 0)), "Oak Planks", "Hole", "Water", "Infinite fall", "Lava"));
		items.add(new TileItem("Oak Wall", (new Sprite(1, 5, 0)), "Oak Wall", "Oak Planks"));
		items.add(new TileItem("Oak Door", (new Sprite(2, 5, 0)), "Oak Door", "Oak Planks"));

		items.add(new TileItem("Spruce Plank", (new Sprite(9, 5, 0)), "Spruce Planks", "Hole", "Water", "Infinite fall", "Lava"));
		items.add(new TileItem("Spruce Wall", (new Sprite(10, 5, 0)), "Spruce Wall", "Spruce Planks"));
		items.add(new TileItem("Spruce Door", (new Sprite(11, 5, 0)), "Spruce Door", "Spruce Planks"));

		items.add(new TileItem("Birch Plank", (new Sprite(12, 5, 0)), "Birch Planks", "Hole", "Water", "Infinite fall", "Lava"));
		items.add(new TileItem("Birch Wall", (new Sprite(13, 5, 0)), "Birch Wall", "Birch Planks"));
		items.add(new TileItem("Birch Door", (new Sprite(14, 5, 0)), "Birch Door", "Birch Planks"));

		items.add(new TileItem("Stone Brick", (new Sprite(3, 5, 0)), "Stone Bricks", "Hole", "Water", "Infinite fall", "Lava"));
		items.add(new TileItem("Stone Wall", (new Sprite(4, 5, 0)), "Stone Wall", "Stone Bricks"));
		items.add(new TileItem("Stone Door", (new Sprite(5, 5, 0)), "Stone Door", "Stone Bricks"));

		items.add(new TileItem("Holy Brick", (new Sprite(3, 5, 0)), "Holy Bricks", "Hole", "Water", "Infinite fall", "Lava"));
		items.add(new TileItem("Holy Wall", (new Sprite(4, 5, 0)), "Holy Wall", "Holy Bricks"));
		items.add(new TileItem("Holy Door", (new Sprite(5, 5, 0)), "Holy Door", "Holy Bricks"));

		items.add(new TileItem("Obsidian Brick", (new Sprite(6, 5, 0)), "Obsidian", "Hole", "Water", "Infinite fall", "Lava", "Raw Obsidian"));
		items.add(new TileItem("Obsidian Wall", (new Sprite(7, 5, 0)), "Obsidian Wall", "Obsidian", "Raw Obsidian"));
		items.add(new TileItem("Obsidian Door", (new Sprite(8, 5, 0)), "Obsidian Door", "Obsidian", "Raw Obsidian"));

		items.add(new TileItem("Wool", (new Sprite(0, 21, 0)), "Wool", "Hole", "Water", "Infinite fall", "Lava"));
		items.add(new TileItem("Light Gray Wool", (new Sprite(1, 21, 0)), "Light Gray Wool", "Hole", "Water", "Infinite fall", "Lava"));
		items.add(new TileItem("Gray Wool", (new Sprite(2, 21, 0)), "Gray Wool", "Hole", "Water", "Infinite fall", "Lava"));
		items.add(new TileItem("Black Wool", (new Sprite(3, 21, 0)), "Black Wool", "Hole", "Water", "Infinite fall", "Lava"));
		items.add(new TileItem("Green Wool", (new Sprite(4, 21, 0)), "Green Wool", "Hole", "Water", "Infinite fall", "Lava"));
		items.add(new TileItem("Lime Wool", (new Sprite(5, 21, 0)), "Lime Wool", "Hole", "Water", "Infinite fall", "Lava"));
		items.add(new TileItem("Yellow Wool", (new Sprite(6, 21, 0)), "Yellow Wool", "Hole", "Water", "Infinite fall", "Lava"));
		items.add(new TileItem("Orange Wool", (new Sprite(7, 21, 0)), "Orange Wool", "Hole", "Water", "Infinite fall", "Lava"));
		items.add(new TileItem("Brown Wool", (new Sprite(8, 21, 0)), "Brown Wool", "Hole", "Water", "Infinite fall", "Lava"));
		items.add(new TileItem("Red Wool", (new Sprite(9, 21, 0)), "Red Wool", "Hole", "Water", "Infinite fall", "Lava"));
		items.add(new TileItem("Pink Wool", (new Sprite(10, 21, 0)), "Pink Wool", "Hole", "Water", "Infinite fall", "Lava"));
		items.add(new TileItem("Magenta Wool", (new Sprite(11, 21, 0)), "Magenta Wool", "Hole", "Water", "Infinite fall", "Lava"));
		items.add(new TileItem("Purple Wool", (new Sprite(12, 21, 0)), "Purple Wool", "Hole", "Water", "Infinite fall", "Lava"));
		items.add(new TileItem("Blue Wool", (new Sprite(13, 21, 0)), "Blue Wool", "Hole", "Water", "Infinite fall", "Lava"));
		items.add(new TileItem("Cyan Wool", (new Sprite(14, 21, 0)), "Cyan Wool", "Hole", "Water", "Infinite fall", "Lava"));
		items.add(new TileItem("Light Blue Wool", (new Sprite(15, 21, 0)), "Light Blue Wool", "Hole", "Infinite fall", "Water", "Lava"));

		items.add(new TileItem("Sand", (new Sprite(0, 3, 0)), "Sand", "Dirt", "Water", "Lava", "Grass"));
		items.add(new TileItem("Snow Ball", (new Sprite(10, 3, 0)), "Snow", "Grass", "Dirt"));
		items.add(new TileItem("Cactus", (new Sprite(2, 3, 0)), "Cactus Sapling", "Sand"));
		items.add(new TileItem("Seeds", (new Sprite(3, 0, 0)), "Wheat", "Farmland"));
		items.add(new TileItem("Carrot", (new Sprite(18, 0, 0)), "Carrot", "Farmland"));
		items.add(new TileItem("Potato", (new Sprite(32, 0, 0)), "Potato", "Farmland"));
		items.add(new TileItem("Sky Wart", (new Sprite(0, 43, 0)), "Sky wart", "Sky farmland"));
		items.add(new TileItem("Sky Seeds", (new Sprite(1, 43, 0)), "Sky Wart", "Sky farmland"));
		items.add(new TileItem("Spores", (new Sprite(7, 43, 0)), "Parsnip", "Sky farmland"));
		items.add(new TileItem("Grass Seeds", (new Sprite(3, 0, 0)), "Grass", "Dirt"));
		items.add(new TileItem("Bone", (new Sprite(3, 3, 0)), "Oak Tree", "Oak Sapling"));
		items.add(new TileItem("Bone Powder", (new Sprite(0, 22, 0)), "Lawn", "Grass"));
		items.add(new TileItem("Ferrosite", (new Sprite(2, 44, 0)), "Ferrosite", "Infinite Fall", "Hole"));
		items.add(new TileItem("Cloud", (new Sprite(4, 3, 0)), "Cloud", "Infinite Fall", "Ferrosite", "Hole"));

		return items;
	}

	public final String model;
	public final List<String> validTiles;

	protected TileItem(String name, Sprite sprite, String model, String... validTiles) {
		this(name, sprite, 1, model, Arrays.asList(validTiles));
	}

	protected TileItem(String name, Sprite sprite, int count, String model, String... validTiles) {
		this(name, sprite, count, model, Arrays.asList(validTiles));
	}

	protected TileItem(String name, Sprite sprite, int count, String model, List<String> validTiles) {
		super(name, sprite, count);
		this.model = model.toUpperCase();
		this.validTiles = new ArrayList<>();
		for (String tile : validTiles)
			this.validTiles.add(tile.toUpperCase());
	}

	public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, Direction attackDir) {
		for (String tilename : validTiles) {
			if (tile.matches(level.getData(xt, yt), tilename)) {
				level.setTile(xt, yt, model); // TODO maybe data should be part of the saved tile..?

				//Sound.playerPlace.playOnDisplay();

				return super.interactOn(true);
			}
		}

		if (Game.debug) Logger.info("{} cannot be placed on {}", model, tile.name);

		String note = "";
		if (model.contains("WALL")) {
			note = "Can only be placed on " + Tiles.getName(validTiles.get(0)) + "!";

		} else if (model.contains("DOOR")) {
			note = "Can only be placed on " + Tiles.getName(validTiles.get(0)) + "!";

		} else if ((model.contains("BRICK") || model.contains("PLANK"))) {
			note = "Dig a hole first!";
		}

		if (note.length() > 0) {
			Game.notifications.add(note);
		}

		return super.interactOn(false);
	}

	@Override
	public boolean equals(Item other) {
		return super.equals(other) && model.equals(((TileItem) other).model);
	}

	@Override
	public int hashCode() {
		return super.hashCode() + model.hashCode();
	}

	public TileItem clone() {
		return new TileItem(getName(), sprite, count, model, validTiles);
	}
}
