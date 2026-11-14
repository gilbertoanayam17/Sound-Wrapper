package project;

// Octave of the key: natural, one octave down (MINOR), one octave up (MAJOR)
public enum Octaves {
	NATURAL(0),
	MINOR(-12), 
	MAJOR(12);

	private final int semitoneOffset;

	Octaves(int semitoneOffset) {
		this.semitoneOffset = semitoneOffset;
	}

	public double adjustFrequency(double baseFrequency) {
		double factor = Math.pow(2.0, semitoneOffset / 12.0);
		return baseFrequency * factor;
	}
}

