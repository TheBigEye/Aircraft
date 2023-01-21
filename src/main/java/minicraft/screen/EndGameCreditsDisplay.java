package minicraft.screen;

import minicraft.core.Game;
import minicraft.core.io.InputHandler;
import minicraft.gfx.Color;
import minicraft.gfx.Font;
import minicraft.gfx.Screen;

public class EndGameCreditsDisplay extends Display {
	
	private int line = -230;
	private int tickTime = 0;
	
	private static final int titleColor = Color.YELLOW;
	private static final int sectionColor = Color.YELLOW;
	private static final int categoryColor = Color.WHITE;
	private static final int nameColor = Color.GRAY;

    public EndGameCreditsDisplay() {
        super(true);
    }
    
    @Override
    public void tick(InputHandler input) {
    	if (tickTime / 6 %2 == 0) {
    		line++;
    	}
    	
    	if (line > 800) {
    		Game.exitDisplay();
    	}
    	
    	super.tick(input);
    	tickTime++;
    }

    @Override
	public void render(Screen screen) {
		super.render(screen);
		
		// TODO: Port credits text to txt or JSON file
		
		// Title sprite
		int h = 6; // Height of squares (on the spritesheet)
		int w = 26; // Width of squares (on the spritesheet)
		int xo = (Screen.w - w * 8) / 2; // X position on screen
		int yo = 25 - line; // Y position on screen

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				screen.render(xo + x * 8, yo + y * 8, x + (y + 7) * 32, 0, 3);
			}
		}
		
		Font.draw("--------[ Aircraft ]--------", 			screen, Screen.h/2  - 40, 190 - line, titleColor);
		
		Font.draw("Aircraft by:", 							screen, Screen.h/2 - 40, 210 - line, categoryColor);
		Font.draw(" - TheBigEye", 							screen, Screen.h/2 - 40, 220 - line, nameColor);
		
		Font.draw("Minicraft plus by:", 					screen, Screen.h/2 - 40, 235 - line, categoryColor);
		Font.draw(" - Davideesk", 							screen, Screen.h/2 - 40, 245 - line, nameColor);
		Font.draw(" - Dillyg10", 							screen, Screen.h/2 - 40, 255 - line, nameColor);
		
		Font.draw("Minicraft by:", 							screen, Screen.h/2 - 40, 270 - line, categoryColor);
		Font.draw(" - Markus persson", 						screen, Screen.h/2 - 40, 280 - line, nameColor);
		
		Font.draw("        -[ Desing ]-        ", 			screen, Screen.h/2  - 40, 300 - line, sectionColor);
		
		Font.draw("Textures artists:", 						screen, Screen.h/2 - 40, 320 - line, categoryColor);
		Font.draw(" - TheBigEye", 							screen, Screen.h/2 - 40, 330 - line, nameColor);
		Font.draw(" - Sanzo", 								screen, Screen.h/2 - 40, 340 - line, nameColor);
		Font.draw(" - GameCreator2004", 					screen, Screen.h/2 - 40, 350 - line, nameColor);
		Font.draw(" - RiverOaken (Rcraft textures)", 		screen, Screen.h/2 - 40, 360 - line, nameColor);
		
		Font.draw("Music artists:", 						screen, Screen.h/2 - 40, 375 - line, categoryColor);
		Font.draw(" - TheBigEye", 							screen, Screen.h/2 - 40, 385 - line, nameColor);
		Font.draw(" - Ed B. Martinez (Minicraft flash)", 	screen, Screen.h/2 - 40, 395 - line, nameColor);
		
		Font.draw("Sounds:", 								screen, Screen.h/2 - 40, 410 - line, categoryColor);
		Font.draw(" - TheBigEye", 							screen, Screen.h/2 - 40, 420 - line, nameColor);
		Font.draw(" - Markus persson (Minicraft)", 			screen, Screen.h/2 - 40, 430 - line, nameColor);
		
		Font.draw("GUI desing:", 							screen, Screen.h/2 - 40, 445 - line, categoryColor);
		Font.draw(" - TheBigEye", 							screen, Screen.h/2 - 40, 455 - line, nameColor);
		
		Font.draw("     >{ Programming };      ", 			screen, Screen.h/2  - 40, 475 - line, sectionColor);
		
		Font.draw("Main developer:", 						screen, Screen.h/2 - 40, 495 - line, categoryColor);
		Font.draw(" - TheBigEye", 							screen, Screen.h/2 - 40, 505 - line, nameColor);
		
		Font.draw("Code contributions:", 					screen, Screen.h/2 - 40, 520 - line, categoryColor);
		Font.draw(" - A.L.I.C.E", 							screen, Screen.h/2 - 40, 530 - line, nameColor);
		Font.draw(" - UdhavKumar", 							screen, Screen.h/2 - 40, 540 - line, nameColor);
		Font.draw(" - pelletsstarPL", 						screen, Screen.h/2 - 40, 550 - line, nameColor);
		
		Font.draw("Thanks to...", 							screen, Screen.h/2 - 40, 570 - line, sectionColor);
		Font.draw(" - pelletsstarPL", 						screen, Screen.h/2 - 40, 580 - line, nameColor);
		Font.draw(" - terrarianmisha", 						screen, Screen.h/2 - 40, 590 - line, nameColor);
		Font.draw(" - Litorom1", 							screen, Screen.h/2 - 40, 600 - line, nameColor);
		Font.draw(" - Felix pants", 						screen, Screen.h/2 - 40, 610 - line, nameColor);
		Font.draw(" - benichi (why not, xd)", 				screen, Screen.h/2 - 40, 620 - line, nameColor);
		Font.draw(" - Fusyon", 								screen, Screen.h/2 - 40, 630 - line, nameColor);
		Font.draw(" - ChrisJ", 								screen, Screen.h/2 - 40, 640 - line, nameColor);
		Font.draw(" - dafist", 								screen, Screen.h/2 - 40, 650 - line, nameColor);
		Font.draw(" - EduardoPlayer13", 					screen, Screen.h/2 - 40, 660 - line, nameColor);
		Font.draw(" - Makkkkus", 							screen, Screen.h/2 - 40, 670 - line, nameColor);
		Font.draw(" - BoxDude", 							screen, Screen.h/2 - 40, 680 - line, nameColor);
		Font.draw(" - MrToad", 								screen, Screen.h/2 - 40, 690 - line, nameColor);
		Font.draw(" - itayfeder", 							screen, Screen.h/2 - 40, 700 - line, nameColor);
		Font.draw("for giving me ideas and", 				screen, Screen.h/2 - 40, 710 - line, Color.YELLOW);
		Font.draw("participate in the ", 					screen, Screen.h/2 - 40, 720 - line, Color.YELLOW);
		Font.draw("development of this nice mod ", 			screen, Screen.h/2 - 40, 730 - line, Color.YELLOW);
		
		Font.draw("And thanks to the Minicraft plus", 		screen, Screen.h/2 - 40, 750 - line, Color.YELLOW);
		Font.draw("maintainers that thanks to their", 		screen, Screen.h/2 - 40, 760 - line, Color.YELLOW);
		Font.draw("  work this mod is possible :)", 		screen, Screen.h/2 - 40, 770 - line, Color.YELLOW);
	}
}
