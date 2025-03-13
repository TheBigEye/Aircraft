package minicraft.graphic;

public class Color {

	/*
	 * To explain this class, you have to know how Bit-Shifting works. David made a
	 * small post, so go here if you don't already know:
	 * http://minicraftforums.com/viewtopic.php?f=9&t=2256
	 *
	 * Note: this class still confuses me a bit, lol. -David
	 *
	 * On bit shifting: the new bits will always be zero, UNLESS it is a negative
	 * number aka the left-most bit is 1. Then, shifting right will fill with 1's,
	 * it seems.
	 */

	/*
	 * To provide a method to the madness, all methods ending in "Color" are copies
	 * of the methods of the same name without the "Color" part, except they convert
	 * the given value to 24bit Java RGB rather than the normal, minicraft 13-bit
	 * total RGB. Methods containing the word "get" deal with converting the
	 * minicraft rgb to something else, and unGet for the other way.
	 *
	 * Another point:
	 *
	 * rgbByte = int using 8 bits to store the r, g, and b.
	 *
	 * rgbInt = int using 24 bits to store r, g, and b, with 8 bits each; this is the
	 * classic 0-255 scale for each color component, compacted into one integer variable.
	 *
	 * rgb4Sprite = int using the 4 bytes in an int to store each of the 4 rgbBytes used
	 * for coloring a sprite.
	 *
	 * rgbReadable = int representing rgb in a readable decimal format, by having the
	 * values 0-5 in the 100s, 10s, and 1s place for r, g, and b respectively.
	 *
	 * rgbMinicraft = int using 12 bits for r, g, and b, 4 bits each, and one for
	 * transparency
	 *
	 * So, the methods ending in "Color" deal with rgbInts, while their counterparts
	 * deal with rgbBytes.
	 */

	public static final int TRANSPARENT = Color.get(0, 0);
	public static final int WHITE = Color.get(1, 255);
	public static final int GRAY = Color.get(1, 153);
	public static final int DARK_GRAY = Color.get(1, 51);
	public static final int BLACK = Color.get(1, 0);
	public static final int RED = Color.get(1, 198, 44, 44);
	public static final int DARK_RED = Color.get(1, 166, 38, 38);
	public static final int GREEN = Color.get(1, 77, 212, 77);
	public static final int DARK_GREEN = Color.get(1, 56, 156, 56);
	public static final int BLUE = Color.get(1, 32, 32, 136);
	public static final int YELLOW = Color.get(1, 254, 254, 0);
	public static final int MAGENTA = Color.get(1, 255, 0, 255);
	public static final int CYAN = Color.get(1, 90, 204, 204);
	public static final int ORANGE = Color.get(1, 255, 138, 28);

	public static final char COLOR_CHAR = '§';

	public static final String TRANSPARENT_CODE = Color.toStringCode(Color.TRANSPARENT);
	public static final String WHITE_CODE = Color.toStringCode(Color.WHITE);
	public static final String GRAY_CODE = Color.toStringCode(Color.GRAY);
	public static final String DARK_GRAY_CODE = Color.toStringCode(Color.DARK_GRAY);
	public static final String BLACK_CODE = Color.toStringCode(Color.BLACK);
	public static final String RED_CODE = Color.toStringCode(Color.RED);
	public static final String GREEN_CODE = Color.toStringCode(Color.GREEN);
	public static final String BLUE_CODE = Color.toStringCode(Color.BLUE);
	public static final String YELLOW_CODE = Color.toStringCode(Color.YELLOW);
	public static final String MAGENTA_CODE = Color.toStringCode(Color.MAGENTA);
	public static final String CYAN_CODE = Color.toStringCode(Color.CYAN);

	/** This returns a rgbMinicraft. alpha should be between 0-1, red, green, and blue should be 0-255 */
	public static int get(int alpha, int red, int green, int blue) {
		return (alpha << 24) + (red << 16) + (green << 8) + (blue);
	}

	/** This returns a rgbMinicraft. alpha should be between 0-1, copy should be 0-255 */
	public static int get(int alpha, int copy) {
		return get(alpha, copy, copy, copy);
	}

	public static String toStringCode(int color) {
		return new String(new char[] {
			Color.COLOR_CHAR,
			(char) ((color >> 24) & 0xFF), // Alpha
			(char) ((color >> 16) & 0xFF), // Red
			(char) ((color >> 8) & 0xFF), // Blue
			(char) (color & 0xFF) // Green
		});
	}

	public static int get(String color) {
		// omit color character if it's present
		int leading = color.length() == 5 ? 1 : 0;
		return Color.get(color.charAt(leading), color.charAt(1 + leading), color.charAt(2 + leading), color.charAt(3 + leading));
	}

	private static int limit(int num, int min, int max) {
		if (num < min) {
			num = min;
		}
		if (num > max) {
			num = max;
		}
		return num;
	}

	// this makes an int that you would pass to the get(a,b,c,d), or get(d), method,
	// from three separate 8-bit r,g,b values.
	public static int rgb(int red, int green, int blue) { // rgbInt array -> rgbReadable
		red = limit(red, 0, 250);
		green = limit(green, 0, 250);
		blue = limit(blue, 0, 250);

		return red / 50 * 100 + green / 50 * 10 + blue / 50; // this is: rgbReadable
	}

	/** This method darkens or lightens a color by the specified amount. */
	public static int tint(int color, int amount, boolean isSpriteCol) {
		if (isSpriteCol) {

			// this just separates the four 8-bit sprite colors; they are still in base-6 added form.
			int[] rgbBytes = separateEncodedSprite(color);
			for (int i = 0; i < rgbBytes.length; i++) {
				rgbBytes[i] = tint(rgbBytes[i], amount);
			}

			return rgbBytes[0] << 24 | rgbBytes[1] << 16 | rgbBytes[2] << 8 | rgbBytes[3]; // this is: rgb4Sprite

		} else {
			return tint(color, amount); // this is: rgbByte
		}
	}

	private static int tint(int rgbByte, int amount) {
		if (rgbByte == 255) {
			return 255; // see description of bit shifting above; it will hold the 255 value, not -1
		}

		// this returns the rgb values as 0-5 numbers.
		int[] rgb = decodeRGB(rgbByte);
		for (int i = 0; i < rgb.length; i++) {
			rgb[i] = limit(rgb[i] + amount, 0, 5);
		}

		return rgb[0] * 36 + rgb[1] * 6 + rgb[2]; // this is: rgbByte
	}

	/**
	 * seperates a 4-part sprite color (rgb4Sprite) into it's original 4 component colors (which are each rgbBytes)
	 */
	/// reverse of Color.get(a, b, c, d).
	public static int[] separateEncodedSprite(int rgb4Sprite) {
		return separateEncodedSprite(rgb4Sprite, false);
	}

	public static int[] separateEncodedSprite(int rgb4Sprite, boolean convertToReadable) {
		// the numbers are stored, most to least shifted, as d, c, b, a.
		int a = (rgb4Sprite >> 24) & 0xFF; // See note at top; this is to remove left-hand 1's.
		int b = (rgb4Sprite & 0x00_FF_00_00) >> 16;
		int c = (rgb4Sprite & 0x00_00_FF_00) >> 8;
		int d = (rgb4Sprite & 0x00_00_00_FF);

		if (convertToReadable) {
			// they become rgbReadable
			a = unGet(a);
			b = unGet(b);
			c = unGet(c);
			d = unGet(d);
		} // else, they are rgbByte

		return new int[] { a, b, c, d };
	}

	/**
	 * This turns a 216 scale rgb int into a 0-5 scale "concatenated" rgb int. (aka rgbByte -> r/g/b Readables)
	 */
	public static int[] decodeRGB(int rgbByte) {
		int r = (rgbByte / 36) % 6;
		int g = (rgbByte / 6) % 6;
		int b = rgbByte % 6;
		return new int[] { r, g, b };
	}

	public static int unGet(int rgbByte) { // rgbByte -> rgbReadable
		int[] cols = decodeRGB(rgbByte);
		return cols[0] * 100 + cols[1] * 10 + cols[2];
	}

	/// this turns a 25-bit minicraft color into a 24-bit rgb color.
	protected static int upgrade(int rgbMinicraft) {

		return rgbMinicraft & 0xFF_FF_FF;
	}

	protected static int tintColor(int rgbInt, int amount) {
		if (rgbInt < 0) {
			return rgbInt; // this is "transparent".
		}

		int[] comps = decodeRGBColor(rgbInt);

		for (int i = 0; i < comps.length; i++) {
			comps[i] = limit(comps[i] + amount, 0, 255);
		}

		return comps[0] << 16 | comps[1] << 8 | comps[2];
	}

	public static int[] decodeRGBColor(int rgbInt) {
	    int r = (rgbInt & 0xFF0000) >> 16;
	    int g = (rgbInt & 0x00FF00) >> 8;
	    int b = (rgbInt & 0x0000FF);

	    return new int[] { r, g, b };
	}

	/// this is for color testing.
	public static void main(String[] args) {
		int r = Integer.parseInt(args[0]); // red
		int g = Integer.parseInt(args[1]); // green
		int b = Integer.parseInt(args[2]); // blue

		System.out.println(rgb(r, g, b));
	}

	/// for sprite colors
	public static String toString(int col) {
		return java.util.Arrays.toString(Color.separateEncodedSprite(col, true));
	}

	protected static int getShadow(int col, int intensity_r, int intensity_g, int intensity_b) {
		int r = (col & 0xFF_00_00) >> 16;
		int g = (col & 0x00_FF_00) >> 8;
		int b = (col & 0x00_00_FF);

		r -= intensity_r;
		if (r < 0) r = 0;

		g -= intensity_g;
		if (g < 0) g = 0;

		b -= intensity_b;
		if (b < 0) b = 0;

		return (r << 16) + (g << 8) + b;
	}

	protected static int getShadow(int col, int intensity) {
		return getShadow(col, intensity, intensity, intensity);
	}
}
