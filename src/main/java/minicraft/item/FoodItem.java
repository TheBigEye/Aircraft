package minicraft.item;

import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.graphic.Sprite;
import minicraft.level.Level;
import minicraft.level.tile.Tile;

import java.util.ArrayList;

public class FoodItem extends StackableItem {

    protected static ArrayList<Item> getAllInstances() {
        ArrayList<Item> items = new ArrayList<>();

        items.add(new FoodItem("Bread", new Sprite(7, 0, 0), 2));
        items.add(new FoodItem("Apple", new Sprite(16, 0, 0), 1));
        items.add(new FoodItem("Frozen palette", new Sprite(31, 0, 0), 4));
        items.add(new FoodItem("Mushroom Soup", new Sprite(27, 0, 0), 2));
        items.add(new FoodItem("Carrot Soup", new Sprite(28, 0, 0), 3));
        items.add(new FoodItem("Raw Chicken", new Sprite(29, 0, 0), 1));
        items.add(new FoodItem("Raw Pork", new Sprite(10, 0, 0), 1));
        items.add(new FoodItem("Raw Fish", new Sprite(14, 0, 0), 1));
        items.add(new FoodItem("Raw Beef", new Sprite(12, 0, 0), 1));
        items.add(new FoodItem("Cooked Chicken", new Sprite(30, 0, 0), 4));
        items.add(new FoodItem("Cooked Fish", new Sprite(15, 0, 0), 3));
        items.add(new FoodItem("Cooked Pork", new Sprite(11, 0, 0), 3));
        items.add(new FoodItem("Baked Potato", new Sprite(33, 0, 0), 3));
        items.add(new FoodItem("Steak", new Sprite(13, 0, 0), 3));
        items.add(new FoodItem("Gold Apple", new Sprite(17, 0, 0), 10));
        items.add(new FoodItem("Carrot", new Sprite(18, 0, 0), 2));
        items.add(new FoodItem("Potato", new Sprite(18, 0, 0), 2));
        items.add(new FoodItem("Gold Carrot", new Sprite(19, 0, 0), 9));

        return items;
    }

    private int heal; // the amount of hunger the food "satisfies" you by.
    private int staminaCost; // the amount of stamina it costs to consume the food.

    private FoodItem(String name, Sprite sprite, int heal) {
        this(name, sprite, 1, heal);
    }

    private FoodItem(String name, Sprite sprite, int count, int heal) {
        super(name, sprite, count);
        this.heal = heal;
        staminaCost = 5;
    }

    /** What happens when the player uses the item on a tile */
    public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, Direction attackDir) {
        boolean success = false;
        // if the player has hunger to fill, and stamina to pay...
        if (count > 0 && player.hunger < Player.maxHunger && player.payStamina(staminaCost)) {
            player.hunger = Math.min(player.hunger + heal, Player.maxHunger); // restore the hunger
            success = true;
        }

        return super.interactOn(success);
    }

    @Override
    public boolean interactsWithWorld() {
        return false;
    }

    public FoodItem clone() {
        return new FoodItem(getName(), sprite, count, heal);
    }
}
