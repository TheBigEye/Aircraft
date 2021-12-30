package minicraft.screen.tutorial;

import minicraft.core.io.InputHandler;
import minicraft.core.io.Localization;
import minicraft.gfx.Color;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.screen.Display;
import minicraft.screen.Menu;
import minicraft.screen.RelPos;
import minicraft.screen.entry.BlankEntry;
import minicraft.screen.entry.StringEntry;

public class MobsTutorial extends Display {

    private int step;

    public MobsTutorial() {
        super(true, new Menu.Builder(false, 6, RelPos.LEFT,
                new StringEntry("            " + Localization.getLocalized("Mobs?, What's that."), Color.ORANGE),
                new BlankEntry(),
                new StringEntry(Localization.getLocalized("Mobs are all the creatures that inhabit"), Color.WHITE),
                new StringEntry(Localization.getLocalized("the world, they can be peaceful like"), Color.WHITE),
                new StringEntry(Localization.getLocalized("Animals, Villagers, etc, and they can be"), Color.WHITE),
                new StringEntry(Localization.getLocalized("Hostile like Zombies, Skeletons, etc."), Color.WHITE),
                new BlankEntry(), new StringEntry(Localization.getLocalized("Peaceful mobs:"), Color.GREEN),
                new BlankEntry(), new BlankEntry(),
                new StringEntry(Localization.getLocalized("Hostile mobs:"), Color.RED), new BlankEntry(),
                new BlankEntry()).setTitle("Tutorial", Color.YELLOW).createMenu());
    }

    @Override
    public void tick(InputHandler input) {
        super.tick(input);

        step++;
        if (step >= 240) {
            step = 0;
        }
    }

    @Override
    public void render(Screen screen) {
        super.render(screen);

        int spriteIndex = (step / 32) % 4;

        // Hostile mobs
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                screen.render(65 + x * 8, 216 + y * 8, new Sprite.Px(x + 2 * (spriteIndex % 2), y, 0, 2)); // Slime
            }
        }

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                screen.render(85 + x * 8, 216 + y * 8, new Sprite.Px(x + 4 + 2 * (spriteIndex % 2), y, 0, 2)); // Creeper
            }
        }

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                screen.render(105 + x * 8, 216 + y * 8, new Sprite.Px(x + 8 + 2 * spriteIndex, y, 0, 2)); // Zombie
            }
        }

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                screen.render(125 + x * 8, 216 + y * 8, new Sprite.Px(x + 16 + 2 * spriteIndex, y, 0, 2)); // Skeleton
            }
        }

        // Passive Mobs
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                screen.render(65 + x * 8, 175 + y * 8, new Sprite.Px(x + 2 * spriteIndex, y + 30, 0, 2)); // Pig
            }
        }

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                screen.render(85 + x * 8, 175 + y * 8, new Sprite.Px(x + 2 * spriteIndex, y + 32, 0, 2)); // Chicken
            }
        }

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                screen.render(105 + x * 8, 175 + y * 8, new Sprite.Px(x + 2 * spriteIndex, y + 34, 0, 2)); // Goat
            }
        }

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                screen.render(125 + x * 8, 175 + y * 8, new Sprite.Px(x + 2 * spriteIndex, y + 26, 0, 2)); // Sheep
            }
        }

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                screen.render(145 + x * 8, 175 + y * 8, new Sprite.Px(x + 2 * spriteIndex, y + 24, 0, 2)); // Cow
            }
        }

        int h = 2;
        int w = 2;
        int xo = 145;
        int yo = 175;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                screen.render(xo + x * 8, yo + y * 8, new Sprite.Px(x + 2 * spriteIndex, y + 24, 0, 2)); // Cow
            }
        }

    }

    @Override
    public void onExit() {

    }
}
