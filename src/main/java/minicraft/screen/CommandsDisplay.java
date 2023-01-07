package minicraft.screen;

import java.util.ArrayList;
import java.util.List;

import minicraft.core.Game;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Localization;
import minicraft.core.io.Sound;
import minicraft.gfx.Color;
import minicraft.gfx.Font;
import minicraft.gfx.Point;
import minicraft.gfx.Screen;
import minicraft.gfx.SpriteSheet;
import minicraft.screen.entry.InputEntry;
import minicraft.util.Command;

public class CommandsDisplay extends Display {
	
	private static final int CHAT_BACKGOUND_COLOR = Color.BLACK;
	private static final int CHAT_FOREGOUND_COLOR = Color.GRAY;
	private static final float CHAT_OPACITY = 0.6F;
	
    public static InputEntry command = new InputEntry("", ".*", 48, false);
    
    public CommandsDisplay() {
        super(new Menu.Builder(false, 3, RelPos.LEFT, command)
            .setTitle("")
            .setTitlePos(RelPos.TOP_LEFT)
            .setPositioning(new Point(SpriteSheet.boxWidth,  Screen.h - 12), RelPos.BOTTOM_RIGHT)
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
    		String commandString = command.getUserInput().toLowerCase(Localization.getSelectedLocale());
    		String[] commandArguments = commandString.split(" ");
    		boolean recognizedCommand = true; // Assume the command is recognized unless proven otherwise
    		
    		if (commandArguments[0].startsWith("/", 0)) {
    			switch (commandArguments[0]) {
    				case "/gamemode": Command.gamemodeCommand(commandArguments); break;
    				case "/time": 	 Command.timeCommand(commandArguments); 	break;
    				case "/kill": 	 Command.killCommand(commandArguments); 	break;
    			
    				case "/say":       
    					String messageString = command.getUserInput().toLowerCase(Localization.getSelectedLocale()).replace("/say ", "");
    					Game.player.sendMessage(messageString);
    					break;
    				
    				default:
    					Game.notifications.add("-- Unknown Command --"); 
    					recognizedCommand = false; // The command is not recognized
    					break;
    			}
			} else {
				String messageString = command.getUserInput().toLowerCase(Localization.getSelectedLocale());
				Game.player.sendMessage("<Amy> " + messageString);
			}
    		
    		command.clearUserInput();
    		
    		// Play the sound effect if the command was recognized
    		if (recognizedCommand) {
    			Sound.Menu_loaded.playOnGui();
    		}
    	}
    }

    @Override
    public void render(Screen screen) {
    	int CHAT_WIDTH = 52;
    	int CHAT_HEIGHT = 8;
    	
    	int CHAT_INPUT_WIDTH = 52;
    	int CHAT_INPUT_HEIGHT = 1;
    	
        int x = 8; // box x pos
        int y = (Screen.h - 12); // box y pos
        
        int w = 52; // length of message in characters.
        int h = 1; // box height
        
        // RENDER THE CHAT BACKGROUND
        for (int xb = 0; xb < CHAT_WIDTH; xb++) {
        	for (int yb = 0; yb < CHAT_HEIGHT; yb++) {
        		screen.renderColor(x + xb * 8, y - yb * 8 - 14, 8, 8, CHAT_BACKGOUND_COLOR, CHAT_OPACITY);
        	}
        }
        
        // RENDER THE MESSAGES
        List<String> msgs = new ArrayList<String>();
        for (int i = 0; i < Game.player.chatMessages.size() && i <= Game.player.chatMessages.size() - 1; i++) {
            String msg = Game.player.chatMessages.get(i);
            String chopped = "";
            if (msg.length() * 8 > 400) {
              int il = 0;
              for (int k = 0; k < msg.length(); k++) {
                if (k * 8 > 400 * (il + 1)) {
                  chopped = String.valueOf(chopped) + "&" + msg.charAt(k);
                  il++;
                } else {
                  chopped = String.valueOf(chopped) + msg.charAt(k);
                } 
              } 
            } 
            if ((chopped.split("&")).length > 1) {
              for (int k = 0; k < (chopped.split("&")).length; k++)
                msgs.add(chopped.split("&")[k]); 
            } else {
              msgs.add(msg);
            } 
          } 
          int line = 0;
          for (int j = (msgs.size() > 7) ? (msgs.size() - 7) : 0; j < msgs.size(); j++) {
            if (j > msgs.size())
              break; 
            Font.drawTransparentBackground(msgs.get(j), screen, 12, line * 8 + 211, CHAT_FOREGOUND_COLOR);
            line++;
          } 
        
        // RENDER THE INPUT ENTRY BACKGROUND
        /*for (int xb = 0; xb < w; xb++) {
            screen.renderColor(x + xb * 8, y - 2, 8, 11, CHAT_BACKGOUND_COLOR, CHAT_OPACITY);
            
        }*/
        
        for (int xb = 0; xb < CHAT_INPUT_WIDTH; xb++) {
        	for (int yb = 0; yb < CHAT_INPUT_HEIGHT; yb++) {
        		screen.renderColor(x + xb * 8, y - yb * 8 - 2, 8, 8 + 3, CHAT_BACKGOUND_COLOR, CHAT_OPACITY);
        	}
        }
        
        // render the entryes in the top
        super.render(screen);
        
    }
}