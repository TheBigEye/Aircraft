package minicraft.entity.furniture.statue;

import minicraft.entity.furniture.Furniture;
import minicraft.gfx.Sprite;

public class SkeletonStatue extends Furniture {
	
	public SkeletonStatue() {
		super("SkeletonStatue", new Sprite(14, 28, 2, 2, 2), 3, 2);
	}
	
	@Override
	public Furniture clone() {
		return new SkeletonStatue();
	}

}
