package minicraft.util;

import minicraft.core.Game;
import minicraft.core.Updater;
import minicraft.core.io.Settings;
import minicraft.core.io.Sound;
import minicraft.entity.Entity;
import minicraft.entity.mob.Player;
import minicraft.level.Level;
import minicraft.level.tile.Tiles;

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
	        case ALL_PLAYERS: entities = currentLevel.getPlayers(); break; // all players
	        case ALL_ENTITIES: entities = currentLevel.getEntities(); break; // all entities
	        case NEAR_PLAYER:  entities = new Entity[]{currentPlayer}; break; // nearest player
	        case EXECUTING_ENTITY: entities = new Entity[]{currentPlayer}; break; // executing entity
	            
	        default:
	            // parse target selector and return matching entities
	            break;
	    }
	    
	    return entities;
	}
	
	public static void gamemodeCommand(String[] arguments) {
        if (arguments.length < 2) {
            Game.player.sendMessage("{?} - Missing gamemode [creative, survival]");
            return;
        }
		
	    String mode = arguments[1];
	    if (mode.equals("creative") || mode.equals("c") || mode.equals("1")) {
	        Settings.set("mode", "creative");
	    } else if (mode.equals("survival") || mode.equals("s") || mode.equals("0")) {
	        Settings.set("mode", "survival");
	    } else {
	        Game.player.sendMessage("{?} - Unknown gamemode");
	    }
	}
	
	public static void timeCommand(String[] arguments) {
		
	    if (arguments.length < 2) {
	    	Game.player.sendMessage("{?} - Missing time action [set, add]");
	        return;
	    }
		
		String timeAction = arguments[1]; // add, set ...
		switch (timeAction) {
			case "set":
				
	            if (arguments.length < 3) {
	            	Game.player.sendMessage("{?} - Missing time of day [day, night, 12000]");
	                return;
	            }
				
				String timeString = arguments[2]; // morning, night, etc
	            int timeValue = 0;
	            
	            if (timeString.matches("\\d+")) {
	                try {
	                    timeValue = Integer.parseInt(timeString);
	                } catch (NumberFormatException exception) {
	                    Game.player.sendMessage("{?} - Time value must be a numeric value");
	                    return;
	                }
	                Updater.setTime(timeValue);
	            } else {
					switch (timeString) {
						case "morning": Updater.changeTimeOfDay(Updater.Time.Morning); break;
						case "day": Updater.changeTimeOfDay(Updater.Time.Day); break;
						case "evening": Updater.changeTimeOfDay(Updater.Time.Evening); break;
						case "night": Updater.changeTimeOfDay(Updater.Time.Night); break;
						
						default:
							Game.player.sendMessage("{?} - Unknown time status '"+ timeString + "'");
							break;
					} 
					break;
	            }
				
			case "add":
				
	            if (arguments.length < 3) {
	                Game.player.sendMessage("{?} - Missing time of day [1000, 6000, 32000]");
	                return;
	            }

	            int timeIndex = 0;
	            
	            try {
					timeIndex = Integer.parseInt(arguments[2]); // 6000, 18000, 64000
	            } catch (NumberFormatException exception) {	
	            	Game.player.sendMessage("{?} - Time value must be a numeric value");
	            }
	            
				Updater.tickCount += timeIndex; 
				
				break;
				
			default:
				Game.player.sendMessage("{?} - Unknown time action '" + timeAction + "'");
				break;
		}
	}
	
	
	public static void playSoundCommand(String[] arguments) {
		
	    if (arguments.length < 2) {
	    	Game.player.sendMessage("{?} - Missing sound name");
	        return;
	    }
		
		String soundName = arguments[1];
		Sound.play(soundName);
	}
	
	public static void setTileCommand(String[] arguments) {
		
	    if (arguments.length < 2) {
	    	Game.player.sendMessage("{?} - Missing tile name or ID [Hard_Rock, 22]");
	        return;
	    }
	    
	    Level currentLevel = Game.levels[Game.currentLevel]; // The command only works on the current level
	    Player currentPlayer = Game.player; // Current player instance
		
		String tileString = arguments[1];
        int tileValue = 0;
        
        if (tileString.matches("\\d+")) {
            try {
                tileValue = Integer.parseInt(tileString);
            } catch (NumberFormatException exception) {
                Game.player.sendMessage("{?} - Tile id must be a numeric value");
                return;
            }
            currentLevel.setTile(currentPlayer.x >> 4, currentPlayer.y >> 4, Tiles.get(tileValue));
        } else {
        	currentLevel.setTile(currentPlayer.x >> 4, currentPlayer.y >> 4, Tiles.get(tileString.replace("_", " ")));
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
	                Game.player.sendMessage("{!} - All the players has died");
	            } 
	            break;
	            
	        case ALL_ENTITIES: // Kills all the entities on the level, also the player
	            if (entities.length > 0) {
	                for (Entity entity : entities) {
	                    entity.die();
	                }
	                Game.player.sendMessage("{!} - All the entities has died");
	            } 
	            break;
	            
	        case NEAR_PLAYER: // Get the closest player on the level and kill them
	            currentPlayer.die();
	            Game.player.sendMessage("{!} - A player has died");
	            break;
	    
	        case EXECUTING_ENTITY: // Kills the entity that executes the command
	        	executorEntity.die();
	            Game.player.sendMessage("{!} - Player has commit suicide");
	            break;
	            
	        default:
	        	Game.player.sendMessage("{!} - Unknown entity selector -"); 
	            break;
	    }
	}
}
