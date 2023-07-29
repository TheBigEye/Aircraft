package minicraft.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

import minicraft.entity.furniture.Furniture;

public class Inventory {
	
	/** Random values used only for inventory **/
    private final Random random = new Random();
    
    private final List<Item> items = new ArrayList<>(); // The list of items that is in the inventory.

    /**
     * Returns all the items which are in this inventory.
     * 
     * @return ArrayList containing all the items in the inventory.
     */
    public List<Item> getItems() {
        return new ArrayList<>(items);
    }
    
    public int size() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }


    /**
     * Get one item in this inventory.
     * 
     * @param index The index of the item in the inventory's item array.
     * @return The specified item.
     */
    public Item get(int index) {
        return items.get(index);
    }

    /**
     * Remove an item in this inventory.
     * 
     * @param index The index of the item in the inventory's item array.
     * @return The removed item.
     */
    public Item remove(int index) {
        return items.remove(index);
    }

    public void addAll(Inventory other) {
        for (Item item : other.getItems()) {
            add(item.clone());
        }
    }

    /** Adds an item to the inventory */
    public void add(@Nullable Item item) {
        if (item != null) {
            add(items.size(), item); // adds the item to the end of the inventory list
        }
    }

    /**
     * Adds several copies of the same item to the end of the inventory.
     * 
     * @param item Item to be added.
     * @param amount Amount of items to add.
     */
    public void add(Item item, int amount) {
        for (int items = 0; items < amount; items++) {
            add(item.clone());
        }
    }

    /**
     * Adds an item to a specific spot in the inventory.
     * 
     * @param slot Index to place item at.
     * @param item Item to be added.
     */
    public void add(int slot, Item item) {
        // if (Game.debug) System.out.println("adding item to an inventory: " + item);
        if (item instanceof PowerGloveItem) {
            Logger.warn("Tried to add power glove to inventory, stack trace:");
            Thread.dumpStack();
            return; // do NOT add to inventory
        }

        if (item instanceof StackableItem) { // if the item is a item...
            StackableItem toTake = (StackableItem) item; // ...convert it into a StackableItem object.

            boolean added = false;
            int itemStackSize = items.size();
            
            for (int i = 0; i < itemStackSize; i++) {
                if (toTake.stacksWith(items.get(i))) {
                    // matching implies that the other item is stackable, too.
                    ((StackableItem) items.get(i)).count += toTake.count;
                    added = true;
                    break;
                }
            }

            if (!added) {
                items.add(slot, toTake);
            }
        } else {
            items.add(slot, item); // add the item to the items list
        }
    }

    /**
     * Removes items from your inventory; looks for stacks, and removes from each
     * until reached count. returns amount removed.
     */
    private int removeFromStack(StackableItem given, int count) {
        int removed = 0; // to keep track of amount removed.
        int itemStackSize = items.size();
        
        for (int item = 0; item < itemStackSize; item++) {
            if (!(items.get(item) instanceof StackableItem)) {
                continue;
            }
            
            StackableItem currentItem = (StackableItem) items.get(item);
            if (!currentItem.stacksWith(given)) {
                continue; // can't do equals, becuase that includes the stack size.
            }
            
            // equals; and current item is stackable.
            int amountRemoving = Math.min(count - removed, currentItem.count); // this is the number of items that are being removed from the stack this run-through.
            
            currentItem.count -= amountRemoving;
            if (currentItem.count == 0) { // remove the item from the inventory if its stack is empty.
                remove(item);
                item--;
            }
            
            removed += amountRemoving;
            if (removed == count) {
                break;
            }
            
            if (removed > count) { // just in case...
                System.out.println("SCREW UP while removing items from stack: " + (removed - count) + " too many.");
                break;
            }
            // if not all have been removed, look for another stack.
        }

        if (removed < count) {
            System.out.println("Inventory: could not remove all items; " + (count - removed) + " left.");
        }
        return removed;
    }

    /**
     * Removes the item from the inventory entirely, whether it's a stack, or a lone
     * item.
     */
    public void removeItem(Item item) {
        // if (Game.debug) System.out.println("original item: " + i);
        if (item instanceof StackableItem) {
            removeItems(item.clone(), ((StackableItem) item).count);
        } else {
            removeItems(item.clone(), 1);
        }
    }

    /**
     * Removes items from this inventory. Note, if passed a stackable item, this
     * will only remove a max of count from the stack.
     * 
     * @param given Item to remove.
     * @param count Max amount of the item to remove.
     */
    public void removeItems(Item given, int count) {
        if (given instanceof StackableItem) {
            count -= removeFromStack((StackableItem) given, count);
        } else {
        	int itemStackSize = items.size();
            for (int itemIndex = 0; itemIndex < itemStackSize; itemIndex++) {
                Item currentItem = items.get(itemIndex);
                if (currentItem.equals(given)) {
                    remove(itemIndex);
                    count--;
                    if (count == 0) {
                        break;
                    }
                }
            }
        }

        if (count > 0) Logger.warn("Could not remove " + count + " " + given + (count > 1 ? "s" : "") + " from inventory");
    }

    /** Returns the how many of an item you have in the inventory. */
    public int count(Item given) {
        if (given == null) {
            return 0; // null requests get no items. :)
        }

        int found = 0; // initialize counting var
        int itemStackSize = items.size();
        
        for (int itemIndex = 0; itemIndex < itemStackSize; itemIndex++) { // loop though items in inventory
            Item currentItem = items.get(itemIndex); // assign current item

            // if the item can be a stack...
            if (currentItem instanceof StackableItem && ((StackableItem) currentItem).stacksWith(given)) {
                found += ((StackableItem) currentItem).count; // add however many items are in the stack.
            } else if (currentItem.equals(given)) {
                found++; // otherwise, just add 1 to the found count.
            }
        }

        return found;
    }

    /**
     * Generates a string representation of all the items in the inventory which can
     * be sent over the network.
     * 
     * @return String representation of all the items in the inventory.
     */
    public String getItemData() {
        String itemdata = "";
        for (Item item : items) {
            itemdata += item.getData() + ":";
        }

        if (itemdata.length() > 0) {
            itemdata = itemdata.substring(0, itemdata.length() - 1); // remove extra ",".
        }

        return itemdata;
    }

    /**
     * Replaces all the items in the inventory with the items in the string.
     * 
     * @param items String representation of an inventory.
     */
    public void updateInventory(String items) {
        clear();

        if (items.length() == 0) {
            return; // there are no items to add.
        }

        for (String item : items.split(":")) { // this still generates a 1-item array when "items" is blank... [""].
            add(Items.get(item));
        }
    }

    /**
     * Tries to add an item to the inventory.
     * 
     * @param chance       Chance for the item to be added.
     * @param item         Item to be added.
     * @param num          How many of the item.
     * @param allOrNothing if true, either all items will be added or none, if false
     *                     its possible to add between 0-num items.
     */
    public void tryAdd(int chance, Item item, int num, boolean allOrNothing) {
        if (!allOrNothing || random.nextInt(chance) == 0) {
            for (int i = 0; i < num; i++) {
                if (allOrNothing || random.nextInt(chance) == 0) {
                    add(item.clone());
                }
            }
        }
    }

    public void tryAdd(int chance, @Nullable Item item, int num) {
        if (item == null) {
            return;
        }
        
        if (item instanceof StackableItem) {
            ((StackableItem) item).count *= num;
            tryAdd(chance, item, 1, true);
        } else {
            tryAdd(chance, item, num, false);
        }
    }

    public void tryAdd(int chance, @Nullable Item item) {
        tryAdd(chance, item, 1);
    }

    public void tryAdd(int chance, ToolType type, int lvl) {
        tryAdd(chance, new ToolItem(type, lvl));
    }

    /**
     * Tries to add an Furniture to the inventory.
     * 
     * @param chance Chance for the item to be added.
     * @param type   Type of furniture to add.
     */
    public void tryAdd(int chance, Furniture type) {
        tryAdd(chance, new FurnitureItem(type));
    }
}
