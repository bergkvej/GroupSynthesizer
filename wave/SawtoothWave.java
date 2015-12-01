package wave;

public class SawtoothWave extends Wave {

	protected short getValue(double waveIndex) {
		return (short)(Short.MAX_VALUE * (waveIndex - Math.floor(waveIndex)));
	}

}
