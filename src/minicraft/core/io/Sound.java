package minicraft.core.io;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import minicraft.core.Game;

public class Sound {// creates sounds from their respective files


    // IMPORTANT: Do not modify these variables, they determine the path of each category of sounds, changing them would cause errors
    private static String MOB_SOUNDS_DIR = "/resources/sound/Mob/";
    private static String FURNITURE_SOUNDS_DIR = "/resources/sound/Furniture/";
    private static String GUI_SOUNDS_DIR = "/resources/sound/GUI/";

    // Mob sounds ===================================================================================================================

    // Player
    public static final Sound Mob_player_hurt = new Sound(MOB_SOUNDS_DIR + "Player/playerhurt.wav");
    public static final Sound Mob_player_death = new Sound(MOB_SOUNDS_DIR + "Player/death.wav");
    public static final Sound Mob_player_craft = new Sound(MOB_SOUNDS_DIR + "Player/craft.wav");
    public static final Sound Mob_player_pickup = new Sound(MOB_SOUNDS_DIR + "Player/pickup.wav");
    public static final Sound Mob_player_pickup_2 = new Sound(MOB_SOUNDS_DIR + "Player/pickup 2.wav");
    public static final Sound Mob_player_pickup_3 = new Sound(MOB_SOUNDS_DIR + "Player/pickup 3.wav");
    public static final Sound Mob_player_pickup_4 = new Sound(MOB_SOUNDS_DIR + "Player/pickup 4.wav");
    public static final Sound place = new Sound(MOB_SOUNDS_DIR + "Player/place.wav");

    // Generic mob
    public static final Sound Mob_generic_hurt = new Sound(MOB_SOUNDS_DIR + "monsterhurt.wav");

    // Air Wizard
    public static final Sound Mob_wizard_death = new Sound(MOB_SOUNDS_DIR + "AirWizard/bossdeath.wav");
    public static final Sound Mob_wizard_changePhase = new Sound(MOB_SOUNDS_DIR + "AirWizard/changephase.wav");
    public static final Sound Mob_wizard_attack = new Sound(MOB_SOUNDS_DIR + "AirWizard/wizardattack.wav");

    // Eye Queen
    public static final Sound Mob_eyeBoss_death = new Sound(MOB_SOUNDS_DIR + "EyeQueen/eyedeath.wav");
    public static final Sound Mob_eyeBoss_changePhase = new Sound(MOB_SOUNDS_DIR + "EyeQueen/changephase.wav");

    // Creeper
    public static final Sound Mob_creeper_fuse = new Sound(MOB_SOUNDS_DIR + "Creeper/fuse.wav");
    public static final Sound Mob_creeper_explode = new Sound(MOB_SOUNDS_DIR + "Creeper/explode.wav");
    public static final Sound Mob_creeper_explode_2 = new Sound(MOB_SOUNDS_DIR + "Creeper/explode 2.wav");
    public static final Sound Mob_creeper_explode_3 = new Sound(MOB_SOUNDS_DIR + "Creeper/explode 3.wav");
    public static final Sound Mob_creeper_explode_4 = new Sound(MOB_SOUNDS_DIR + "Creeper/explode 4.wav");

    // Furniture sounds =============================================================================================================

    // Spawner
    public static final Sound Furniture_spawner_destroy = new Sound(FURNITURE_SOUNDS_DIR + "Spawner/Destroy.wav");
    public static final Sound Furniture_spawner_destroy_2 = new Sound(FURNITURE_SOUNDS_DIR + "Spawner/Destroy 2.wav");
    public static final Sound Furniture_spawner_destroy_3 = new Sound(FURNITURE_SOUNDS_DIR + "Spawner/Destroy 3.wav");
    public static final Sound Furniture_spawner_spawn = new Sound(FURNITURE_SOUNDS_DIR + "Spawner/Spawn.wav");
    public static final Sound Furniture_spawner_hurt = new Sound(FURNITURE_SOUNDS_DIR + "Spawner/Hurt.wav");

    // Tnt 
    public static final Sound Furniture_tnt_fuse = new Sound(FURNITURE_SOUNDS_DIR + "Tnt/fuse.wav");
    public static final Sound Furniture_tnt_explode = new Sound(FURNITURE_SOUNDS_DIR + "Tnt/explode.wav");
    public static final Sound Furniture_tnt_explode_2 = new Sound(FURNITURE_SOUNDS_DIR + "Tnt/explode 2.wav");
    public static final Sound Furniture_tnt_explode_3 = new Sound(FURNITURE_SOUNDS_DIR + "Tnt/explode 3.wav");
    public static final Sound Furniture_tnt_explode_4 = new Sound(FURNITURE_SOUNDS_DIR + "Tnt/explode 4.wav");


    // Pickup
    //public static final Sound pickup = new Sound(Mob_Sounds + "Player/pickup.wav");
    //public static final Sound pickup2 = new Sound(Mob_Sounds + "Player/pickup 2.wav");
    //public static final Sound pickup3 = new Sound(Mob_Sounds + "Player/pickup 3.wav");
    //public static final Sound pickup4 = new Sound(Mob_Sounds + "Player/pickup 4.wav");

    // GUI sounds ===================================================================================================================

    // Menu
    public static final Sound GUI_back = new Sound(GUI_SOUNDS_DIR + "back.wav");
    public static final Sound GUI_select = new Sound(GUI_SOUNDS_DIR + "select.wav");
    public static final Sound GUI_confirm = new Sound(GUI_SOUNDS_DIR + "confirm.wav");
    public static final Sound GUI_PageUp = new Sound(GUI_SOUNDS_DIR + "Page Up.wav");

    public static final Sound Intro = new Sound("/resources/sound/Music/Title/Intro.wav");
    public static final Sound Intro2 = new Sound("/resources/sound/Music/Title/Intro 2.wav");

    public static final Sound Amulet_locked = new Sound("/resources/sound/Music/Amulet/Amulet locked.wav");
    public static final Sound Amulet_locked2 = new Sound("/resources/sound/Music/Amulet/Amulet locked 2.wav");
    public static final Sound Amulet_sucess = new Sound("/resources/sound/Music/Amulet/Amulet sucess.wav");

    // Cave and ambience sounds
    public static final Sound Ambience1 = new Sound("/resources/sound/Enviroment/Ambience1.wav");
    public static final Sound Ambience2 = new Sound("/resources/sound/Enviroment/Ambience2.wav");
    public static final Sound Ambience3 = new Sound("/resources/sound/Enviroment/Ambience3.wav");
    public static final Sound Ambience4 = new Sound("/resources/sound/Enviroment/Ambience4.wav");
    public static final Sound Ambience5 = new Sound("/resources/sound/Enviroment/Ambience5.wav");

    public static final Sound HeavenAmbience = new Sound("/resources/sound/Enviroment/HeavenAmbience.wav");

    // Snow tile
    public static final Sound Snow = new Sound("/resources/sound/Tile/Snow.wav");
    public static final Sound Snow2 = new Sound("/resources/sound/Tile/Snow 2.wav");
    public static final Sound Snow3 = new Sound("/resources/sound/Tile/Snow 3.wav");
    public static final Sound Snow4 = new Sound("/resources/sound/Tile/Snow 4.wav");

    public static final Sound Tile_generic_hurt = new Sound("/resources/sound/Tile/Hurt.wav");

    // Themes
    public static final Sound Theme_Fall = new Sound("/resources/sound/Music/Background/Fall.wav");
    public static final Sound Theme_Surface = new Sound("/resources/sound/Music/Background/Surface.wav");
    public static final Sound Theme_Peaceful = new Sound("/resources/sound/Music/Background/Peaceful.wav");
    public static final Sound Theme_Cave = new Sound("/resources/sound/Music/Background/Cave.wav");
    public static final Sound Theme_Cavern = new Sound("/resources/sound/Music/Background/Cavern.wav");
    public static final Sound Theme_Cavern_drip = new Sound("/resources/sound/Music/Background/Cavern drip.wav");



    private Clip clip; // Creates a audio clip to be played

    public static void init() {} // A way to initialize the class without actually doing anything

    private Sound(String name) {
        if (!Game.HAS_GUI)
            return;

        try {
            URL url = getClass().getResource(name);

            DataLine.Info info = new DataLine.Info(Clip.class, AudioSystem.getAudioFileFormat(url).getFormat());

            if (!AudioSystem.isLineSupported(info)) {
                System.err.println("ERROR: Audio format of file " + name + " is not supported: " + AudioSystem.getAudioFileFormat(url));

                System.out.println("Supported audio formats:");
                System.out.println("-source:");
                Line.Info[] sinfo = AudioSystem.getSourceLineInfo(info);
                Line.Info[] tinfo = AudioSystem.getTargetLineInfo(info);
                for (int i = 0; i < sinfo.length; i++) {
                    if (sinfo[i] instanceof DataLine.Info) {
                        DataLine.Info dataLineInfo = (DataLine.Info) sinfo[i];
                        AudioFormat[] supportedFormats = dataLineInfo.getFormats();
                        for (AudioFormat af: supportedFormats)
                            System.out.println(af);
                    }
                }
                System.out.println("-target:");
                for (int i = 0; i < tinfo.length; i++) {
                    if (tinfo[i] instanceof DataLine.Info) {
                        DataLine.Info dataLineInfo = (DataLine.Info) tinfo[i];
                        AudioFormat[] supportedFormats = dataLineInfo.getFormats();
                        for (AudioFormat af: supportedFormats)
                            System.out.println(af);
                    }
                }

                return;
            }

            clip = (Clip) AudioSystem.getLine(info);
            clip.open(AudioSystem.getAudioInputStream(url));

            clip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP) {
                    clip.flush();
                    clip.setFramePosition(0);
                }
            });

        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            System.err.println("Could not load sound file " + name);
            e.printStackTrace();
        }
    }
    // clip = music/sound name

    // This plays the clip only once, Syntax: Sound.clip.play();
    public void play() {
        if (!(boolean) Settings.get("sound") || clip == null)
            return;
        if (Game.isValidServer())
            return;

        if (clip.isRunning() || clip.isActive())
            clip.stop();

        clip.start();
    }


    public void loop(boolean start) {// This repeats the same clip over and over again,
        if (!(boolean) Settings.get("sound") || clip == null)
            return;

        if (start)
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        else
            clip.stop();
        
    }


    public void stop() {// This stops the clip
        clip.stop();

    }

}