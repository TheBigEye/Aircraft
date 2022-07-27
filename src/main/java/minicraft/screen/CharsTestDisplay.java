package minicraft.screen;

import minicraft.core.Game;
import minicraft.core.io.InputHandler;
import minicraft.gfx.Color;
import minicraft.gfx.Screen;
import minicraft.screen.entry.BlankEntry;
import minicraft.screen.entry.StringEntry;

public class CharsTestDisplay extends Display {

    public CharsTestDisplay() {
        super(true);

        Menu charsList = new Menu.Builder(false, 6, RelPos.LEFT,
            new BlankEntry(),
            new StringEntry(" !\"#$%&'()*+,-./0123456789:;<=>?", Color.WHITE),
            new StringEntry("@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_", Color.WHITE),
            new StringEntry("`abcdefghijklmnopqrstuvwxyz{|}~∞", Color.WHITE),
            new StringEntry("ÇÜÉÂÄÀÅÇÊËÈÏÎÌÄÅÉæÆÔÖÒÛÙŸÖÜ¢£¥₧Ƒ", Color.WHITE),
            new StringEntry("ÁÍÓÚñÑªº¿⌐¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐", Color.WHITE),
            new StringEntry("└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀", Color.WHITE),
            new StringEntry("αβΓπΣσµτΦΘΩδ∞φε∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■А", Color.WHITE),
            new StringEntry("БВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ", Color.WHITE),
            new StringEntry("абвгдеёжзийклмнопрстуфхцчшщъыьэю", Color.WHITE),
            new StringEntry("яÁÍíÓÚÀÂÈÊËÌÎÏÒÔŒœÙÛÝýŸÃãÕõ     ", Color.WHITE)
        )
        .setTitle("Chars test")
        .createMenu();

        menus = new Menu[]{
            charsList
        };
    }
    
    @Override
    public void tick(InputHandler input) {
    	if (input.getKey("exit").clicked) {
            Game.exitDisplay(); 
        }
    }

    @Override
	public void render(Screen screen) {
		super.render(screen);
	}
}

