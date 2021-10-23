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

public class Sound { // Creates sounds from their respective files

    // IMPORTANT: Do not modify these variables, they determine the path of each
    // category of sounds, changing them would cause many errors
    private static final String MOB_SOUNDS_DIR = "/resources/sounds/entity/mob/";
    private static final String PARTICLE_SOUNDS_DIR = "/resources/sounds/entity/particle/";
    private static final String FURNITURE_SOUNDS_DIR = "/resources/sounds/entity/furniture/";
    private static final String MENU_SOUNDS_DIR = "/resources/sounds/menu/";
    private static final String TILE_SOUNDS_DIR = "/resources/sounds/tile/";

    // Mob sounds
    // ===================================================================================================================

    // Player and mobs
    public static final Sound Mob_player_hurt = new Sound(MOB_SOUNDS_DIR + "Player/playerhurt.wav");
    public static final Sound Mob_player_changelevel = new Sound(MOB_SOUNDS_DIR + "Player/changelevel.wav");
    public static final Sound Mob_player_death = new Sound(MOB_SOUNDS_DIR + "Player/death.wav");
    public static final Sound Mob_player_craft = new Sound(MOB_SOUNDS_DIR + "Player/craft.wav");
    public static final Sound Mob_player_pickup = new Sound(MOB_SOUNDS_DIR + "Player/pickup.wav");
    public static final Sound Mob_player_pickup_2 = new Sound(MOB_SOUNDS_DIR + "Player/pickup 2.wav");
    public static final Sound Mob_player_pickup_3 = new Sound(MOB_SOUNDS_DIR + "Player/pickup 3.wav");
    public static final Sound Mob_player_pickup_4 = new Sound(MOB_SOUNDS_DIR + "Player/pickup 4.wav");
    public static final Sound Mob_player_place = new Sound(MOB_SOUNDS_DIR + "Player/place.wav");

    // Generic mob
    public static final Sound Mob_generic_hurt = new Sound(MOB_SOUNDS_DIR + "monsterhurt.wav");

    // Air Wizard
    public static final Sound Mob_wizard_death = new Sound(MOB_SOUNDS_DIR + "AirWizard/bossdeath.wav");
    public static final Sound Mob_wizard_changePhase = new Sound(MOB_SOUNDS_DIR + "AirWizard/changephase.wav");
    public static final Sound Mob_wizard_attack = new Sound(MOB_SOUNDS_DIR + "AirWizard/wizardattack.wav");
    
    // Keeper
    public static final Sound Mob_keeper_death = new Sound(MOB_SOUNDS_DIR + "Keeper/keeperdeath.wav");

    // Eye Queen
    public static final Sound Mob_eyeBoss_death = new Sound(MOB_SOUNDS_DIR + "EyeQueen/eyedeath.wav");
    public static final Sound Mob_eyeBoss_changePhase = new Sound(MOB_SOUNDS_DIR + "EyeQueen/changephase.wav");

    // Creeper
    public static final Sound Mob_creeper_fuse = new Sound(MOB_SOUNDS_DIR + "Creeper/fuse.wav");
    public static final Sound Mob_creeper_explode = new Sound(MOB_SOUNDS_DIR + "Creeper/explode.wav");
    public static final Sound Mob_creeper_explode_2 = new Sound(MOB_SOUNDS_DIR + "Creeper/explode 2.wav");
    public static final Sound Mob_creeper_explode_3 = new Sound(MOB_SOUNDS_DIR + "Creeper/explode 3.wav");
    public static final Sound Mob_creeper_explode_4 = new Sound(MOB_SOUNDS_DIR + "Creeper/explode 4.wav");

    // Particles sounds
    // =============================================================================================================

    // Sparks
    public static final Sound Particle_spark_spawn = new Sound(PARTICLE_SOUNDS_DIR + "Spark/spawn.wav");

    // Furniture sounds
    // =============================================================================================================

    // Spawner
    public static final Sound Furniture_spawner_destroy = new Sound(FURNITURE_SOUNDS_DIR + "Spawner/destroy.wav");
    public static final Sound Furniture_spawner_destroy_2 = new Sound(FURNITURE_SOUNDS_DIR + "Spawner/destroy 2.wav");
    public static final Sound Furniture_spawner_destroy_3 = new Sound(FURNITURE_SOUNDS_DIR + "Spawner/destroy 3.wav");
    public static final Sound Furniture_spawner_spawn = new Sound(FURNITURE_SOUNDS_DIR + "Spawner/spawn.wav");
    public static final Sound Furniture_spawner_hurt = new Sound(FURNITURE_SOUNDS_DIR + "Spawner/hurt.wav");

    // Tnt
    public static final Sound Furniture_tnt_fuse = new Sound(FURNITURE_SOUNDS_DIR + "Tnt/fuse.wav");
    public static final Sound Furniture_tnt_explode = new Sound(FURNITURE_SOUNDS_DIR + "Tnt/explode.wav");
    public static final Sound Furniture_tnt_explode_2 = new Sound(FURNITURE_SOUNDS_DIR + "Tnt/explode 2.wav");
    public static final Sound Furniture_tnt_explode_3 = new Sound(FURNITURE_SOUNDS_DIR + "Tnt/explode 3.wav");
    public static final Sound Furniture_tnt_explode_4 = new Sound(FURNITURE_SOUNDS_DIR + "Tnt/explode 4.wav");

    // GUI sounds
    // ===================================================================================================================

    // Menu
    public static final Sound Menu_back = new Sound(MENU_SOUNDS_DIR + "back.wav");
    public static final Sound Menu_select = new Sound(MENU_SOUNDS_DIR + "select.wav");
    public static final Sound Menu_confirm = new Sound(MENU_SOUNDS_DIR + "confirm.wav");
    public static final Sound Menu_loaded = new Sound(MENU_SOUNDS_DIR + "loaded.wav");
    public static final Sound Menu_page_up = new Sound(MENU_SOUNDS_DIR + "page up.wav");

    public static final Sound Intro = new Sound("/resources/sounds/music/title/Intro.wav");
    public static final Sound Intro2 = new Sound("/resources/sounds/music/title/Intro 2.wav");

    public static final Sound Amulet_locked = new Sound("/resources/sounds/music/amulet/Amulet locked.wav");
    public static final Sound Amulet_locked_2 = new Sound("/resources/sounds/music/amulet/Amulet locked 2.wav");
    public static final Sound Amulet_sucess = new Sound("/resources/sounds/music/amulet/Amulet sucess.wav");

    // Cave and ambience sounds
    public static final Sound Ambience1 = new Sound("/resources/sounds/ambient/Ambience1.wav");
    public static final Sound Ambience2 = new Sound("/resources/sounds/ambient/Ambience2.wav");
    public static final Sound Ambience3 = new Sound("/resources/sounds/ambient/Ambience3.wav");
    public static final Sound Ambience4 = new Sound("/resources/sounds/ambient/Ambience4.wav");
    public static final Sound Ambience5 = new Sound("/resources/sounds/ambient/Ambience5.wav");

    public static final Sound HeavenAmbience = new Sound("/resources/sounds/ambient/HeavenAmbience.wav");

    // Snow tile
    public static final Sound Tile_snow = new Sound(TILE_SOUNDS_DIR + "Snow/snow.wav");
    public static final Sound Tile_snow_2 = new Sound(TILE_SOUNDS_DIR + "Snow/snow 2.wav");
    public static final Sound Tile_snow_3 = new Sound(TILE_SOUNDS_DIR + "Snow/snow 3.wav");
    public static final Sound Tile_snow_4 = new Sound(TILE_SOUNDS_DIR + "Snow/snow 4.wav");
    
    // Farmland and Sky farmland tile
    public static final Sound Tile_farmland = new Sound(TILE_SOUNDS_DIR + "Farmland/farmland.wav");
    public static final Sound Tile_farmland_2 = new Sound(TILE_SOUNDS_DIR + "Farmland/farmland 2.wav");
    public static final Sound Tile_farmland_3 = new Sound(TILE_SOUNDS_DIR + "Farmland/farmland 3.wav");

    public static final Sound Tile_generic_hurt = new Sound(TILE_SOUNDS_DIR + "tilehurt.wav");

    // Themes
    public static final Sound Theme_Fall = new Sound("/resources/sounds/ambient/music/Fall.wav");
    public static final Sound Theme_Surface = new Sound("/resources/sounds/ambient/music/Surface.wav");
    public static final Sound Theme_Peaceful = new Sound("/resources/sounds/ambient/music/Peaceful.wav");
    public static final Sound Theme_Cave = new Sound("/resources/sounds/ambient/music/Cave.wav");
    public static final Sound Theme_Cavern = new Sound("/resources/sounds/ambient/music/Cavern.wav");
    public static final Sound Theme_Cavern_drip = new Sound("/resources/sounds/ambient/music/Cavern drip.wav");

    private Clip clip; // Creates a audio clip to be played

    public static void init() {
    } // A way to initialize the class without actually doing anything

    private Sound(String name) {
        if (!Game.HAS_GUI) {
            return;
        }

        try {
            URL url = getClass().getResource(name);

            DataLine.Info info = new DataLine.Info(Clip.class, AudioSystem.getAudioFileFormat(url).getFormat());

            if (!AudioSystem.isLineSupported(info)) {
                System.err.println("ERROR: Audio format of file " + name + " is not supported: "
                        + AudioSystem.getAudioFileFormat(url));

                System.out.println("Supported audio formats:");
                System.out.println("-source:");

                Line.Info[] sinfo = AudioSystem.getSourceLineInfo(info);
                Line.Info[] tinfo = AudioSystem.getTargetLineInfo(info);

                for (int i = 0; i < sinfo.length; i++) {
                    if (sinfo[i] instanceof DataLine.Info) {

                        DataLine.Info dataLineInfo = (DataLine.Info) sinfo[i];
                        AudioFormat[] supportedFormats = dataLineInfo.getFormats();

                        for (AudioFormat af : supportedFormats) {
                            System.out.println(af);
                        }
                    }
                }
                System.out.println("-target:");
                for (int i = 0; i < tinfo.length; i++) {
                    if (tinfo[i] instanceof DataLine.Info) {

                        DataLine.Info dataLineInfo = (DataLine.Info) tinfo[i];
                        AudioFormat[] supportedFormats = dataLineInfo.getFormats();

                        for (AudioFormat af : supportedFormats) {
                            System.out.println(af);
                        }
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

    public void play() { // This plays the clip only once, Syntax: Sound.clip.play();
        if (!(boolean) Settings.get("sound") || clip == null) {
            return;
        }
        if (Game.isValidServer()) {
            return;
        }
        if (clip.isRunning() || clip.isActive()) {
            clip.stop();
        }
        clip.start();
    }

    public void loop(boolean start) { // This repeats the same clip over and over again,
        if (!(boolean) Settings.get("sound") || clip == null) {
            return;
        }
        if (start) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            clip.stop();
        }
    }

    public void stop() { // This stops the clip
        clip.stop();
    }

}