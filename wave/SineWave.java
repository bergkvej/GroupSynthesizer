package wave;

public class SineWave extends Wave {

	protected short getValue(double waveIndex) {
		return (short)(Short.MAX_VALUE * Math.sin(2 * Math.PI * waveIndex));
	}
}
