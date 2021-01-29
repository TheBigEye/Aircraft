package minicraft.entity.furniture.statue;

import minicraft.entity.furniture.Furniture;
import minicraft.gfx.Sprite;

public class SlimeStatue extends Furniture {
	
	public SlimeStatue() {
		super("SlimeStatue", new Sprite(10, 28, 2, 2, 2), 3, 2);
	}
	
	@Override
	public Furniture clone() {
		return new SlimeStatue();
	}

}
