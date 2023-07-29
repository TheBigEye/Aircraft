package minicraft.entity;

public enum Direction {

	NONE(0, 0), DOWN(0, 1), UP(0, -1), LEFT(-1, 0), RIGHT(1, 0);
	
	public static final Direction[] values = Direction.values();
	
	private final int x;
	private final int y;
	
	Direction(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public static Direction getDirection(int dir) {
		return values[dir + 1];
	}

	public static Direction getDirection(int xd, int yd) {
		// the attack was from the same entity, probably; or at least the exact same space.
		if (xd == 0 && yd == 0) return Direction.NONE; 

		if (Math.abs(xd) > Math.abs(yd)) {
			// the x distance is more prominent than the y distance
			if (xd < 0) { 
				return Direction.LEFT;        
			} else {
				return Direction.RIGHT;     
			}
		} else {
			if (yd < 0) {
				return Direction.UP;
			} else {
				return Direction.DOWN; 
			}
		}
	}


	public int getDir() {
		return ordinal() - 1;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
