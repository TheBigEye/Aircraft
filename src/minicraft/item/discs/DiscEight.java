package minicraft.item.discs;

import java.util.ArrayList;

import minicraft.Sound;
import minicraft.entity.Player;
import minicraft.gfx.Color;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.level.Level;
import minicraft.level.tile.Tile;

public class DiscEight extends Item {
	public static ArrayList<Item> getAllInstances() {
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(new DiscEight("Disc 8", Color.get(-1, 800, 400, 444), disc));
		
		return items;
	}
	
	protected static String disc;
	
	private DiscEight(String title, int color, String disc) {
		super(title, new Sprite(30, 4, color));
		this.disc = disc;
	}



	public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, int attackDir) {
		Sound.Disc8.play();
		return true;
	}
	
	
	public boolean canAttack() {
		return true; 
		
	}
	
	public boolean canLight() {
		return true;
	}



	@Override
	public Item clone() {
		return new DiscEight(name, sprite.color, disc);
	}


}

