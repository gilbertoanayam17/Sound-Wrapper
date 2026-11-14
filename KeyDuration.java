package project;

/*
 Rhythmic note value.
 Expressed as the number of quarter notes each value lasts.
 */
public enum KeyDuration {

	WHOLE(4.0), // 4 quarter notes
	HALF(2.0), // 2 quarter notes
	QUARTER(1.0), // 1 quarter note (the BPM base pulse)
	EIGHTH(0.5), // half a quarter note
	SIXTEENTH(0.25); // a quarter of a quarter note

	// How many quarter notes this value lasts.
	private final double beats;

	KeyDuration(double beats) {
		this.beats = beats;
	}

	
	 // Converts the note value to milliseconds at the given tempo. BPM means "quarter notes per minute".

	public int toMilliseconds(int bpm) {
		// Length of a quarter note in milliseconds:
		// 60,000 ms (1 min) / BPM.
		double quarterMs = 60000.0 / bpm;

		double durationMs = beats * quarterMs;

		// Round to the nearest integer.
		return (int) Math.round(durationMs);
	}
}
