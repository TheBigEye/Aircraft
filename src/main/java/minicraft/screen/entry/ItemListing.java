package minicraft.screen.entry;

import minicraft.item.Item;

public class ItemListing extends ItemEntry {

    private String info;

    public ItemListing(Item item, String text) {
        super(item);
        setSelectable(false);
        this.info = text;
    }

    public void setText(String text) {
        info = text;
    }

    @Override
    public String toString() {
        return " " + info;
    }
}
