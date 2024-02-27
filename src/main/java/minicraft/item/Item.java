package minicraft.item;

import java.util.Random;

import minicraft.core.io.Localization;
import minicraft.entity.Direction;
import minicraft.entity.Entity;
import minicraft.entity.mob.Player;
import minicraft.graphic.Color;
import minicraft.graphic.Font;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.level.Level;
import minicraft.level.tile.Tile;

public abstract class Item {

    /*
     * Note: Most of the stuff in the class is expanded upon in
     * StackableItem/PowerGloveItem/FurnitureItem/etc
     */
	
	/** Random values used for all the items instances **/
	protected static final Random random = new Random();

    public final String name;
    public Sprite sprite;
    
    public int durabilityOffset;
    public int arrowOffset;

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
    public void renderInventory(Screen screen, int x, int y, boolean inInventory) {
        String displayName = getDisplayName();
        sprite.render(screen, x, y);
        
        if (inInventory) {
            String shortname = displayName.length() > 20 ? displayName.substring(0, 20) : displayName;
            Font.draw(shortname, screen, x + 8, y, Color.WHITE);
        } else {
            Font.draw(displayName, screen, x + 8, y, Color.get(0, 555));
        }
    }

    /** Renders an item on the HUD */
    public void renderHUD(Screen screen, int x, int y, int fontColor) {

    	String displayName = getDisplayName();
    	durabilityOffset = (displayName.length() - 6) << 2;
    	arrowOffset = (displayName.length() - 9) << 1;

    	// Renders the box, item name and item sprite
    	Font.drawBox(screen, x, y, displayName.length() + 1, 1);
    	Font.draw(displayName, screen, x + 8, y, fontColor);
    	sprite.render(screen, x, y);
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
    
    public final Sprite getSprite() {
        return sprite;
    }

    // returns the String that should be used to display this item in a menu or list.
    public String getDisplayName() {
        return " " + Localization.getLocalized(getName());
    }

    public boolean interactsWithWorld() {
        return true;
    }
}