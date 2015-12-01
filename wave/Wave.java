package wave;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;

import sound.Sound;

public abstract class Wave {

	//Default audio format will be 44100 Hz, 2 byte sample size (= 1 short), mono, signed, big endian.
	public static final AudioFormat defaultFormat = new AudioFormat(44100, 16, 1, true, true);
	
	//This assumes 16 bit sample size
	public Sound getPeriod(double frequency) {
		double wavePeriod = 1.0 / frequency;
		double samplePeriod = 1.0 / 44100.0;
		
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
		
		return new Sound(buffer.array(), defaultFormat);
	}
	
	//Wave index is a double that goes from 0 to 1, hitting all parts of the period.
	protected abstract short getValue(double waveIndex);
}
