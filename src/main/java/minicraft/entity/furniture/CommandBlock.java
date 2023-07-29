package minicraft.entity.furniture;

import minicraft.graphic.Sprite;

public class CommandBlock extends Furniture {

	public CommandBlock() {
		super("CommandBlock", new Sprite(18, 26, 2, 2, 2), 3, 2);
	}

	@Override
	public Furniture clone() {
		return new CommandBlock();
	}
}