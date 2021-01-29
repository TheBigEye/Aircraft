package minicraft.item.enchanted;

import java.util.ArrayList;

import minicraft.core.Game;
import minicraft.gfx.Sprite;
import minicraft.item.Item;

public class SharpSwordII extends Item{
	public static ArrayList<Item> getAllInstances() {
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(new SharpSwordII("Sharp II Sword", new Sprite(2, 18, 0), null));
		items.add(new SharpSwordII("Sharp II Claymore", new Sprite(6, 18, 0), null));
		return items;
	}
	
	protected String SharpII;
	private Sprite sprite;
	public int dur;
	
	private SharpSwordII(String title, Sprite sprite, String SharpII)
    {
		super(title, sprite);
		this.SharpII = SharpII;
		this.sprite = sprite;
	}
	
	/** You can attack mobs with tools. */
	public boolean canAttack() {
		return true;
	}
	
	public boolean payDurability() {
		if(dur <= 0) return false;
        if(!Game.isMode("creative")) dur--;
		return true;
	}

	public String getData() {
		return super.getData()+"_"+dur;
	}

	@Override
	public Item clone() {
		SharpSwordII ti = new SharpSwordII(getName(), sprite, SharpII);
		ti.dur = dur;
		return ti;
	}
			
}

