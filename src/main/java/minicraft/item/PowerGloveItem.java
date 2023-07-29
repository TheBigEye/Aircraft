package minicraft.item;

import minicraft.graphic.Sprite;

public class PowerGloveItem extends Item {

	public PowerGloveItem() {
		super("Power Glove", new Sprite(0, 12, 0));
	}

	public PowerGloveItem clone() {
		return new PowerGloveItem();
	}
}
