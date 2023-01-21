package minicraft.util;

import minicraft.core.Game;
import minicraft.core.Updater;
import minicraft.core.io.Settings;
import minicraft.entity.Entity;
import minicraft.entity.mob.Player;
import minicraft.level.Level;

public class Command {
	
	// Entities selectors
	private static final String ALL_PLAYERS = "@a";
	private static final String ALL_ENTITIES = "@e";
	private static final String NEAR_PLAYER = "@p";
	private static final String EXECUTING_ENTITY = "@s";
	
	
	public Entity[] getEntitiesBySelector(String targetSelector) {
	    Level currentLevel = Game.levels[Game.currentLevel]; // The command only works on the current level
	    Player currentPlayer = Game.player; // Current player instance
	    
	    Entity[] entities = new Entity[0];
	    
	    switch (targetSelector) {
	        case "@a": entities = currentLevel.getPlayers(); break; // all players
	        case "@e": entities = currentLevel.getEntities(); break; // all entities
	        case "@p":  entities = new Entity[]{currentPlayer}; break; // nearest player
	        case "@s": entities = new Entity[]{currentPlayer}; break; // executing entity
	            
	        default:
	            // parse target selector and return matching entities
	            break;
	    }
	    
	    return entities;
	}
	
	public static void gamemodeCommand(String[] arguments) {
        if (arguments.length < 2) {
            Game.notifications.add("- missing gamemode -");
            return;
        }
		
	    String mode = arguments[1];
	    if (mode.equals("creative") || mode.equals("c") || mode.equals("1")) {
	        Settings.set("mode", "creative");
	    } else if (mode.equals("survival") || mode.equals("s") || mode.equals("0")) {
	        Settings.set("mode", "survival");
	    } else {
	        Game.notifications.add("- unknown gamemode -");
	    }
	}
	
	public static void timeCommand(String[] arguments) {
		
	    if (arguments.length < 2) {
	        Game.notifications.add("- missing time action -");
	        return;
	    }
		
		String timeAction = arguments[1]; // add, set ...
		switch (timeAction) {
			case "set":
				
	            if (arguments.length < 3) {
	                Game.notifications.add("- missing time of day -");
	                return;
	            }
				
				String timeString = arguments[2]; // morning, night, etc
	            int timeValue = 0;
	            if (timeString.matches("\\d+")) {
	                try {
	                    timeValue = Integer.parseInt(timeString);
	                } catch (NumberFormatException exception) {
	                    Game.notifications.add("- Time value must be a numeric value -");
	                    return;
	                }
	                Updater.tickCount = timeValue;
	            } else {
					switch (timeString) {
						case "morning": Updater.changeTimeOfDay(Updater.Time.Morning); break;
						case "day": Updater.changeTimeOfDay(Updater.Time.Day); break;
						case "evening": Updater.changeTimeOfDay(Updater.Time.Evening); break;
						case "night": Updater.changeTimeOfDay(Updater.Time.Night); break;
						
						default: Game.notifications.add("- Unknown time status '"+ timeString + "' -"); break;
					} 
					break;
	            }
				
			case "add":
				
	            if (arguments.length < 3) {
	                Game.notifications.add("- missing time of day -");
	                return;
	            }
				
				int timeIndex = Integer.parseInt(arguments[2]); // 6000, 18000, 64000
				Updater.tickCount += timeIndex; break;
				
			default: Game.notifications.add("- Unknown time modifier'" + timeAction + "' -"); break;
		}
	}
	
	public static void killCommand(String[] arguments) {
		// Use "@s" as the default selector if no selector is specified
	    String selector = arguments.length > 1 ? arguments[1] : EXECUTING_ENTITY; 
	    
	    Level currentLevel = Game.levels[Game.currentLevel]; // The command only works on the current level
	    Player currentPlayer = Game.player; // Current player instance
	    
	    Entity executorEntity = currentPlayer; // The entity that executes the command
	    
	    Player[] players = currentLevel.getPlayers();
	    Entity[] entities = currentLevel.getEntities();

	    switch (selector) {
        	case ALL_PLAYERS: // Kills all the players on the level
	            if (players.length > 0) {
	                for (Player player : players) {
	                    player.die();
	                }
	                Game.notifications.add("Ouch! That looked like it hurt"); 
	            } 
	            break;
	            
	        case ALL_ENTITIES: // Kills all the entities on the level, also the player
	            if (entities.length > 0) {
	                for (Entity entity : entities) {
	                    entity.die();
	                }
	                Game.notifications.add("Ouch! That looked like it hurt"); 
	            } 
	            break;
	            
	        case NEAR_PLAYER: // Get the closest player on the level and kill them
	            currentPlayer.die();
	            Game.notifications.add("Ouch! That looked like it hurt"); 
	            break;
	    
	        case EXECUTING_ENTITY: // Kills the entity that executes the command
	        	executorEntity.die();
	            Game.notifications.add("Ouch! That looked like it hurt"); 
	            break;
	            
	        default:
	        	Game.notifications.add("- missing entity selector -"); 
	            break;
	    }
	}
}
