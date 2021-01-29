package minicraft.entity.furniture.statue;

import minicraft.entity.furniture.Furniture;
import minicraft.gfx.Sprite;

public class ZombieStatue extends Furniture {
	
	public ZombieStatue() {
		super("ZombieStatue", new Sprite(12, 28, 2, 2, 2), 3, 2);
	}
	
	@Override
	public Furniture clone() {
		return new ZombieStatue();
	}

}
