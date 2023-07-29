package minicraft.graphic;

public class MobSprite extends Sprite {
	/**
	 * This class is meant specifically for mobs, becuase they have a special way of
	 * flipping and such. It's not only the pixels, as much as the whole sprite the
	 * flips.
	 */
	public MobSprite(int sx, int sy, int w, int h, int mirror) {
		/// this assumes the pixels are all neatly laid out on the spreadsheet, and should be flipped in position according to their mirroring.
	    super(new Pixel[h][w]);
	    boolean flipX = (mirror & 0x01) > 0;
	    boolean flipY = (mirror & 0x02) > 0;

	    for (int row = 0; row < spritePixels.length; row++) {  // loop down through each row
	    	int y = flipY ? h - 1 - row : row; // y offset
	        for (int column = 0; column < spritePixels[row].length; column++) { // loop across through each column
				// the offsets are there to determine the pixel that will be there: the one in order, or on the opposite side.
	            int x = flipX ? w - 1 - column : column; // x offset
	            spritePixels[row][column] = new Px(sx + x, sy + y, mirror, 2);
	        }
	    }
	}

	/**
	 * This is an easy way to make a list of sprites that are all part of the same
	 * "Sprite", so they have similar parameters, but they're just at different
	 * locations on the spreadsheet.
	 */
	public static MobSprite[] compileSpriteList(int sheetX, int sheetY, int width, int height, int mirror, int number) {
		MobSprite[] sprites = new MobSprite[number];
		for (int i = 0; i < number; i++) {
			sprites[i] = new MobSprite(sheetX + width * i, sheetY, width, height, mirror);
		}
		return sprites;
	}
	
    public static MobSprite[][] compileMobSpriteAnimations(int sheetX, int sheetY,int spriteW,int spriteH) {
        MobSprite[][] sprites = new MobSprite[4][2];
        // dir numbers: 0=down, 1=up, 2=left, 3=right.
        /// On the spritesheet, most mobs have 4 sprites there, first facing down, then up, then right 1, then right 2. The first two get flipped to animate them, but the last two get flipped to change direction.

        // Contents: down 1, up 1, right 1, right 2
        MobSprite[] set1 = MobSprite.compileSpriteList(sheetX, sheetY, spriteW, spriteH, 0, 4);

        // Contents: down 2, up 2, left 1, left 2
        MobSprite[] set2 = MobSprite.compileSpriteList(sheetX, sheetY, spriteW, spriteH, 1, 4);

        // Down
        sprites[0][0] = set1[0];
        sprites[0][1] = set2[0];

        // Up
        sprites[1][0] = set1[1];
        sprites[1][1] = set2[1];

        // Left
        sprites[2][0] = set2[2];
        sprites[2][1] = set2[3];

        // Right
        sprites[3][0] = set1[2];
        sprites[3][1] = set1[3];

        return sprites;
    }

	public static MobSprite[][] compileMobSpriteAnimations(int sheetX, int sheetY) {
        return compileMobSpriteAnimations(sheetX,sheetY, 2, 2);
    }

	public void render(Screen screen, int x, int y, boolean fullbright) {
		int pixelsLength = spritePixels.length;
		for (int row = 0; row < pixelsLength; row++) { // loop down through each row
			// row << 3 is equivalent to row * 8
			renderRow(row, screen, x, y + (row << 3), fullbright);
		}
	}

	public void renderRow(int r, Screen screen, int x, int y, boolean fullbright) {
		Pixel[] row = spritePixels[r];
		int rowLength = row.length;
		for (int column = 0; column < rowLength; column++) { // loop across through each column
			// column << 3 is equivalent to column * 8
			screen.render(x + (column << 3), y, row[column], -1, fullbright); // render the sprite pixel.
		}
	}
}
