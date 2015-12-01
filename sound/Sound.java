package sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;

import wave.Wave;

public class Sound {
	
	private final byte[] data;
	private final AudioFormat format;
	private final Wave wave;
	private Clip clip;
	
	public Sound(byte[] data, AudioFormat format, Wave wave) {
		this.data = data;
		this.format = format;
		this.wave = wave;
	}
	
	public Sound(byte[] data, AudioFormat format) {
		this(data,format,null);
	}
	
	public byte[] getData() {
		return data;
	}
	
	public AudioFormat getFormat() {
		return format;
	}
	
	//Returns true if audio starts playing, else returns false.
	public boolean start() {
		if(clip != null) {
			clip.start();
			return true;
		}
		try {
			clip = Audio.getClip(this);
			return true;
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void stop() {
		clip.stop();
	}
	
	public boolean setGain(float gain) {
		
		System.out.println(clip.getControls().length);
		for(Control control: clip.getControls()) {
			System.out.println(control.getType());
		}
		
		
		if(clip != null && clip.isActive() && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(gain);
			return true;
		}
		return false;
	}
	
	public String toString() {
		return "Length in bytes: " + data.length + "\nAudio Format: " + format.toString() + "\nWave info: " + wave.toString();
	}
}
