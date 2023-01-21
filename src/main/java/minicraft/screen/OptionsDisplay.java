package minicraft.screen;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.tinylog.Logger;

import minicraft.core.Game;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Localization;
import minicraft.core.io.Settings;
import minicraft.gfx.Color;
import minicraft.gfx.Screen;
import minicraft.saveload.Save;
import minicraft.screen.entry.BlankEntry;
import minicraft.screen.entry.SelectEntry;
import minicraft.screen.entry.StringEntry;

public class OptionsDisplay extends Display {
	
    boolean originalSound = Settings.getBoolean("sound");

	public OptionsDisplay() {
        super(true);

        Menu optionsMenu = new Menu.Builder(true, 6, RelPos.LEFT,
            new BlankEntry(),
            Settings.getEntry("diff"),
            Settings.getEntry("sound"),
            Settings.getEntry("ambient"),
            Settings.getEntry("autosave"),
            Settings.getEntry("skinon"),
            Settings.getEntry("language"),
            new SelectEntry("Video options", () -> Game.setDisplay(new VideoOptionsDisplay())),
            new SelectEntry("Change Key Bindings", () -> Game.setDisplay(new KeyInputDisplay())),
            new SelectEntry("Texture packs", () -> Game.setDisplay(new TexturePackDisplay())),
            new BlankEntry(),
            new SelectEntry("Open Game Folder", () -> {
                try {
                    Desktop.getDesktop().open(new File(Game.gameDir));
                } catch (IOException exception) {
                    Logger.error(exception, "Could not find the program requested for this action");
                    exception.printStackTrace();
                }
            })
        )
        .setTitle("Options")
        .createMenu();

        Menu popupMenu = new Menu.Builder(true, 4, RelPos.CENTER)
            .setShouldRender(false)
            .setSelectable(false)
            .setEntries(StringEntry.useLines(Color.RED, "A restart will be required, you can continue playing anyway", "enter to confirm", "escape to cancel"))
            .setTitle("Confirm Action")
            .createMenu();

        menus = new Menu[]{
            optionsMenu,
            popupMenu
        };
    }

    @Override
    public void render(Screen screen) {
        super.render(screen);

        // Forcefully render the popup menu above everything else
        if (menus[1].shouldRender) {
            menus[1].render(screen);
        }
    }

    @Override
    public void tick(InputHandler input) {
        if (menus[1].shouldRender) {
            if (input.getKey("enter").clicked) {
                menus[1].shouldRender = false;
                Game.exitDisplay();
            } else if (input.getKey("exit").clicked) {
                menus[1].shouldRender = false;
            }
            return;
        }

        // If exit key is pressed, then display the popup menu if changes requiring a restart have been made
        if (input.getKey("exit").clicked && originalSound != Settings.getBoolean("sound")) {
            menus[1].shouldRender = true;
            return;
        }

        super.tick(input);
    }

    @Override
    public void onExit() {
        Localization.changeLanguage((String)Settings.get("language"));
        new Save();
    }
}