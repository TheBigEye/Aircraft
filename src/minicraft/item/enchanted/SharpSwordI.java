package minicraft.item.enchanted;

import java.util.ArrayList;
import java.util.Random;

import minicraft.core.Game;
import minicraft.gfx.Sprite;
import minicraft.item.Item;

public class SharpSwordI extends Item{
	public static ArrayList<Item> getAllInstances() {
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(new SharpSwordI("Sharp I Sword", new Sprite(2, 18, 0), null));
		items.add(new SharpSwordI("Sharp I Claymore", new Sprite(6, 18, 0), null));
		return items;
	}
	
	private Random random = new Random();
	protected String SharpI;
	private Sprite sprite;
	public int dur;
	public int level;
	
	private SharpSwordI(String title, Sprite sprite, String SharpI)
    {
		super(title, sprite);
		this.SharpI = SharpI;
		this.sprite = sprite;
	}
	
	/** You can attack mobs with tools. */
	public boolean canAttack() {
		return true;
	}
	
	/*public int getAttackDamageBonus(Entity e) {
		if(!payDurability())
			return 0;
		
		if(e instanceof Mob) {
			if (SharpI == SharpSwordI.super.getDisplayName()) {
				return (level + 1) * 3 + random.nextInt(4 + level * level * 3); // wood: 3-6 damage; gem: 15-66 damage.
			}
			return 1; // all other tools do very little damage to mobs.
		}
		
		return 0;
	}*/
	
	
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
		SharpSwordI ti = new SharpSwordI(getName(), sprite, SharpI);
		ti.dur = dur;
		return ti;
	}
			
}

