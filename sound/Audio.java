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
	
	static {
		bestMixer = getBestMixer();
	}
	
	public static void main(String[] args) {
		File file = new File("/Users/jesse/Documents/workspace_new/Real Synthesizer/res/pew.wav");
		
		Sound sound = null;
		try {
			sound = getSound(file);
		} catch (UnsupportedAudioFileException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		sound.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(sound.setGain(-10));
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sound.stop();
	}
	
	//returns true if gain was set
	public static boolean setMasterGain(float gain) {
		if(bestMixer != null && bestMixer.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
			FloatControl gainControl = (FloatControl) bestMixer.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(gain);
			return true;
		}
		return false;
	}
	
	static Clip getClip(Sound sound) throws LineUnavailableException {
		Clip clip = getClip(bestMixer, sound.getFormat());
		
		clip.open(sound.getFormat(), sound.getData(), 0, sound.getData().length);
		clip.stop();

		clip.setLoopPoints(0, -1);
		clip.loop(Clip.LOOP_CONTINUOUSLY);
		
		return clip;
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
	
}
