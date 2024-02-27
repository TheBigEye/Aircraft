package minicraft.screen;

import minicraft.core.Game;
import minicraft.core.io.InputHandler;
import minicraft.graphic.Color;
import minicraft.graphic.Font;
import minicraft.graphic.Screen;

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
    	tickTime++;
    	
    	if (tickTime % 5 == 0) {
    		line++;
    	}
    	
    	if (line > 800) {
    		Game.exitDisplay();
    	}
    	
    	super.tick(input);
    }

    @Override
	public void render(Screen screen) {
		super.render(screen);
		
		// TODO: Move credits texts to txt or JSON file
		
		// Title sprite
		int h = 6; // Height of squares (on the spritesheet)
		int w = 26; // Width of squares (on the spritesheet)
		int xo = (Screen.w - (w << 3)) / 2; // X position on screen
		int yo = 25 - line; // Y position on screen
		
		int sh = Screen.h / 2;

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				screen.render(xo + (x << 3), yo + (y << 3), x + ((y + 7) << 5), 0, 3);
			}
		}
		
		Font.draw("--------[ Aircraft ]--------", 			screen, sh - 40, 190 - line, titleColor);
		
		Font.draw("Aircraft by:", 							screen, sh - 40, 210 - line, categoryColor);
		Font.draw(" - TheBigEye", 							screen, sh - 40, 220 - line, nameColor);
		
		Font.draw("Minicraft plus by:", 					screen, sh - 40, 235 - line, categoryColor);
		Font.draw(" - Davideesk", 							screen, sh - 40, 245 - line, nameColor);
		Font.draw(" - Dillyg10", 							screen, sh - 40, 255 - line, nameColor);
		
		Font.draw("Minicraft by:", 							screen, sh - 40, 270 - line, categoryColor);
		Font.draw(" - Markus persson", 						screen, sh - 40, 280 - line, nameColor);
		
		Font.draw("        -[ Desing ]-        ", 			screen, sh - 40, 300 - line, sectionColor);
		
		Font.draw("Textures artists:", 						screen, sh - 40, 320 - line, categoryColor);
		Font.draw(" - TheBigEye", 							screen, sh - 40, 330 - line, nameColor);
		Font.draw(" - Sanzo", 								screen, sh - 40, 340 - line, nameColor);
		Font.draw(" - GameCreator2004", 					screen, sh - 40, 350 - line, nameColor);
		Font.draw(" - RiverOaken (Rcraft textures)", 		screen, sh - 40, 360 - line, nameColor);
		
		Font.draw("Music artists:", 						screen, sh - 40, 375 - line, categoryColor);
		Font.draw(" - TheBigEye", 							screen, sh - 40, 385 - line, nameColor);
		Font.draw(" - Ed B. Martinez (Minicraft flash)", 	screen, sh - 40, 395 - line, nameColor);
		
		Font.draw("Sounds:", 								screen, sh - 40, 410 - line, categoryColor);
		Font.draw(" - TheBigEye", 							screen, sh - 40, 420 - line, nameColor);
		Font.draw(" - Markus persson (Minicraft)", 			screen, sh - 40, 430 - line, nameColor);
		
		Font.draw("GUI desing:", 							screen, sh - 40, 445 - line, categoryColor);
		Font.draw(" - TheBigEye", 							screen, sh - 40, 455 - line, nameColor);
		
		Font.draw("     >{ Programming };      ", 			screen, sh  - 40, 475 - line, sectionColor);
		
		Font.draw("Main developer:", 						screen, sh - 40, 495 - line, categoryColor);
		Font.draw(" - TheBigEye", 							screen, sh - 40, 505 - line, nameColor);
		
		Font.draw("Code contributions:", 					screen, sh - 40, 520 - line, categoryColor);
		Font.draw(" - A.L.I.C.E", 							screen, sh - 40, 530 - line, nameColor);
		Font.draw(" - UdhavKumar", 							screen, sh - 40, 540 - line, nameColor);
		Font.draw(" - pelletsstarPL", 						screen, sh - 40, 550 - line, nameColor);
		
		Font.draw("Thanks to...", 							screen, sh - 40, 570 - line, sectionColor);
		Font.draw(" - pelletsstarPL", 						screen, sh - 40, 580 - line, nameColor);
		Font.draw(" - terrarianmisha", 						screen, sh - 40, 590 - line, nameColor);
		Font.draw(" - Litorom1", 							screen, sh - 40, 600 - line, nameColor);
		Font.draw(" - Felix pants", 						screen, sh - 40, 610 - line, nameColor);
		Font.draw(" - benichi (why not, xd)", 				screen, sh - 40, 620 - line, nameColor);
		Font.draw(" - Fusyon", 								screen, sh - 40, 630 - line, nameColor);
		Font.draw(" - ChrisJ", 								screen, sh - 40, 640 - line, nameColor);
		Font.draw(" - dafist", 								screen, sh - 40, 650 - line, nameColor);
		Font.draw(" - EduardoPlayer13", 					screen, sh - 40, 660 - line, nameColor);
		Font.draw(" - Makkkkus", 							screen, sh - 40, 670 - line, nameColor);
		Font.draw(" - BoxDude", 							screen, sh - 40, 680 - line, nameColor);
		Font.draw(" - MrToad", 								screen, sh - 40, 690 - line, nameColor);
		Font.draw(" - itayfeder", 							screen, sh - 40, 700 - line, nameColor);
		Font.draw("for giving me ideas and", 				screen, sh - 40, 720 - line, Color.YELLOW);
		Font.draw("   participate in the ", 			    screen, sh - 40, 730 - line, Color.YELLOW);
		Font.draw("development of this nice mod ", 			screen, sh - 50, 740 - line, Color.YELLOW);
		
		Font.draw("And thanks to the Minicraft plus", 		screen, sh - 58, 760 - line, Color.YELLOW);
		Font.draw("maintainers that thanks to their", 		screen, sh - 58, 770 - line, Color.YELLOW);
		Font.draw("work this mod is possible :)", 		    screen, sh - 40, 780 - line, Color.YELLOW);
	}
}
