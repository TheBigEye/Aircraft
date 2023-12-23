package minicraft.screen;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import minicraft.core.Game;
import minicraft.core.Renderer;
import minicraft.core.World;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Sound;
import minicraft.graphic.Color;
import minicraft.graphic.Font;
import minicraft.graphic.Point;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.level.Level;
import minicraft.screen.entry.BlankEntry;
import minicraft.screen.entry.LinkEntry;
import minicraft.screen.entry.SelectEntry;
import minicraft.screen.tutorial.TutorialDisplay;
import minicraft.util.BookData;
import minicraft.util.TimeData;

public class TitleDisplay extends Display {

	private int rand;
	private int count = 0; // this and reverse are for the logo; they produce the fade-in/out effect.
	private boolean reverse = false;

	private int time = 0;
	private int tickTime = 0;

	private boolean shouldRender = false;

	private static List<String> splashes = new ArrayList<>();

	static {
		try (InputStream stream = Game.class.getResourceAsStream("/resources/texts/splashes.json")) {
			if (stream != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

				String splashesJson = reader.lines().collect(Collectors.joining("\n"));
				JSONObject json = new JSONObject(splashesJson);

				JSONArray splashes = json.getJSONArray("splashes");
				List<String> list = new ArrayList<>();
				for (Object obj : splashes) {
					String s = (String) obj;
					list.add(s);
				}
				
				TitleDisplay.splashes = list;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public TitleDisplay() {
		super(false, false, new Menu.Builder(true, 1, RelPos.CENTER,
			new SelectEntry("Play", () -> {
				// if there are no worlds, it redirects to WorldGenDisplay()
				if (!WorldSelectDisplay.getWorldNames().isEmpty()) {
					Game.setDisplay(new Display(true, new Menu.Builder(false, 2, RelPos.CENTER,
							new SelectEntry("Load World", () -> Game.setDisplay(new WorldSelectDisplay())),
							new SelectEntry("New World", () -> Game.setDisplay(new WorldGenDisplay()))
						)
						.createMenu()
					));
				} else {
					Game.setDisplay(new WorldGenDisplay());
				}
			}),

			new SelectEntry("Options", () -> Game.setDisplay(new OptionsDisplay())),
			new SelectEntry("Credits", () -> Game.setDisplay(new EndGameCreditsDisplay())),
			new SelectEntry("Help", () -> Game.setDisplay(new Display(true, new Menu.Builder(true, 2, RelPos.CENTER,
					new BlankEntry(),
					new LinkEntry(Color.CYAN, "Minicraft discord", "https://discord.me/minicraft"),
					new BlankEntry(),
					new SelectEntry("Instructions", () -> Game.setDisplay(new BookDisplay(BookData.instructions))),
					new SelectEntry("Story guide", () -> Game.setDisplay(new BookDisplay(BookData.storylineGuide))),
					new SelectEntry("Tutorial", () -> Game.setDisplay(new TutorialDisplay())),
					new SelectEntry("About", () -> Game.setDisplay(new BookDisplay(BookData.about))),
					new BlankEntry()
				)
				.setTitle("Help")
				.createMenu()
			))),
			new SelectEntry("Exit", Game::quit)
		)
		.setPositioning(new Point(Screen.w / 2, Screen.h * 3 / 5), RelPos.CENTER)
		.setShouldRender(false)
		.createMenu()
		);
	}

	@Override
	public void init(Display parent) {
		super.init(null); // The TitleScreen never has a parent.
		Renderer.readyToRenderGameplay = false;

		if (TimeData.month() != Month.OCTOBER) {
			switch (random.nextInt(4)) {
				case 0: Sound.Theme_Cave.playOnDisplay(); break; 
				case 1: Sound.Theme_Surface.playOnDisplay(); break;
				case 2: Sound.Theme_Fall.playOnDisplay(); break;
				case 3: Sound.Theme_Peaceful.playOnDisplay(); break;
				default: Sound.Theme_Fall.playOnDisplay(); break;
			}
		}

		/*if (TimeData.month() == Month.DECEMBER) {
			if (TimeData.day() == 19) rand = 1;
			if (TimeData.day() == 25) rand = 2;
		} else {
			rand = random.nextInt(splashes.size() - 3) + 3;
		}

		if (TimeData.month() == Month.FEBRUARY) {
			if (TimeData.day() == 14) rand = 0;
			if (TimeData.day() == 15) rand = 0;
			if (TimeData.day() == 16) rand = 0;
		} else {
			rand = random.nextInt(splashes.size() - 3) + 3;
		}

		if (TimeData.month() == Month.JULY) {
			if (TimeData.day() == 6) rand = 3;
		} else {
			rand = random.nextInt(splashes.size() - 3) + 3;
		}

		if (TimeData.month() == Month.SEPTEMBER) {
			if (TimeData.day() == 18) rand = 4;
		} else {
			rand = random.nextInt(splashes.size() - 3) + 3;
		}

		if (TimeData.month() == Month.OCTOBER) {
			if (TimeData.day() == 8) Sound.Theme_Cavern.playOnDisplay();
			if (TimeData.day() == 16) Sound.Theme_Cavern_drip.playOnDisplay();
		}

		if (TimeData.month() == Month.AUGUST) {
			if (TimeData.day() == 29) rand = 5;
			if (TimeData.day() == 10) rand = 6;
		} else {
			rand = random.nextInt(splashes.size() - 3) + 3;
		}*/

		World.levels = new Level[World.levels.length];

		//  TODO: remove multiplayer references
		if (Game.player == null) {
			// Was online, need to reset player
			World.resetGame(false);
		}
	}

	@Override
	public void tick(InputHandler input) {
		if (input.getKey("r").clicked) rand = random.nextInt(splashes.size() - 3) + 3;
        if (input.getKey("shift-c").clicked) Game.setDisplay(new CharsTestDisplay());
      
		super.tick(input);

		if (shouldRender) menus[0].shouldRender = true;
		if (time > 72) shouldRender = true;
		if (tickTime /1 %2 == 0) time++;

		tickTime++;
	}

	@Override
	public void render(Screen screen) {
	    screen.clear(0);

	    if (shouldRender) {
	        int hh = 39; // Height of squares (on the spritesheet)
	        int ww = 416; // Width of squares (on the spritesheet)
	        int xxo = (Screen.w - ww * 8) / 2; // X location of the title
	        int yyo = 0; // Y location of the title

	        for (int y = 0; y < hh; y++) {
	            for (int x = 0; x < ww; x++) {
	                screen.render(xxo + x * 8, yyo + y * 8, new Sprite.Px(x - 8, y, 0, 5));
	            }
	        }
	    }

	    if (shouldRender) {
	        super.render(screen);
	        menus[0].render(screen);
	    }

	    if (shouldRender) {
	        int h = 6; // Height of squares (on the spritesheet)
	        int w = 26; // Width of squares (on the spritesheet)
	        int xo = (Screen.w - w * 8) / 2; // X location of the title
	        int yo = 55; // Y location of the title

	        for (int y = 0; y < h; y++) {
	            for (int x = 0; x < w; x++) {
	                screen.render(xo + x * 8, yo + y * 8, x + (y + 7) * 32, 0, 3);
	            }
	        }
	    }

	    if (shouldRender) {
	    	
	    	boolean isblue = splashes.get(rand).contains("blue");
			boolean isGreen = splashes.get(rand).contains("Green");
			boolean isRed = splashes.get(rand).contains("Red");
			boolean isOrange = splashes.get(rand).contains("Orange");
			boolean isYellow = splashes.get(rand).contains("Yellow") || splashes.get(rand).contains("Coffee edition") || splashes.get(rand).contains("The movie");

			if (reverse) {
				count--;
				if (count == 0) reverse = false;
			} else {
				count++;
				if (count == 25) reverse = true;
			}

			/// This isn't as complicated as it looks. It just gets a color based off of count, which oscilates between 0 and 25.
			int textColor = 5 - count / 5; // this number ends up being between 1 and 5, inclusive.
			int splashColor =
				isblue ? Color.BLUE :
				isRed ? Color.RED :
				isGreen ? Color.GREEN :
				isOrange ? Color.ORANGE :
				isYellow ? Color.YELLOW :
			Color.get(1, textColor * 51, textColor * 51, textColor * 25);
	    	
	        Font.drawCentered(splashes.get(rand), screen, 101, splashColor & 2);
	        Font.drawCentered(splashes.get(rand), screen, 100, splashColor);
	        
	        Font.draw("Mod by TheBigEye", screen, 4, Screen.h - 9, Color.get(-1, 240, 240, 240) % 2);
	        Font.draw("Mod by TheBigEye", screen, 4, Screen.h - 10, Color.get(-1, 240, 240, 240));
	        
	        Font.draw("Version " + Game.BUILD, screen, Screen.w - (10 * 8) - 2, Screen.h - 9, Color.get(-1, 240, 240, 240) % 2);
	        Font.draw("Version " + Game.BUILD, screen, Screen.w - (10 * 8) - 2, Screen.h - 10, Color.get(-1, 240, 240, 240));
	    }

	    int transitionStart = Math.max(-140, -(time * 2));
	    for (int x = 0; x < 200; x++) {
	        for (int y = 0; y < 150; y++) {
	            int dd = (y + x % 2 * 2 + x / 2) - time * 2;
	            if (dd < 0 && dd > transitionStart) {
	                screen.render(x * 8, Screen.h - y * 8 - 8, 12 + 24 * 32, 0, 3);
	            }
	        }
	    }
	}
}
