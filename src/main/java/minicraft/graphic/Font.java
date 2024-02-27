package minicraft.graphic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Font {
    
    private static final int CHAR_SHEET_Y = 0;
    
    // These are all the characters that will be translated to the screen. (The spaces and the UTF8 incoding are important)
    private static final String chars =
		" !\"#$%&'()*+,-./0123456789:;<=>?" +
        "@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_" +
        "`abcdefghijklmnopqrstuvwxyz{|}~∞" +
        "ÇÜÉÂÄÀÅÇÊËÈÏÎÌÄÅÉæÆÔÖÒÛÙŸÖÜ¢£¥₧Ƒ" +
        "ÁÍÓÚñÑªº¿⌐¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐" +
        "└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀" +
        "αβΓπΣσµτΦΘΩδ∞φε∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■А" +
		"БВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ" +
        "абвгдеёжзийклмнопрстуфхцчшщъыьэю" +
        "яÁÍíÓÚÀÂÈÊËÌÎÏÒÔŒœÙÛÝýŸÃãÕõ";
    
    /*
     * The order of the letters in the chars string is represented in the order that
     * they appear in the sprite-sheet.
     */
    
	private static final int[] charsAdvance = new int[Font.chars.length()];

	public static void updateCharAdvances(SpriteSheet font) {
		for (int i = 0; i < chars.length(); ++i) {
			int c = i % 32;
			int r = (i / 32);

			int advance = 8;
			adfinder: for (int j = 7; j >= 0; --j) {
				int u = c * 8 + j;
				for (int k = 0; k < 8; ++k) {
					int v = r * 8 + k;
					if ((font.pixels[v * font.height + u] >> 24) != 0) {
						advance = j + 2;
						break adfinder;
					}
				}
			}

			Font.charsAdvance[i] = advance;
		}
	}

    public static void draw(String msg, Screen screen, int x, int y) {
        draw(msg, screen, x, y, -1);
    }

    /**
     * Draws the message to the x & y coordinates on the screen.
     * @param msg The message to be drawn on the screen
     * @param screen The screen object where the message will be drawn
     * @param x The x coordinate of the message on the screen
     * @param y The y coordinate of the message on the screen
     * @param whiteTint The white tint applied to the message
     */
    public static void draw(String msg, Screen screen, int x, int y, int whiteTint) {
        int len = msg.length(); // The length of the message
        
        int xx = x;
        
        // Loops through all the characters in the message
        for (int chr = 0; chr < len; chr++) {
            int charIndex = chars.indexOf(msg.charAt(chr)); // The current character's index in the `chars` string
            if (charIndex >= 0) { // Renders the character if it's index is valid             
                // Renders the character on the screen
                screen.render(xx, y, (charIndex % 32 + CHAR_SHEET_Y) + ((charIndex / 32 + CHAR_SHEET_Y) << 5), 0, 4, whiteTint);
            }
            xx += msg.charAt(chr) == ' ' ? 8 : charIndex >= 0 ? Font.charsAdvance[charIndex] : 8;
        }
    }

    
    public static void drawColor(String message, Screen screen, int x, int y) {
        // set default color message if it doesn't have initially
        if (message.charAt(0) != Color.COLOR_CHAR) {
            message = Color.WHITE_CODE + message;
        }

        int leading = 0;
        for (String data : message.split(String.valueOf(Color.COLOR_CHAR))) {
            if (data.isEmpty()) {
                continue;
            }

            String text;
            String color;

            try {
                text = data.substring(4);
                color = data.substring(0, 4); // ARGB value
            } catch (IndexOutOfBoundsException ignored) {
                // bad formatted colored string
                text = data;
                color = Color.WHITE_CODE;
            }

            Font.draw(text, screen, x + leading, y, Color.get(color));
            leading += Font.textWidth(text);
        }
    }

    public static void drawBackground(String msg, Screen screen, int x, int y) {
        drawBackground(msg, screen, x, y, -1);
    }

    public static void drawBackground(String msg, Screen screen, int x, int y, int whiteTint) {
    	int xx = x;
        
        for (int i = 0; i < msg.length(); i++) {
            // render the black background
            screen.render(xx, y, 12 + (24 << 5), 0, 3);
			int ix = chars.indexOf(msg.charAt(i));
			xx += msg.charAt(i) == ' ' ? 8 : ix >= 0 ? Font.charsAdvance[ix] : 8;
            
        }
        draw(msg, screen, x, y, whiteTint);
    }

    public static int textWidth(String text) {
		if (text == null) return 0;

		int width = 0;

		for (int i = 0; i < text.length(); ++i) {
			char chr = text.charAt(i);

			if (chr == Color.COLOR_CHAR) {
				i += 5;
				continue;
			}

			int idx = Font.chars.indexOf(chr);
			width += idx >= 0 ? Font.charsAdvance[idx] : 8;
		}

		return width;
    }

    public static int textWidth(String[] paragraph) {
        // this returns the maximum length of all the lines.
        if (paragraph == null || paragraph.length == 0) {
            return 0;
        }

        int max = textWidth(paragraph[0]);

        for (int i = 1; i < paragraph.length; i++) {
            max = Math.max(max, textWidth(paragraph[i]));
        }

        return max;
    }

    public static int textHeight() {// noinspection SuspiciousNameCombination
        return SpriteSheet.boxWidth;
    }

    public static void drawCentered(String msg, Screen screen, int y, int color) {
        new FontStyle(color).setYPos(y).draw(msg, screen);
    }

    /// note: the y centering values in the FontStyle object will be used as a
    /// paragraph y centering value instead.
    public static void drawParagraph(String para, Screen screen, FontStyle style, int lineSpacing) {
        drawParagraph(para, screen, Screen.w, Screen.h, style, lineSpacing);
    }

    public static void drawParagraph(String para, Screen screen, int w, int h, FontStyle style, int lineSpacing) {
        drawParagraph(screen, style, lineSpacing, getLines(para, w, h, lineSpacing));
    }

    /// all the other drawParagraph() methods end up calling this one.
    public static void drawParagraph(List<String> lines, Screen screen, FontStyle style, int lineSpacing) {
        drawParagraph(screen, style, lineSpacing, lines.toArray(new String[lines.size()]));
    }

    public static void drawParagraph(Screen screen, FontStyle style, int lineSpacing, String... lines) {
        for (int i = 0; i < lines.length; i++) {
            style.drawParagraphLine(lines, i, lineSpacing, screen);
        }
    }

    public static String[] getLines(String para, int w, int h, int lineSpacing) {
        return getLines(para, w, h, lineSpacing, false);
    }

    public static String[] getLines(String para, int w, int h, int lineSpacing, boolean keepEmptyRemainder) {
        ArrayList<String> lines = new ArrayList<>();

        // So, I have a paragraph. I give it to getLine, and it returns an index. Cut
        // the string at that index, and add it to the lines list.
        // check if the index returned by getLine is less than para.length(), and is a
        // space, and if so skip the space character.
        // then I reset the para String at the index, and do it again until para is an
        // empty string.

        int height = textHeight();
        while (para.length() > 0) { // continues to loop as long as there are more characters to parse.

            int splitIndex = getLine(para, w); // determine how many letters can be fit on to this line.
            lines.add(para.substring(0, splitIndex)); // add the specified number of characters.

            // if there are more characters to do, and the next character is a space or
            // newline, skip it (because the getLine() method will always break before
            // newlines, and will usually otherwise break before spaces.
            if (splitIndex < para.length() && para.substring(splitIndex, splitIndex + 1).matches("[ \n]")) {
                splitIndex++; 
            }
                              
             // remove the characters that have now been added on to the line
            para = para.substring(splitIndex); 

            // move y pos down a line
            height += lineSpacing + textHeight(); 
            
            // If we've run out of space to draw lines, then there's no point
            // in parsing more characters, so we should break out of the loop.
            if (height > h) {
                break; 
            }
        }
        // add remainder, but don't add empty lines unintentionally.
        if (para.length() > 0 || keepEmptyRemainder) {
            lines.add(para); 
        }
        return lines.toArray(new String[lines.size()]);
    }

    // this returns the position index at which the given string should be split so
    // that the first part is the longest line possible.
    // note, the index returned is exclusive; it should not be included in the line.
    private static int getLine(String text, int maxWidth) {
        if (maxWidth <= 0) {
            return 0; // just to pass the monkey test. :P
        }

        text = text.replaceAll(" ?\n ?", " \n ");

        String[] words = text.split(" ", -1);

        int curWidth = textWidth(words[0]);

        if (curWidth > maxWidth) {
            // we can't even fit the first word on to the line, even by itself. So we'll have to fit what we can.
            int i;
            for (i = 1; i < words[0].length(); i++) // find how many characters do fit
                if (textWidth(words[0].substring(0, i + 1)) > maxWidth){
                    break;
                }
            return i; // stop here and return, because we know we can't fit more so we can ignore all that's below
        }

        int i;
        for (i = 1; i < words.length; i++) {
            if (words[i].equals("\n")) break;

            curWidth += textWidth(" " + words[i]);
            if (curWidth > maxWidth) break;
        }
        // i now contains the number of words that fit on the line.
        String line = String.join(" ", Arrays.copyOfRange(words, 0, i));
        return line.length();
    }
    
    public static void drawBar(Screen screen, int x, int y, int length) {
        final int maxBarLength = 8;
        final int barLength = length / 12;
        final int inactiveBossbar = 24;
        final int activeBossbar = 25;

        screen.render(x + (maxBarLength * 2), y, 0 + inactiveBossbar * 32, 1, 3);

        for (int bx = 0; bx < maxBarLength; bx++) {
            screen.render(x + bx * 2, y, 1 + inactiveBossbar * 32, 0, 3);
        }

        screen.render(x - 5, y, 0 + activeBossbar * 32, 0, 3);

        for (int bx = 0; bx < barLength; bx++) {
            screen.render(x + bx * 2, y, 1 + activeBossbar * 32, 0, 3);
        }
    }

    
    public static void drawBox(Screen screen, int x, int y, int w, int h) {
		// Renders the four corners of the box	
		screen.render(x - 8, y - 8, 0 + (21 << 5), 0, 3);
		screen.render(x + (w << 3), y - 8, 0 + (21 << 5), 1, 3);
		screen.render(x - 8, y + 8 * h, 0 + (21 << 5), 2, 3);
		screen.render(x + (w << 3), y + 8 * h, 0 + (21 << 5), 3, 3);


		// Renders each part of the box...
		for (int xb = 0; xb < w; xb++) {
			screen.render(x + (xb << 3), y - 8, 1 + (21 << 5), 0, 3); // ...top part
			screen.render(x + (xb << 3), y + 8 * h, 1 + (21 << 5), 2, 3); // ...bottom part
		}
		for (int yb = 0; yb < h; yb++) {
			screen.render(x - 8, y + (yb << 3), 2 + (21 << 5), 0, 3); // ...left part
			screen.render(x + (w << 3), y + (yb << 3), 2 + (21 << 5), 1, 3); // ...right part
		}

		// The middle
		for (int xb = 0; xb < w; xb++) {
			for (int yb = 0; yb < h; yb++) {
				screen.render(x + (xb << 3), y + (yb << 3), 3 + (21 << 5), 0, 3);
			}
		}
    }
    
}