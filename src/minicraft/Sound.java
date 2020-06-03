package minicraft;

import java.applet.Applet;
import java.applet.AudioClip;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import minicraft.Sound;

import minicraft.screen.OptionsMenu;

public class Sound {
	//creates sounds from their respective files
	public static final Sound playerHurt = loadSound("/playerhurt.wav");
	public static final Sound playerDeath = loadSound("/death.wav");
	public static final Sound monsterHurt = loadSound("/monsterhurt.wav");
	public static final Sound test = loadSound("/test.wav");
	public static final Sound pickup = loadSound("/pickup.wav");
	public static final Sound bossdeath = loadSound("/bossdeath.wav");
	public static final Sound craft = loadSound("/craft.wav");
	public static final Sound fuse = loadSound("/fuse.wav");
	public static final Sound explode = loadSound("/explode.wav");
	public static final Sound Disc11 = loadSound("/death.wav");

	public static Sound loadSound(String fileName) {
		Sound 	sound = new Sound();
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(Sound.class.getResource(fileName));
			Clip clip = AudioSystem.getClip();
			clip.open(ais);
			sound.clip = clip;
		} catch (Exception e) {
			System.out.println(e);
		}
		return sound;
	}

	private Clip clip;

	public void play() {
		try {
			if (clip != null) {
				new Thread() {
					public void run() {
						synchronized (clip) {
							clip.stop();
							clip.setFramePosition(0);
							clip.start();
						}
					}
				}.start();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void stop() {
		clip.stop();
		
	}
}