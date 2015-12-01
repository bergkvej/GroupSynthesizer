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
	boolean fading = false;
	float currentGain;
	float targetGain = -50.0f;
	float fadePerStep = 1f;
	
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
	public boolean start(float gain) {
		if(clip != null) {
			clip.start();
			return true;
		}
		try {
			clip = Audio.getClip(this, gain);
			return true;
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void stop() {
		clip.stop();
	}
	
	public void setGain(float gain) {
		
		System.out.println(clip.getControls().length);
		for(Control control: clip.getControls()) {
			System.out.println(control.getType());
		}
		if(clip != null && clip.isActive() && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(gain);
			currentGain = gain;
		}
	}
	//Must be a value between 0.0 and 1.0
		public void shiftGain(double value) 
		{
			if(value < 0.0)
			{
				value = 0.0;
			}
			else if(value >= 1.0)
			{
				value = 1.0;
			}
			targetGain = (float)(Math.log(value)/Math.log(10.0) * 20.0);
			if(!fading) {
				Thread t = new Thread();
				t.start();
			}
		}
		public void run()
		{
			fading = true;
			if(currentGain > targetGain) {
				while(currentGain > targetGain)
				{
					currentGain -= fadePerStep;
					System.out.println(currentGain);
					setGain(currentGain);
					try	{
						Thread.sleep(10);
					}
					catch(Exception e) {
						
					}
				}
			}
		}
	
	public String toString() {
		return "Length in bytes: " + data.length + "\nAudio Format: " + format.toString() + "\nWave info: " + wave.toString();
	}
}
