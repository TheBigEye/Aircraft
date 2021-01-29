package minicraft.screen;

import java.io.IOException;

import minicraft.saveload.Load;

public class BookData {
	
	public static final String about = "Modded by TheBigEye My goal is to expand Minicraft with this Mod so that you can enjoy another adventure ... have fun and improve both the gameplay and the diversity of objects, mobs, Bosses and biomes.\nMinicraft was originally made by Markus Persson for ludum dare 22 competition.";
	
	public static final String instructions = "With the default controls...\n\nMove your character with arrow keys or WSAD. Press C to attack and X to open the inventory, and to use items. Pickup furniture and torches with V. Select an item in the inventory to equip it.\n\nThe Goal: Defeat Cthulhu!";
	
	public static final String credits = "Thanks to...\n\nShylor !!!, although i never mention me or the mod... thanks to him there is the playminicraft community.. the one that is unique because of the people who make it up... thanks to him the revive minicraft project is up and running like the server discord.. thanks to you this and everything is possible... thanks. i hope you have read this.\n\nAlice for helping me a lot (help me to solve problems, and thanks to her for encouraging me to continue with the development :,) ).\n\nZucc, GC4k, Fusyon and the entire community for giving me ideas and helping me decide each change without affecting the gameplay (ideas and making decisions).\n\nRiverOaken, for making sn epic texture pack, i decided to put you here because you are the owner of Rcraft (the texture pack that you use for the mod), i also made some minor modifications for better details, thanks for making this also possible (Texture pack).\n\nChris and Cristoffer, thanks to you i was able to improve my programming skills and hel me, also without the minicraft plus mod... this would have been possible.. thanks (help and mod base).";
	
	public static final String antVenomBook = loadBook("antidous");
	public static final String storylineGuide = loadBook("story_guide");
	public static final String NecroBook = loadBook("necronomicon");
	
	private static final String loadBook(String bookTitle) {
		String book;
		try {
			book = String.join("\n", Load.loadFile("/resources/"+bookTitle+".txt"));
			book = book.replaceAll("\\\\0", "\0");
		} catch (IOException ex) {
			ex.printStackTrace();
			book = "";
		}
		
		return book;
	}
}
