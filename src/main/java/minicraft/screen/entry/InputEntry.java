package minicraft.screen.entry;

import minicraft.core.io.ClipboardHandler;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Localization;
import minicraft.graphic.Color;
import minicraft.graphic.Font;
import minicraft.graphic.Screen;

public class InputEntry extends ListEntry {

	private String prompt;
	private String regex;
	private int maxLength;
	private boolean colon;

	public String userInput;

	private ChangeListener listener;

	private ClipboardHandler clipboardHandler = new ClipboardHandler();

	public InputEntry(String prompt, boolean colon) {
		this(prompt, null, 0, colon);
	}
	public InputEntry(String prompt, String regex, int maxLen, boolean colon) {
		this(prompt, regex, maxLen, "", colon);
	}
	public InputEntry(String prompt, String regex, int maxLen, String initValue, boolean colon) {
		this.prompt = prompt;
		this.regex = regex;
		this.maxLength = maxLen;
		this.colon = colon;

		userInput = initValue;
	}

	@Override
	public void tick(InputHandler input) {
		String prev = userInput;
		userInput = input.addKeyTyped(userInput, regex);

		if (!prev.equals(userInput) && listener != null)
			listener.onChange(userInput);

		if (maxLength > 0 && userInput.length() > maxLength)
			userInput = userInput.substring(0, maxLength); // truncates extra
		if (input.getKey("CTRL-V").clicked) {
			userInput = userInput + clipboardHandler.getClipboardContents();
		}
		if (!userInput.isEmpty()) {
			if (input.getKey("CTRL-C").clicked) {
				clipboardHandler.setClipboardContents(userInput);
			}
			if (input.getKey("CTRL-X").clicked) {
				clipboardHandler.setClipboardContents(userInput);
				userInput = "";
			}
		}
	}

	public String getUserInput() {
		return userInput;
	}

	public void clearUserInput() {
		userInput = "";
	}

	public String toString() {
		return Localization.getLocalized(prompt) + (prompt.isEmpty() ? "" : ": ") + userInput;
	}

	public void render(Screen screen, int x, int y, boolean isSelected) {
		if (colon) {
			Font.draw(toString(), screen, x, y, isValid() ? isSelected ? Color.DARK_GREEN : COLOR_UNSELECTED : Color.DARK_RED);
		} else {
			Font.draw(toString().replace(": ", " "), screen, x, y, isValid() ? isSelected ? Color.GREEN : COLOR_UNSELECTED : Color.DARK_RED);
		}
	}

	public boolean isValid() {
		return userInput.matches(regex);
	}

	public void setChangeListener(ChangeListener newListener) {
		listener = newListener;
	}
}
