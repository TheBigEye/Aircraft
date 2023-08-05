package minicraft.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import minicraft.core.Game;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Sound;
import minicraft.graphic.Color;
import minicraft.graphic.Font;
import minicraft.graphic.Point;
import minicraft.graphic.Screen;
import minicraft.graphic.SpriteSheet;
import minicraft.screen.entry.InputEntry;
import minicraft.util.Command;

public class CommandsDisplay extends Display {
	
    public static InputEntry command = new InputEntry("", ".*", 43, false);
    
    public CommandsDisplay() {
        super(new Menu.Builder(false, 3, RelPos.LEFT, command)
            .setTitle("")
            .setTitlePos(RelPos.TOP_LEFT)
            .setPositioning(new Point(SpriteSheet.boxWidth + 40, Screen.h - 16), RelPos.BOTTOM_RIGHT)
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
    		String commandString = command.getUserInput();
    		String[] commandArguments = commandString.split(" ");
    		boolean recognizedCommand = true; // Assume the command is recognized unless proven otherwise
    		
    		if (commandArguments[0].startsWith("/", 0)) {
    			switch (commandArguments[0]) {
    				case "/gamemode": Command.gamemodeCommand(commandArguments); break;
    				case "/time": Command.timeCommand(commandArguments); break;
    				case "/kill": Command.killCommand(commandArguments); break;
    			
    				case "/say":       
    					String messageString = command.getUserInput().replace("/say ", "");
    					Game.player.sendMessage(messageString);
    					break;
    				
    				default:
    					Game.notifications.add("-- Unknown Command --"); 
    					recognizedCommand = false; // The command is not recognized
    					break;
    			}
    			
        		// Play the sound effect if the command was recognized
        		if (recognizedCommand) {
        			Sound.Menu_loaded.playOnDisplay();
        		}
 
			} else {
				String messageString = command.getUserInput();
				Game.player.sendMessage(messageString);
				Sound.Menu_back.playOnDisplay();
			}
    		
    		command.clearUserInput();
    	}
    }

    @Override
    public void render(Screen screen) {
        Font.drawBox(screen, 8, (Screen.h - 16) - 66, 52, 8);
        Font.drawBox(screen, 8, (Screen.h - 16), 52, 1);
        Font.draw("[Amy]", screen, 8, Screen.h - 16, Color.GREEN);
        
		for (int j = 0; j < 4; j++) {
			screen.render(200 + j * 8, Screen.h - 90, 3 + 21 * 32, 0, 3);
		}
        Font.drawCentered("Chat", screen, Screen.h - 90, Color.YELLOW);
        
        // Render the messages
        List<String> messages = new ArrayList<>();
        for (String msg : Game.player.chatMessages) {
            if (msg.length() > 10) {
                String[] lines = msg.split("(?<=\\G.{46})");
                messages.addAll(Arrays.asList(lines));
            } else {
                messages.add(msg);
            }
        }
        
        int lineNumber = 0;
        for (int j = Math.max(0, messages.size() - 7); j < messages.size(); j++) {
        	Font.draw("<Amy>", screen, 8, (lineNumber * 8) + Screen.h - 80, Color.DARK_GREEN);
            Font.draw(messages.get(j), screen, 6 * 8 + 4, (lineNumber * 8) + Screen.h - 80, Color.GRAY);
            lineNumber++;
        }
        
        super.render(screen);
    }
}