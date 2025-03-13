package minicraft.screen.entry;

import minicraft.core.io.InputHandler;
import minicraft.core.io.Localization;
import minicraft.graphic.Color;
import minicraft.graphic.Font;
import minicraft.graphic.Screen;

import java.util.ArrayList;
import java.util.Collections;

// an unselectable line.
public class StringEntry extends ListEntry {

	private static final int DEFAULT_COLOR = Color.WHITE;

	private String text;
	private int color;

	public static StringEntry[] useLines(String... lines) {
		return useLines(DEFAULT_COLOR, lines);
	}

	public static StringEntry[] useLines(int color, String... lines) {
        return useLines(color, false, lines);
    }

	public static StringEntry[] useLines(int color, boolean getLocalized, String... lines) {
		ArrayList<String> linesList = new ArrayList<>();
		for (String line : lines) {
            Collections.addAll(linesList, Font.getLines(getLocalized ? Localization.getLocalized(line) : line, Screen.w - 20, Screen.h * 2, 0));
		}

		StringEntry[] entries = new StringEntry[linesList.size()];
		int linesListSize = linesList.size();

		for (int i = 0; i < linesListSize; i++) {
			entries[i] = new StringEntry(linesList.get(i), color);
        }

		return entries;
	}

	public StringEntry(String text) {
		this(text, DEFAULT_COLOR);
	}

	public StringEntry(String text, int color) {
        this(text, color, false);
    }

	public StringEntry(String text, int color, boolean getLocalized) {
		setSelectable(false);
		this.text = getLocalized? Localization.getLocalized(text): text;
		this.color = color;
	}

	public void setText(String text) {
		this.text = Localization.getLocalized(text);
	}

	@Override
	public void tick(InputHandler input) {}

	@Override
	public int getColor(boolean isSelected) {
        return color;
    }

	@Override
	public String toString() {
        return text;
    }
}
