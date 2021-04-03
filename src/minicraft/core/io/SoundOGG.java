package minicraft.core.io;

import java.io.IOException;

import org.newdawn.easyogg.OggClip;

public class SoundOGG {
	// Ogg support! ;)

	// Player
	public static final SoundOGG oplayerHurt = new SoundOGG("/resources/sound/entity/Player/playerhurt.wav");
	public static final SoundOGG oplayerDeath = new SoundOGG("/resources/sound/entity/Player/death.wav");
	public static final SoundOGG oHeart = new SoundOGG("/resources/sound/entity/Player/hearth.wav");
	public static final SoundOGG ocraft = new SoundOGG("/resources/sound/entity/Player/craft.wav");

	public static final SoundOGG omonsterHurt = new SoundOGG("/resources/sound/monsterhurt.wav");

	public static final SoundOGG oDestroySpawner = new SoundOGG("/resources/sound/DestroySpawner.wav");
	public static final SoundOGG oDestroySpawner2 = new SoundOGG("/resources/sound/DestroySpawner 2.wav");
	public static final SoundOGG oDestroySpawner3 = new SoundOGG("/resources/sound/DestroySpawner 3.wav");

	// Air Wizard
	public static final SoundOGG obossDeath = new SoundOGG("/resources/sound/entity/AirWizard/bossdeath.wav");
	public static final SoundOGG ochangePhase = new SoundOGG("/resources/sound/entity/AirWizard/changephase.wav");
	public static final SoundOGG owizardAttack = new SoundOGG("/resources/sound/entity/AirWizard/wizardattack.wav");

	// Eye Queen
	public static final SoundOGG oeyeBossDeath = new SoundOGG("/resources/sound/entity/EyeQueen/eyedeath.wav");

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
