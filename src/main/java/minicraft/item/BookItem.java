package minicraft.item;

import java.util.ArrayList;

import minicraft.core.Game;
import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.graphic.Sprite;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.screen.BookDisplay;
import minicraft.util.BookData;

public class BookItem extends Item {

    protected static ArrayList<Item> getAllInstances() {
        ArrayList<Item> items = new ArrayList<Item>();
        items.add(new BookItem("Book", new Sprite(0, 8, 0), null));
        items.add(new BookItem("Antidious", new Sprite(1, 8, 0), BookData.antVenomBook, true));
        items.add(new BookItem("Grimoire", new Sprite(2, 8, 0), BookData.Grimoire, true));
        return items;
    }

    protected String book; // TODO this is not saved yet; it could be, for editable books.
    public final boolean hasTitlePage;
    private Sprite sprite;

    private BookItem(String title, Sprite sprite, String book) {
        this(title, sprite, book, false);
    }

    private BookItem(String title, Sprite sprite, String book, boolean hasTitlePage) {
        super(title, sprite);
        this.book = book;
        this.hasTitlePage = hasTitlePage;
        this.sprite = sprite;
    }

    @Override
    public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, Direction attackDir) {
        Game.setDisplay(new BookDisplay(book, hasTitlePage));
        // level.add(new Cthulhu(1), player.x, player.y);
        return true;
    }

    @Override
    public boolean interactsWithWorld() {
        return false;
    }

    @Override
    public BookItem clone() {
        return new BookItem(getName(), sprite, book, hasTitlePage);
    }
}
