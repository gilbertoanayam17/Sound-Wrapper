package proyecto;

/*
 Motor de sonido genérico. Define lo común: BPM, preset de teclado y la firma
 de las operaciones que cualquier implementación concreta debe ofrecer.
 */
public abstract class AbstractSoundEngine {

	// Tempo actual en BPM (negras por minuto).
	protected int bpm = 120; // valor por defecto

	// Preset actual del teclado.
	protected Keyboard keyboardActual = Keyboard.ORG_16_2;

	// --------- BPM ---------

	public void setBpm(int bpm) {
		if (bpm <= 0) {
			System.out.println("BPM inválido, se mantiene " + this.bpm);
			return;
		}
		this.bpm = bpm;
	}

	public int getBpm() {
		return bpm;
	}

	// --------- Keyboard/preset del teclado ---------

	public void setKeyboard(Keyboard keyboard) {
		if (keyboard == null) {
			System.out.println("Keyboard nulo, se mantiene " + this.keyboardActual);
			return;
		}
		this.keyboardActual = keyboard;
	}

	public Keyboard getKeyboard() {
		return keyboardActual;
	}

	// --------- Operaciones obligatorias ---------

	/*
	 Reproduce una tecla con una duración rítmica. Cada implementación concreta
	 decide cómo generar el audio.
	 */
	public abstract void playKey(Key key, Octaves octave, KeyDuration duration);

	/*
	 Versión comodín: negra por defecto. Se implementa aquí porque es lógica común
	 a cualquier motor.
	 */
	public void playKey(Key key, Octaves octave) {
		playKey(key, octave, KeyDuration.NEGRA);
	}

	/*
	 Liberar recursos asociados al motor de sonido.
	 */
	public abstract void shutdown();
}
