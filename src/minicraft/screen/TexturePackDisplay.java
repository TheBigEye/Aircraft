package minicraft.screen;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import minicraft.core.FileHandler;
import minicraft.core.Game;
import minicraft.core.io.InputHandler;
import minicraft.core.io.Sound;
import minicraft.gfx.Color;
import minicraft.gfx.Font;
import minicraft.gfx.Screen;
import minicraft.gfx.SpriteSheet;

public class TexturePackDisplay extends Display {

    private static final String DEFAULT_TEXTURE_PACK = "Default"; // Default texture :)
    private static final String LEGACY_TEXTURE_PACK = "Legacy"; // Legacy texture :)
    private static final String[] ENTRY_NAMES = new String[] {

        "items.png", // Items sheet (0)
        "tiles.png", // Tiles sheet  (1)
        "entities.png", // Entities sheet (2)
        "gui.png", // GUI Elements sheet (3)
        "icons.png", // More GUI Elements sheet (4)
        
        "background.png" // More GUI Elements sheet (5)

    };

    private final List < String > textureList;
    private final File location;

    private int selected;
    private boolean shouldUpdate;

    /* The texture packs are put in a folder "Textures packs".
     * Many texture packs can be put according to the number of files.
     */

    public TexturePackDisplay() {
        this.textureList = new ArrayList < > ();
        this.textureList.add(TexturePackDisplay.DEFAULT_TEXTURE_PACK); // Entries are added
        this.textureList.add(TexturePackDisplay.LEGACY_TEXTURE_PACK);

        // Generate texture packs folder
        this.location = new File(FileHandler.getSystemGameDir() + "/" + FileHandler.getLocalGameDir() + "/Texture Packs");
        this.location.mkdirs();

        // Read and add the .zip file to the texture pack list
        for (String fileName: Objects.requireNonNull(location.list())) {
            if (fileName.endsWith(".zip")) { // only .zip files ok?
                textureList.add(fileName);
            }
        }
    }

    private void updateSpriteSheet(Screen screen) throws IOException {
        SpriteSheet[] sheets = new SpriteSheet[TexturePackDisplay.ENTRY_NAMES.length];

        if (selected == 1) { // Legacy textures
            sheets[0] = new SpriteSheet(ImageIO.read(Game.class.getResourceAsStream("/resources/textures/legacy/items_legacy.png")));
            sheets[1] = new SpriteSheet(ImageIO.read(Game.class.getResourceAsStream("/resources/textures/legacy/tiles_legacy.png")));
            sheets[2] = new SpriteSheet(ImageIO.read(Game.class.getResourceAsStream("/resources/textures/legacy/entities_legacy.png")));
            sheets[3] = new SpriteSheet(ImageIO.read(Game.class.getResourceAsStream("/resources/textures/legacy/gui_legacy.png")));
            sheets[4] = new SpriteSheet(ImageIO.read(Game.class.getResourceAsStream("/resources/textures/legacy/icons_legacy.png")));
            sheets[5] = new SpriteSheet(ImageIO.read(Game.class.getResourceAsStream("/resources/textures/legacy/background_legacy.png")));
            
        } else if (selected == 0) { // Default textures
            sheets[0] = new SpriteSheet(ImageIO.read(Game.class.getResourceAsStream("/resources/textures/items.png")));
            sheets[1] = new SpriteSheet(ImageIO.read(Game.class.getResourceAsStream("/resources/textures/tiles.png")));
            sheets[2] = new SpriteSheet(ImageIO.read(Game.class.getResourceAsStream("/resources/textures/entities.png")));
            sheets[3] = new SpriteSheet(ImageIO.read(Game.class.getResourceAsStream("/resources/textures/gui.png")));
            sheets[4] = new SpriteSheet(ImageIO.read(Game.class.getResourceAsStream("/resources/textures/icons.png")));
            sheets[5] = new SpriteSheet(ImageIO.read(Game.class.getResourceAsStream("/resources/textures/background.png")));
            
        } else {
            try (ZipFile zipFile = new ZipFile(new File(location, textureList.get(selected)))) {
                for (int i = 0; i < TexturePackDisplay.ENTRY_NAMES.length; i++) {
                    ZipEntry entry = zipFile.getEntry(TexturePackDisplay.ENTRY_NAMES[i]);
                    if (entry != null) {
                        try (InputStream inputEntry = zipFile.getInputStream(entry)) {
                            sheets[i] = new SpriteSheet(ImageIO.read(inputEntry));
                        }
                    }
                }
            }
        }

        // Set texture pack
        screen.setSheet(sheets[0], sheets[1], sheets[2], sheets[3], sheets[4], sheets[5]);
    }

    @Override
    public void tick(InputHandler input) {
        if (input.getKey("exit").clicked) {
            Game.exitMenu();
            return;
        }

        if (input.getKey("MOVE-DOWN").clicked && selected > 0) {
            selected--;

        }
        if (input.getKey("MOVE-UP").clicked && selected < textureList.size() - 1) {
            selected++;

        }

        if (input.getKey("SELECT").clicked) {
            shouldUpdate = true;
            Sound.GUI_confirm.play();
        }
    }


    // In case the name is too big ...
    private static String shortNameIfLong(String name) {
        return name.length() > 24 ? name.substring(0, 16) + "..." : name;
    }

    @Override
    public void render(Screen screen) {
        screen.clear(0);

        if (shouldUpdate) {
            shouldUpdate = false;

            try {
                updateSpriteSheet(screen);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        String selectedUp = selected + 1 > textureList.size() - 1 ? "" : textureList.get(selected + 1);
        String selectedDown = selected - 1 < 0 ? "" : textureList.get(selected - 1);

        // Render the menu
        Font.drawCentered("Texture Packs", screen, Screen.h - 280, Color.get(0, 555, 555, 555)); // Title
        Font.drawCentered(TexturePackDisplay.shortNameIfLong(selectedDown), screen, Screen.h - 110, Color.GRAY); // Unselected space
        Font.drawCentered(TexturePackDisplay.shortNameIfLong(textureList.get(selected)), screen, Screen.h - 120, Color.GREEN); // Selection
        Font.drawCentered(TexturePackDisplay.shortNameIfLong(selectedUp), screen, Screen.h - 130, Color.GRAY); // Other unselected space
        Font.drawCentered("Use " + Game.input.getMapping("MOVE-DOWN") + ", " + Game.input.getMapping("MOVE-UP"), screen, Screen.h - 11, Color.get(0, 111, 111, 111)); // Controls

        
        int h = 2;
        int w = 15;
        int xo = (Screen.w - w * 8) / 2;
        int yo = 28;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                screen.render(xo + x * 8, yo + y * 8, x + y * 32, 0, 3); // Texture pack logo
            }
        }
        
        
    }
}