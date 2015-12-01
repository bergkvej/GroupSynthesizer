package wave;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.UnsupportedAudioFileException;

import sound.Audio;
import sound.Sound;

public class FileWave extends Wave {
	
	private final File file;
	private final Sound sound;
	
	public FileWave(File file) {
		this.file = file;
		
		Sound sound = null;
		try {
			sound = Audio.getSound(file);
		} catch (UnsupportedAudioFileException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		this.sound = sound;
		System.out.println("This is working.");
	}

	public Sound getPeriod(double frequency) {
		this.frequency = frequency;
		
		//double wavePeriod = 1.0 / frequency;
		double samplePeriod = 1.0 / 44100.0;
		double wavePeriod = samplePeriod * sound.getData().length;
		
		double periodicIncrementer = samplePeriod / wavePeriod;
		int numSteps = (int)(wavePeriod / samplePeriod);
		double waveIndex = 0;

		//index goes from 0 -> numSteps as waveIndex goes from 0 -> 1
		
		//A short is 2 bytes, so the size of the bytebuffer is numSteps * 2
		ByteBuffer buffer = ByteBuffer.allocate(numSteps * 2);
		
		for(int index = 0; index < numSteps; index++) {
			buffer.putShort(getValue(waveIndex));
			waveIndex += periodicIncrementer;
		}
		
		return new Sound(buffer.array(), defaultFormat, this);
	}
	
	protected short getValue(double waveIndex) {
		int index = (int)(waveIndex * sound.getData().length);
		int beginIndex = (index % 2 == 0) ? index : index - 1;
		int endIndex = beginIndex + 1;
		byte value0 = sound.getData()[beginIndex];
		byte value1 = sound.getData()[endIndex];
		ByteBuffer bb = ByteBuffer.allocate(2);
		bb.order(sound.getFormat().isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
		bb.put(value0);
		bb.put(value1);
		return bb.getShort(0);
	}
	
	public String toString() {
		return "File wave with frequency " + frequency;
	}

}
