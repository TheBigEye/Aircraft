package minicraft.screen.entry;

import minicraft.core.io.ClipboardHandler;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Localization;
import minicraft.gfx.Color;
import minicraft.gfx.Font;
import minicraft.gfx.Screen;

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
		if (!userInput.equals("")) {
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

	public String toString() {
		return Localization.getLocalized(prompt) + (prompt.length() == 0 ? "" : ": ") + userInput;
	}

	public void render(Screen screen, int x, int y, boolean isSelected) {
		if (colon == true) {
			Font.draw(toString(), screen, x, y, isValid() ? isSelected ? Color.GREEN : COL_UNSLCT : Color.DARK_RED);
		} else {
			Font.draw(toString().replace(": ", ""), screen, x, y, isValid() ? isSelected ? Color.GREEN : COL_UNSLCT : Color.DARK_RED);
		}
	}

	public boolean isValid() {
		return userInput.matches(regex);
	}

	public void setChangeListener(ChangeListener l) {
		listener = l;
	}
}
