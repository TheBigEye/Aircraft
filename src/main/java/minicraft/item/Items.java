package minicraft.item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

import java.util.ArrayList;

public class Items {

    /// I've checked -- this is only used for making the creative inventory, and in
    /// Load.java.
    /// ...well, that used to be true...

    /**
     * Ok, so here's the actual big idea:
     * <p>
     * This class is meant to define all the different kinds of items in minicraft.
     * Item(Type).java might be what maps the different item sprites in the
     * spritesheet to a name, but it doesn't really define anything final. This
     * class has all the items you could possibly have, and every form of them, more
     * or less.
     * <p>
     * If you want to access one of those items, you do it through this class, by
     * calling get("item name"); casing does not matter.
     */
    private static final ArrayList<Item> items = new ArrayList<>();

    private static void add(Item item) {
        items.add(item);
    }

    private static void addAll(ArrayList<Item> items) {
        for (Item item : items) {
            add(item);
    	}
    }

    static {
        add(new PowerGloveItem());
        addAll(FurnitureItem.getAllInstances());
        add(new BoatItem("Boat"));
        addAll(TorchItem.getAllInstances());
        addAll(BucketItem.getAllInstances());
        addAll(BookItem.getAllInstances());
        add(new MapItem());
        addAll(AmuletItem.getAllInstances());
        addAll(TileItem.getAllInstances());
        addAll(FishingRodItem.getAllInstances());
        addAll(ToolItem.getAllInstances());
        addAll(FoodItem.getAllInstances());
        addAll(StackableItem.getAllInstances());
        addAll(ClothingItem.getAllInstances());
        addAll(ArmorItem.getAllInstances());
        addAll(PotionItem.getAllInstances());
    }

    /** fetches an item from the list given its name. */
    @NotNull
    public static Item get(String name) {
        Item item = get(name, false);
        if (item == null) {
            return new UnknownItem("NULL"); // technically shouldn't ever happen
        }
        return item;
    }

    @Nullable
    public static Item get(String name, boolean allowNull) {
        name = name.toUpperCase();
        // System.out.println("fetching name: \"" + name + "\"");
        int data = 1;
        boolean hadUnderscore = false;
        if (name.contains("_")) {
            hadUnderscore = true;
            try {
                data = Integer.parseInt(name.substring(name.indexOf("_") + 1));
            } catch (Exception exception) {
            	exception.printStackTrace();
            }
            name = name.substring(0, name.indexOf("_"));
        } else if (name.contains(";")) {
            hadUnderscore = true;
            try {
                data = Integer.parseInt(name.substring(name.indexOf(";") + 1));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            name = name.substring(0, name.indexOf(";"));
        }

        if (name.equalsIgnoreCase("NULL")) {
            if (allowNull) {
                return null;
            } else {
            	Logger.warn("Items.get passed argument \"null\" when null is not allowed; returning UnknownItem.");
                return new UnknownItem("NULL");
            }
        }

        if (name.equals("UNKNOWN"))
            return new UnknownItem("BLANK");

        Item item = null;
        for (Item currentItem : items) {
            if (currentItem.getName().equalsIgnoreCase(name)) {
                item = currentItem;
                break;
            }
        }

        if (item != null) {
            item = item.clone();
            if (item instanceof StackableItem) {
                ((StackableItem) item).count = data;
            }
            if (item instanceof ToolItem && hadUnderscore) {
                ((ToolItem) item).durability = data;
            }
            return item;
        } else {
			Logger.error("Requested invalid item with name: '{}'", name);
            return new UnknownItem(name);
        }
    }

    public static Item arrowItem = get("arrow");

    public static void fillCreativeInventory(Inventory inventory) {
        fillCreativeInventory(inventory, true);
    }

    public static void fillCreativeInventory(Inventory inventory, boolean addAll) {
        for (Item item : items) {
            if (!(item instanceof PowerGloveItem) && (addAll || inventory.count(item) == 0)) {
            	inventory.add(item.clone());
            }
        }
    }

}
