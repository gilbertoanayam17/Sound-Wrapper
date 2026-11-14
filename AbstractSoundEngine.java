package project;

/*
 Generic sound engine. Defines the shared parts: BPM, keyboard preset and the
 signature of the operations every concrete implementation must provide.
 */
public abstract class AbstractSoundEngine {

	// Current tempo in BPM (quarter notes per minute).
	protected int bpm = 120; // default value

	// Current keyboard preset.
	protected Keyboard currentKeyboard = Keyboard.ORG_16_2;

	// --------- BPM ---------

	public void setBpm(int bpm) {
		if (bpm <= 0) {
			System.out.println("Invalid BPM, keeping " + this.bpm);
			return;
		}
		this.bpm = bpm;
	}

	public int getBpm() {
		return bpm;
	}

	// --------- Keyboard preset ---------

	public void setKeyboard(Keyboard keyboard) {
		if (keyboard == null) {
			System.out.println("Null keyboard, keeping " + this.currentKeyboard);
			return;
		}
		this.currentKeyboard = keyboard;
	}

	public Keyboard getKeyboard() {
		return currentKeyboard;
	}

	// --------- Required operations ---------

	/*
	 Plays a key with a rhythmic duration. Each concrete implementation
	 decides how to generate the audio.
	 */
	public abstract void playKey(Key key, Octaves octave, KeyDuration duration);

	/*
	 Convenience overload: quarter note by default. Implemented here because the
	 logic is shared by every engine.
	 */
	public void playKey(Key key, Octaves octave) {
		playKey(key, octave, KeyDuration.QUARTER);
	}

	/*
	 Releases the resources held by the sound engine.
	 */
	public abstract void shutdown();
}
