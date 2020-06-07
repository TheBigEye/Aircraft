package minicraft.item.discs;

import java.util.ArrayList;

import minicraft.Sound;
import minicraft.entity.Player;
import minicraft.gfx.Color;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.level.Level;
import minicraft.level.tile.Tile;

public class DiscFour extends Item {
	public static ArrayList<Item> getAllInstances() {
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(new DiscFour("Disc 4", Color.get(-1, 400, 800, 444), disc));
		
		return items;
	}
	
	protected static String disc;
	
	private DiscFour(String title, int color, String disc) {
		super(title, new Sprite(30, 4, color));
		this.disc = disc;
	}



	public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, int attackDir) {
		Sound.Disc4.play();
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
		return new DiscFour(name, sprite.color, disc);
	}


}
