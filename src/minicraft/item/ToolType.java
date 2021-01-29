package minicraft.item;

public enum ToolType {
	Hoe (1, 61), // if there's a second number, it specifies durability.
	Shovel (0, 75),
	Sword (2, 93),
	Spear (7, 83),
	Pickaxe (3, 79),
	Axe (4, 75),
	Bow (5, 61),
	Claymore (6, 94);

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
