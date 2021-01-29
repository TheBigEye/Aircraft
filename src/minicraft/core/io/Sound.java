package minicraft.core.io;

import javax.sound.sampled.*;

import java.io.IOException;
import java.net.URL;

import minicraft.core.Game;

public class Sound {
	//creates sounds from their respective files
	public static final Sound playerHurt = new Sound("/resources/sound/playerhurt.wav");
	public static final Sound playerDeath = new Sound("/resources/sound/death.wav");
	public static final Sound Heart = new Sound("/resources/sound/hearth.wav");
	public static final Sound monsterHurt = new Sound("/resources/sound/monsterhurt.wav");
	
	public static final Sound DestroySpawner = new Sound("/resources/sound/DestroySpawner.wav");
	public static final Sound DestroySpawner2 = new Sound("/resources/sound/DestroySpawner 2.wav");
	public static final Sound DestroySpawner3 = new Sound("/resources/sound/DestroySpawner 3.wav");	
	
	public static final Sound bossDeath = new Sound("/resources/sound/bossdeath.wav");
	public static final Sound changePhase = new Sound("/resources/sound/changephase.wav");
	public static final Sound wizardAttack = new Sound("/resources/sound/wizardattack.wav");
	public static final Sound fuse = new Sound("/resources/sound/fuse.wav");
	
	public static final Sound explode = new Sound("/resources/sound/explode.wav");
	public static final Sound explode2 = new Sound("/resources/sound/explode 2.wav");
	public static final Sound explode3 = new Sound("/resources/sound/explode 3.wav");
	public static final Sound explode4 = new Sound("/resources/sound/explode 4.wav");
	
	public static final Sound pickup = new Sound("/resources/sound/pickup.wav");
	public static final Sound pickup2 = new Sound("/resources/sound/pickup 2.wav");
	public static final Sound pickup3 = new Sound("/resources/sound/pickup 3.wav");
	public static final Sound pickup4 = new Sound("/resources/sound/pickup 4.wav");
	
	public static final Sound craft = new Sound("/resources/sound/craft.wav");
	public static final Sound back = new Sound("/resources/sound/craft.wav");
	public static final Sound select = new Sound("/resources/sound/select.wav");
	public static final Sound confirm = new Sound("/resources/sound/confirm.wav");
	public static final Sound PageUp = new Sound("/resources/sound/Page Up.wav");
	
	public static final Sound Intro = new Sound("/resources/sound/Music/Intro.wav");
	public static final Sound Intro2 = new Sound("/resources/sound/Music/Intro 2.wav");
	public static final Sound Intro3 = new Sound("/resources/sound/Music/Intro 3.wav");
	
	public static final Sound Call = new Sound("/resources/sound/Music/Call.wav");
	
	// Cave and ambience sounds
	public static final Sound Ambience1 = new Sound("/resources/sound/Enviroment/Ambience1.wav");
	public static final Sound Ambience2 = new Sound("/resources/sound/Enviroment/Ambience2.wav");
	public static final Sound Ambience3 = new Sound("/resources/sound/Enviroment/Ambience3.wav");
	public static final Sound Ambience4 = new Sound("/resources/sound/Enviroment/Ambience4.wav");
	public static final Sound Ambience5 = new Sound("/resources/sound/Enviroment/Ambience5.wav");
	//public static final Sound Ambience6 = new Sound("/resources/sound/Enviroment/Ambience6.wav");
	public static final Sound HeavenAmbience = new Sound("/resources/sound/Enviroment/HeavenAmbience.wav");
	
	public static final Sound Snow = new Sound("/resources/sound/Tile/Snow.wav");
	public static final Sound Snow2 = new Sound("/resources/sound/Tile/Snow 2.wav");
	public static final Sound Snow3 = new Sound("/resources/sound/Tile/Snow 3.wav");
	public static final Sound Snow4 = new Sound("/resources/sound/Tile/Snow 4.wav");

	
	private Clip clip; // Creates a audio clip to be played
	
	public static void init() {} // a way to initialize the class without actually doing anything
	
	private Sound(String name) {
		if(!Game.HAS_GUI) return;
		
		try {
			URL url = getClass().getResource(name);
			
			DataLine.Info info = new DataLine.Info(Clip.class, AudioSystem.getAudioFileFormat(url).getFormat());
			
			if(!AudioSystem.isLineSupported(info)) {
				System.err.println("ERROR: Audio format of file " + name + " is not supported: " + AudioSystem.getAudioFileFormat(url));
				
				System.out.println("Supported audio formats:");
				System.out.println("-source:");
				Line.Info[] sinfo = AudioSystem.getSourceLineInfo(info);
				Line.Info[] tinfo = AudioSystem.getTargetLineInfo(info);
				for (int i = 0; i < sinfo.length; i++)
				{
					if (sinfo[i] instanceof DataLine.Info)
					{
						DataLine.Info dataLineInfo = (DataLine.Info) sinfo[i];
						AudioFormat[] supportedFormats = dataLineInfo.getFormats();
						for(AudioFormat af: supportedFormats)
							System.out.println(af);
					}
				}
				System.out.println("-target:");
				for (int i = 0; i < tinfo.length; i++)
				{
					if (tinfo[i] instanceof DataLine.Info)
					{
						DataLine.Info dataLineInfo = (DataLine.Info) tinfo[i];
						AudioFormat[] supportedFormats = dataLineInfo.getFormats();
						for(AudioFormat af: supportedFormats)
							System.out.println(af);
					}
				}
				
				return;
			}
			
			clip = (Clip)AudioSystem.getLine(info);
			clip.open(AudioSystem.getAudioInputStream(url));
			
			clip.addLineListener(e -> {
				if(e.getType() == LineEvent.Type.STOP) {
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
		if (!(boolean)Settings.get("sound") || clip == null) return;
		if(Game.isValidServer()) return;
		
		if(clip.isRunning() || clip.isActive())
			clip.stop();
		
		clip.start();
	}
	

	// This repeats the same clip over and over again, Syntax: Sound.clip.loop(true);
	public void loop(boolean start) {
		if (!(boolean)Settings.get("sound") || clip == null) return;
		
		if(start)
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		else
			clip.stop();
	}
	
	// This stops the clip, Syntax: Sound.clip.stop();
	public void stop() {
		clip.stop();
		
	}
	
}
