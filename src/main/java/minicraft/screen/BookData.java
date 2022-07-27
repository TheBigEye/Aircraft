package minicraft.screen;

import java.io.IOException;
import minicraft.saveload.Load;

public class BookData {

    public static final String about = "Modded by TheBigEye My goal is to expand Minicraft Plus with this Mod so that you can enjoy another adventure ... have fun and improve both the gameplay and the diversity of objects, mobs, Bosses and biomes.\n\nMinicraft was originally made by Markus Persson for ludum dare 22 competition.";

    public static final String instructions = "With the default controls...\n\nMove your character with arrow keys or WSAD. Press C to attack and X to open the inventory, and to use items. Pickup furniture and torches with V. Select an item in the inventory to equip it.\n\nThe Goal: Defeat the Eye queen!";

    public static final String credits = loadBook("credits");
    public static final String antVenomBook = loadBook("antidous");
    public static final String storylineGuide = loadBook("story_guide");
    public static final String AlAzif = loadBook("alazif");

    private static String loadBook(String bookTitle) {
        String book;
        try {
            book = String.join("\n", Load.loadFile("/resources/books/" + bookTitle + ".txt"));
            book = book.replaceAll("\\\\0", "\0");
        } catch (IOException ex) {
            ex.printStackTrace();
            book = "";
        }

        return book;
    }
}
