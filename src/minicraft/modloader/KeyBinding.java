package minicraft.modloader;

import java.util.ArrayList;
import java.util.List;

public class KeyBinding {

	public static final List<KeyBinding> keybindings = new ArrayList<KeyBinding>();

	public int presses, absorbs;
	public boolean down, clicked;

	public KeyBinding() {
		keybindings.add(this);
	}

	public void toggle(boolean pressed) {
		if (pressed != down) {
			down = pressed;
		}
		if (pressed) {
			presses++;
		}
	}

	public void tick() {
		if (absorbs < presses) {
			absorbs++;
			clicked = true;
		} else {
			clicked = false;
		}
	}
}