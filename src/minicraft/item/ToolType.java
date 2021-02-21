package minicraft.item;

public enum ToolType {
	Hoe (1, 64), // if there's a second number, it specifies durability.
	Shovel (0, 60),
	Sword (2, 68),
	Spear (7, 64),
	Pickaxe (3, 65),
	Axe (4, 65),
	Bow (5, 55),
	Claymore (6, 90);

	public final int sprite; // sprite location on the spritesheet
	public final int durability;
	
	ToolType(int sprite, int dur) {
		this.sprite = sprite;
		durability = dur;
	}
	ToolType(int sprite) {
		this(sprite, -1); // durability defualts to -1 if not specified (means infinite durability)
	}
}
