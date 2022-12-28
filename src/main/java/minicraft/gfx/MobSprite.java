package minicraft.gfx;

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

	    for (int r = 0; r < spritePixels.length; r++) {  // loop down through each row
	        for (int c = 0; c < spritePixels[r].length; c++) { // loop across through each column
				// the offsets are there to determine the pixel that will be there: the one in order, or on the opposite side.
	            int x = flipX ? w - 1 - c : c; // x offset
	            int y = flipY ? h - 1 - r : r; // y offset
	            spritePixels[r][c] = new Px(sx + x, sy + y, mirror, 2);
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
		for (int i = 0; i < sprites.length; i++) {
			sprites[i] = new MobSprite(sheetX + width * i, sheetY, width, height, mirror);
		}
		return sprites;
	}

	public static MobSprite[][] compileMobSpriteAnimations(int sheetX, int sheetY) {
	    // Create a two-dimensional array to hold the sprite animations
	    MobSprite[][] sprites = new MobSprite[4][2];

	    // Load in the sprite sheet and divide it into four sets of sprites
	    MobSprite[] set1 = MobSprite.compileSpriteList(sheetX, sheetY, 2, 2, 0, 4); // down, up, right 1, right 2
	    MobSprite[] set2 = MobSprite.compileSpriteList(sheetX, sheetY, 2, 2, 1, 4); // down, up, left 1, left 2

	    // Populate the array with the appropriate sprites for each direction and frame
	    sprites[0][0] = set1[0]; // down, first frame
	    sprites[0][1] = set2[0]; // down, second frame
	    
	    sprites[1][0] = set1[1]; // up, first frame
	    sprites[1][1] = set2[1]; // up, second frame
	    
	    sprites[2][0] = set2[2]; // left, first frame
	    sprites[2][1] = set2[3]; // left, second frame
	    
	    sprites[3][0] = set1[2]; // right, first frame
	    sprites[3][1] = set1[3]; // right, second frame

	    // Return the array of sprites
	    return sprites;
	}

	public void render(Screen screen, int x, int y, boolean fullbright) {
		for (int row = 0; row < spritePixels.length; row++) { // loop down through each row
			renderRow(row, screen, x, y + row * 8, fullbright);
		}
	}

	public void renderRow(int r, Screen screen, int x, int y, boolean fullbright) {
		Pixel[] row = spritePixels[r];
		for (int c = 0; c < row.length; c++) { // loop across through each column
			screen.render(x + c * 8, y, row[c], -1, fullbright); // render the sprite pixel.
		}
	}
}
