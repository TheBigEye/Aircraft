package minicraft.screen.tutorial;

import minicraft.core.Game;
import minicraft.core.io.Localization;
import minicraft.gfx.Color;
import minicraft.gfx.Font;
import minicraft.gfx.Screen;
import minicraft.screen.Display;
import minicraft.screen.Menu;
import minicraft.screen.RelPos;
import minicraft.screen.entry.BlankEntry;
import minicraft.screen.entry.StringEntry;

public class ControlsTutorial extends Display {

    public ControlsTutorial() {
        super(true,
                new Menu.Builder(false, 6, RelPos.LEFT,
                        new StringEntry("       " + Localization.getLocalized("Moving the character."), Color.ORANGE),
                        new BlankEntry(),
                        new StringEntry(Localization.getLocalized("You can move your character through"), Color.WHITE),
                        new StringEntry(Localization.getLocalized("the keyboard, unfortunately"), Color.WHITE),
                        new StringEntry(Localization.getLocalized("there is no mouse :("), Color.WHITE),
                        new BlankEntry(), new StringEntry(Localization.getLocalized("You can move with:"), Color.WHITE),
                        new BlankEntry(), new BlankEntry(),
                        new StringEntry(Localization.getLocalized("You can also change the controls to"), Color.WHITE),
                        new StringEntry(Localization.getLocalized("your liking in:"), Color.WHITE), new BlankEntry(),
                        new BlankEntry()).setTitle("Tutorial", Color.YELLOW).createMenu());
    }

    @Override
    public void render(Screen screen) {
        super.render(screen);

        // Font.drawCentered(Settings.getEntry("mode")+"", screen, Screen.h - 190,
        // Color.GRAY);
        Font.drawCentered(
                Game.input.getMapping("MOVE-DOWN") + ", " + Game.input.getMapping("MOVE-UP") + ", "
                        + Game.input.getMapping("MOVE-LEFT") + ", " + Game.input.getMapping("MOVE-RIGHT"),
                screen, Screen.h - 128, Color.GRAY); // Controls
        Font.drawCentered(Localization.getLocalized("Options > Change Key bindings"), screen, Screen.h - 70,
                Color.GRAY); // Option location
    }
}
