package minicraft.screen;

import minicraft.core.Game;
import minicraft.core.io.InputHandler;
import minicraft.gfx.Color;
import minicraft.gfx.Font;
import minicraft.gfx.Screen;
import minicraft.screen.entry.BlankEntry;

public class EndGameCreditsDisplay extends Display {
	
	private int line = 0;
	private int step = 0;
	private int tickTime = 0;

    public EndGameCreditsDisplay() {
        super(true);

        Menu credits = new Menu.Builder(false, 6, RelPos.LEFT,
            new BlankEntry())
            .setTitle("")
            .createMenu();

        menus = new Menu[]{
            credits
        };
    }
    
    public void tick(InputHandler input) {
    	if (tickTime / 3 %16 == 0) {
    		line++;
    	}
    	
    	if (line > 775) {
    		Game.exitDisplay();
    	}
    	
    	super.tick(input);
    	tickTime++;
    }

	public void render(Screen screen) {
		super.render(screen);

		Font.draw("--------[ Aircraft ]--------", screen, Screen.h / 2  - 40, 190 - line, Color.YELLOW);
		
		Font.draw("Aircraft by:", screen, Screen.h/2 - 40, 210 - line, Color.WHITE);
		Font.draw(" - TheBigEye", screen, Screen.h/2 - 40, 220 - line, Color.GRAY);
		
		Font.draw("Minicraft plus by:", screen, Screen.h/2 - 40, 235 - line, Color.WHITE);
		Font.draw(" - Davideesk", screen, Screen.h/2 - 40, 245 - line, Color.GRAY);
		Font.draw(" - Dillyg10", screen, Screen.h/2 - 40, 255 - line, Color.GRAY);
		
		Font.draw("Minicraft by:", screen, Screen.h/2 - 40, 270 - line, Color.WHITE);
		Font.draw(" - Markus persson", screen, Screen.h/2 - 40, 280 - line, Color.GRAY);
		
		Font.draw("        -[ Desing ]-        ", screen, Screen.h / 2  - 40, 300 - line, Color.YELLOW);
		
		Font.draw("Textures artists:", screen, Screen.h/2 - 40, 320 - line, Color.WHITE);
		Font.draw(" - TheBigEye", screen, Screen.h/2 - 40, 330 - line, Color.GRAY);
		Font.draw(" - Sanzo", screen, Screen.h/2 - 40, 340 - line, Color.GRAY);
		Font.draw(" - GameCreator2004", screen, Screen.h/2 - 40, 350 - line, Color.GRAY);
		Font.draw(" - RiverOaken (Rcraft textures)", screen, Screen.h/2 - 40, 360 - line, Color.GRAY);
		
		Font.draw("Music artists:", screen, Screen.h/2 - 40, 375 - line, Color.WHITE);
		Font.draw(" - TheBigEye", screen, Screen.h/2 - 40, 385 - line, Color.GRAY);
		Font.draw(" - Ed B. Martinez (Minicraft flash)", screen, Screen.h/2 - 40, 395 - line, Color.GRAY);
		
		Font.draw("Sounds:", screen, Screen.h/2 - 40, 410 - line, Color.WHITE);
		Font.draw(" - TheBigEye", screen, Screen.h/2 - 40, 420 - line, Color.GRAY);
		Font.draw(" - Markus person (Minicraft)", screen, Screen.h/2 - 40, 430 - line, Color.GRAY);
		
		Font.draw("GUI desing:", screen, Screen.h/2 - 40, 445 - line, Color.WHITE);
		Font.draw(" - TheBigEye", screen, Screen.h/2 - 40, 455 - line, Color.GRAY);
		
		Font.draw("     >{ Programming };     ", screen, Screen.h / 2  - 40, 475 - line, Color.YELLOW);
		
		Font.draw("Main developer:", screen, Screen.h/2 - 40, 495 - line, Color.WHITE);
		Font.draw(" - TheBigEye", screen, Screen.h/2 - 40, 505 - line, Color.GRAY);
		
		Font.draw("Code contributions:", screen, Screen.h/2 - 40, 520 - line, Color.WHITE);
		Font.draw(" - A.L.I.C.E", screen, Screen.h/2 - 40, 530 - line, Color.GRAY);
		Font.draw(" - UdhavKumar", screen, Screen.h/2 - 40, 540 - line, Color.GRAY);
		
		Font.draw("Thanks to...", screen, Screen.h/2 - 40, 560 - line, Color.YELLOW);
		Font.draw(" - pelletsstarPL", screen, Screen.h/2 - 40, 570 - line, Color.GRAY);
		Font.draw(" - terrarianmisha", screen, Screen.h/2 - 40, 580 - line, Color.GRAY);
		Font.draw(" - Litorom1", screen, Screen.h/2 - 40, 590 - line, Color.GRAY);
		Font.draw(" - Felix pants", screen, Screen.h/2 - 40, 600 - line, Color.GRAY);
		Font.draw(" - benichi (why not xd)", screen, Screen.h/2 - 40, 610 - line, Color.GRAY);
		Font.draw(" - Fusyon", screen, Screen.h/2 - 40, 620 - line, Color.GRAY);
		Font.draw(" - ChrisJ", screen, Screen.h/2 - 40, 630 - line, Color.GRAY);
		Font.draw(" - dafist", screen, Screen.h/2 - 40, 640 - line, Color.GRAY);
		Font.draw(" - EduardoPlayer13", screen, Screen.h/2 - 40, 650 - line, Color.GRAY);
		Font.draw(" - Makkkkus", screen, Screen.h/2 - 40, 660 - line, Color.GRAY);
		Font.draw(" - BoxDude", screen, Screen.h/2 - 40, 670 - line, Color.GRAY);
		Font.draw(" - MrToad", screen, Screen.h/2 - 40, 680 - line, Color.GRAY);
		Font.draw(" - itayfeder", screen, Screen.h/2 - 40, 690 - line, Color.GRAY);
		Font.draw("for giving me ideas and", screen, Screen.h/2 - 40, 700 - line, Color.YELLOW);
		Font.draw("participate in the ", screen, Screen.h/2 - 40, 710 - line, Color.YELLOW);
		Font.draw("development of this nice mod ", screen, Screen.h/2 - 40, 720 - line, Color.YELLOW);
		
		Font.draw("And thanks to the Minicraft plus", screen, Screen.h/2 - 40, 740 - line, Color.YELLOW);
		Font.draw("maintainers that thanks to their", screen, Screen.h/2 - 40, 750 - line, Color.YELLOW);
		Font.draw("work this nice mod is possible :)", screen, Screen.h/2 - 40, 760 - line, Color.YELLOW);
		
	}

	@Override
	public void onExit() {

	}
}
