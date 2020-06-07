package minicraft.item.discs;

import java.util.ArrayList;

import minicraft.Sound;
import minicraft.entity.Player;
import minicraft.gfx.Color;
import minicraft.gfx.Sprite;
import minicraft.item.Item;
import minicraft.level.Level;
import minicraft.level.tile.Tile;

public class DiscSix extends Item {
	public static ArrayList<Item> getAllInstances() {
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(new DiscSix("Disc 6", Color.get(-1, 600, 600, 444), disc));
		
		return items;
	}
	
	protected static String disc;
	
	private DiscSix(String title, int color, String disc) {
		super(title, new Sprite(30, 4, color));
		this.disc = disc;
	}



	public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, int attackDir) {
		Sound.Disc6.play();
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
		return new DiscSix(name, sprite.color, disc);
	}


}