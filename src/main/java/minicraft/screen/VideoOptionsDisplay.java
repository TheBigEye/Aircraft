package minicraft.screen;

import minicraft.core.Game;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Settings;
import minicraft.gfx.Color;
import minicraft.gfx.Screen;
import minicraft.saveload.Save;
import minicraft.screen.entry.BlankEntry;
import minicraft.screen.entry.StringEntry;

public class VideoOptionsDisplay extends Display {
	
    int originalFPS = (int) Settings.get("fps");
    boolean originalVsync = (boolean) Settings.get("vsync");

	public VideoOptionsDisplay() {
        super(true);

        Menu optionsMenu = new Menu.Builder(true, 6, RelPos.LEFT,
            new BlankEntry(),
            Settings.getEntry("fps"),
            Settings.getEntry("vsync"),
            Settings.getEntry("particles"),
            Settings.getEntry("shadows"),
            new BlankEntry(),
            Settings.getEntry("bossbar")
        )
        .setTitle("Video options")
        .createMenu();

        Menu popupMenu = new Menu.Builder(true, 4, RelPos.CENTER)
            .setShouldRender(false)
            .setSelectable(false)
            .setEntries(StringEntry.useLines(Color.RED,"A restart will be required, you can continue playing anyway", "enter to confirm", "escape to cancel"))
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
        
        if (originalVsync == true) {
            Settings.set("fps", Settings.getRefreshRate());
        } else if (originalFPS != (int) Settings.get("fps")) {
            Settings.set("vsync", false);
        }

        // If exit key is pressed, then display the popup menu if changes requiring a restart have been made
        if (input.getKey("exit").clicked && originalFPS != (int) Settings.get("fps") || input.getKey("exit").clicked && originalVsync != (boolean) Settings.get("vsync")) {
            menus[1].shouldRender = true;
            return;
        }

        super.tick(input);
    }

    @Override
    public void onExit() {
        Game.MAX_FPS = (int) Settings.get("fps");
        new Save();
    }
}
