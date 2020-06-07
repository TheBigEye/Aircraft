package minicraft.item.discs;

import java.util.ArrayList;

import minicraft.Sound;
import minicraft.entity.Player;
import minicraft.gfx.Color;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.level.Level;
import minicraft.level.tile.Tile;

public class DiscTwo extends Item {
	public static ArrayList<Item> getAllInstances() {
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(new DiscTwo("Disc 2", Color.get(-1, 200, 696, 444), disc));
		
		return items;
	}
	
	protected static String disc;
	
	private DiscTwo(String title, int color, String disc) {
		super(title, new Sprite(30, 4, color));
		this.disc = disc;
	}



	public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, int attackDir) {
		Sound.Disc2.play();
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
		return new DiscTwo(name, sprite.color, disc);
	}


}
