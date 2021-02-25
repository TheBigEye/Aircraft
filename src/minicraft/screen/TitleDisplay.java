package minicraft.screen;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Random;

import minicraft.core.Game;
import minicraft.core.Renderer;
import minicraft.core.World;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Sound;
import minicraft.entity.mob.RemotePlayer;
import minicraft.gfx.Color;
import minicraft.gfx.Font;
import minicraft.gfx.Point;
import minicraft.gfx.Screen;
import minicraft.level.Level;
import minicraft.screen.entry.BlankEntry;
import minicraft.screen.entry.ListEntry;
import minicraft.screen.entry.SelectEntry;
import minicraft.screen.entry.StringEntry;

import org.jetbrains.annotations.NotNull;

public class TitleDisplay extends Display {
	private static final Random random = new Random();
	
	private int rand;
	private int count = 0; // this and reverse are for the logo; they produce the fade-in/out effect.
	private boolean reverse = false;

	private Object timer;
	
	public TitleDisplay() {
		super(true, false, new Menu.Builder(false, 2, RelPos.CENTER,
			new StringEntry(""),
			new BlankEntry(),
			new SelectEntry("Singleplayer", () -> {
				if(WorldSelectDisplay.getWorldNames().size() > 0)
					Game.setMenu(new Display(true, new Menu.Builder(false, 2, RelPos.CENTER,
						new SelectEntry("Load World", () -> Game.setMenu(new WorldSelectDisplay())),
						new SelectEntry("New World", () -> Game.setMenu(new WorldGenDisplay()))
					).createMenu()));
				else
					Game.setMenu(new WorldGenDisplay());
			}),
			new SelectEntry("Multiplayer", () -> Game.setMenu(new MultiplayerDisplay())),
			new SelectEntry("Options", () -> Game.setMenu(new OptionsDisplay())),
			new SelectEntry("Credits", () -> Game.setMenu(new BookDisplay(BookData.credits))),
			displayFactory("Help",
				new SelectEntry("Instructions", () -> Game.setMenu(new BookDisplay(BookData.instructions))),
				new BlankEntry(),
				new SelectEntry("Storyline Guide (for the weak)", () -> Game.setMenu(new BookDisplay(BookData.storylineGuide))),
				new BlankEntry(),
				new SelectEntry("About", () -> Game.setMenu(new BookDisplay(BookData.about)))
			),
			new SelectEntry("Exit", Game::quit)
			)
				
			.setPositioning(new Point(Screen.w/2, Screen.h*3/5), RelPos.CENTER)
			.createMenu()
		);
	}
	
	@Override
	public void init(Display parent) {
		super.init(null); // The TitleScreen never has a parent.
		Renderer.readyToRenderGameplay = false;
		
		Sound.Heart.stop();
		
		if (random.nextInt(2) == 0) {
			Sound.Intro.loop(true);
			Sound.Intro2.stop();
		}
		if (random.nextInt(2) == 1) {
			Sound.Intro2.loop(true);
			Sound.Intro.stop();

		}




		// check version
		//checkVersion();

		/// this is useful to just ensure that everything is really reset as it should be. 
		if (Game.server != null) {
			if (Game.debug) System.out.println("wrapping up loose server ends");
			Game.server.endConnection();
			Game.server = null;
		}
		if (Game.client != null) {
			if (Game.debug) System.out.println("wrapping up loose client ends");
			Game.client.endConnection();
			Game.client = null;
		}
		Game.ISONLINE = false;

		
		//events
		LocalDateTime time = LocalDateTime.now();
		if (time.getMonth() == Month.DECEMBER) {
			if (time.getDayOfMonth() == 19) rand = 1;
			if (time.getDayOfMonth() == 25) rand = 2;
		} else {
			rand = random.nextInt(splashes.length - 3) + 3;
			
		}
		
		if (time.getMonth() == Month.FEBRUARY) {
			if (time.getDayOfMonth() == 14) rand = 0;
			if (time.getDayOfMonth() == 15) rand = 0;
			if (time.getDayOfMonth() == 16) rand = 0;
		} else {
			rand = random.nextInt(splashes.length - 3) + 3;
			
		}
		
		if (time.getMonth() == Month.JULY) {
			if (time.getDayOfMonth() == 6) rand = 3;
		} else {
			rand = random.nextInt(splashes.length - 3) + 3;
			
		}
		if (time.getMonth() == Month.SEPTEMBER) {
			if (time.getDayOfMonth() == 18) rand = 4;
		} else {
			rand = random.nextInt(splashes.length - 3) + 3;
			
		}
		if (time.getMonth() == Month.AUGUST) {
			if (time.getDayOfMonth() == 29) rand = 5;
			if (time.getDayOfMonth() == 10) rand = 6;
		} else {
			rand = random.nextInt(splashes.length - 3) + 3;
			
		}
		
		
		World.levels = new Level[World.levels.length];
		
		if(Game.player == null || Game.player instanceof RemotePlayer)
			// was online, need to reset player
			World.resetGame(false);
	}
	
	/*
	private void checkVersion() {
		VersionInfo latestVersion = Network.getLatestVersion();
		if(latestVersion == null) {
			Network.findLatestVersion(this::checkVersion);
		}
		else {
			if(Game.debug) System.out.println("latest version = "+latestVersion.version);
			if(latestVersion.version.compareTo(Game.VERSION) > 0) { // link new version
				menus[0].updateEntry(0, new StringEntry("New: "+latestVersion.releaseName, Color.GREEN));
				menus[0].updateEntry(1, new LinkEntry(Color.GREEN, "--Select here to Download--", latestVersion.releaseUrl, "Direct link to latest version: " + latestVersion.releaseUrl + "\nCan also be found here with change log: https://www.github.com/TheBigEye/Cthulhucraft/releases"));
			}
			else if(latestVersion.releaseName.length() > 0)
				menus[0].updateEntry(0, new StringEntry("You have the latest version.", Color.DARK_GRAY));
			else
				menus[0].updateEntry(0, new StringEntry("Connection failed, could not check for updates.", Color.RED));
		}
	}*/
	
	@NotNull
	private static SelectEntry displayFactory(String entryText, ListEntry... entries) {
		return new SelectEntry(entryText, () -> Game.setMenu(new Display(true, new Menu.Builder(false, 2, RelPos.CENTER, entries).createMenu())));
	}
	
	@Override
	public void tick(InputHandler input) {
		if (input.getKey("r").clicked) rand = random.nextInt(splashes.length - 3) + 3;
		
		if (!reverse) {
			count++;
			if (count == 25) reverse = true;
		} else {
			count--;
			if (count == 0) reverse = false;
		}
		
		Sound.Heart.stop();
		
		super.tick(input);	
		
	}
		
		
	
	@Override
	public void render(Screen screen) {
		super.render(screen);
		
		int h = 6; // Height of squares (on the spritesheet)
		int w = 20; // Width of squares (on the spritesheet)
		int xo = (Screen.w - w * 8) / 2; // X location of the title
		int yo = 55; // Y location of the title
		
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				screen.render(xo + x * 8, yo + y * 8, x + (y + 7) * 32, 0, 3);
			}
		}
		
		
		
		boolean isblue = splashes[rand].contains("blue");
		boolean isGreen = splashes[rand].contains("Green");
		boolean isRed = splashes[rand].contains("Red");
		boolean isRed2 = splashes[rand].contains("Red");
		boolean isOrange = splashes[rand].contains("Orange");
		boolean isYellow = splashes[rand].contains("Yellow");
		
		/// this isn't as complicated as it looks. It just gets a color based off of count, which oscilates between 0 and 25.
		int bcol = 5 - count / 5; // this number ends up being between 1 and 5, inclusive.
		int splashColor = isblue ? Color.BLUE : isRed ? Color.RED : isRed2 ? Color.RED : isGreen ? Color.GREEN : isOrange ? Color.ORANGE : isYellow ? Color.YELLOW : Color.get(1, bcol*51, bcol*51, bcol*25);

		
		Font.drawCentered(splashes[rand], screen, 100, splashColor);
		
		//Font.draw("Version " + Game.BUILD, screen, 1, 1, Color.get(1, 51));
		Font.draw(Game.BUILD, screen, 1, 1, Color.get(1, 51));
		
		Font.drawCentered("Mod by TheBigEye", screen, Screen.h - 12, Color.get(1, 51));
	}
	
	private static final String[] splashes = {
		"Happy birthday Minicraft!",
		"Happy XMAS!",
		"Happy birthday BigEye :)",
		"Happy birthday Zaq :)",
		"Happy birthday A.L.I.C.E :)",
		
        //"Bye ben :(",
		
        // Also play 
		"Also play InfinityTale!",
		"Also play Minicraft Deluxe!",
		"Also play Alecraft!",
		"Also play Hackcraft!",
		"Also play MiniCrate!",
		"Also play MiniCraft Mob Overload!",
		
		// Now with...
		"Now with better fishing!",
		"Now with better tools!",
		"Now with better chests!",
		"Now with better dungeons!",
        "Now with better sounds!",
		"Only on PlayMinicraft.com!",
		"Playminicraft.com is the bomb!",
		"@MinicraftPlus on Twitter",
		"MinicraftPlus on Youtube",
		"Join the Forums!",
		"The Wiki is weak! Help it!",
		
		"Notch is Awesome!",
		"Dillyg10 is cool as Ice!",
		"Shylor is the man!",
		"Chris J is great with portals!",
		"AntVenom loves cows! Honest!",
		"TheBigEye.... Cake rain!",
		"ASCII",
		
		"You should read Antidious Venomi!",
		"Oh Hi Mark",
		"Use the force!",
		"Keep calm!",
		"Get him, Steve!",
		"Forty-Two!",
		
		// kill
		"Kill Creeper, get Gunpowder!",
		"Kill Cow, get Beef!",
		"Kill Zombie, get Cloth!",
		"Kill Slime, get Slime!",
		"Kill Skeleton, get Bones!",
		"Kill Sheep, get Wool!",
		"Kill Pig, get Porkchop!",
		"Kill Chicken, get Feathers!",
		
		// Mineral levels
		"Gold > Iron",
		"Gem > Gold",
		
		"Test == InDev!",
		"Story? Uhh...",
		"Mod on phase A",
		
		// What's that?
		"Infinite terrain? What's that?",
		"Redstone? What's that?",
		"Minecarts? What are those?",
		"Windows? I prefer Doors!",
		"2.5D FTW!",
		"Grab your friends!",
		
		// Not Included
		"Null not included",
		"Herobine not included",
		"Mouse not included!",
		"No spiders included!",
		"No Endermen included!",
		"3rd dimension not included!",
		"Orange box not included",
		
		// Included
		"Villagers included!",
		"Creepers included!",
		"Skeletons included!",
		"Knights included!",
		"Snakes included!",
		"Cows included!",
		"Sheep included!",
		"Chickens included!",
		"Pigs included!",
		"Cthulhu included!",
		"Enchantments Now Included!",
		"Multiplayer Now Included!",
		"Carrots Now Included!",
		"Boats Now Included!",
		"Maps Now Included!",
		"Books included!",
		"Sad music included!",
		"Big eye included!",
	  //"Nether Now Included?",
		
		// Worlds
		"Bigger Worlds!",
		"World types!",
		"World themes!",
		"Mushroom Biome!",
		"Desert Biome!",
		"Forest Biome!",
		"Snow Biome!",
		
		// Ideas
		"Sugarcane is a Idea!",
		"Milk is an idea!",
		"Cakes is an idea!",
		
		"Creeper, aw man",
		"So we back in the mine,",
		"pickaxe swinging from side to side",
		"In search of Gems!",
		"Life itself suspended by a thread",
		"saying ay-oh, that creeper's KO'd!",
		"Gimmie a bucket!",
		"Farming with water!",
		"Press \"R\"!",
		"Get the High-Score!",
		"Potions ftw!",
		"Beds ftw!",
		"Defeat the Air Wizard!",
		"Conquer the Dungeon!",
		"One down, one to go...",
		"Loom + Wool = String!",
		"String + Wood = Rod!",
		"Sand + Gunpowder = TNT!",
		"Sleep at Night!",
		"Farm at Day!",
		"Explanation Mark!",
		"!sdrawkcab si sihT",
		"This is forwards!",
		"Why is this blue?",
		"Green is a nice color!",
		"Red is my favorite color!",
		"Hmmmm Orange!",
		"Yellow = Happy!",
		"Y U NO BOAT!?",
		"Made with 10000% Vitamin Z!",
		"Too much DP!",
		"Punch the Moon!",
		"This is String qq!",
		"Why?",
		"You are null!",
		"hello down there!",
		"That guy is such a sly fox!",
		"Hola senor!",
		"Sonic Boom!",
		"Hakuna Matata!",
		"One truth prevails!",
		"Awesome!",
		"Sweet!",
		"Great!",
		"Cool!",
		"Radical!",
		"011011000110111101101100!",
		"001100010011000000110001!",
		"011010000110110101101101?",
		"...zzz...",
		"The cake is a lie!",
	};
}
