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
    public static InputEntry command = new InputEntry("Command", "[a-zA-Z0-9 ]+", 39, true);
    
    public CommandsDisplay() {
        super(new Menu.Builder(false, 3, RelPos.LEFT, command)
            .setTitle("")
            .setTitlePos(RelPos.TOP_LEFT)
            .setPositioning(new Point(SpriteSheet.boxWidth,  Screen.h - 20), RelPos.BOTTOM_RIGHT)
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
	    		case "help":
	    			Game.notifications.add("gamemode survival | creative");
	    			Game.notifications.add("time set day | morning | evening | night");
	    			Game.notifications.add("time add 1000");
	    			Game.notifications.add("say something");
	    			break;
	    		case "gamemode":
	    			switch (CommandArguments[1]) {
	    			case "creative": Settings.set("mode", "creative"); break;
	    			case "survival": Settings.set("mode", "survival"); break;
	    			case "c": Settings.set("mode", "creative"); break;
	    			case "s": Settings.set("mode", "survival"); break;
	    			default:
	    				Game.notifications.add("Unknown Gamemode.");
	    				break;
	    			}
	    			break;
	    		case "weather":
	    			switch (CommandArguments[1]) {
	    			case "rain": Game.player.isRaining = true; break;
	    			case "clear": Game.player.isRaining = false; break;
	    			default:
	    				Game.notifications.add("Unknown weather status.");
	    				break;
	    			}
	    			break;
	    		case "time":
	    			switch (CommandArguments[1]) {
	    			case "set":
	    				switch (CommandArguments[2]) {
	    				case "morning": Updater.changeTimeOfDay(Updater.Time.Morning); break;
	    				case "day": Updater.changeTimeOfDay(Updater.Time.Day); break;
	    				case "evening": Updater.changeTimeOfDay(Updater.Time.Evening); break;
	    				case "night": Updater.changeTimeOfDay(Updater.Time.Night); break;
	    				default:
	    					Game.notifications.add("Unknown time status.");
	    					break;
	    				}
	    				break;
	    			case "add": 
	    				Updater.tickCount += Integer.parseInt(CommandArguments[2]); break;
	    			default:
	    				Game.notifications.add("Unknown time modifier.");
	    				break;
	    			}
	    		case "say":       
	    			String sayStr = command.getUserInput().toLowerCase(Localization.getSelectedLocale()).replace("say ", "");
	    			Game.notifications.add(sayStr);
	    			break;
                case "kill":       
	    			Game.player.die();
	    			Game.notifications.add("ooof!");
	    			break;
	    		default:
	    			Game.notifications.add("Unknown Command.");
	    			break;
    		}
    		Game.exitDisplay();
    	}
    }

    @Override
    public void render(Screen screen) {
        int x = 12; // box x pos
        int y = (Screen.h - 20); // box y pos
        int w = command.userInput.length() + 12; // length of message in characters.
        int h = 1; // box height

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
        
        // render the entryes in the top
        super.render(screen);
    }
}