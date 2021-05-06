package minicraft.item;

public enum ToolType {
	Hoe (1, 64), // if there's a second number, it specifies durability.
	Shovel (0, 60),
	Sword (2, 68),
	Spear (7, 64),
	Pickaxe (3, 65),
	Axe (4, 65),
	Bow (5, 55),
	Claymore (6, 90),
	Shear (1, 42, true);

	public final int xPos; // X Position of origin
	public final int yPos; // Y position of origin
	public final int durability;
	public final boolean noLevel;
	
	ToolType(int xPos, int dur) {
		this.xPos = xPos;
		yPos = 13;
		durability = dur;
		noLevel = false;
	}

	ToolType(int xPos, int dur, boolean noLevel) {
		yPos = 12;
		this.xPos = xPos;
		durability = dur;
		this.noLevel = noLevel;
	}
}
