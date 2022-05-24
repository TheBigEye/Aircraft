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
    public static InputEntry command = new InputEntry("Command", "[a-zA-Z0-9 ]+", 40, true);
    
    public CommandsDisplay() {
        super(new Menu.Builder(false, 3, RelPos.LEFT, command)
        		.setTitle("")
        		.setTitlePos(RelPos.TOP_LEFT)
        		.setPositioning(new Point(SpriteSheet.boxWidth,  Screen.h - 24), RelPos.BOTTOM_RIGHT)
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
	    		default:
	    			Game.notifications.add("Unknown Command.");
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