package minicraft.core.io;

import com.jogamp.openal.AL;
import com.jogamp.openal.ALC;
import com.jogamp.openal.ALException;
import com.jogamp.openal.ALFactory;
import com.jogamp.openal.util.ALut;
import minicraft.core.Game;
import minicraft.entity.mob.Player;
import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

import java.io.BufferedInputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Objects;

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
                break;
            } catch (ALException ignored) {
                // Sorry, ignored! LOL
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
            {"eyeQueenPuff", 		"/resources/sounds/mob/eyequeen/eyepuff.wav"},
            {"eyeQueenChangePhase", "/resources/sounds/mob/eyequeen/changephase.wav"},

            {"dungeonChest1", 		"/resources/sounds/furniture/dungeonchest/dungeonchest1.wav"},
            {"dungeonChest2", 		"/resources/sounds/furniture/dungeonchest/dungeonchest2.wav"},
            {"dungeonChest3", 		"/resources/sounds/furniture/dungeonchest/dungeonchest3.wav"},

            {"spawnerDestroy1", 	"/resources/sounds/furniture/spawner/destroy1.wav"},
            {"spawnerDestroy2", 	"/resources/sounds/furniture/spawner/destroy2.wav"},
            {"spawnerDestroy3", 	"/resources/sounds/furniture/spawner/destroy3.wav"},

            {"menuBack", 			"/resources/sounds/gui/back.wav"},
            {"menuSelect", 		    "/resources/sounds/gui/select.wav"},
            {"menuConfirm", 		"/resources/sounds/gui/confirm.wav"},
            {"menuLoaded", 		    "/resources/sounds/gui/loaded.wav"},

            {"caveMood", 		    "/resources/sounds/ambient/cavemood.wav"},
            {"caveBreath", 		    "/resources/sounds/ambient/cavebreath.wav"},
            {"caveScream", 		    "/resources/sounds/ambient/cavescream.wav"},
            {"heavenWind", 		    "/resources/sounds/ambient/heavenwind.wav"},

            {"rainThunder1", 		"/resources/sounds/ambient/thunder1.wav"},
            {"rainThunder2", 		"/resources/sounds/ambient/thunder2.wav"},
            {"rainThunder3", 		"/resources/sounds/ambient/thunder3.wav"},

            {"musicTheme1", 		"/resources/sounds/music/fall.wav"},
            {"musicTheme2", 		"/resources/sounds/music/surface.wav"},
            {"musicTheme3", 		"/resources/sounds/music/paradise.wav"},
            {"musicTheme4", 		"/resources/sounds/music/peaceful.wav"},
            {"musicTheme5", 		"/resources/sounds/music/cave.wav"},
            {"musicTheme6", 		"/resources/sounds/music/cavern.wav"},
            {"musicTheme8", 		"/resources/sounds/music/deeper.wav"},

            {"genericExplode", 		"/resources/sounds/genericExplode.wav"},
            {"genericFuse", 		"/resources/sounds/genericFuse.wav"},
            {"genericHurt", 		"/resources/sounds/genericHurt.wav"}
        };

        for (String[] sound : sounds) {
            loadSound(sound[0], new BufferedInputStream(Objects.requireNonNull(Game.class.getResourceAsStream(sound[1]))));
        }
    }

	public static void shutdown() {
		Logger.info("Shutting down sound engine ...");
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
		if (sound != null) sound.playAt(x, y, true);
	}

	public static void playAt(String key, int x, int y, boolean async) {
		Sound sound = sounds.get(key);
		// Logger.debug("Playing sound clip '{}', at ({}, {}) ...", key, x, y);
		if (sound != null) sound.playAt(x, y, async);
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

	private void playAt(int x, int y, boolean async) {
	    if (!Settings.getBoolean("sound")) {
	        return;
	    }

	    Player player = Game.levels[Game.currentLevel].getClosestPlayer(x, y);

	    if (player == null) {
	        return;
	    }

	    if (!async) {
	    	int[] state = new int[1];
	    	al.alGetSourcei(source, AL.AL_SOURCE_STATE, state, 0);
	    	if (state[0] != AL.AL_PLAYING) {
	    		al.alSourcePlay(source);
	    	}
	    } else {
	    	al.alSourcePlay(source);
	    }

        // Math.hypot should fix that fu*ing precision overflow
        double distance = Math.hypot(x - player.x, y - player.y);

        // Set the volume based on the distance from the player
        float volume = 1.0f - ((float) distance / 176.0f);

        al.alSourcef(source, AL.AL_GAIN, Math.max(volume, 0.0f));
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
