package minicraft.screen;

import javax.swing.Timer;

import minicraft.core.Game;
import minicraft.core.World;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Localization;
import minicraft.core.io.Sound;
import minicraft.graphic.Color;
import minicraft.graphic.Ellipsis;
import minicraft.graphic.Font;
import minicraft.graphic.FontStyle;
import minicraft.graphic.Screen;
import minicraft.saveload.Save;

public class LoadingDisplay extends Display {

	private static float percentage = 0;
	private static String progressType;
	private static String message;

	private final Timer timer;

	private Ellipsis ellipsis = (Ellipsis) new Ellipsis.SmoothEllipsis((Ellipsis.DotUpdater) new Ellipsis.DotUpdater.TimeUpdater());
	
	public LoadingDisplay() {
		super(true, false);
		timer = new Timer(500, event -> new Thread(() -> {
			try {
				World.initWorld();
				Game.setDisplay(null);
			} catch (RuntimeException exception) {
				exception.printStackTrace();
			}
		}, "World Loader Thread").start());
		timer.setRepeats(false);
	}

	public void tick(InputHandler input) {
		super.tick(input);
	}

	@Override
	public void init(Display parent) {
		super.init(parent);
		percentage = 0;
		progressType = "World";
		if (WorldSelectDisplay.hasLoadedWorld()) {
			message = Localization.getLocalized("Loading");
		} else {
			message = Localization.getLocalized("Starting");
		}
		timer.start();
	}

	@Override
	public void onExit() {
		percentage = 0;
		if (!WorldSelectDisplay.hasLoadedWorld()) {
			progressType = "World";
			message = Localization.getLocalized("Rendering");
			new Save(WorldSelectDisplay.getWorldName());
			Game.notifications.clear();
		}
	}

	public static void setPercentage(float percent) {
		percentage = percent;
	}

	public static float getPercentage() {
		return percentage;
	}

	public static void setProgressType(String progressType) {
		LoadingDisplay.progressType = progressType;
	}
	
	public static void setMessage(String message) {
		LoadingDisplay.message = message;
	}

	public static void progress(float amt) {
		percentage = Math.min(100, percentage + amt);
	}

	@Override
	public void render(Screen screen) {
		super.render(screen);
		int percent = Math.round(percentage);

		if (!WorldSelectDisplay.hasLoadedWorld()) {
			Font.drawParagraph(screen, new FontStyle(Color.YELLOW), 0, message + ellipsis.updateAndGet(), percent + "%");
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
