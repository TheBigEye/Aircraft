package minicraft.screen;

import minicraft.core.Game;
import minicraft.core.Renderer;
import minicraft.core.World;
import minicraft.core.io.CrashHandler;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Sound;
import minicraft.graphic.*;
import minicraft.level.Level;
import minicraft.screen.entry.BlankEntry;
import minicraft.screen.entry.LinkEntry;
import minicraft.screen.entry.SelectEntry;
import minicraft.screen.tutorial.TutorialDisplay;
import minicraft.util.BookData;
import minicraft.util.TimeData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TitleDisplay extends Display {

	private int rand;
	private int count = 0; // this and reverse are for the logo; they produce the fade-in/out effect.
	private boolean reverse = false;

	private int time = 0;
	private int tickTime = 0;

	private boolean shouldRender = false;

	private static List<String> splashes = new ArrayList<>();

	private static final String[] musicThemes = {
		"musicTheme5", "musicTheme2", "musicTheme1", "musicTheme4", "musicTheme8", "musicTheme3"
	};

	static {
		try (InputStream stream = Game.class.getResourceAsStream("/resources/splashes.json")) {
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
			new SelectEntry("Help", () -> Game.setDisplay(new Display(true, new Menu.Builder(true, 2, RelPos.CENTER,
					new BlankEntry(),
					new LinkEntry(Color.CYAN, "Minicraft discord", "https://discord.me/minicraft"),
					new BlankEntry(),
					new SelectEntry("Instructions", () -> Game.setDisplay(new BookDisplay(BookData.instructions))),
					//new SelectEntry("Story guide", () -> Game.setDisplay(new BookDisplay(BookData.storylineGuide))),
					new SelectEntry("Tutorial", () -> Game.setDisplay(new TutorialDisplay())),
					new SelectEntry("Credits", () -> Game.setDisplay(new CreditsDisplay())),
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

	    int day = TimeData.day();
	    Month month = TimeData.month();

	    if (TimeData.month() != Month.OCTOBER) {
	        Sound.play(musicThemes[random.nextInt(musicThemes.length)]);
	    } else {
	        Sound.play("musicTheme6");
	    }

	    if (month == Month.DECEMBER && (day == 19 || day == 25)) {
	        rand = day == 19 ? 1 : 2;
	    } else if (month == Month.FEBRUARY && (day >= 14 && day <= 16)) {
	        rand = 0;
	    } else if (month == Month.JULY && day == 6) {
	        rand = 3;
	    } else if (month == Month.SEPTEMBER && day == 18) {
	        rand = 4;
	    } else if (month == Month.AUGUST && (day == 29 || day == 10)) {
	        rand = day == 29 ? 5 : 6;
	    } else {
	        rand = random.nextInt(splashes.size() - 3) + 3;
	    }

	    World.levels = new Level[World.levels.length];

	    if (Game.player == null) {
	        // Stops the level background music if playing
	        if (World.currentMusicTheme != null) {
	            Sound.stop(World.currentMusicTheme);
	        }
	        Sound.stop("heavenWind");
	        // Was online, need to reset player
	        World.resetGame(false);
	    }
	}


	@Override
	public void tick(InputHandler input) {
		if (input.getKey("r").clicked) rand = random.nextInt(splashes.size() - 3) + 3;
        if (input.getKey("shift-c").clicked) Game.setDisplay(new CharsTestDisplay());
        if (input.getKey("shift-x").clicked) CrashHandler.crashMePlease();

		super.tick(input);

		if (shouldRender) menus[0].shouldRender = true;
		if (time > 72) shouldRender = true;
		if (tickTime % 2 == 0) time++;

		if (tickTime % 9000 == 8999) {
			Sound.play(musicThemes[random.nextInt(musicThemes.length)]);
		}

		tickTime++;
	}

	@Override
	public void render(Screen screen) {
	    screen.clear(0);

	    if (shouldRender) {

	    	/// Render the background image
	        int hh = 39; // Height of squares (on the spritesheet)
	        int ww = 416; // Width of squares (on the spritesheet)
	        int xxo = (Screen.w - (ww << 3)) / 2;
	        int yyo = 0;

	        for (int y = 0; y < hh; y++) {
	            for (int x = 0; x < ww; x++) {
	                screen.render(xxo + (x << 3), yyo + (y << 3), new Sprite.Px(x - 8, y, 0, 5));
	            }
	        }

	        super.render(screen);
	        menus[0].render(screen);

	        /// Render the title sprite
	        int h = 6; // Height of squares (on the spritesheet)
	        int w = 26; // Width of squares (on the spritesheet)
	        int xo = (Screen.w - (w << 3)) / 2; // X location of the title
	        int yo = 75; // Y location of the title

	        for (int y = 0; y < h; y++) {
	            for (int x = 0; x < w; x++) {
	                screen.render(xo + (x << 3), yo + (y << 3), x + ((y + 7) << 5), 0, 3);
	            }
	        }

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

	        Font.drawCentered(splashes.get(rand), screen, 121, splashColor & 2);
	        Font.drawCentered(splashes.get(rand), screen, 120, splashColor);

	        Font.draw("Version " + Game.BUILD, screen, 4, Screen.h - 9, Color.get(-1, 240, 240, 240) % 2);
	        Font.draw("Version " + Game.BUILD, screen, 4, Screen.h - 10, Color.get(-1, 240, 240, 240));

	        Font.draw("Mod by TheBigEye", screen, Screen.w - (15 * 8) - 2, Screen.h - 9, Color.get(-1, 240, 240, 240) % 2);
	        Font.draw("Mod by TheBigEye", screen, Screen.w - (15 * 8) - 2, Screen.h - 10, Color.get(-1, 240, 240, 240));
	    }

	    // Render the transition
	    int transitionStart = Math.max(-140, -(time * 2));
	    for (int x = 0; x < 200; x++) {
	        for (int y = 0; y < 150; y++) {
	            int dd = (y + x % 2 * 2 + x / 2) - time * 2;
	            if (dd < 0 && dd > transitionStart) {
	                screen.render(x << 3, Screen.h - y * 8 - 8, 12 + (24 << 5), 0, 3);
	            }
	        }
	    }
	}
}
