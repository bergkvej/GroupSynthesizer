package wave;

public class SawtoothWave extends Wave {

	protected short getValue(double waveIndex) {
		return (short)(Short.MAX_VALUE * (2 * waveIndex - Math.floor(waveIndex)));
	}
	
	public String toString() {
		return "Sawtooth wave with frequency " + frequency;
	}

}
