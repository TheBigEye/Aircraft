package minicraft.screen;

import minicraft.core.Game;
import minicraft.core.io.InputHandler;
import minicraft.gfx.Screen;

public class TransitionDisplay extends Display {

	private static final int DURATION = 256;

	private int dt; // Direction that you are changing levels. (going up or down stairs)
	private int time = 0; // Time it spends on this menu

	public TransitionDisplay(int dt) {
		super(false, false);
		this.dt = dt;
	}

	public void tick(InputHandler input) {
		time++; // Ticks up 2 times per tick
		if (time == DURATION / 2)
			Game.setDisplay(null);

		// When time equals 60, it will get out of this menu
		if (time == DURATION)
			Game.setDisplay(null); 
	}

	public void render(Screen screen) {
		for (int x = 0; x < 200; x++) { // Loop however many times depending on the width (It's divided by 3 because the
			// pixels are scaled up by 3)
			for (int y = 0; y < 150; y++) { // Loop however many times depending on the height (It's divided by 3
				// because the pixels are scaled up by 3)
				int dd = (y + x % 2 * 2 + x / 3) - time * 2; // Used as part of the positioning.
				if (dd < 0 && dd > -100) {
					if (dt > 0) {
						// If the direction is upwards then render the squares going up
						screen.render(x * 8, y * 8, 14 + 24 * 32, 0, 3); 
					} else {
						// If the direction is negative, then the squares will go down.
						screen.render(x * 8, Screen.h - y * 8 - 8, 14 + 24 * 32, 0, 3); 
					}
				}
				
			}
		}
		
	}
	

}