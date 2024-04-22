package minicraft.screen.tutorial;

import minicraft.core.io.InputHandler;
import minicraft.core.io.Localization;
import minicraft.graphic.Color;
import minicraft.graphic.Screen;
import minicraft.graphic.Sprite;
import minicraft.screen.Display;
import minicraft.screen.Menu;
import minicraft.screen.RelPos;
import minicraft.screen.entry.BlankEntry;
import minicraft.screen.entry.StringEntry;

public class MobsTutorial extends Display {

    private int step;

    protected MobsTutorial() {
        super(true, new Menu.Builder(false, 6, RelPos.LEFT,
            new StringEntry("          " + Localization.getLocalized("Meet the Mobs"), Color.ORANGE),
            new BlankEntry(),
            new StringEntry(Localization.getLocalized("Mobs are all the creatures that inhabit"), Color.WHITE),
            new StringEntry(Localization.getLocalized("the world, they can be peaceful like"), Color.WHITE),
            new StringEntry(Localization.getLocalized("Pigs, Cows, Sheeps (), etc, and they can be"), Color.WHITE),
            new StringEntry(Localization.getLocalized("Hostile like Zombies, Skeletons, Creepers."), Color.WHITE),
            
            new BlankEntry(), 
            new StringEntry(Localization.getLocalized("Peaceful mobs:"), Color.GREEN),
            new BlankEntry(), 
            
            new BlankEntry(),
            new StringEntry(Localization.getLocalized("Hostile mobs:"), Color.RED), 
            new BlankEntry(),
            
            new BlankEntry()).setTitle("Tutorial", Color.YELLOW).createMenu()
        );
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

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {   	
            	/// Hostile mobs
                screen.render(75 + x * 8, 216 + y * 8, new Sprite.Px(x + 2 * (spriteIndex % 2), y, 0, 2)); // Slime
                screen.render(95 + x * 8, 216 + y * 8, new Sprite.Px(x + 4 + 2 * (spriteIndex % 2), y, 0, 2)); // Creeper
                screen.render(115 + x * 8, 216 + y * 8, new Sprite.Px(x + 10 + 2 * spriteIndex, y, 0, 2)); // Zombie
                screen.render(135 + x * 8, 216 + y * 8, new Sprite.Px(x + 20 + 2 * spriteIndex, y, 0, 2)); // Skeleton
                
                /// Passive Mobs
                screen.render(75 + x * 8, 175 + y * 8, new Sprite.Px(x + 10 + 2 * spriteIndex, y + 38, 0, 2)); // Pig
                screen.render(95 + x * 8, 175 + y * 8, new Sprite.Px(x + 10 + 2 * spriteIndex, y + 40, 0, 2)); // Chicken
                screen.render(115 + x * 8, 175 + y * 8, new Sprite.Px(x + 10 + 2 * spriteIndex, y + 42, 0, 2)); // Goat
                screen.render(135 + x * 8, 175 + y * 8, new Sprite.Px(x + 2 * spriteIndex, y + 42, 0, 2)); // Sheep
                screen.render(155 + x * 8, 175 + y * 8, new Sprite.Px(x + 2 * spriteIndex, y + 40, 0, 2)); // Cow
            }
        }
    }
}
