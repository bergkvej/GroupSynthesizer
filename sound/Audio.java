package sound;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.stage.FileChooser;
import wave.SawtoothWave;
import wave.SineWave;
import wave.SquareWave;
import wave.TriangleWave;
import wave.Wave;

public class Audio {
	
	private static Mixer bestMixer;
	static boolean fading = false;
	static float currentGain;
	static float targetGain = -50.0f;
	static float fadePerStep = .05f;
	static FloatControl gainControl;
	
	static {
		bestMixer = getBestMixer();
		printMixers();
	}
	
	static Clip getClip(Sound sound, float gain) throws LineUnavailableException {
		Clip clip = getClip(bestMixer, sound.getFormat());
		
		clip.open(sound.getFormat(), sound.getData(), 0, sound.getData().length);
		clip.stop();

		clip.setLoopPoints(0, -1);
		clip.loop(Clip.LOOP_CONTINUOUSLY);
		gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(gain);
		currentGain = gain;		
		//shiftGain(1.0f);
		return clip;
	}
	public static void shiftGain(double value) 
	{
		if(value < 0.0)
		{
			value = 0.0;
		}
		else if(value >= 1.0)
		{
			value = 1.0;
		}		
		final double newValue = value;
			new Thread() {
				public void run()
				{
					fading = newValue == 0 ? false: true;
					
					long initialTime = System.currentTimeMillis();
					long lastTimePassed = 0;
					int timeToFade = (int) (5000 * newValue);
					while(fading) {
						int timePassed = (int)(System.currentTimeMillis() - initialTime);
						if(timePassed > lastTimePassed) {
							float value0 = -70 + 76 * (float)timePassed / (float)timeToFade;
							//System.out.println(timePassed + " " + value);
							gainControl.setValue(value0);
							
							if(timePassed >= timeToFade) {
								fading = false;
							}
							lastTimePassed = timePassed;
						}
					}
				}
			}.start();
		
		
	}
	

	public static Mixer getBestMixer() {
		//Create a list of mixers to parse through later.
		ArrayList<Mixer> goodMixers = new ArrayList<Mixer>();
		for(Mixer.Info mixerInfo: AudioSystem.getMixerInfo()) {
			//We are only looking for target mixers.
			if(mixerInfo.getDescription().contains("Microphone") || mixerInfo.getDescription().contains("microphone")) {
				continue;
			}
			Mixer mixer = AudioSystem.getMixer(mixerInfo);
			for(Line.Info lineInfo: mixer.getTargetLineInfo()) {
				//If the mixer has an unspecified amount of lines, choose that mixer.
				if(mixer.getMaxLines(lineInfo) == AudioSystem.NOT_SPECIFIED) {
					return mixer;
				}
				
				if(mixer.getMaxLines(lineInfo) != 0) {
					if(!goodMixers.contains(mixer)) {
						goodMixers.add(mixer);
					}
				}
			}
		}
		
		//If we get here then we have to parse through the mixers again and find the best one.
		Mixer mixerToReturn = null;
		int maxLines = 0;
		for(Mixer mixer: goodMixers) {
			for(Line.Info lineInfo: mixer.getTargetLineInfo()) {
				if(mixer.getMaxLines(lineInfo) > maxLines) {
					maxLines = mixer.getMaxLines(lineInfo);
					mixerToReturn = mixer;
				}
			}
		}
		return mixerToReturn;
	}
	
	public static Clip getClip(Mixer mixer, AudioFormat format) throws LineUnavailableException {
		DataLine.Info clipInfo = new DataLine.Info(Clip.class, format);
		Clip clip = (Clip) mixer.getLine(clipInfo);
		return clip;
	}
	
	public static Sound getSound(File file) throws UnsupportedAudioFileException, IOException {
		AudioFileFormat format = AudioSystem.getAudioFileFormat(file);
		AudioInputStream stream = AudioSystem.getAudioInputStream(file);
		byte[] data = new byte[format.getByteLength()];
		stream.read(data, 0, data.length);
		
		Sound sound = new Sound(data, format.getFormat());
		return sound;
	}
	
	public static void printMixers() {
		for(Mixer.Info mixerInfo: AudioSystem.getMixerInfo()) {
			System.out.println(mixerInfo.getDescription());
		}
	}
	
}
