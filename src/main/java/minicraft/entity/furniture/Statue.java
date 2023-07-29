package minicraft.entity.furniture;

import java.util.ArrayList;

import minicraft.entity.mob.Player;
import minicraft.graphic.Sprite;

public class Statue extends Furniture {
	
	public static ArrayList<String> names = new ArrayList<>();

	public enum Type {
		Slime(new Sprite(8, 32, 2, 2, 2), 3, 2, 2),
		Zombie(new Sprite(10, 32, 2, 2, 2), 3, 2, 2),
		Skeleton(new Sprite(12, 32, 2, 2, 2), 3, 2, 2);
		
		protected Sprite sprite;
		protected int xr;
		protected int yr;
		protected int lightRadius;

		Type(Sprite sprite, int xr, int yr, int lightRadius) {
			this.sprite = sprite;
			this.xr = xr;
			this.yr = yr;
			this.lightRadius = lightRadius;
			Statue.names.add(this.name() + " Statue");
		}
	}

	public Statue.Type type;

	/**
	 * Creates a statue of a given type.
	 * 
	 * @param type What type of statue this is.
	 */
	public Statue(Statue.Type type) { // item
		super((type.name() + " Statue"), type.sprite, type.xr, type.yr);
		this.type = type;
	}

	@Override
	public boolean use(Player player) {
		return true;
	}
	
    @Override
    public int getLightRadius() {
        return type.lightRadius;
    }

	@Override
	public Furniture clone() {
		return new Statue(type);
	}

	@Override
	public String toString() {
		return type.name() + " Statue" + getDataPrints();
	}
}
