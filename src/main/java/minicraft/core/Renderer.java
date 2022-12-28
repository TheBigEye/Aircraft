package minicraft.core;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import javax.imageio.ImageIO;

import org.tinylog.Logger;

import minicraft.core.io.Settings;
import minicraft.entity.Entity;
import minicraft.entity.furniture.Bed;
import minicraft.entity.mob.Player;
import minicraft.entity.mob.boss.AirWizard;
import minicraft.entity.mob.boss.EyeQueen;
import minicraft.gfx.Color;
import minicraft.gfx.Ellipsis;
import minicraft.gfx.Ellipsis.DotUpdater.TickUpdater;
import minicraft.gfx.Ellipsis.SmoothEllipsis;
import minicraft.gfx.Font;
import minicraft.gfx.FontStyle;
import minicraft.gfx.Rectangle;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
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

/*
* Make the game display logic
*/
public class Renderer extends Game {
	private Renderer() {}

	public static final int HEIGHT = 288; // This is the height of the game * scale
	public static final int WIDTH = 432; // This is the width of the game * scale

	static float SCALE = 2; // Scales the window

	private static String levelName; // Used to store the names of the levels in the debug GUI
	public static Screen screen; // Creates the main screen

	static Canvas canvas = new Canvas();

	private static BufferedImage image; // Creates an image to be displayed on the screen.
	public static Screen lightScreen; // Creates a front screen to render the darkness in caves (Fog of war).
	public static boolean readyToRenderGameplay = false;
	public static boolean showDebugInfo = false;
	public static boolean renderRain = false;

	private static Ellipsis ellipsis = new SmoothEllipsis(new TickUpdater());

	public static SpriteSheet[] loadDefaultSpriteSheets() {
		SpriteSheet itemSheet, tileSheet, entitySheet, guiSheet, iconsSheet, background;
		try {
			// These set the sprites to be used.
			itemSheet = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/resources/textures/items.png"))));
			tileSheet = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/resources/textures/tiles.png"))));
			entitySheet = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/resources/textures/entities.png"))));
			guiSheet = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/resources/textures/gui.png"))));
			iconsSheet = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/resources/textures/icons.png"))));
			background = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/resources/textures/background.png"))));

		} catch (NullPointerException e) {
			// If a provided InputStream has no name. (in practice meaning it cannot be found.)
			e.printStackTrace();
			Logger.error("Default sprites a sprite sheet was not found.");
			System.exit(-1);
			return null;

		} catch (IOException | IllegalArgumentException e) {
			// If there is an error reading the file.
			e.printStackTrace();
			Logger.error("Default sprites could not load a sprite sheet.");
			System.exit(-1);
			return null;
		}

		Logger.debug("Default sprites loaded!");

		return new SpriteSheet[] { itemSheet, tileSheet, entitySheet, guiSheet, iconsSheet, background };
	}

	public static SpriteSheet[] loadLegacySpriteSheets() {
		SpriteSheet itemSheet, tileSheet, entitySheet, guiSheet, iconsSheet, background;
		try {
			itemSheet = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/resources/textures/legacy/items.png"))));
			tileSheet = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/resources/textures/legacy/tiles.png"))));
			entitySheet = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/resources/textures/legacy/entities.png"))));
			guiSheet = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/resources/textures/legacy/gui.png"))));
			iconsSheet = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/resources/textures/legacy/icons.png"))));
			background = new SpriteSheet(ImageIO.read(Objects.requireNonNull(Game.class.getResourceAsStream("/resources/textures/legacy/background.png"))));

		} catch (NullPointerException e) {
			e.printStackTrace();
			Logger.error("Legacy sprites a sprite sheet was not found.");
			System.exit(-1);
			return null;

		} catch (IOException | IllegalArgumentException e) {
			e.printStackTrace();
			Logger.error("Legacy sprites could not load a sprite sheet.");
			System.exit(-1);
			return null;
		}

		Logger.debug("Legacy sprites loaded!");

		return new SpriteSheet[] { itemSheet, tileSheet, entitySheet, guiSheet, iconsSheet, background };
	}

	static void initScreen() {
		Logger.debug("Loading spriteheets...");

		SpriteSheet[] sheets = loadDefaultSpriteSheets();
		screen = new Screen(sheets[0], sheets[1], sheets[2], sheets[3], sheets[4], sheets[5]);
		lightScreen = new Screen(sheets[0], sheets[1], sheets[2], sheets[3], sheets[4], sheets[5]);
		
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		screen.pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		
		Initializer.startCanvasRendering();
		canvas.createBufferStrategy(3);
		canvas.requestFocus();
	}

	/** Renders the current screen. Called in game loop, a bit after tick(). */
	public static void render() {
		if (screen == null) {
			return; // No point in this if there's no gui... :P
		}

		if (readyToRenderGameplay) {
			renderLevel();
			renderGui();
		}

		if (display != null) { // Renders menu, if present.
			display.render(screen);
		}

		if (!canvas.hasFocus()) {
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

		Random rnd = new Random();

		int xScroll = player.x - Screen.w / 2; // Scrolls the screen in the x axis.
		int yScroll = player.y - (Screen.h - 8) / 2; // Scrolls the screen in the y axis.

		// Stop scrolling if the screen is at the ...
		if (xScroll < 0) xScroll = 0; // ...Left border.
		if (yScroll < 0) yScroll = 0; // ...Top border.
		if (xScroll > level.w * 16 - Screen.w) xScroll = level.w * 16 - Screen.w; // ...right border.
		if (yScroll > level.h * 16 - Screen.h) yScroll = level.h * 16 - Screen.h; // ...bottom border.

		if (currentLevel > 3) { // If the current level is higher than 3 (which only the sky level (and dungeon) is)
			for (int y = 0; y < 56; y++) {
				for (int x = 0; x < 96; x++) {
					// Creates the background for the sky (and dungeon) level:
					screen.render(x * 8 - ((xScroll / 4) & 7), y * 8 - ((yScroll / 4) & 7), rnd.nextInt(2) + 25 * 32, 0, 1);
				}
			}
		}

		level.renderBackground(screen, xScroll, yScroll); // Renders current level background
		level.renderSprites(screen, xScroll, yScroll); // Renders level sprites on screen

		// this creates the darkness in the caves
		if ((currentLevel != 3 || Updater.tickCount < Updater.dayLength / 4 || Updater.tickCount > Updater.dayLength / 2) && !Game.isMode("Creative")) {

			// This doesn't mean that the pixel will be black; it means that the pixel will
			// be DARK, by default; lightScreen is about light vs. dark, not necessarily a
			// color. The light level it has is compared with the minimum light values in
			// dither to decide whether to leave the cell alone, or mark it as "dark", which
			// will do different things depending on the game level and time of day.
			lightScreen.clear(0); 

			// Brightens all
			int brightnessMultiplier = player.potionEffects.containsKey(PotionType.Light) ? 12 : 8; 

			// Light sources by a factor of 1.5 when the player has the Light potion effect. (8 above is normal)
			level.renderLight(lightScreen, xScroll, yScroll, brightnessMultiplier); // Finds (and renders) all the light from objects (like the player, lanterns, and lava).
			screen.overlay(lightScreen, currentLevel, xScroll, yScroll); // Overlays the light screen over the main screen.
		}
		
		if (player != null && !Game.isMode("Creative")) {
			if (!player.isNiceNight && currentLevel == 3 || !player.isNiceNight && currentLevel == 4) {
				lightScreen.clear(0); 
	
				// Brightens all
				int brightnessMultiplier = player.potionEffects.containsKey(PotionType.Light) ? 12 : 8; 
				level.renderLight(lightScreen, xScroll, yScroll, brightnessMultiplier); // Finds (and renders) all the light from objects (like the player, lanterns, and lava).
				screen.darkness(lightScreen, currentLevel, xScroll, yScroll);
			}
		}
	}

	/**
	 * Renders the main game GUI (hearts, Stamina bolts, name of the current item,
	 * etc.)
	 */
	@SuppressWarnings("unchecked")
	private static void renderGui() {
		
		Random rnd = new Random();
		
		/*renderRain();*/

		// ARROWS COUNT STATUS
		if (player.activeItem instanceof ToolItem) {
			if (((ToolItem) player.activeItem).type == ToolType.Bow) {
				int ac = player.getInventory().count(Items.arrowItem);

				int x = (Screen.w) / 2 - 32 - player.activeItem.arrAdjusted; // the width of the box
				int y = (Screen.h - 8) - 13; // the height of the box
				int w = 3; // length of message in characters.
				int h = 1;

				// Renders the four corners of the box
				screen.render(x - 8, y - 8, 0 + 21 * 32, 0, 3);
				screen.render(x + w * 8, y - 8, 0 + 21 * 32, 1, 3);
				screen.render(x - 8, y + 8, 0 + 21 * 32, 2, 3);
				screen.render(x + w * 8, y + 8, 0 + 21 * 32, 3, 3);

				// Renders each part of the box...
				for (int xb = 0; xb < w; xb++) {
					screen.render(x + xb * 8, y - 8, 1 + 21 * 32, 0, 3); // ...top part
					screen.render(x + xb * 8, y + 8, 1 + 21 * 32, 2, 3); // ...bottom part
				}
				for (int yb = 0; yb < h; yb++) {
					screen.render(x - 8, y + yb * 8, 2 + 21 * 32, 0, 3); // ...left part
					screen.render(x + w * 8, y + yb * 8, 2 + 21 * 32, 1, 3); // ...right part
				}

				// The middle
				for (int xb = 0; xb < w; xb++) {
					screen.render(x + xb * 8, y, 3 + 21 * 32, 0, 3);
				}

				if (isMode("Creative") || ac >= 10000) {
					Font.drawTransparentBackground(" x" + "∞", screen, 184 - player.activeItem.arrAdjusted, Screen.h - 24);
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

			int x = (Screen.w) / 2 + 8 + player.activeItem.durAdjusted; // The width of the box
			int y = (Screen.h - 8) - 13; // The height of the box
			int w = 3; // Length of message in characters.
			int h = 1;

			// Renders the four corners of the box
			screen.render(x - 8, y - 8, 0 + 21 * 32, 0, 3);
			screen.render(x + w * 8, y - 8, 0 + 21 * 32, 1, 3);
			screen.render(x - 8, y + 8, 0 + 21 * 32, 2, 3);
			screen.render(x + w * 8, y + 8, 0 + 21 * 32, 3, 3);

			// Renders each part of the box...
			for (int xb = 0; xb < w; xb++) {
				screen.render(x + xb * 8, y - 8, 1 + 21 * 32, 0, 3); // ...top part
				screen.render(x + xb * 8, y + 8, 1 + 21 * 32, 2, 3); // ...bottom part
			}
			for (int yb = 0; yb < h; yb++) {
				screen.render(x - 8, y + yb * 8, 2 + 21 * 32, 0, 3); // ...left part
				screen.render(x + w * 8, y + yb * 8, 2 + 21 * 32, 1, 3); // ...right part
			}

			// The middle
			for (int xb = 0; xb < w; xb++) {
				screen.render(x + xb * 8, y, 3 + 21 * 32, 0, 3);
			}

			Font.drawTransparentBackground(dura + "%", screen, 221 + player.activeItem.durAdjusted, Screen.h - 24, Color.get(1, 255 - green, green, 0));
		}


		// Shows active item sprite and name in bottom toolbar.
		if (player.activeItem != null) {
			player.activeItem.renderHUD(screen, 20 * 8, Screen.h - 8, Color.GRAY);
		}

		ArrayList <String> permStatus = new ArrayList<>();

		if (Updater.saving) {
			permStatus.add("Saving... " + Math.round(LoadingDisplay.getPercentage()) + "%");
		}

		if (Bed.sleeping()) {
			permStatus.add("Sleeping" + ellipsis.updateAndGet());
		}

		/*if (Bed.inBed(Game.player)) {
			permStatus.add("Press " + input.getMapping("exit") + " to cancel");
		}*/

		if (!permStatus.isEmpty()) {
			FontStyle style = new FontStyle(Color.WHITE).setYPos(Screen.h / 2 - 25).setRelTextPos(RelPos.TOP).setShadowType(Color.DARK_GRAY, false);
			Font.drawParagraph(permStatus, screen, style, 1);
		}

		/// NOTIFICATIONS
		if (permStatus.isEmpty() && !notifications.isEmpty()) {
			Updater.notetick++;
			if (notifications.size() > 3) { // Only show 3 notifs max at one time; erase old notifs.
				notifications = notifications.subList(notifications.size() - 3, notifications.size());
			}

			if (Updater.notetick > 100) { // Display time per notification.
				notifications.remove(0);
				Updater.notetick = 0;
			}

			// draw each current notification, with shadow text effect.
			FontStyle style = new FontStyle(Color.WHITE).setShadowType(Color.DARK_GRAY, false).setYPos(Screen.h * 2 / 5).setRelTextPos(RelPos.TOP, false);
			Font.drawParagraph(notifications, screen, style, 0);
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
		if (player.showPotionEffects && !player.potionEffects.isEmpty()) {
			Map.Entry <PotionType, Integer> [] effects = player.potionEffects.entrySet().toArray(new Map.Entry[0]);
				
			String title = "(" + input.getMapping("potionEffects") + " to hide)";
			
			PotionType pType;
			Sprite pIcon = new Sprite(0, 7, 0);

			int pxx = 0; // the width of the box
			int pyy = 0; // the height of the box
			int pw = 0; // length of message in characters.
			int ph = 0;
			int px = (Screen.w - 8) / 3 + pxx - 8;
			int py = (Screen.h - 8) / 36 - 130;

			for (int i = 0; i < effects.length; i++) {
				pType = effects[i].getKey();
				
				pxx = (Screen.w - 118) / 2 + px; // the width of the box
				pw = 16; // length of message in characters.
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
				
				pIcon.color = pType.dispColor;
                
				int minutes = pTime / 60;
				int seconds = pTime % 60;
				
				// Title background
				for (int j = 0; j < title.length(); j++) {
					screen.render(310 + j * 8, 9, 3 + 21 * 32, 0, 3);
				}
				Font.draw(title, screen, 310, 9, Color.YELLOW);
				
				Font.drawTransparentBackground(pType + " ", screen, 300 , 17 + i * Font.textHeight(), pType.dispColor);
				Font.drawTransparentBackground("(" + minutes + ":" + (seconds < 10? "0" + seconds:seconds) + ")", screen, 373 , 17 + i * Font.textHeight(), pType.dispColor);
				pIcon.render(screen, 290, 17 + i * Font.textHeight());
			}
		}

		// This is the status icons, like health hearts, stamina bolts, and hunger "burger".
		if (!isMode("Creative")) {
			for (int i = 0; i < Player.maxStat; i++) {

				// Renders armor
				int armor = player.armor * Player.maxStat / Player.maxArmor;
				if (i <= armor && player.currentArmor != null) {
					screen.render(i * 8, Screen.h - 24, (player.currentArmor.level - 1) + 9 * 32, 0, 0);
				}

				/// Renders your current red hearts, hardcore hearts, or black hearts for damaged health.
				if (!isMode("Hardcore")) { // Survival hearts
					if (i < player.health) {
						
						// wobbling hearts if the player health is less than 5
						if (player.health < 5) {
							int blinking = (rnd.nextInt(2) - rnd.nextInt(2));
							screen.render(i * 8, Screen.h - (16 + blinking), 0 + 2 * 32, 0, 3);
						} else { // normal hearts if more than 4
							screen.render(i * 8, Screen.h - 16, 0 + 2 * 32, 0, 3);
						}
					} else { // hearts cotainer
						if (player.health < 5) {
							int blinking = (rnd.nextInt(2) - rnd.nextInt(2));
							screen.render(i * 8, Screen.h - (16 + blinking), 0 + 3 * 32, 0, 3); // wobbling hearts cotainer
						} else {
							screen.render(i * 8, Screen.h - 16 , 0 + 3 * 32, 0, 3); // nomral hearts container
						}
					}
				
				} else { // Hardcore hearts
					
					// the same that the survival hearts, but with hardcore spirtes
					if (i < player.health) {
						if (player.health < 6) {
							int blinking = (rnd.nextInt(2) - rnd.nextInt(2));
							screen.render(i * 8, Screen.h - (16 + blinking), 7 + 2 * 32, 0, 3);
						} else {
							screen.render(i * 8, Screen.h - 16, 7 + 2 * 32, 0, 3);
						}
					
				} else { // hearts cotainer
						if (player.health < 5) {
							int blinking = (rnd.nextInt(2) - rnd.nextInt(2));
							screen.render(i * 8, Screen.h - (16 + blinking), 7 + 3 * 32, 0, 3); // wobbling hearts cotainer
						} else {
							screen.render(i * 8, Screen.h - 16 , 7 + 3 * 32, 7, 3); // nomral hearts container
						}
					}
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
		
        // Render the bosses health bar on screen
		if (!player.isRemoved() && Settings.get("bossbar").equals("On screen")){
			// Check if are a boss close to the player in the current level
			List<Entity> entitiesInRange = Game.levels[Game.currentLevel].getEntitiesInRect(new Rectangle(player.x, player.y, 360 * 2, 360 * 2, Rectangle.CENTER_DIMS));
			
			if (AirWizard.active) {
            	AirWizard boss = AirWizard.entity;
            	for (Entity entity: entitiesInRange) { 
            		if (entity instanceof AirWizard) {
            			if (boss.secondform) {
            				renderBossbar((int) ((((float) boss.health) / boss.maxHealth) * 100), "Air Wizard II");
            			} else {
            				renderBossbar((int) ((((float) boss.health) / boss.maxHealth) * 100), "Air Wizard");
            			}
            		}
            	}    
            	
            } 
			if (EyeQueen.active) {
            	EyeQueen boss = EyeQueen.entity;
            	for (Entity entity: entitiesInRange) {
            		if (entity instanceof EyeQueen) {
            			renderBossbar((int) ((((float) boss.health) / boss.maxHealth) * 100), "Eye Queen");
            		}
            	}           	
            }
		}
		
		renderDebugInfo();
	}
	
	/*public static void renderRain() {
		// Check if it is raining and the current level is 3
		if (currentLevel == 3 && player.isRaining == true) {
		    Random rnd = new Random(); // Create a Random object to generate random numbers

		    // Loop through the screen width
		    for (int x = 0; x < 200; x++) {
		    	// Loop through the screen height
		    	for (int y = 0; y < 75; y++) {
		    	  	int dd = (y + x % 2 * 2 + x / 3) - player.rainTick * 2; // Used as part of the positioning.

		        	// Check if the rain should be rendered upwards or downwards
		        	if (dd < 0 && dd > -140) {
		        		screen.render(x * 16 - rnd.nextInt(8), y * 16 - rnd.nextInt(8), 14 + 24 * 32, 0, 3); // Render the rain upwards
		        	} else {
		        		screen.render(x * 16 - rnd.nextInt(12), y * 16 - rnd.nextInt(12), 14 + 24 * 32, 0, 3); // Render the rain downwards
		        	}
		      	}
		    }
		}
	}*/

	public static void renderBossbar(int length, String title) {

		int x = Screen.w / 4 + 4;
		int y = Screen.h / 8 - 28;

		int max_bar_length = 100;
		int bar_length = length; // Bossbar size.

		int INACTIVE_BOSSBAR = 24; // sprite y position
		int ACTIVE_BOSSBAR = 25; // sprite y position

		if (bar_length > 1) {
			screen.render(x + (max_bar_length * 2) , y , 0 + INACTIVE_BOSSBAR * 32, 1, 3); // left corner
	
			// The middle
			for (int bx = 0; bx < max_bar_length; bx++) {
				for (int by = 0; by < 1; by++) {
					screen.render(x + bx * 2, y + by * 8, 1 + INACTIVE_BOSSBAR * 32, 0, 3);
				}
			}  
	
			screen.render(x - 5 , y , 0 + ACTIVE_BOSSBAR * 32, 0, 3); // right corner
	
			for (int bx = 0; bx < bar_length; bx++) {
				for (int by = 0; by < 1; by++) {
					screen.render(x + bx * 2, y + by * 8, 1 + ACTIVE_BOSSBAR * 32, 0, 3);
				}
			}
	
			Font.drawCentered(title, screen, y + 8, Color.GRAY);
		}
	}

	private static final LocalDateTime time = LocalDateTime.now();

	// Renders show debug info on the screen.
	private static void renderDebugInfo() { 
		int textColor = Color.WHITE;

		if (showDebugInfo) {
			ArrayList <String> info = new ArrayList <> ();
			ArrayList <String> subinfo = new ArrayList <> ();

			info.add("Version: " + Game.BUILD + " (" + Game.VERSION + ")");               subinfo.add("Time:" + InfoDisplay.getTimeString());
			info.add("Base: " + "Minicraft Plus Legacy");                                 subinfo.add("Java:" + Info.Java_Version);
			info.add("" + time.toLocalDate());                                            subinfo.add("Java arch: x" + Info.Java_Arch);
			info.add(Initializer.fra + " fps");                                           subinfo.add("Max mem:" + Info.max_Memory);
			info.add("day tiks: " + Updater.tickCount + " (" + Updater.getTime() + ")");   subinfo.add("Total mem:" + Info.total_Memory);
			info.add((Updater.normSpeed * Updater.gamespeed) + " tps ");                  subinfo.add("Free mem: " + Info.free_Memory);

			// player info
			info.add("walk spd: " + player.moveSpeed);
			info.add("X: " + (player.x / 16) + "." + (player.x % 16));
			info.add("Y: " + (player.y / 16) + "." + (player.y % 16));
			info.add("");

			info.add("Tile: " + levels[currentLevel].getTile(player.x >> 4, player.y >> 4).name);
			info.add("Id: " + levels[currentLevel].getTile(player.x >> 4, player.y >> 4).id);
			info.add("Data: " + levels[currentLevel].getData(player.x >> 4, player.y >> 4));
			info.add("Depth: " + levels[currentLevel].depth + " (" + levels[currentLevel].w +"x" + levels[currentLevel].h +")");
			info.add("");

			// screen info
			info.add("Screen: " + java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight() + "x" + java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth());
			info.add("Current: " + getWindowSize().getHeight() + "x" + getWindowSize().getWidth());

			if (isMode("score")) {
				info.add("Score " + player.getScore());
			}

			if (levels[currentLevel] != null) {
				info.add("Level: " + levelName);

				switch (levels[currentLevel].depth) {
					case 1: levelName = "Aether"; break;
					case 0: levelName = "Surface"; break;
					case -1: levelName = "Caves"; break;
					case -2: levelName = "Caverns"; break;
					case -3: levelName = "Hell"; break;
					case -4: levelName = "Dungeon"; break;
					case -5: levelName = "Void"; break;
					default: levelName = "Secret dimension"; break;
				}

				info.add("Mob Cnt: " + levels[currentLevel].mobCount + "/" + levels[currentLevel].maxMobCount);
			}

			/// Displays number of chests left, if on dungeon level.
			if (levels[currentLevel] != null && currentLevel == 6) {
				if (levels[6].chestCount > 0) {
					info.add("Chests: " + levels[6].chestCount);
				} else {
					info.add("Chests: Complete!");
				}
			}

			info.add("Hunger stam: " + player.getDebugHunger());
			if (player.armor > 0) {
				info.add("Armor: " + player.armor);
				info.add("Dam buffer: " + player.armorDamageBuffer);
			}

			if (levels[currentLevel] != null) {
				info.add("");
				info.add("Level seed: " + levels[currentLevel].getSeed());
				info.add("Night factor: " + player.isNiceNight + " > " + player.nightCount + "/4");
				info.add("Rain factor: " + player.isRaining + " > " + player.rainCount + "/8");
				info.add("Music factor: " + (levels[currentLevel].randomMusic / 1000) + "/16");
			}

			FontStyle style = new FontStyle(textColor).setShadowType(Color.BLACK, true).setXPos(1).setYPos(1);
			FontStyle substyle = new FontStyle(textColor).setShadowType(Color.BLACK, true).setXPos(Screen.w - 116).setYPos(1);

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