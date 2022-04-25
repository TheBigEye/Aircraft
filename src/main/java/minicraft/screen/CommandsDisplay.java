package minicraft.screen;

import minicraft.core.Game;
import minicraft.core.Updater;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Localization;
import minicraft.core.io.Settings;
import minicraft.gfx.Point;
import minicraft.gfx.Screen;
import minicraft.gfx.SpriteSheet;
import minicraft.screen.entry.InputEntry;

public class CommandsDisplay extends Display {
    public static InputEntry command = new InputEntry("Command", "[a-zA-Z0-9 ]+", 36);
    
    public CommandsDisplay() {
        super(new Menu.Builder(false, 3, RelPos.LEFT, command)
        		.setTitle("")
        		.setTitlePos(RelPos.TOP_LEFT)
        		.setPositioning(new Point(SpriteSheet.boxWidth,  Screen.h - 14), RelPos.BOTTOM_RIGHT)
        		.createMenu()
        );
        command.userInput = "";
    }

    @Override
    public void tick(InputHandler input) {
        super.tick(input);
        if (input.getKey("exit").clicked) {
            Game.exitDisplay();
        }
        if (input.getKey("select").clicked) {
            String CommandStr = command.getUserInput().toLowerCase(Localization.getSelectedLocale());
            String[] CommandArguments = CommandStr.split(" ");
            switch (CommandArguments[0]) {
                case "gamemode":
                    switch (CommandArguments[1]) {
                        case "creative": Settings.set("mode", "creative"); break;
                        case "survival": Settings.set("mode", "survival"); break;
                        default:
                            Game.notifications.add("Unknown Gamemode. Try again!");
                            break;
                    }
                    break;
                case "time":
                    switch (CommandArguments[1]) {
                        case "morning": Updater.changeTimeOfDay(Updater.Time.Morning); break;
                        case "day": Updater.changeTimeOfDay(Updater.Time.Day); break;
                        case "evening": Updater.changeTimeOfDay(Updater.Time.Evening); break;
                        case "night": Updater.changeTimeOfDay(Updater.Time.Night); break;
                        default:
                            Game.notifications.add("Unknown Time. Try again!");
                            break;
                    }
                    break;
                default:
                    Game.notifications.add("Unknown Command. Try again!");
                    break;
            }
            Game.exitDisplay();
        }
    }

    @Override
    public void render(Screen screen) {
        super.render(screen);
    }
}