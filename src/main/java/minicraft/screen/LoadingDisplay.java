package minicraft.screen;

import javax.swing.Timer;

import minicraft.core.Game;
import minicraft.core.World;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Localization;
import minicraft.core.io.Sound;
import minicraft.gfx.Color;
import minicraft.gfx.Ellipsis;
import minicraft.gfx.Font;
import minicraft.gfx.FontStyle;
import minicraft.gfx.Screen;
import minicraft.saveload.Save;

public class LoadingDisplay extends Display {

	private int step;
	private static float percentage = 0;
	private static String progressType;

	private static final String[] BuildString = {
		"Generating", "Separating", "Planting", "Eroding", "Digging",
		"Raising", "Leveling", "Flattening", "Molding", "Building"
	};

	private final Timer timer;
	private String msg;
	
	private Ellipsis ellipsis = (Ellipsis) new Ellipsis.SmoothEllipsis((Ellipsis.DotUpdater) new Ellipsis.DotUpdater.TimeUpdater());
	
	public LoadingDisplay() {
		super(true, false);
		timer = new Timer(500, e -> {
			World.initWorld();
			msg = Localization.getLocalized("Rendering");
			Game.setDisplay(null);
		});
		timer.setRepeats(false);
	}

	public void tick(InputHandler input) {
		super.tick(input);
		step++;
	}

	@Override
	public void init(Display parent) {
		super.init(parent);
		percentage = 0;
		progressType = "World";
		if (WorldSelectDisplay.hasLoadedWorld()) {
			msg = Localization.getLocalized("Loading");
		} else {
			msg = Localization.getLocalized(BuildString[random.nextInt(BuildString.length)]);
		}
		timer.start();
	}

	@Override
	public void onExit() {
		percentage = 0;
		if (!WorldSelectDisplay.hasLoadedWorld()) {
			progressType = "World";
			msg = Localization.getLocalized("Rendering");
			new Save(WorldSelectDisplay.getWorldName());
			Game.notifications.clear();
		}

		Sound.playerChangeLevel.playOnGui();
	}

	public static void setPercentage(float percent) {
		percentage = percent;
	}

	public static float getPercentage() {
		return percentage;
	}

	public static void setMessage(String progressType) {
		LoadingDisplay.progressType = progressType;
	}

	public static void progress(float amt) {
		percentage = Math.min(100, percentage + amt);
	}

	@Override
	public void render(Screen screen) {
		super.render(screen);
		int percent = Math.round(percentage);

		int LoadingIndex = (step / 4) % 24;

		if (!WorldSelectDisplay.hasLoadedWorld()) {

			if (LoadingIndex == 0) {
				msg = BuildString[random.nextInt(BuildString.length)];
			}

			Font.drawParagraph(screen, new FontStyle(Color.YELLOW), 0, Localization.getLocalized(msg) + ellipsis.updateAndGet(), percent + "%");
			Font.drawCentered(Localization.getLocalized("May take a while, be patient"), screen, Screen.h - 12, Color.get(1, 51));

		} else {
			Font.drawParagraph(screen, new FontStyle(Color.YELLOW), 0, Localization.getLocalized("Loading") + ellipsis.updateAndGet(), percent + "%");
		}

		Font.drawCentered(((progressType.length() > 0) ? (" " + Localization.getLocalized(progressType)) : ""), screen, Screen.h - 30, Color.get(1, 51));

		Sound.Theme_Cave.stop();
		Sound.Theme_Cavern.stop();
		Sound.Theme_Fall.stop();
		Sound.Theme_Peaceful.stop();
		Sound.Theme_Surface.stop();

	}
}
