package minicraft.core.io;

import java.io.IOException;

import org.newdawn.easyogg.OggClip;

public class SoundOGG {
    // Ogg support! ;)

    // Player
    public static final SoundOGG Theme_Surface = new SoundOGG("/resources/sound/Background/Surface.OGG");

    OggClip ogg;

    private SoundOGG(String filename) {
        try {
            ogg = new OggClip(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean flipMute = true;

    public void toggleSound() {
        try {
            if (flipMute) {
                ogg.stop();
            } else {
                ogg.loop();
            }
            flipMute = !flipMute;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        try {
            ogg.loop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        try {
            ogg.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            ogg.stop();
            ogg.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
