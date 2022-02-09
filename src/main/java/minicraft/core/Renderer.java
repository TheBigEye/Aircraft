package minicraft.core;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import javax.imageio.ImageIO;

import org.tinylog.Logger;

import minicraft.entity.furniture.Bed;
import minicraft.entity.mob.Player;
import minicraft.entity.mob.boss.AirWizard;
import minicraft.entity.mob.boss.AirWizardPhase2;
import minicraft.entity.mob.boss.AirWizardPhase3;
import minicraft.gfx.Color;
import minicraft.gfx.Ellipsis;
import minicraft.gfx.Ellipsis.DotUpdater.TickUpdater;
import minicraft.gfx.Ellipsis.SmoothEllipsis;
import minicraft.gfx.Font;
import minicraft.gfx.FontStyle;
import minicraft.gfx.Screen;
import minicraft.gfx.SpriteSheet;
import minicraft.item.Items;
import minicraft.item.PotionType;
import minicraft.item.ToolItem;
import minicraft.item.ToolType;
import minicraft.level.Level;
import minicraft.screen.InfoDisplay;
import minicraft.screen.LoadingDisplay;
import minicraft.screen.RelPos;
import minicraft.util.Info;
import minicraft.util.MyUtils;

public class Renderer extends Game {
	private Renderer() {}

	public static final int HEIGHT = 288; // This is the height of the game * scale
	public static final int WIDTH = 432; // This is the width of the game * scale

	static float SCALE = 2; // Scales the window

	private static String levelName; // Used to store the names of the levels in the debug GUI

	public static Screen screen; // Creates the main screen

	static Canvas canvas = new Canvas();

	private static BufferedImage image; // Creates an image to be displayed on the screen.
	private static int[] pixels; // The array of pixels that will be displayed on the screen.
	public static Screen lightScreen; // Creates a front screen to render the darkness in caves (Fog of war).
	public static boolean readyToRenderGameplay = false;
	public static boolean showinfo = false;
	
	
	private static Ellipsis ellipsis = new SmoothEllipsis(new TickUpdater());

	public static SpriteSheet[] loadDefaultSpriteSheets() {
		SpriteSheet itemSheet, tileSheet, entitySheet, guiSheet, iconsSheet, background;
		try {
			// These set the sprites to be used.
			itemSheet = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/resources/textures/default/items.png"))));
			tileSheet = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/resources/textures/default/tiles.png"))));
			entitySheet = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/resources/textures/default/entities.png"))));
			guiSheet = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/resources/textures/default/gui.png"))));
			iconsSheet = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/resources/textures/default/icons.png"))));
			background = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/resources/textures/default/background.png"))));

		} catch (NullPointerException e) {
			// If a provided InputStream has no name. (in practice meaning it cannot be found.)
			e.printStackTrace();
			Logger.error("A sprite sheet was not found.");
			System.exit(-1);
			return null;

		} catch (IOException | IllegalArgumentException e) {
			// If there is an error reading the file.
			e.printStackTrace();
			Logger.error("Could not load a sprite sheet.");
			System.exit(-1);
			return null;
		}

		return new SpriteSheet[] { itemSheet, tileSheet, entitySheet, guiSheet, iconsSheet, background };
	}

	public static SpriteSheet[] loadLegacySpriteSheets() {
		SpriteSheet itemSheet, tileSheet, entitySheet, guiSheet, iconsSheet, background;
		try {
			// These set the sprites to be used.
			itemSheet = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/resources/textures/legacy/items.png"))));
			tileSheet = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/resources/textures/legacy/tiles.png"))));
			entitySheet = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/resources/textures/legacy/entities.png"))));
			guiSheet = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/resources/textures/legacy/gui.png"))));
			iconsSheet = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/resources/textures/legacy/icons.png"))));
			background = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/resources/textures/legacy/background.png"))));

		} catch (NullPointerException e) {
			// If a provided InputStream has no name. (in practice meaning it cannot be found.)
			e.printStackTrace();
			Logger.error("A sprite sheet was not found.");
			System.exit(-1);
			return null;

		} catch (IOException | IllegalArgumentException e) {
			// If there is an error reading the file.
			e.printStackTrace();
			Logger.error("Could not load a sprite sheet.");
			System.exit(-1);
			return null;
		}

		return new SpriteSheet[] { itemSheet, tileSheet, entitySheet, guiSheet, iconsSheet, background };
	}

	static void initScreen() {
		if (!HAS_GUI) {
			return;
		}

		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

		Logger.debug("Loading spriteheets...");
		
		SpriteSheet[] sheets = loadDefaultSpriteSheets();
		screen = new Screen(sheets[0], sheets[1], sheets[2], sheets[3], sheets[4], sheets[5]);
		lightScreen = new Screen(sheets[0], sheets[1], sheets[2], sheets[3], sheets[4], sheets[5]);

		screen.pixels = pixels;

		if (HAS_GUI) {
			canvas.createBufferStrategy(3);
			canvas.requestFocus();
		}
	}
	

	/** Renders the current screen. Called in game loop, a bit after tick(). */
	public static void render() {
		if (!HAS_GUI || screen == null) {
			return; // No point in this if there's no gui... :P
		}

		if (readyToRenderGameplay) {
			if (isValidServer()) {
				screen.clear(0);
				Font.drawCentered("Awaiting client connections" + ellipsis.updateAndGet(), screen, 10, Color.get(-1, 444));
				Font.drawCentered("So far:", screen, 20, Color.get(-1, 444));
				int i = 0;
				for (String playerString: server.getClientInfo()) {
					Font.drawCentered(playerString, screen, 30 + i * 10, Color.get(-1, 134));
					i++;
				}
				renderDebugInfo();
				
			} else {
				renderLevel();
				renderGui();
				
			}
		}

		if (menu != null) { // Renders menu, if present.
			menu.render(screen);
		}

		if (!canvas.hasFocus() && !ISONLINE) {
			renderFocusNagger(); // Calls the renderFocusNagger() method, which creates the "Click to Focus" message.
		}

		BufferStrategy bs = canvas.getBufferStrategy(); // Creates a buffer strategy to determine how the graphics should be buffered.
		Graphics g = bs.getDrawGraphics(); // Gets the graphics in which java draws the picture
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight()); // draws a rect to fill the whole window (to cover last?)

		// Scale the pixels.
		int ww = getWindowSize().width;
		int hh = getWindowSize().height;

		// Gets the image offset.
		int xo = (canvas.getWidth() - ww) / 2 + canvas.getParent().getInsets().left;
		int yo = (canvas.getHeight() - hh) / 2 + canvas.getParent().getInsets().top;
		g.drawImage(image, xo, yo, ww, hh, null); // Draws the image on the window
		g.dispose(); // Releases any system items that are using this method. (so we don't have crappy framerates)

		bs.show(); // Makes the picture visible. (probably)
	}

	private static void renderLevel() {
		Level level = levels[currentLevel];
		
		if (level == null) {
			return;
		}

		int xScroll = player.x - Screen.w / 2; // Scrolls the screen in the x axis.
		int yScroll = player.y - (Screen.h - 8) / 2; // Scrolls the screen in the y axis.

		// Top scrolling if the screen is at the ...
		if (xScroll < 0) xScroll = 0; // ...Left border.
		if (yScroll < 0) yScroll = 0; // ...Top border.
		if (xScroll > level.w * 16 - Screen.w) xScroll = level.w * 16 - Screen.w; // ...right border.
		if (yScroll > level.h * 16 - Screen.h) yScroll = level.h * 16 - Screen.h; // ...bottom border.
		
		if (currentLevel > 3) { // If the current level is higher than 3 (which only the sky level (and dungeon) is)
			for (int y = 0; y < 56; y++) {
				for (int x = 0; x < 96; x++) {
					// Creates the background for the sky (and dungeon) level:
					screen.render(x * 8 - ((xScroll / 4) & 7), y * 8 - ((yScroll / 4) & 7), 2 + 25 * 32, 0, 1);
				}
			}
		}

		level.renderBackground(screen, xScroll, yScroll); // Renders current level background
		level.renderSprites(screen, xScroll, yScroll); // Renders level sprites on screen

		// this creates the darkness in the caves
		if ((currentLevel != 3 || Updater.tickCount < Updater.dayLength / 4 || Updater.tickCount > Updater.dayLength / 2)) {

			// This doesn't mean that the pixel will be black; it means that the pixel will
			// be DARK, by default; lightScreen is about light vs. dark, not necessarily a
			// color. The light level it has is compared with the minimum light values in
			// dither to decide whether to leave the cell alone, or mark it as "dark", which
			// will do different things depending on the game level and time of day.
			lightScreen.clear(0); 

			// Brightens all
			int brightnessMultiplier = player.potioneffects.containsKey(PotionType.Light) ? 12 : 8; 

			// Light sources by a factor of 1.5 when the player has the Light potion effect. (8 above is normal)
			level.renderLight(lightScreen, xScroll, yScroll, brightnessMultiplier); // Finds (and renders) all the light from objects (like the player, lanterns, and lava).
			screen.overlay(lightScreen, currentLevel, xScroll, yScroll); // Overlays the light screen over the main screen.
		}

		if (player != null && player.potioneffects.containsKey(PotionType.Blindness) && currentLevel == 3 || player != null && player.potioneffects.containsKey(PotionType.Blindness) && currentLevel == 4) {
			screen.Blind(lightScreen, currentLevel, xScroll, yScroll);
		}
	}

	/**
	 * Renders the main game GUI (hearts, Stamina bolts, name of the current item,
	 * etc.)
	 */
	@SuppressWarnings("unchecked")
	private static void renderGui() {

		// ARROWS COUNT STATUS
		if (player.activeItem instanceof ToolItem) {
			if (((ToolItem) player.activeItem).type == ToolType.Bow) {
				int ac = player.getInventory().count(Items.arrowItem);

				int xx = (Screen.w) / 2 - 32 - player.activeItem.arrAdjusted; // the width of the box
				int yy = (Screen.h - 8) - 13; // the height of the box
				int w = 3; // length of message in characters.
				int h = 1;

				int x = 170;
				int y = 25;

				// Renders the four corners of the box
				screen.render(xx - 8, yy - 8, 0 + 21 * 32, 0, 3);
				screen.render(xx + w * 8, yy - 8, 0 + 21 * 32, 1, 3);
				screen.render(xx - 8, yy + 8, 0 + 21 * 32, 2, 3);
				screen.render(xx + w * 8, yy + 8, 0 + 21 * 32, 3, 3);

				// Renders each part of the box...
				for (x = 0; x < w; x++) {
					screen.render(xx + x * 8, yy - 8, 1 + 21 * 32, 0, 3); // ...top part
					screen.render(xx + x * 8, yy + 8, 1 + 21 * 32, 2, 3); // ...bottom part
				}
				for (y = 0; y < h; y++) {
					screen.render(xx - 8, yy + y * 8, 2 + 21 * 32, 0, 3); // ...left part
					screen.render(xx + w * 8, yy + y * 8, 2 + 21 * 32, 1, 3); // ...right part
				}

				// The middle
				for (x = 0; x < w; x++) {
					screen.render(xx + x * 8, yy, 3 + 21 * 32, 0, 3);
				}

				if (isMode("creative") || ac >= 10000) {
					Font.drawTransparentBackground(" x" + "Ω", screen, 184 - player.activeItem.arrAdjusted, Screen.h - 24);
				} else {
					Font.drawTransparentBackground(" x" + ac, screen, 184 - player.activeItem.arrAdjusted, Screen.h - 24);
				}

				// Displays the arrow icon
				screen.render(20 * 8 + 20 - player.activeItem.arrAdjusted, Screen.h - 24, 5 + 3 * 32, 0, 3);
			}
		}

		// TOOL DURABILITY STATUS
		if (player.activeItem instanceof ToolItem) {
			// Draws the text
			ToolItem tool = (ToolItem) player.activeItem;
			int dura = tool.dur * 100 / (tool.type.durability * (tool.level + 1));
			int green = (int)(dura * 2.55f);

			int xx = (Screen.w) / 2 + 8 + player.activeItem.durAdjusted; // The width of the box
			int yy = (Screen.h - 8) - 13; // The height of the box
			int w = 3; // Length of message in characters.
			int h = 1;

			int x = 250;
			int y = 25;

			// Renders the four corners of the box
			screen.render(xx - 8, yy - 8, 0 + 21 * 32, 0, 3);
			screen.render(xx + w * 8, yy - 8, 0 + 21 * 32, 1, 3);
			screen.render(xx - 8, yy + 8, 0 + 21 * 32, 2, 3);
			screen.render(xx + w * 8, yy + 8, 0 + 21 * 32, 3, 3);

			// Renders each part of the box...
			for (x = 0; x < w; x++) {
				screen.render(xx + x * 8, yy - 8, 1 + 21 * 32, 0, 3); // ...top part
				screen.render(xx + x * 8, yy + 8, 1 + 21 * 32, 2, 3); // ...bottom part
			}
			for (y = 0; y < h; y++) {
				screen.render(xx - 8, yy + y * 8, 2 + 21 * 32, 0, 3); // ...left part
				screen.render(xx + w * 8, yy + y * 8, 2 + 21 * 32, 1, 3); // ...right part
			}

			// The middle
			for (x = 0; x < w; x++) {
				screen.render(xx + x * 8, yy, 3 + 21 * 32, 0, 3);
			}

			Font.drawTransparentBackground(dura + "%", screen, 221 + player.activeItem.durAdjusted, Screen.h - 24, Color.get(1, 255 - green, green, 0));
		}

		// This draws the black square where the selected item would be if you were holding it
		if (!isMode("creative") || player.activeItem != null) {
			for (int x = 20; x < 36; x++) {
				screen.render(x * 8, Screen.h - 8, 31 + 30 * 32, 0, 3);
			}
		}

		// Shows active item sprite and name in bottom toolbar.
		if (player.activeItem != null) {
			player.activeItem.renderHUD(screen, 20 * 8, Screen.h - 8, Color.GRAY);
		}

		ArrayList <String> permStatus = new ArrayList <> ();

		if (Updater.saving) {
			permStatus.add("Saving... " + Math.round(LoadingDisplay.getPercentage()) + "%");
		}

		if (Bed.sleeping()) {
			permStatus.add("Sleeping...");
		}

		else if (!Game.isValidServer() && Bed.getPlayersAwake() > 0) {
			int numAwake = Bed.getPlayersAwake();

			if (Bed.inBed(Game.player)) {
				permStatus.add(MyUtils.plural(numAwake, "player") + " still awake");
				permStatus.add(" ");
				permStatus.add("Press " + input.getMapping("exit") + " to cancel");

			} else if (Game.isValidClient()) {
				// Draw it in a corner
				int total = Game.client.getPlayerCount();
				int sleepCount = total - numAwake;

				if (sleepCount > 0) {
					new FontStyle(Color.WHITE).setRelTextPos(RelPos.BOTTOM_LEFT).setAnchor(Screen.w, 0).draw(sleepCount + "/" + total + " players sleeping", screen);
				}
			}
		}

		if (permStatus.size() > 0) {
			FontStyle style = new FontStyle(Color.WHITE).setYPos(Screen.h / 2 - 25).setRelTextPos(RelPos.TOP).setShadowType(Color.DARK_GRAY, false);
			Font.drawParagraph(permStatus, screen, style, 1);
		}

		/// NOTIFICATIONS
		if (permStatus.size() == 0 && notifications.size() > 0) {
			Updater.notetick++;
			if (notifications.size() > 3) { // Only show 3 notifs max at one time; erase old notifs.
				notifications = notifications.subList(notifications.size() - 3, notifications.size());
			}

			if (Updater.notetick > 120) { // Display time per notification.
				notifications.remove(0);
				Updater.notetick = 0;
			}

			// draw each current notification, with shadow text effect.
			FontStyle style = new FontStyle(Color.WHITE).setShadowType(Color.DARK_GRAY, false).setYPos(Screen.h * 2 / 5).setRelTextPos(RelPos.TOP, false);
			Font.drawParagraph(notifications, screen, style, 0);
		}


		// AirWizard bossbar
		if (currentLevel == 4 && isMode("survival")) {
			if (!AirWizard.beaten) renderBossbar(AirWizard.length, "Phase I");
			else if (!AirWizardPhase2.beaten) renderBossbar(AirWizardPhase2.length, "Phase II");
			else if (!AirWizardPhase3.beaten) renderBossbar(AirWizardPhase3.length, "Phase III");
		}

		// SCORE MODE ONLY:
		if (isMode("score")) {
			int seconds = (int) Math.ceil(Updater.scoreTime / (double) Updater.normSpeed);
			int minutes = seconds / 60;
			int hours = minutes / 60;
			minutes %= 60;
			seconds %= 60;

			int timeCol;
			if (Updater.scoreTime >= 18000) {
				timeCol = Color.get(0, 555);
			}
			else if (Updater.scoreTime >= 3600) {
				timeCol = Color.get(330, 555);
			} else {
				timeCol = Color.get(400, 555);
			}

			Font.draw("Time left " + (hours > 0 ? hours + "h " : "") + minutes + "m " + seconds + "s", screen, Screen.w / 2 - 9 * 8, 2, timeCol);

			String scoreString = "Current score: " + player.getScore();
			Font.draw(scoreString, screen, Screen.w - Font.textWidth(scoreString) - 2, 3 + 8, Color.WHITE);

			if (player.getMultiplier() > 1) {
				int multColor = player.getMultiplier() < Player.MAX_MULTIPLIER ? Color.get(-1, 540) : Color.RED;
				String mult = "X" + player.getMultiplier();

				Font.draw(mult, screen, Screen.w - Font.textWidth(mult) - 2, 4 + 2 * 8, multColor);
			}
		}

		/// This renders the potions overlay
		if (player.showpotioneffects && player.potioneffects.size() > 0) {
			Map.Entry < PotionType, Integer > [] effects = player.potioneffects.entrySet().toArray(new Map.Entry[0]);

			PotionType pType;

			int pxx = 0; // the width of the box
			int pyy = 0; // the height of the box
			int pw = 0; // length of message in characters.
			int ph = 0;
			int px = (Screen.w - 8) / 3 + pxx;
			int py = (Screen.h - 8) / 36 - 130;

			for (int i = 0; i < effects.length; i++) {
				pType = effects[i].getKey();
				int pTime = effects[i].getValue() / Updater.normSpeed;

				pxx = (Screen.w - Font.textWidth(pType.name + pTime)) / 2 + px; // the width of the box
				pw = pType.name.length() + 2; // length of message in characters.
				pyy = (HEIGHT - 8) / 2 + py; // the height of the box
				ph = effects.length;

			}

			// Renders the four corners of the box
			screen.render(pxx - 8, pyy - 8, 0 + 21 * 32, 0, 3);
			screen.render(pxx + pw * 8, pyy - 8, 0 + 21 * 32, 1, 3);
			screen.render(pxx - 8, pyy + 8 * ph, 0 + 21 * 32, 2, 3);
			screen.render(pxx + pw * 8, pyy + 8 * ph, 0 + 21 * 32, 3, 3);

			// 21, sheet y :)

			// Renders each part of the box...
			for (int x = 0; x < pw; x++) {
				screen.render(pxx + x * 8, pyy - 8, 1 + 21 * 32, 0, 3); // ...top part
				screen.render(pxx + x * 8, pyy + 8 * ph, 1 + 21 * 32, 2, 3); // ...bottom part
			}
			for (int y = 0; y < ph; y++) {
				screen.render(pxx - 8, pyy + y * 8, 2 + 21 * 32, 0, 3); // ...left part
				screen.render(pxx + pw * 8, pyy + y * 8, 2 + 21 * 32, 1, 3); // ...right part
			}

			// The middle
			for (int x = 0; x < pw; x++) {
				for (int y = 0; y < ph; y++) {
					screen.render(pxx + x * 8, pyy + y * 8, 3 + 21 * 32, 0, 3);
				}
			}

			for (int i = 0; i < effects.length; i++) {
				pType = effects[i].getKey();
				int pTime = effects[i].getValue() / Updater.normSpeed;

				int minutes = pTime / 60;
				int seconds = pTime % 60;

				Font.drawTransparentBackground("(" + input.getMapping("potionEffects") + " to hide!)", screen, 300, 9);
				Font.drawTransparentBackground(pType + " (" + minutes + ":" + (seconds < 10? "0" + seconds:seconds) + ")", screen, 300, 17 + i * Font.textHeight(), pType.dispColor);
			}

		}

		// This is the status icons, like health hearts, stamina bolts, and hunger "burger".
		if (!isMode("creative")) {
			for (int i = 0; i < Player.maxStat; i++) {

				// Renders armor
				int armor = player.armor * Player.maxStat / Player.maxArmor;
				if (i <= armor && player.curArmor != null) {
					screen.render(i * 8, Screen.h - 24, (player.curArmor.level - 1) + 9 * 32, 0, 0);
				}

				// Renders your current red hearts, or black hearts for damaged health.
				if (i < player.health) {
					screen.render(i * 8, Screen.h - 16, 0 + 2 * 32, 0, 3);
				} else {
					screen.render(i * 8, Screen.h - 16, 0 + 3 * 32, 0, 3);
				}

				if (player.staminaRechargeDelay > 0) {
					// Creates the white/gray blinking effect when you run out of stamina.
					if (player.staminaRechargeDelay / 4 % 2 == 0) {
						screen.render(i * 8, Screen.h - 8, 1 + 4 * 32, 0, 3);
					} else {
						screen.render(i * 8, Screen.h - 8, 1 + 3 * 32, 0, 3);
					}
				} else {
					// Renders your current stamina, and uncharged gray stamina.
					if (i < player.stamina) {
						screen.render(i * 8, Screen.h - 8, 1 + 2 * 32, 0, 3);
					} else {
						screen.render(i * 8, Screen.h - 8, 1 + 3 * 32, 0, 3);
					}
				}

				// Renders hunger
				if (i < player.hunger) {
					screen.render(i * 8 + (Screen.w - 80), Screen.h - 16, 2 + 2 * 32, 0, 3);
				} else {
					screen.render(i * 8 + (Screen.w - 80), Screen.h - 16, 2 + 3 * 32, 0, 3);
				}
			}
		}

		renderDebugInfo();
	}
	
	public static void renderBossbar(int length, String title) {
		
		int x = Screen.w / 6 + 10;
		int y = Screen.h / 8 - 24;

		int w = length; // Bossbar size.
		int h = 1;


		// The middle
		for (int bx = 0; bx < 100 / 3; bx++) {
			for (int by = 0; by < h; by++) {
				screen.render(x + bx * 8, y + by * 8, 3 + 24 * 32, 0, 3);
			}
		}  

		for (int bx = 0; bx < w; bx++) {
			for (int by = 0; by < h; by++) {
				screen.render(x + bx * 8, y + by * 8, 3 + 25 * 32, 0, 3);
			}
		}

		Font.drawCentered(title, screen, y + 8, Color.WHITE);
	}

	private static LocalDateTime time = LocalDateTime.now();

	// Renders show debug info on the screen.
	private static void renderDebugInfo() { 

		int textcol;
		String separator = "                                       ";

		if (debug) textcol = Color.GREEN;
		else if (dev) textcol = Color.RED;
		else textcol = Color.WHITE;
		
		if (showinfo) {
			ArrayList <String> info = new ArrayList <> ();
			ArrayList <String> subinfo = new ArrayList <> ();

			info.add("Version: " + Game.BUILD + " (" + Game.VERSION + ")");               subinfo.add(separator + "Time:" + InfoDisplay.getTimeString());
			info.add("Engine: " + "Minicraft Plus Legacy");                               subinfo.add(separator + "Java:" + Info.Java_Version);
			info.add("" + time.toLocalDate());                                            subinfo.add(separator + "Java arch: x" + Info.Java_Arch);
			info.add(Initializer.fra + " fps");                                           subinfo.add(separator + "Max mem:" + Info.max_Memory);
			info.add("day tiks:" + Updater.tickCount + " (" + Updater.getTime() + ")");   subinfo.add(separator + "Total mem:" + Info.total_Memory);
			info.add((Updater.normSpeed * Updater.gamespeed) + " tps ");                  subinfo.add(separator + "Free mem: " + Info.free_Memory);
			if (!isValidServer()) {

				// player info
				info.add("walk spd:" + player.moveSpeed);
				info.add("X:" + (player.x / 16) + "." + (player.x % 16));
				info.add("Y:" + (player.y / 16) + "." + (player.y % 16));
				info.add("");

				// tile
				if (levels[currentLevel] != null)
				info.add("Tile:" + levels[currentLevel].getTile(player.x >> 4, player.y >> 4).name);
				info.add("Id:" + levels[currentLevel].getTile(player.x >> 4, player.y >> 4).id);
				info.add("Data:" + levels[currentLevel].getData(player.x >> 4, player.y >> 4));
				info.add("Depth:" + levels[currentLevel].depth);
				info.add("");

				// screen info
				info.add("Screen: " + java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight() + "x" + java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth());
				info.add("Current: " + getWindowSize().getHeight() + "x" + getWindowSize().getWidth());

				if (isMode("score")) {
					info.add("Score " + player.getScore());
				}
			}

			if (levels[currentLevel] != null) {
				info.add("Level: " + levelName);

				switch (levels[currentLevel].depth) {
					case 1: levelName = "Aether"; break;
					case 0: levelName = "Surface"; break;
					case -1: levelName = "Caves"; break;
					case -2: levelName = "Caverns"; break;
					case -3: levelName = "Core"; break;
					case -4: levelName = "Dungeon"; break;
					case -5: levelName = "Hell"; break;
					default: levelName = "Secret dimension"; break;
				}

				if (!isValidClient()) {
					info.add("Mob Cnt " + levels[currentLevel].mobCount + "/" + levels[currentLevel].maxMobCount);
				} else {
					info.add("Mob Load Cnt " + levels[currentLevel].mobCount);
				}
			}

			/// Displays number of chests left, if on dungeon level.
			if (levels[currentLevel] != null && (isValidServer() || currentLevel == 5 && !isValidClient())) {
				if (levels[5].chestCount > 0) {
					info.add("Chests: " + levels[5].chestCount);
				} else {
					info.add("Chests: Complete!");
				}
			}

			if (!isValidServer()) {
				info.add("Hunger stam: " + player.getDebugHunger());
				if (player.armor > 0) {
					info.add("armor: " + player.armor);
					info.add("dam buffer: " + player.armorDamageBuffer);
				}
			}

			if (levels[currentLevel] != null) {
				info.add("");
				info.add("Seed: " + levels[currentLevel].getSeed());
				info.add("Music factor: " + Level.randomMusic + "/16000");
			}

			FontStyle style = new FontStyle(textcol).setShadowType(Color.BLACK, true).setXPos(1);
			FontStyle substyle = new FontStyle(textcol).setShadowType(Color.BLACK, true).setXPos(1);
			
			if (Game.isValidServer()) {
				style.setYPos(Screen.h).setRelTextPos(RelPos.TOP_RIGHT, true);
				substyle.setYPos(Screen.h).setRelTextPos(RelPos.TOP_LEFT, true);
				
				for (int i = 1; i < info.size(); i++) { // Reverse order
					info.add(0, info.remove(i));
				}
				
				for (int i = 1; i < subinfo.size(); i++) { // Reverse order
					subinfo.add(0, subinfo.remove(i));
				}
				
			} else {
				style.setYPos(2);
				substyle.setYPos(2);
			}

			Font.drawParagraph(info, screen, style, 2);
			Font.drawParagraph(subinfo, screen, substyle, 2);
		}
	}

	/** Renders the "Click to focus" box when you click off the screen. */
	private static void renderFocusNagger() {
		String msg = "Click to focus!"; // the message when you click off the screen.
		Updater.paused = true; // perhaps paused is only used for this.

		int xx = (Screen.w - Font.textWidth(msg)) / 2; // the width of the box
		int yy = (HEIGHT - 8) / 2; // the height of the box
		int w = msg.length(); // length of message in characters.
		int h = 1;

		// Renders the four corners of the box
		screen.render(xx - 8, yy - 8, 0 + 21 * 32, 0, 3);
		screen.render(xx + w * 8, yy - 8, 0 + 21 * 32, 1, 3);
		screen.render(xx - 8, yy + 8, 0 + 21 * 32, 2, 3);
		screen.render(xx + w * 8, yy + 8, 0 + 21 * 32, 3, 3);

		// Renders each part of the box...
		for (int x = 0; x < w; x++) {
			screen.render(xx + x * 8, yy - 8, 1 + 21 * 32, 0, 3); // ...top part
			screen.render(xx + x * 8, yy + 8, 1 + 21 * 32, 2, 3); // ...bottom part
		}
		for (int y = 0; y < h; y++) {
			screen.render(xx - 8, yy + y * 8, 2 + 21 * 32, 0, 3); // ...left part
			screen.render(xx + w * 8, yy + y * 8, 2 + 21 * 32, 1, 3); // ...right part
		}

		// The middle
		for (int x = 0; x < w; x++) {
			screen.render(xx + x * 8, yy, 3 + 21 * 32, 0, 3);
		}

		// Renders the focus nagger text with a flash effect...
		if ((Updater.tickCount / 20) % 2 == 0) { // ...medium yellow color
			Font.draw(msg, screen, xx, yy, Color.get(1, 153));

		} else { // ...bright yellow color
			Font.draw(msg, screen, xx, yy, Color.get(5, 255));
		}
	}

	public static java.awt.Dimension getWindowSize() {
		return new java.awt.Dimension((int) (WIDTH * SCALE), (int) (HEIGHT * SCALE));
	}
}