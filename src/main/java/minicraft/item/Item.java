package minicraft.item;

import minicraft.core.io.Localization;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Player;
import minicraft.gfx.Color;
import minicraft.gfx.Font;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.level.Level;
import minicraft.level.tile.Tile;

public abstract class Item {

    /*
     * Note: Most of the stuff in the class is expanded upon in
     * StackableItem/PowerGloveItem/FurnitureItem/etc
     */

    public final String name;
    public Sprite sprite;
    
    public int durAdjusted;
    public int arrAdjusted;

    // This is for multiplayer, when an item has been used, and is pending server
    // response as to the outcome, this is set to true so it cannot be used
    // again unless the server responds that the item wasn't used. Which should
    // basically replace the item anyway, soo... yeah. this never gets set back.
    public boolean usedPending = false;

    protected Item(String name) {
        sprite = Sprite.missingTexture(1, 1);
        this.name = name;
    }

    protected Item(String name, Sprite sprite) {
        this.name = name;
        this.sprite = sprite;
    }

    /// TODO this method (and Menu.renderItemList) is actually slowly getting
    /// depricated; I just haven't gotten around to updating all the menus yet.
    /// legacy; for compatibility

    /** Renders an item (sprite & name) in an inventory */
    public void renderInventory(Screen screen, int x, int y, boolean ininv) {
        String dispName = getDisplayName();
        sprite.render(screen, x, y);
        
        if (ininv) {
            String shortname = dispName.length() > 20 ? dispName.substring(0, 20) : dispName;
            Font.draw(shortname, screen, x + 8, y, Color.WHITE);
        } else {
            Font.draw(dispName, screen, x + 8, y, Color.get(0, 555));
        }
    }

    /** Renders an item on the HUD */
    public void renderHUD(Screen screen, int x, int y, int fontColor) {

        String dispName = getDisplayName();
        
        switch (dispName.length()) {
            case 6: durAdjusted = 0; break;
            case 7: durAdjusted = 4; break;
            case 8: durAdjusted = 8; arrAdjusted = 0; break;
            case 9: durAdjusted = 12; arrAdjusted = 4; break;
            case 10: durAdjusted = 16; arrAdjusted = 8; break;
            case 11: durAdjusted = 20; arrAdjusted = 12; break;
            case 12: durAdjusted = 24; arrAdjusted = 16; break;
            case 13: durAdjusted = 28; arrAdjusted = 20;break;
            case 14: durAdjusted = 32; arrAdjusted = 24; break;
            case 15: durAdjusted = 36; arrAdjusted = 28; break;
            case 16: durAdjusted = 40; arrAdjusted = 32; break;
            case 17: durAdjusted = 44; arrAdjusted = 36;break;
            case 18: durAdjusted = 48; arrAdjusted = 40; break;
            case 19: durAdjusted = 52; arrAdjusted = 44; break;
            case 20: durAdjusted = 56; arrAdjusted = 48; break;
            case 21: durAdjusted = 60; arrAdjusted = 52; break;
            case 22: durAdjusted = 64; arrAdjusted = 56; break;
        }

        int xx = (Screen.w - Font.textWidth(dispName)) / 2; // The width of the box
        int yy = (Screen.h - 8) - 1; // The height of the box
        int w = dispName.length() + 1; // Length of message in characters.
        int h = 1;

        // Renders the four corners of the box
        screen.render(xx - 8, yy - 8, 0 + 21 * 32, 0, 3);
        screen.render(xx + w * 8, yy - 8, 0 + 21 * 32, 1, 3);
        screen.render(xx - 8, yy + 8, 0 + 21 * 32, 2, 3);
        screen.render(xx + w * 8, yy + 8, 0 + 21 * 32, 3, 3);

        // Renders each part of the box...
        for (x = 0; x < w; x++) {
            screen.render(xx + x * 8, yy - 8, 1 + 21 * 32, 0, 3); // ...top part
            screen.render(xx + x * 8, yy + 8, 1 + 21 * 32, 2, 3); // ...bottom part
        }
        for (y = 0; y < h; y++) {
            screen.render(xx - 8, yy + y * 8, 2 + 21 * 32, 0, 3); // ...left part
            screen.render(xx + w * 8, yy + y * 8, 2 + 21 * 32, 1, 3); // ...right part
        }

        // The middle
        for (x = 0; x < w; x++) {
            screen.render(xx + x * 8, yy, 3 + 21 * 32, 0, 3);
        }

        // Item sprite
        sprite.render(screen, xx, yy);

        // Item name
        Font.drawTransparentBackground(dispName, screen, xx + 8, yy, fontColor);

    }

    /** Determines what happens when the player interacts with an entity */
    // TODO I want to move this to the individual entity classes.
    public boolean interact(Player player, Entity entity, Direction attackDir) {
        return false;
    }

    /** Determines what happens when the player interacts with a tile */
    public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, Direction attackDir) {
        return false;
    }

    /**
     * Returning true causes this item to be removed from the player's active item
     * slot
     */
    public boolean isDepleted() {
        return false;
    }

    /** Returns if the item can attack mobs or not */
    public boolean canAttack() {
        return false;
    }

    /** Sees if an item equals another item */
    public boolean equals(Item item) {
        return item != null && item.getClass().equals(getClass()) && item.name.equals(name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /** This returns a copy of this item, in all necessary detail. */
    public abstract Item clone();

    @Override
    public String toString() {
        return name + "-Item";
    }

    /**
     * Gets the necessary data to send over a connection. This data should always be
     * directly input-able into Items.get() to create a valid item with the given
     * properties.
     */
    public String getData() {
        return name;
    }

    public final String getName() {
        return name;
    }

    // returns the String that should be used to display this item in a menu or list.
    public String getDisplayName() {
        return Localization.getLocalized(getName());
    }

    public boolean interactsWithWorld() {
        return true;
    }
}