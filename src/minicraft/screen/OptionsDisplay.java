package minicraft.screen;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import minicraft.core.Game;
import minicraft.core.io.Localization;
import minicraft.core.io.Settings;
import minicraft.saveload.Save;
import minicraft.screen.entry.BlankEntry;
import minicraft.screen.entry.SelectEntry;

public class OptionsDisplay extends Display {

    public OptionsDisplay() {
        super(false,
                new Menu.Builder(true, 6, RelPos.LEFT, new BlankEntry(), Settings.getEntry("diff"),
                        Settings.getEntry("fps"), Settings.getEntry("sound"), Settings.getEntry("ambient"),
                        Settings.getEntry("autosave"), Settings.getEntry("skinon"),
                        new SelectEntry("Change Key Bindings", () -> Game.setMenu(new KeyInputDisplay())),
                        Settings.getEntry("language"),
                        new SelectEntry("Texture packs", () -> Game.setMenu(new TexturePackDisplay())),
                        new BlankEntry(), new SelectEntry("Open Game Folder", () -> {
                            try {
                                Desktop.getDesktop().open(new File(Game.gameDir));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        })

                ).setTitle("Options").createMenu());
    }

    @Override
    public void onExit() {
        Localization.changeLanguage((String) Settings.get("language"));
        new Save();
        Game.MAX_FPS = (int) Settings.get("fps");
    }
}
