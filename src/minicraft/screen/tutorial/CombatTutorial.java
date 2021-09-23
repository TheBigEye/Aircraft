package minicraft.screen.tutorial;

import minicraft.core.Game;
import minicraft.core.io.Localization;
import minicraft.gfx.Color;
import minicraft.gfx.Font;
import minicraft.gfx.Screen;
import minicraft.gfx.Sprite;
import minicraft.screen.Display;
import minicraft.screen.Menu;
import minicraft.screen.RelPos;
import minicraft.screen.entry.BlankEntry;
import minicraft.screen.entry.StringEntry;

public class CombatTutorial extends Display {
   
	
	public CombatTutorial() {
		super(true, new Menu.Builder(false, 6, RelPos.LEFT,
				new StringEntry("                "+ Localization.getLocalized("How to Attack."), Color.ORANGE),
				new BlankEntry(),
				new StringEntry(Localization.getLocalized("You can attack with:"), Color.WHITE),
				new BlankEntry(),
				new BlankEntry(),
				new StringEntry(Localization.getLocalized("When killing some mobs, they drop loot or"), Color.WHITE),
				new StringEntry(Localization.getLocalized("materials for the crafting of other items:"), Color.WHITE),
				new BlankEntry(),
				new StringEntry("         =  , ,", Color.GRAY),
				new BlankEntry(),
				new StringEntry(Localization.getLocalized("You can also hit Tiles, like trees or rocks"), Color.WHITE),
				new StringEntry(Localization.getLocalized("to get resources and modify your environment:"), Color.WHITE),
				new BlankEntry(),
				new StringEntry("         =  , ,", Color.GRAY),
				new BlankEntry()			
			)
			.setTitle("Tutorial", Color.YELLOW)
			.createMenu()
		);
	}

	@Override
	public void render(Screen screen) {
		super.render(screen);	
		
		// player attacking a zombie -------------------------------------------------------------------------------
		
		Font.drawCentered(Game.input.getMapping("ATTACK"), screen, Screen.h - 190, Color.GRAY); // Attack controls
              
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                screen.render(65 + x * 8, 155 + y * 8, new Sprite.Px(x + 4 , y + 16, 0, 2)); // Player sprite
            }
        } 

        for (int y = 0; y < 1; y++) {
            for (int x = 0; x < 1; x++) {
                screen.render(79 + x * 8, 155 + y * 8, new Sprite.Px(x + 4 , y + 2, 0, 3)); // Player hit sprite (half-up)
            }
        }
      
        for (int y = 0; y < 1; y++) {
            for (int x = 0; x < 1; x++) {
                screen.render(79 + x * 8, 163 + y * 8, new Sprite.Px(x + 4 , y + 2, 2, 3)); // Player hit sprite (half-down)
            }
        }
        
        for (int y = 0; y < 1; y++) {
            for (int x = 0; x < 1; x++) {
                screen.render(79 + x * 8, 160 + y * 8, new Sprite.Px(x + 2 , y + 17, 0, 0)); // Gem sword 
            }
        }
        
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                screen.render(88 + x * 8, 155 + y * 8, new Sprite.Px(x + 13 , y, 1, 2)); // Zombie sprite (F xd)
            }
        }
        
        
        // Zombie loot  
        for (int y = 0; y < 1; y++) {
            for (int x = 0; x < 1; x++) {
                screen.render(124 + x * 8, 161 + y * 8, new Sprite.Px(x + 9 , y, 0, 0)); // Cloth
            }
        }
        
        for (int y = 0; y < 1; y++) {
            for (int x = 0; x < 1; x++) {
                screen.render(140 + x * 8, 161 + y * 8, new Sprite.Px(x + 32 , y, 0, 0)); // Potato
            }
        }
        
        for (int y = 0; y < 1; y++) {
            for (int x = 0; x < 1; x++) {
                screen.render(156 + x * 8, 161 + y * 8, new Sprite.Px(x + 1 , y + 8, 0, 0)); // Antidious
            }
        }
        //----------------------------------------------------------------------------------------------------------  
        
        // Player hit tree -----------------------------------------------------------------------------------------
        
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                screen.render(65 + x * 8, 224 + y * 8, new Sprite.Px(x + 4 , y + 16, 0, 2)); // Player sprite
            }
        } 

        /*
        for (int y = 0; y < 1; y++) {
            for (int x = 0; x < 1; x++) {
                screen.render(79 + x * 8, 224 + y * 8, new Sprite.Px(x + 4 , y + 2, 0, 3)); // Player hit sprite (half-up)
            }
        }
      
        for (int y = 0; y < 1; y++) {
            for (int x = 0; x < 1; x++) {
                screen.render(79 + x * 8, 232 + y * 8, new Sprite.Px(x + 4 , y + 2, 2, 3)); // Player hit sprite (half-down)
            }
        }*/

        for (int y = 0; y < 1; y++) {
            for (int x = 0; x < 1; x++) {
                screen.render(79 + x * 8, 229 + y * 8, new Sprite.Px(x + 4 , y + 16, 0, 0)); // Gem axe
            }
        }
        
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 1; x++) {
                screen.render(88 + x * 8, 224 + y * 8, new Sprite.Px(x , y, 0, 1)); // Tree (half- left)
            }
        }
         
        for (int y = 0; y < 1; y++) {
            for (int x = 0; x < 1; x++) {
                screen.render(96 + x * 8, 224 + y * 8, new Sprite.Px(x + 1 , y, 0, 1)); // Tree (half- right- up)
            }
        }
        
        for (int y = 0; y < 1; y++) {
            for (int x = 0; x < 1; x++) {
                screen.render(96 + x * 8, 232 + y * 8, new Sprite.Px(x + 1 , y + 3, 0, 1)); // Tree (half- right- down)
            }
        }
        
        // Tree loot 
        for (int y = 0; y < 1; y++) {
            for (int x = 0; x < 1; x++) {
                screen.render(124 + x * 8, 232 + y * 8, new Sprite.Px(x + 2 , y + 1, 0, 0)); // Wood
            }
        }
        
        for (int y = 0; y < 1; y++) {
            for (int x = 0; x < 1; x++) {
                screen.render(140 + x * 8, 232 + y * 8, new Sprite.Px(x + 1 , y + 3, 0, 0)); // Acorn
            }
        }
        
        for (int y = 0; y < 1; y++) {
            for (int x = 0; x < 1; x++) {
                screen.render(156 + x * 8, 232 + y * 8, new Sprite.Px(x + 16 , y, 0, 0)); // Apple
            }
        }
        
        /*
        int h = 1;
        int w = 1;
        int xo = 175;
        int yo = 232;
        
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                screen.render(xo + x * 8, yo + y * 8, new Sprite.Px(x + 16 , y, 0, 0)); // Apple
            }
        }*/
        
		
	}

	
	@Override
	public void onExit() {

	}
}
