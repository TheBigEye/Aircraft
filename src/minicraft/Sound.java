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
	public static final Sound Disc11 = loadSound("/Discs/Disc 8.wav");
	
	//Discs
	public static final Sound Disc1 = loadSound("/Discs/Disc 1.wav");
	public static final Sound Disc2 = loadSound("/Discs/Disc 2.wav");
	public static final Sound Disc3 = loadSound("/Discs/Disc 3.wav");
	public static final Sound Disc4 = loadSound("/Discs/Disc 4.wav");
	public static final Sound Disc5 = loadSound("/Discs/Disc 5.wav");
	public static final Sound Disc6 = loadSound("/Discs/Disc 6.wav");
	public static final Sound Disc7 = loadSound("/Discs/Disc 7.wav");
	public static final Sound Disc8 = loadSound("/Discs/Disc 8.wav");

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