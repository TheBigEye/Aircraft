package minicraft.screen;

import minicraft.core.Game;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Sound;
import minicraft.graphic.Color;
import minicraft.graphic.Font;
import minicraft.graphic.Screen;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class CreditsDisplay extends Display {

	private int line = -230;
	private int tickTime = 0;

    private static class CreditLine {
        public final String text;
        public final int yOffset;
        public final int color;
        public CreditLine(String text, int yOffset, int color) {
            this.text = text;
            this.yOffset = yOffset;
            this.color = color;
        }
    }
    private List<CreditLine> creditLines = new ArrayList<>();

    public CreditsDisplay() {
        super(true);

		Sound.stop("musicTheme1");
		Sound.stop("musicTheme2");
		Sound.stop("musicTheme3");
		Sound.stop("musicTheme4");
		Sound.stop("musicTheme5");
		Sound.stop("musicTheme6");
		Sound.stop("musicTheme8");

        Sound.play("musicTheme3");
        loadCredits();
    }

    private void loadCredits() {
        try {
            InputStreamReader isr = new InputStreamReader(getClass().getResourceAsStream("/credits.json"));
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONArray arr = new JSONArray(sb.toString());
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String colorName = obj.getString("color").toUpperCase();
                int colorValue;
                switch (colorName) {
                    case "YELLOW": colorValue = Color.YELLOW; break;
                    case "WHITE": colorValue = Color.WHITE; break;
                    case "GRAY": colorValue = Color.GRAY; break;
                    case "DARK_GRAY": colorValue = Color.DARK_GRAY; break;
                    case "BLACK": colorValue = Color.BLACK; break;
                    case "RED": colorValue = Color.RED; break;
                    case "GREEN": colorValue = Color.GREEN; break;
                    case "BLUE": colorValue = Color.BLUE; break;
                    case "MAGENTA": colorValue = Color.MAGENTA; break;
                    case "CYAN": colorValue = Color.CYAN; break;
                    case "ORANGE": colorValue = Color.ORANGE; break;
                    default: colorValue = Color.WHITE; break;
                }
                creditLines.add(new CreditLine(
                    obj.getString("text"),
                    obj.getInt("yOffset"),
                    colorValue
                ));
            }
        } catch (IOException | NullPointerException e) {
            // fallback: do nothing, credits will not show
        }
    }

    @Override
    public void tick(InputHandler input) {
    	tickTime++;

    	if (tickTime % 7 == 0) {
    		line++;
    	}

    	if (line >= 860) {
    		Sound.stop("musicTheme3");
    		Game.exitDisplay();
    	}

    	super.tick(input);

    	if (input.getKey("exit").clicked) {
    		Sound.stop("musicTheme3");
    	}
    }

    @Override
	public void render(Screen screen) {
		super.render(screen);

		// Title sprite
		int h = 6; // Height of squares (on the spritesheet)
		int w = 26; // Width of squares (on the spritesheet)
		int xo = (Screen.w - (w << 3)) / 2; // X position on screen
		int yo = 25 - line; // Y position on screen

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				screen.render(xo + (x << 3), yo + (y << 3), x + ((y + 7) << 5), 0, 3);
			}
		}

		int sh = Screen.h / 2;
		for (CreditLine cl : creditLines) {
			Font.draw(cl.text, screen, sh - 41, cl.yOffset - line, cl.color);
		}
	}
}
