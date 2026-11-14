package project;

/*
 Wraps the math that generates the waveform
 for the current keyboard preset (Keyboard).
 */
public class KeyboardVariant {

	public static double generateValue(double t, double frequency, Keyboard keyboard) {
		return keyboard.getWaveform().sampleAt(t, frequency);
	}
}
