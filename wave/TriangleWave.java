package wave;

public class TriangleWave extends Wave{

	protected short getValue(double waveIndex) {
		return waveIndex < 0.5 ? (short)(Short.MAX_VALUE * (-1 + waveIndex * 4)) : (short)(Short.MAX_VALUE * (1 - (waveIndex-0.5) * 4));
	}

}
