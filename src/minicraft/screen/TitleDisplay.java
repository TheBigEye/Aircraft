package minicraft.screen;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Random;

import org.jetbrains.annotations.NotNull;

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
import minicraft.gfx.Sprite;
import minicraft.level.Level;
import minicraft.screen.entry.BlankEntry;
import minicraft.screen.entry.LinkEntry;
import minicraft.screen.entry.ListEntry;
import minicraft.screen.entry.SelectEntry;
import minicraft.screen.tutorial.TutorialDisplay;

public class TitleDisplay extends Display {
    private static final Random random = new Random();

    private int rand;
    private int count = 0; // this and reverse are for the logo; they produce the fade-in/out effect.
    private boolean reverse = false;

    public TitleDisplay() {

        super(false, false, new Menu.Builder(true, 1, RelPos.CENTER,
                // new StringEntry(""),
                new SelectEntry("Singleplayer", () -> {
                    if (WorldSelectDisplay.getWorldNames().size() > 0)
                        Game.setMenu(new Display(true,
                                new Menu.Builder(false, 2, RelPos.CENTER,
                                        new SelectEntry("Load World", () -> Game.setMenu(new WorldSelectDisplay())),
                                        new SelectEntry("New World", () -> Game.setMenu(new WorldGenDisplay())))
                                                .createMenu()));
                    else
                        Game.setMenu(new WorldGenDisplay());
                }),
                // new SelectEntry("Multiplayer", () -> Game.setMenu(new MultiplayerDisplay())),
                new SelectEntry("Options", () -> Game.setMenu(new OptionsDisplay())),
                new SelectEntry("Credits", () -> Game.setMenu(new BookDisplay(BookData.credits))),
                displayFactory("Help",
                        new SelectEntry("Instructions", () -> Game.setMenu(new BookDisplay(BookData.instructions))),
                        new BlankEntry(),
                        // new SelectEntry("Storyline Guide", () -> Game.setMenu(new
                        // BookDisplay(BookData.storylineGuide))),
                        new SelectEntry("Tutorial", () -> Game.setMenu(new TutorialDisplay())), new BlankEntry(),
                        new SelectEntry("About", () -> Game.setMenu(new BookDisplay(BookData.about))), new BlankEntry(),
                        new BlankEntry(), new LinkEntry(Color.BLUE, "Minicraft discord", "https://discord.me/minicraft")

                ), new SelectEntry("Exit", Game::quit))

                        .setPositioning(new Point(Screen.w / 2, Screen.h * 3 / 5), RelPos.CENTER).createMenu());
    }

    @Override
    public void init(Display parent) {
        super.init(null); // The TitleScreen never has a parent.
        Renderer.readyToRenderGameplay = false;

        if (random.nextInt(2) == 0) {
            Sound.Intro.play();

        }
        if (random.nextInt(2) == 1) {
            Sound.Intro2.play();

        }

        /// This is useful to just ensure that everything is really reset as it should
        /// be.
        if (Game.server != null) {
            if (Game.debug)
                System.out.println("wrapping up loose server ends");
            Game.server.endConnection();
            Game.server = null;
        }
        if (Game.client != null) {
            if (Game.debug)
                System.out.println("wrapping up loose client ends");
            Game.client.endConnection();
            Game.client = null;
        }
        Game.ISONLINE = false;

        // Events
        LocalDateTime time = LocalDateTime.now();
        if (time.getMonth() == Month.DECEMBER) {
            if (time.getDayOfMonth() == 19)
                rand = 1;
            if (time.getDayOfMonth() == 25)
                rand = 2;
        } else {
            rand = random.nextInt(splashes.length - 3) + 3;

        }

        if (time.getMonth() == Month.FEBRUARY) {
            if (time.getDayOfMonth() == 14)
                rand = 0;
            if (time.getDayOfMonth() == 15)
                rand = 0;
            if (time.getDayOfMonth() == 16)
                rand = 0;
        } else {
            rand = random.nextInt(splashes.length - 3) + 3;

        }

        if (time.getMonth() == Month.JULY) {
            if (time.getDayOfMonth() == 6)
                rand = 3;
        } else {
            rand = random.nextInt(splashes.length - 3) + 3;

        }
        if (time.getMonth() == Month.SEPTEMBER) {
            if (time.getDayOfMonth() == 18)
                rand = 4;
        } else {
            rand = random.nextInt(splashes.length - 3) + 3;

        }
        if (time.getMonth() == Month.AUGUST) {
            if (time.getDayOfMonth() == 29)
                rand = 5;
            if (time.getDayOfMonth() == 10)
                rand = 6;
        } else {
            rand = random.nextInt(splashes.length - 3) + 3;

        }

        World.levels = new Level[World.levels.length];

        if (Game.player == null || Game.player instanceof RemotePlayer)
            // Was online, need to reset player
            World.resetGame(false);
    }

    @NotNull
    private static SelectEntry displayFactory(String entryText, ListEntry... entries) {
        return new SelectEntry(entryText,
                () -> Game.setMenu(new Display(true, new Menu.Builder(false, 2, RelPos.CENTER, entries).createMenu())));
    }

    @Override
    public void tick(InputHandler input) {
        if (input.getKey("r").clicked)
            rand = random.nextInt(splashes.length - 3) + 3;

        if (!reverse) {
            count++;
            if (count == 25)
                reverse = true;
        } else {
            count--;
            if (count == 0)
                reverse = false;
        }

        super.tick(input);

    }

    @Override
    public void render(Screen screen) {
        screen.clear(0);

        // Background sprite
        int hh = 39; // Height of squares (on the spritesheet)
        int ww = 416; // Width of squares (on the spritesheet)
        int xxo = (Screen.w - ww * 8) / 2; // X location of the title
        int yyo = 0; // Y location of the title

        for (int y = 0; y < hh; y++) {
            for (int x = 0; x < ww; x++) {
                screen.render(xxo + x * 8, yyo + y * 8, new Sprite.Px(x - 8, y, 0, 5));
            }
        }

        // Render the options
        super.render(screen);

        // Title sprite
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
        boolean isOrange = splashes[rand].contains("Orange");
        boolean isYellow = splashes[rand].contains("Yellow");

        /// This isn't as complicated as it looks. It just gets a color based off of
        /// count, which oscilates between 0 and 25.
        int bcol = 5 - count / 5; // this number ends up being between 1 and 5, inclusive.
        int splashColor = isblue ? Color.BLUE
                : isRed ? Color.RED
                        : isGreen ? Color.GREEN
                                : isOrange ? Color.ORANGE
                                        : isYellow ? Color.YELLOW : Color.get(1, bcol * 51, bcol * 51, bcol * 25);

        Font.drawCentered(splashes[rand], screen, 100, splashColor);

        /*
         * In case the game has the "in_dev" mode set to true it will show the version
         * as in "Development"
         * 
         * In case it is false, it will show the numerical version of the game
         */
        if (Game.in_dev == true) {
            Font.draw("Pre " + Game.BUILD, screen, 1, 280, Color.GRAY);
        } else {
            Font.draw(Game.BUILD, screen, 1, 280, Color.get(1, 100));
        }

        /*
         * Show the author's name below the options
         */
        Font.draw("Mod by TheBigEye", screen, 300, 280, Color.GRAY);
    }

    private static final String[] splashes = { "Happy birthday Minicraft!", "Happy XMAS!", "Happy birthday Eye :)",
            "Happy birthday Zaq :)", "Thanks A.L.I.C.E!",

            // "Bye ben :(",

            // Also play
            "Also play Minicraft Plus!", "Also play InfinityTale!", "Also play Minicraft Deluxe!",
            "Also play Alecraft!", "Also play Hackcraft!", "Also play MiniCrate!", "Also play MiniCraft Mob Overload!",
            "Also play Minitale!, oh right :(",

            "Playing " + Game.BUILD + ", nice!", "Based in Minicraft+, nice!", "Updates always!, nice?",

            // Now with...
            "Now with better fishing!", "Now with better Weapons!", "Now with better tools!", "Now with better chests!",
            "Now with better dungeons!", "Now with better sounds!", "Air Wizard now with phases!",

            "Only on PlayMinicraft.com!", "Playminicraft.com is the bomb!", "@MinicraftPlus on Twitter",
            "MinicraftPlus on Youtube", "Join the Forums!", "The Wiki is weak! Help it!", "Great little community!",

            "Notch is Awesome!", "Dillyg10 is cool as Ice!", "Shylor is the man!", "Chris J is great with portals!",
            "AntVenom loves cows! Honest!", "The eye and Cake rain!", "ASCII", "32.872 lines of code!",

            "Nobody should read this! #404", "You should read Antidious Venomi!", "Oh Hi Mark", "Use the force!",
            "Keep calm!", "Get him, Steve!", "Forty-Two!", "A hostile paradise",

            // kill
            "Kill Creeper, get Gunpowder!", "Kill Cow, get Beef!", "Kill Zombie, get Cloth!", "Kill Slime, get Slime!",
            "Kill Slime, get Problems!", "Kill Skeleton, get Bones!", "Kill Skeleton, get Arrows!",
            "Kill Sheep, get Wool!", "Kill Goat, get Leather!", "Kill Pig, get Porkchop!",
            "Kill Chicken, get Feathers!", "Kill Guiman, get more Feathers!",

            // Mineral levels
            "Gold > Iron", "Gem > Gold",

            "Test == InDev!", "Story? yes!", "Mod on phase B-eta",

            "Axes: good against plants!", "Picks: good against rocks!", "Shovels: good against dirt!",
            "Swords: good against mobs!",

            // What's that?
            "Infinite terrain? What's that?", "Ceilings? What's is that?", "Redstone? What's that?",
            "Minecarts? What are those?", "Windows? I prefer Doors!", "2.5D FTW!", "Grab your friends!",
            "Sky?, better Aether!",

            // Not Included
            "Null not included", "Humans not included", "Herobine not included?", "Mouse not included!",
            "No spiders included!", "No Endermen included!", "3rd dimension not included!", "Orange box not included!",
            "Alpha version not included!", "Cthulhu sold separately!", "Skins not included!",

            // Included
            "Villagers included!", "Creepers included!", "Skeletons included!", "Knights included!", "Snakes included!",
            "Cows included!", "Sheep included!", "Chickens included!", "Goats included!", "Pigs included!",
            "Cthulhu included?", "Enchantments Now Included!", "Multiplayer Now Included!", "Carrots Now Included!",
            "Potatos Now Included!", "Boats Now Included!", "Maps Now Included!", "Books included!",
            "Sad music included!", "Big eye included!",
            // "Nether Now Included?",

            "Shhh!,secret dimension!", "A nice cup of coffee!",

            // Worlds
            "Bigger Worlds!", "World types!", "World themes!", "Mushroom Biome!", "Desert Biome!", "Forest Biome!",
            "Snow Biome!", "Better sky", "Slow world gen :(",

            // Ideas
            "Sugarcane is a Idea!", "Milk is an idea!", "Cakes is an idea!", "Coffee is another idea!",

            "Texture packs!",

            "Creeper, aw man", "So we back in the mine,", "pickaxe swinging from side to side", "In search of Gems!",
            "Life itself suspended by a thread", "saying ay-oh, that creeper's KO'd!",

            "Gimmie a bucket!", "Farming with water!", "Press \"R\"!", "Get the High-Score!", "I see a dreamer!",
            "Potions ftw!", "Beds ftw!",

            "Defeat the Air Wizard!", "Defeat the Eye queen!", "Defeat the Keeper!", "Defeat me...",

            "Conquer the Dungeon!", "One down, one to go...", "Loom + Wool = String!", "String + Wood = Rod!",
            "Sand + Gunpowder = TNT!",

            "Try Eyenglish!",

            "Sleep at Night!", "Farm at Day!",

            "Explanation Mark!", "!sdrawkcab si sihT", "This is forwards!", "Why is this blue?",
            "Green is a nice color!", "Red is my favorite color!", "Hmmm Orange!", "Yellow = Happy!",
            // "Y U NO BOAT!?",
            "Made with 10000% Vitamin Z!", "Too much DP!", "Punch the Moon!", "This is String qq!", "Why?",
            "You are null!", "hello down there!", "That guy is such a sly fox!", "Hola senor!", "Sonic Boom!",
            "Hakuna Matata!", "One truth prevails!", "Awesome!", "Sweet!", "Great!", "Cool!", "Radical!",
            "011011000110111101101100!", "001100010011000000110001!", "011010000110110101101101?", "...zzz...",

            // Tributes
            "Rick May, 1940 - 2020",

            "Something cool is coming ;)", };
}