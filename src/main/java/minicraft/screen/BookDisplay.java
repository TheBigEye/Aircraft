package minicraft.screen;

import java.util.ArrayList;
import java.util.Arrays;

import minicraft.core.Game;
import minicraft.core.io.InputHandler;
import minicraft.graphic.Color;
import minicraft.graphic.Font;
import minicraft.graphic.Point;
import minicraft.graphic.Screen;
import minicraft.graphic.SpriteSheet;
import minicraft.screen.entry.StringEntry;
import minicraft.util.BookData;

public class BookDisplay extends Display {

    // null characters "\0" denote page breaks.
    private static final String defaultBook = " \n \0" + "There is nothing of use here." + "\0 \0" + "Still nothing... :P";

    private static final int spacing = 3;
    private static final int minX = 15; 
    private static final int maxX = 15 + 8 * 32;
    private static final int minY = 8 * 5;
    private static final int maxY = 8 * 5 + 8 * 16;

    private String[][] lines;
    protected int page;

    private final boolean hasTitle;
    private final boolean showPageCount;
    private final int pageOffset;

    public BookDisplay(String book) {
        this(book, false);
    }

    public BookDisplay(String book, boolean hasTitle) {
        page = 0;
        if (book == null) {
            book = defaultBook;
            hasTitle = false;
        }
        this.hasTitle = hasTitle;

        ArrayList<String[]> pages = new ArrayList<>();
        String[] splitContents = book.split("\0");
        for (String content : splitContents) {
            String[] remainder = {
            		content 
            };
            while (remainder[remainder.length - 1].length() > 0) {
                remainder = Font.getLines(remainder[remainder.length - 1], maxX - minX, maxY - minY, spacing, true);
                pages.add(Arrays.copyOf(remainder, remainder.length - 1)); // removes the last element of remainder, which is the leftover.
            }
        }

        lines = pages.toArray(new String[pages.size()][]);

        showPageCount = hasTitle || lines.length != 1;
        pageOffset = showPageCount ? 1 : 0;

        Menu.Builder builder = new Menu.Builder(true, spacing, RelPos.CENTER);

        Menu pageCount = builder.createMenu();
        
        if (book == BookData.Grimoire) {
            pageCount = builder
        		.setPositioning(new Point(Screen.w / 2, 48 + spacing), RelPos.BOTTOM)
                .setSize(maxX - minX + SpriteSheet.boxWidth * 2, maxY - minY + SpriteSheet.boxWidth * 2)
                .setShouldRender(false)
                .setBackground(18)
                .createMenu();
        } else {
            pageCount = builder
        		.setPositioning(new Point(Screen.w / 2, 48 + spacing), RelPos.BOTTOM)
                .setSize(maxX - minX + SpriteSheet.boxWidth * 2, maxY - minY + SpriteSheet.boxWidth * 2)
                .setShouldRender(false)
                .setBackground(19)
                .createMenu();
        }

        
        menus = new Menu[lines.length + pageOffset];
        if (showPageCount) {
            menus[0] = pageCount;
        }
        
        if (book == BookData.Grimoire) {
	        for (int i = 0; i < lines.length; i++) {
	            menus[i + pageOffset] = builder.setEntries(StringEntry.useLines(Color.GREEN, lines[i])).createMenu();
	        }
        } else {
	        for (int i = 0; i < lines.length; i++) {
	            menus[i + pageOffset] = builder.setEntries(StringEntry.useLines(Color.WHITE, lines[i])).createMenu();
	        }
        }

        menus[page + pageOffset].shouldRender = true;
    }

    protected void turnPage(int dir) {
        if (page + dir >= 0 && page + dir < lines.length) {
            menus[page + pageOffset].shouldRender = false;
            page += dir;
            if (showPageCount) {
                menus[0].updateSelectedEntry(new StringEntry(page == 0 && hasTitle ? "Title" : (page + 1) + "/" + lines.length, Color.BLACK));
            }
            menus[page + pageOffset].shouldRender = true;
        }
    }

    @Override
    public void tick(InputHandler input) {
    	// this is what closes the book; TODO if books were editable, I would probably remake the book here with the edited pages.
        if (input.getKey("menu").clicked || input.getKey("exit").clicked) {
            Game.exitDisplay(); 
        }
        
        if (input.getKey("cursor-left").clicked) {
            turnPage(-1); // this is what turns the page back
        }

        if (input.getKey("cursor-right").clicked) {
            turnPage(1); // this is what turns the page forward
        }

    }
    
    @Override
    public void render(Screen screen) {
        super.render(screen);
        
        String text = (page == 0 && hasTitle ? "Title" : (page + 1) + "/" + lines.length);
        int x = Screen.w / 2 + 128 - (Font.textWidth(text));
        Font.draw(text, screen, x, 180, Color.GRAY);
    }
}
