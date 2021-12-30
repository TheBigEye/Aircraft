package minicraft.screen;

import java.util.Random;

import javax.swing.Timer;

import minicraft.core.Game;
import minicraft.core.World;
import minicraft.core.io.Localization;
import minicraft.core.io.Sound;
import minicraft.gfx.Color;
import minicraft.gfx.Ellipsis;
import minicraft.gfx.Ellipsis.DotUpdater.TimeUpdater;
import minicraft.gfx.Ellipsis.SmoothEllipsis;
import minicraft.gfx.Font;
import minicraft.gfx.FontStyle;
import minicraft.gfx.Screen;
import minicraft.saveload.Save;

public class LoadingDisplay extends Display {

	private static float percentage = 0;
	private static String progressType = "";

	private static String Build = "";

	String[] BuildString = { "Generating", "Calculating", "Pre Calculating", "Building", "Melting", "Eroding", "Planting", "Populating", "Molding", "Raising" };

	private static Random random = new Random();

	private final Timer t;
	private String msg = "";
	private final Ellipsis ellipsis = new SmoothEllipsis(new TimeUpdater());

	public LoadingDisplay() {
		super(true, false);
		t = new Timer(500, e -> {
			World.initWorld();
			msg = Localization.getLocalized("Rendering");
			Game.setMenu(null);
		});
		t.setRepeats(false);
	}

	@Override
	public void init(Display parent) {
		super.init(parent);
		percentage = 0;
		progressType = "World";

		if (WorldSelectDisplay.loadedWorld()) {
			msg = Localization.getLocalized("Loading");
		} else {
			LoadingDisplay.Build = Localization.getLocalized(BuildString[random.nextInt(BuildString.length)]);
		}

		msg = Build;
		t.start();

	}

	@Override
	public void onExit() {
		percentage = 0;
		if (!WorldSelectDisplay.loadedWorld()) {
			progressType = "World";
			msg = Localization.getLocalized("Rendering");
			new Save(WorldSelectDisplay.getWorldName());
			Game.notifications.clear();
		}

		Sound.Mob_player_changelevel.play();

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
		Font.drawParagraph(screen, new FontStyle(Color.YELLOW), 0, Localization.getLocalized(msg) + ellipsis.updateAndGet(), percent + "%");

		if (!WorldSelectDisplay.loadedWorld()) {
			Font.drawParagraph(screen, new FontStyle(Color.YELLOW), 0, Localization.getLocalized(msg) + ellipsis.updateAndGet(), percent + "%");
			Font.drawCentered(Localization.getLocalized("May take a while, be patient"), screen, Screen.h - 12, Color.get(1, 51));
		}

		Font.drawCentered(((progressType.length() > 0) ? (" " + Localization.getLocalized(progressType)) : ""), screen, Screen.h - 30, Color.get(1, 51));

		Sound.Intro.stop();
		Sound.Intro2.stop();

	}
}
