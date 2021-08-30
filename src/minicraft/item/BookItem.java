package minicraft.item;

import java.util.ArrayList;

import minicraft.core.Game;
import minicraft.entity.Direction;
import minicraft.entity.mob.Player;
import minicraft.gfx.Sprite;
import minicraft.level.Level;
import minicraft.level.tile.Tile;
import minicraft.screen.BookData;
import minicraft.screen.BookDisplay;
import minicraft.screen.BookEditableDisplay;

public class BookItem extends Item {

	protected static ArrayList<Item> getAllInstances() {
		ArrayList<Item> items = new ArrayList<Item>();
		//items.add(new BookItem("Book", new Sprite(0, 8, 0), null));
		items.add(new BookItem("Antidious", new Sprite(1, 8, 0), BookData.antVenomBook, true));
		items.add(new BookItem("AlAzif", new Sprite(0, 28, 0), BookData.AlAzif, true));
		items.add(new BookItem("Editable Book", new Sprite(0, 8, 0), "type here", false, true));
		return items;
	}

	protected String text; // TODO this is not saved yet; it could be, for editable books.
	public final boolean hasTitlePage;
	public final boolean editable;
	private Sprite sprite;

	private BookItem(String title, Sprite sprite, String text, boolean hasTitlePage) { this(title, sprite, text, hasTitlePage, false); }
	
	private BookItem(String title, Sprite sprite, String text, boolean hasTitlePage, boolean editable) {
		super(title, sprite);
		this.text = text;
		this.hasTitlePage = hasTitlePage;
		this.sprite = sprite;
		this.editable = editable;
	}


	public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, Direction attackDir) {
		Game.setMenu(editable ? new BookEditableDisplay(this) : new BookDisplay(text, hasTitlePage));
		return true;
	}

	@Override
	public boolean interactsWithWorld() { return false; }
	
	public BookItem clone() {
		return new BookItem(getName(), sprite, text, hasTitlePage, editable);
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return this.text;
	}
}
