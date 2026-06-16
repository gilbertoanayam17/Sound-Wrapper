package proyecto;

/*
 Encapsula la lógica matemática para generar la forma de onda
 según el preset del teclado (Keyboard).
 */
public class KeyboardVariant {

	public static double generateValue(double t, double frequency, Keyboard keyboard) {
		return keyboard.getWaveform().sampleAt(t, frequency);
	}
}
