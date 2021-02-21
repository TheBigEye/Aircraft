package minicraft.core.io;

import java.io.IOException;

import org.newdawn.easyogg.OggClip;

public class MusicPlayer {
	// No one wants a music player crashing their game... ;)
	
	// Player
	public static final MusicPlayer oplayerHurt = new MusicPlayer("/resources/sound/entity/Player/playerhurt.wav");
	public static final MusicPlayer oplayerDeath = new MusicPlayer("/resources/sound/entity/Player/death.wav");
	public static final MusicPlayer oHeart = new MusicPlayer("/resources/sound/entity/Player/hearth.wav");
	public static final MusicPlayer ocraft = new MusicPlayer("/resources/sound/entity/Player/craft.wav");
	
	public static final MusicPlayer omonsterHurt = new MusicPlayer("/resources/sound/monsterhurt.wav");
	
	public static final MusicPlayer oDestroySpawner = new MusicPlayer("/resources/sound/DestroySpawner.wav");
	public static final MusicPlayer oDestroySpawner2 = new MusicPlayer("/resources/sound/DestroySpawner 2.wav");
	public static final MusicPlayer oDestroySpawner3 = new MusicPlayer("/resources/sound/DestroySpawner 3.wav");	
	
	// Air Wizard
	public static final MusicPlayer obossDeath = new MusicPlayer("/resources/sound/entity/AirWizard/bossdeath.wav");
	public static final MusicPlayer ochangePhase = new MusicPlayer("/resources/sound/entity/AirWizard/changephase.wav");
	public static final MusicPlayer owizardAttack = new MusicPlayer("/resources/sound/entity/AirWizard/wizardattack.wav");
	
	// Eye Queen
	public static final MusicPlayer oeyeBossDeath = new MusicPlayer("/resources/sound/entity/EyeQueen/eyedeath.wav");
	
	OggClip ogg;
	
	private MusicPlayer(String filename) {
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
