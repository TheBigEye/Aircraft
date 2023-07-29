package minicraft.item;

import java.util.ArrayList;

import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.graphic.Sprite;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.level.tile.TorchTile;

public class TorchItem extends TileItem {

    public static ArrayList<Item> getAllInstances() {
        ArrayList<Item> items = new ArrayList<>();
        items.add(new TorchItem());
        return items;
    }

    private TorchItem() {
        this(1);
    }

    private TorchItem(int count) {
        super("Torch", (new Sprite(5, 3, 0)), count, "", "Dirt", "Oak Planks", "Stone Bricks", "Obsidian", "Wool",
                "Red Wool", "Blue Wool", "Lime Wool", "Yellow Wool", "Purple Wool", "Pink Wool", "Green Wool",
                "Gray Wool", "Brown Wool", "Magenta Wool", "Light Blue Wool", "Cyan Wool", "Orange Wool", "Black Wool",
                "Grass", "Sand", "Spruce Planks", "Birch Planks", "Sky grass", "Sky high grass", "Sky dirt",
                "Ferrosite");
    }

    @Override
    public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, Direction attackDir) {
        if (validTiles.contains(tile.name)) {
            level.setTile(xt, yt, TorchTile.getTorchTile(tile));
            return super.interactOn(true);
        }
        return super.interactOn(false);
    }

    @Override
    public boolean equals(Item other) {
        return other instanceof TorchItem;
    }

    @Override
    public int hashCode() {
        return 8931;
    }

    @Override
    public TorchItem clone() {
        return new TorchItem(count);
    }
}
