package sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;

public class Sound {
	
	private final byte[] data;
	private final AudioFormat format;
	private Clip clip;
	
	public Sound(byte[] data, AudioFormat format) {
		this.data = data;
		this.format = format;
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
		if(clip != null && clip.isActive() && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(gain);
			return true;
		}
		return false;
	}
}
