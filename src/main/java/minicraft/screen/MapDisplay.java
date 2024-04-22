package minicraft.screen;

import java.util.List;

import minicraft.core.Game;
import minicraft.core.io.InputHandler;
import minicraft.entity.Entity;
import minicraft.entity.mob.AirWizard;
import minicraft.entity.mob.EyeQueen;
import minicraft.graphic.Color;
import minicraft.graphic.Rectangle;
import minicraft.graphic.Screen;
import minicraft.util.MapData;

public class MapDisplay extends Display {
	
	private static final int ENTITIES_RADIUS = 360 * 2;

	private static final int PLAYER_MARKER_SPRITE = 6 + 20 * 32;
	private static final int PLAYER_MARKER_COLOR = Color.get(-1, 255, 0, 0);
	
	private static final int AIRWIZARD_MARKER_SPRITE = 6 + 21 * 32;
	private static final int AIRWIZARD_MARKER_COLOR = Color.get(-1, 255, 0, 0);
	
	private static final int EYEQUEEN_MARKER_SPRITE = 6 + 22 * 32;
	private static final int EYEQUEEN_MARKER_COLOR = Color.get(-1, 255, 0, 0);
	
	private static final int UNEXPLORED_COLOR = Color.get(-1, 8, 8, 8);

    private int mapRadius = 10;
    private int worldSize = Game.levels[Game.currentLevel].w;   // Adjust this to match the world size
    
    private int tickTime = 0;
	
	public MapDisplay() {


		Menu.Builder builder = new Menu.Builder(true, 0, RelPos.CENTER);
		//builder.setTitle(Localization.getLocalized("Map"));
		builder.setSize(144, 144);
		builder.setFrame(443, 1, 443);
		builder.setBackground(22);

		menus = new Menu[1];
		menus[0] = builder.createMenu();

		menus[0].shouldRender = true;
	}

	@Override
	public void render(Screen screen) {
		Menu menu = menus[0];
		menu.render(screen);
		
		// Player Tile Coordinates
		int ptx = Game.player.x >> 4;
		int pty = Game.player.y >> 4;
		
		// Get a list of closest entities in player range
		List<Entity> entitiesInRange = Game.levels[Game.currentLevel].getEntitiesInRect(
			new Rectangle(Game.player.x, Game.player.y, ENTITIES_RADIUS, ENTITIES_RADIUS, Rectangle.CENTER_DIMS)
	    );
		
		/*
		 * This is for worlds large than 128x128 due that map can just display 128x128
		 * pixels thus we can fix position to get tiles correctly
		 *
		 * To explain this, we divide by 128 to get an integer about how many tiles we
		 * have skipped, once done that, we can multiply 128 again to shift those
		 * coordinates, keeping in mind that, smx != ptx and smy != pty, due that those
		 * operations didn't use decimal part
		 *
		 * smx and smy mean Shift Map X and Shift Map Y
		 */

		int smx = (ptx >> 7) << 7;
		int smy = (pty >> 7) << 7;

		Rectangle menuBounds = menu.getBounds();

        for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 128; x++) {
                int worldX = x + smx;
                int worldY = y + smy;
                if (Game.levels[Game.currentLevel].explored[worldY][worldX]) {
                    MapData mapData = MapData.getById(Game.levels[Game.currentLevel].getTile(worldX, worldY).id);
                    int color = mapData != null ? mapData.color : 0;
                    screen.setPixel((x + menuBounds.getLeft()) + 8, (y + menuBounds.getTop()) + 8, color);
                } else {
                    // Render fog of war for unexplored areas
                    screen.setPixel((x + menuBounds.getLeft()) + 8, (y + menuBounds.getTop()) + 8, UNEXPLORED_COLOR);
                }
                
                if (Game.isMode("Creative")) {
                    MapData mapData = MapData.getById(Game.levels[Game.currentLevel].getTile(worldX, worldY).id);
                    int color = mapData != null ? mapData.color : 0;
                    screen.setPixel((x + menuBounds.getLeft()) + 8, (y + menuBounds.getTop()) + 8, color);
                }
            }
        }
		
		/// MAP MARKERS :D

		// Render the marker for the player
		screen.render((ptx % 128) + menuBounds.getLeft() + 4, (pty % 128) + menuBounds.getTop() + 4, MapDisplay.PLAYER_MARKER_SPRITE, MapDisplay.PLAYER_MARKER_COLOR, 3);
		
		// Render the marker for the air wizard
		if (AirWizard.active) {
			AirWizard aw = AirWizard.entity;
			for (Entity entity: entitiesInRange) { 
        		if (entity instanceof AirWizard) {
					int awtx = aw.x >> 4;
					int awty = aw.y >> 4;
					screen.render(awtx % 128 + menuBounds.getLeft() + 4, awty % 128 + menuBounds.getTop() + 4, MapDisplay.AIRWIZARD_MARKER_SPRITE, MapDisplay.AIRWIZARD_MARKER_COLOR, 3);
        		}
			}
		}
		
		// Render the marker for the eye queen
		if (EyeQueen.active) {
			EyeQueen eq = EyeQueen.entity;
			for (Entity entity: entitiesInRange) { 
        		if (entity instanceof EyeQueen) {
					int eqtx = eq.x >> 4;
					int eqty = eq.y >> 4;
					screen.render(eqtx % 128 + menuBounds.getLeft() + 4, eqty % 128 + menuBounds.getTop() + 4, MapDisplay.EYEQUEEN_MARKER_SPRITE, MapDisplay.EYEQUEEN_MARKER_COLOR, 3);
        		}
			}
		}
	}

    @Override
    public void tick(InputHandler input) {
    	tickTime++;
    	
        if (input.getKey("menu").clicked || input.getKey("attack").clicked || input.getKey("exit").clicked) {
            Game.exitDisplay();
        }
        
    	if (tickTime % 5 == 0) {
    	
	        // Update the explored array based on the player's position
	        int ptx = Game.player.x >> 4;
	        int pty = Game.player.y >> 4;
	        int visibleRadiusSquared = (mapRadius - 1) * (mapRadius - 1); // Square of the inner radius (visible area)
	        int ditherRadiusSquared = mapRadius * mapRadius; // Square of the outer radius (dithered area)
	
	        for (int y = -mapRadius; y <= mapRadius; y++) {
	            for (int x = -mapRadius; x <= mapRadius; x++) {
	                int worldX = (ptx + x) % worldSize;
	                int worldY = (pty + y) % worldSize;
	
	                if (worldX < 0) worldX += worldSize;
	                if (worldY < 0) worldY += worldSize;
	
	                // Calculate the squared distance from the player's position to the current tile
	                int dx = ptx - worldX;
	                int dy = pty - worldY;
	                int distanceSquared = dx * dx + dy * dy;
	
	                // Check if the tile is within the circular exploration area (visible area)
	                if (distanceSquared <= visibleRadiusSquared) {
	                    Game.levels[Game.currentLevel].explored[worldY][worldX] = true;
	                } else if (distanceSquared <= ditherRadiusSquared) {
	                    // Apply a dither pattern for the dithered area
	                    int ditherValue = (distanceSquared - visibleRadiusSquared) * 255 / (ditherRadiusSquared - visibleRadiusSquared - 1);
	                    if (Math.random() * 255 < ditherValue) {
	                        Game.levels[Game.currentLevel].explored[worldY][worldX] = true;
	                    }
	                }
	            }
	        }
    	}
    }
}
