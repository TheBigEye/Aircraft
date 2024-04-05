package minicraft.core.io;

import java.io.BufferedInputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

import com.jogamp.openal.AL;
import com.jogamp.openal.ALC;
import com.jogamp.openal.ALException;
import com.jogamp.openal.ALFactory;
import com.jogamp.openal.util.ALut;

import minicraft.core.Game;
import minicraft.entity.mob.Player;

// Creates sounds from their respective files
public class Sound { 
    private static final HashMap<String, Sound> sounds = new HashMap<>();

	static AL al = ALFactory.getAL();
	static ALC alc = ALFactory.getALC();
	
	private final int source;

    public static void initialize() {
    	Logger.debug("Initializing sound engine ...");
    	
        while (true) {
            try {
                ALut.alutInit();
                al.alGetError();
                break; // Salir del bucle si la inicialización tiene éxito
            } catch (ALException e) {
            }
        }
    	
    	// This sucks, but is better thant statics variables for each one :)
    	
    	String[][] sounds = {
            {"playerHurt", 			"/resources/sounds/mob/player/hurt.wav"},
            {"playerDeath", 		"/resources/sounds/mob/player/death.wav"},
            {"playerCraft", 		"/resources/sounds/mob/player/craft.wav"},
            {"playerPickup", 		"/resources/sounds/mob/player/pickup.wav"},
            {"playerPlace", 		"/resources/sounds/mob/player/place.wav"},
            {"playerChangeLevel", 	"/resources/sounds/mob/player/changelevel.wav"},
            
            {"sheepSay1", 			"/resources/sounds/mob/sheep/say1.wav"},
            {"sheepSay2", 			"/resources/sounds/mob/sheep/say2.wav"},
            {"sheepSay3", 			"/resources/sounds/mob/sheep/say3.wav"},
            
            {"cowSay1", 			"/resources/sounds/mob/cow/say1.wav"},
            {"cowSay2", 			"/resources/sounds/mob/cow/say2.wav"},
            {"cowSay3", 			"/resources/sounds/mob/cow/say3.wav"},
            
            {"pigSay1", 			"/resources/sounds/mob/pig/say1.wav"},
            {"pigSay2", 			"/resources/sounds/mob/pig/say2.wav"},
            {"pigSay3", 			"/resources/sounds/mob/pig/say3.wav"},
            
            {"chickenSay1", 		"/resources/sounds/mob/chicken/say1.wav"},
            {"chickenSay2", 		"/resources/sounds/mob/chicken/say2.wav"},
            {"chickenSay3", 		"/resources/sounds/mob/chicken/say3.wav"},
                     
            {"wizardDeath", 		"/resources/sounds/mob/airwizard/bossdeath.wav"},
            {"wizardAttack", 		"/resources/sounds/mob/airwizard/wizardattack.wav"},
            {"wizardChangePhase", 	"/resources/sounds/mob/airwizard/changephase.wav"},
            {"wizardSpawnSpark", 	"/resources/sounds/mob/airwizard/spawnspark.wav"},
            
            {"keeperDeath", 		"/resources/sounds/mob/keeper/keeperdeath.wav"},
            
            {"eyeQueenDeath", 		"/resources/sounds/mob/eyequeen/eyedeath.wav"},
            {"eyeQueenChangePhase", "/resources/sounds/mob/eyequeen/changephase.wav"},
            
            {"dungeonChest1", 		"/resources/sounds/furniture/dungeonchest/dungeonchest1.wav"},
            {"dungeonChest2", 		"/resources/sounds/furniture/dungeonchest/dungeonchest2.wav"},
            {"dungeonChest3", 		"/resources/sounds/furniture/dungeonchest/dungeonchest3.wav"},
            
            {"spawnerSpawn", 		"/resources/sounds/furniture/spawner/spawn.wav"},
            {"spawnerHurt", 		"/resources/sounds/furniture/spawner/hurt.wav"},
            {"spawnerDestroy1", 	"/resources/sounds/furniture/spawner/destroy1.wav"},
            {"spawnerDestroy2", 	"/resources/sounds/furniture/spawner/destroy2.wav"},
            {"spawnerDestroy3", 	"/resources/sounds/furniture/spawner/destroy3.wav"},
            
            {"Menu_back", 			"/resources/sounds/gui/back.wav"},
            {"Menu_select", 		"/resources/sounds/gui/select.wav"},
            {"Menu_confirm", 		"/resources/sounds/gui/confirm.wav"},
            {"Menu_loaded", 		"/resources/sounds/gui/loaded.wav"},
            {"Menu_page_up", 		"/resources/sounds/gui/page up.wav"},
                       
            {"Ambience1", 			"/resources/sounds/ambient/Ambience1.wav"},
            {"Ambience2", 			"/resources/sounds/ambient/Ambience2.wav"},
            {"Ambience3", 			"/resources/sounds/ambient/Ambience3.wav"},
            {"Ambience4", 			"/resources/sounds/ambient/Ambience4.wav"},
            {"Ambience5", 			"/resources/sounds/ambient/Ambience5.wav"},
            
            {"Sky_enviroment", 		"/resources/sounds/ambient/sky environment.wav"},
            
            {"Tile_snow", 			"/resources/sounds/tiles/Snow/snow.wav"},
            {"Tile_snow_2", 		"/resources/sounds/tiles/Snow/snow 2.wav"},
            {"Tile_snow_3", 		"/resources/sounds/tiles/Snow/snow 3.wav"},
            {"Tile_snow_4", 		"/resources/sounds/tiles/Snow/snow 4.wav"},
            {"Tile_farmland", 		"/resources/sounds/tiles/Farmland/farmland.wav"},
            {"Tile_farmland_2", 	"/resources/sounds/tiles/Farmland/farmland 2.wav"},
            {"Tile_farmland_3", 	"/resources/sounds/tiles/Farmland/farmland 3.wav"},
            
            {"musicTheme1", 		"/resources/sounds/music/fall.wav"},
            {"musicTheme2", 		"/resources/sounds/music/surface.wav"},
            {"musicTheme3", 		"/resources/sounds/music/paradise.wav"},
            {"musicTheme4", 		"/resources/sounds/music/peaceful.wav"},
            {"musicTheme5", 		"/resources/sounds/music/cave.wav"},
            {"musicTheme6", 		"/resources/sounds/music/cavern.wav"},
            {"musicTheme7", 		"/resources/sounds/music/dripping.wav"},
            {"musicTheme8", 		"/resources/sounds/music/deeper.wav"},
            
            {"rainThunder1", 		"/resources/sounds/ambient/weather/thunder1.wav"},
            {"rainThunder2", 		"/resources/sounds/ambient/weather/thunder2.wav"},
            {"rainThunder3", 		"/resources/sounds/ambient/weather/thunder3.wav"},
            
            {"genericExplode", 		"/resources/sounds/genericExplode.wav"},
            {"genericFuse", 		"/resources/sounds/genericFuse.wav"},
            {"genericHurt", 		"/resources/sounds/genericHurt.wav"}
        };

        for (String[] sound : sounds) {
            loadSound(sound[0], new BufferedInputStream(Game.class.getResourceAsStream(sound[1])));
        }
    }
    
	public static void shutdown() {
		Logger.debug("Shutting down sound engine ...");
		
		ALut.alutExit();
		
		sounds.clear();
	}

	private Sound(int source) {
		this.source = source;
	}
	
	public static void resetSounds() {
		sounds.clear();
	}
	
	public static void loadSound(String key, BufferedInputStream in) {

		if (Game.debug) Logger.debug("Loading sound clip '{}' ...", key);

		int[] buffer = new int[1];
		int[] source = new int[1];

		int[] format = new int[1];
		int[] size = new int[1];
		
		ByteBuffer[] data = new ByteBuffer[1];
		
		int[] freq = new int[1];
		int[] loop = new int[1];
		al.alGenBuffers(1, buffer, 0);
		
		ALut.alutLoadWAVFile(in, format, data, size, freq, loop);
		al.alBufferData(buffer[0], format[0], data[0], size[0], freq[0]);

		al.alGenSources(1, source, 0);
		al.alSourcei(source[0], AL.AL_BUFFER, buffer[0]);
		sounds.put(key, new Sound(source[0]));
	}

	@Nullable
	public static Sound getSound(String key) {
		return sounds.get(key);
	}
	
	public static void play(String key) {
		Sound sound = sounds.get(key);
		// Logger.debug("Playing sound clip '{}' ...", key);
		if (sound != null) sound.play();
	}
	
	public static void playAt(String key, int x, int y) {
		Sound sound = sounds.get(key);
		// Logger.debug("Playing sound clip '{}', at ({}, {}) ...", key, x, y);
		if (sound != null) sound.playAt(x, y);
	}
	
	public static void loop(String key, boolean start) {
		Sound sound = sounds.get(key);
		// Logger.debug("Playing as loop sound clip '{}' ...", key);
		if (sound != null) sound.loop(start);
	}
	
	public static void stop(String key) {
		Sound sound = sounds.get(key);
		if (sound != null) al.alSourceStop(sound.source);
	}
    
	private void playAt(int x, int y) {
	    if (!Settings.getBoolean("sound")) {
	        return;
	    }

	    Player player = Game.levels[Game.currentLevel].getClosestPlayer(x, y);
	    
	    if (player == null) {
	        return;
	    }

	    // Play the source without setting the volume
	    al.alSourcePlay(source);

        // Calculate the distance between the sound source and the player
        double distance = Math.sqrt(Math.pow(x - player.x, 2) + Math.pow(y - player.y, 2));

        // TODO: improve this using an thread to dynamically change the 'Sound Gain' while the player moves
        	
        // Set the volume based on the distance from the player
        float volume = 1.0f - (float) distance / 180.0f;
        if (volume < 0.0f) {
            return;
        }

        // Set the source volume
        al.alSourcef(source, AL.AL_GAIN, volume);
	}

    private void play() {
        if (!Settings.getBoolean("sound")) {
            return;
        }
        
        al.alSourcePlay(source);
    }


    private void loop(boolean start) {
        if (!Settings.getBoolean("sound")) {
            return;
        }
        
        if (start) {
            al.alSourcei(source, AL.AL_LOOPING, AL.AL_TRUE);
            al.alSourcePlay(source);
        } else {
            al.alSourcei(source, AL.AL_LOOPING, AL.AL_FALSE);
            al.alSourceStop(source);
        }
    }
}