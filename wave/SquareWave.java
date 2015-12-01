package wave;

public class SquareWave extends Wave {

	protected short getValue(double waveIndex) {
		return waveIndex < 0.5 ? Short.MIN_VALUE : Short.MAX_VALUE;
	}
	
	public String toString() {
		return "Square wave with frequency " + frequency;
	}

}
