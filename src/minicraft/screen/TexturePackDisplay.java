package minicraft.screen;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import minicraft.core.Game;
import minicraft.core.io.InputHandler;
import minicraft.gfx.Color;
import minicraft.gfx.Font;
import minicraft.gfx.Screen;
import minicraft.gfx.SpriteSheet;


public class TexturePackDisplay extends Display {
	  
	  public String Default = "Default";
	  
	  public static int selected = 0;
	  
	  public int numberoftps = 0;
	  
	  public List<String> texturepacks = new ArrayList<String>();
	  
	  public File location = new File(String.valueOf(System.getenv("APPDATA")) + "/playminicraft/mods/Aircraft/TexturePacks");
	  
	  public TexturePackDisplay() {
	    texturepacks.add(this.Default);
	    numberoftps++;
	    location.mkdirs();
	    File[] listOfFiles = location.listFiles();
	    for (int i = 0; i < listOfFiles.length; i++) {
	      String files = listOfFiles[i].getName();
	      if (files.length() > 4 && files.substring(files.length() - 4).equals(".zip")) {
	        texturepacks.add(listOfFiles[i].getName());
	        numberoftps++;
	      } 
	    } 
	  }
	  
	  public void tick(InputHandler input) {
          if (input.getKey("MOVE-DOWN").clicked && 
	      selected > 0)
	      selected--; 
	    if (input.getKey("MOVE-UP").clicked && 
	      selected < numberoftps - 1)
	      selected++; 
		if (input.getKey("menu").clicked || input.getKey("attack").clicked || input.getKey("exit").clicked) {
	      Game.exitMenu();
	    if ((((texturepacks.size() > 0) ? 1 : 0) & ((selected > 0) ? 1 : 0)) != 0) {
	      File f = new File(this.location + "/" + (String)this.texturepacks.get(selected));
	      setSpriteSheet(f);
	    } else {
	      try {
	      	SpriteSheet itemSheet = new SpriteSheet(ImageIO.read(Game.class.getResourceAsStream("/items.png")));
	      	SpriteSheet tileSheet = new SpriteSheet(ImageIO.read(Game.class.getResourceAsStream("/tiles.png")));
			SpriteSheet entitySheet = new SpriteSheet(ImageIO.read(Game.class.getResourceAsStream("/entities.png")));
			SpriteSheet guiSheet = new SpriteSheet(ImageIO.read(Game.class.getResourceAsStream("/gui.png")));
			
			
	      } catch (IOException e) {
	        e.printStackTrace();
	      } 
	    } 
	    }
	  }
	  
	  public void render(Screen screen) {
	    screen.clear(0);
	    int selectedp1 = selected + 1;
	    int selectedm1 = selected - 1;
	    String dselectedp1 = "";
	    String dselectedm1 = "";
	    if (selectedp1 > numberoftps - 1) {
	      dselectedp1 = "";
	    } else {
	      dselectedp1 = texturepacks.get(selectedp1);
	    } 
	    if (selectedm1 < 0) {
	      dselectedm1 = "";
	    } else {
	      dselectedm1 = texturepacks.get(selectedm1);
	    } 
	    Font.draw("Texture Packs", screen, 28, 8, Color.get(0, 555, 555, 555));
	    Font.draw(NameShortenIfLong(texturepacks.get(selected)), screen, 2, 80, Color.get(0, 555, 555, 555));
	    Font.draw(NameShortenIfLong(dselectedm1), screen, 2, 72, Color.get(0, 222, 222, 222));
	    Font.draw(NameShortenIfLong(dselectedp1), screen, 2, 90, Color.get(0, 222, 222, 222));
	    Font.drawCentered("Arrows keys, X, C", screen, Screen.h - 11, Color.get(0, 222, 222, 222));
	    int h = 2;
	    int w = 13;
	    int titleColor = Color.get(0, 8, 131, 551);
	    int xo = (screen.w - w * 8) / 2;
	    int yo = 24;
	    for (int y = 0; y < h; y++) {
	      for (int x = 0; x < w; x++)
	        screen.render(xo + x * 8, yo + y * 8, x + (y + 6) * 32, titleColor, 0); 
	    } 
	    screen.render(24, 48, 134, Color.get(-1, 110, 330, 550), 0);
	    screen.render(48, 48, 136, Color.get(-1, 110, 330, 550), 0);
	    screen.render(72, 48, 164, Color.get(-1, 100, 321, 45), 0);
	    screen.render(96, 48, 384, Color.get(0, 200, 500, 533), 0);
	    screen.render(120, 48, 135, Color.get(-1, 100, 320, 430), 0);
	  }
	  
	  public String NameShortenIfLong(String s) {
	    String New;
	    if (s.length() > 20) {
	      New = String.valueOf(s.substring(0, 16)) + "...";
	    } else {
	      New = s;
	    } 
	    return New;
	  }
	  
	  public void setSpriteSheet(File f) {
	    try {
	      ZipFile zfile = new ZipFile(f);
	      Enumeration<? extends ZipEntry> entries = zfile.entries();
	      while (entries.hasMoreElements()) {
	        ZipEntry zipEntry = entries.nextElement();
	        if (!zipEntry.isDirectory()) {
	          String fileName = zipEntry.getName();
	          if (fileName.endsWith(".png")) {
	            InputStream zis = zfile.getInputStream(zipEntry);
	            if (selected != 0)
	              try {
	                SpriteSheet itemSheet = new SpriteSheet(ImageIO.read(zis));
	                SpriteSheet tileSheet = new SpriteSheet(ImageIO.read(zis));
	                SpriteSheet entitySheet = new SpriteSheet(ImageIO.read(zis));
	                SpriteSheet guiSheet = new SpriteSheet(ImageIO.read(zis));
	              } catch (IOException iOException) {} 
	          } 
	        } 
	      } 
	      zfile.close();
	    } catch (ZipException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    } 
	  }
	}
